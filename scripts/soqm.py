#!/usr/bin/env python3
"""Local, standard-library-only SOQM validation and deterministic packaging."""
from __future__ import annotations

import argparse
from concurrent.futures import ThreadPoolExecutor, as_completed
from datetime import datetime, timezone
import hashlib
import json
from pathlib import Path, PurePosixPath
import re
import sys
import time
import tomllib
from urllib.parse import urlparse
from urllib.request import Request, urlopen
from zipfile import ZipFile, ZipInfo, ZIP_DEFLATED

ROOT = Path(__file__).resolve().parent.parent
PAYLOAD = ROOT / 'overrides'


def read(path):
    return json.loads(path.read_text(encoding='utf-8'))


def write(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(value, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')


def digest(path):
    with path.open('rb') as stream:
        return hashlib.file_digest(stream, 'sha256').hexdigest()


def require(ok, message):
    if not ok:
        raise ValueError(message)


def safe_name(name):
    path = PurePosixPath(name)
    require(bool(name) and not path.is_absolute() and '\\' not in name,
            f'Unsafe relative path: {name}')
    require(all(part not in ('', '.', '..') and ':' not in part
                for part in name.split('/')), f'Unsafe relative path: {name}')
    return name


def payload_hashes():
    result = {}
    for path in sorted(PAYLOAD.rglob('*')):
        require(not path.is_symlink(), 'Payload symlink rejected')
        if path.is_file():
            name = safe_name(path.relative_to(PAYLOAD).as_posix())
            require(name.casefold() not in {n.casefold() for n in result},
                    f'Case-colliding file: {name}')
            result[name] = digest(path)
    return result


def metadata():
    return read(ROOT / 'soqm.json'), read(ROOT / 'metadata/mods.lock.json')['mods']


def manifest():
    pack, mods = metadata()
    return {
        'minecraft': {'version': pack['minecraft'], 'modLoaders': [
            {'id': 'neoforge-' + pack['neoforge'], 'primary': True}]},
        'manifestType': 'minecraftModpack', 'manifestVersion': 1,
        'name': pack['name'], 'version': pack['version'], 'author': pack['author'],
        'files': [{'projectID': row['project_id'], 'fileID': row['file_id'],
                   'required': True} for row in mods],
        'overrides': 'overrides',
    }


def package_path():
    pack, _ = metadata()
    return ROOT / 'dist' / f"SOQM-{pack['version']}-curseforge.zip"


def check():
    pack, mods = metadata()
    require(pack['minecraft'] == '1.21.1' and pack['java_major'] == 21,
            'Unexpected supported runtime')
    require(bool(re.fullmatch(r'\d+\.\d+\.\d+(?:-[a-z0-9.]+)?', pack['version'])),
            'Invalid pack version')
    lock = read(ROOT / 'metadata/mods.lock.json')
    require(lock['minecraft'] == pack['minecraft'] and lock['neoforge'] == pack['neoforge'],
            'Mod lock runtime mismatch')
    seen_names, seen_projects = set(), set()
    for row in mods:
        name = safe_name(row['filename'])
        require('/' not in name and name.endswith('.jar'), 'Invalid mod filename')
        require(name.casefold() not in seen_names, 'Duplicate mod filename')
        require(row['project_id'] not in seen_projects, 'Duplicate mod project')
        seen_names.add(name.casefold()); seen_projects.add(row['project_id'])
        require(type(row['project_id']) is int and row['project_id'] > 0
                and type(row['file_id']) is int and row['file_id'] > 0, 'Invalid CurseForge pin')
        require(bool(re.fullmatch('[0-9a-f]{64}', row['sha256'])), 'Invalid mod SHA256')
        require(row['bytes'] > 0, 'Missing mod byte count')
        url = urlparse(row['source_url'])
        require(url.scheme == 'https' and url.hostname == 'www.curseforge.com',
                'Mod source must be an official CurseForge page')
    actual = payload_hashes()
    expected = read(ROOT / 'metadata/payload.sha256.json')
    require(actual == expected, 'Payload changed: inspect edits, then run `python3 scripts/soqm.py lock`')
    authored = read(ROOT / 'metadata/authored-mods.json')
    expected_jars = {'mods/' + row['filename']: row['sha256'] for row in authored}
    jars = {name: h for name, h in actual.items()
            if PurePosixPath(name).suffix.casefold() == '.jar'}
    require(jars == expected_jars, 'Only hash-pinned authored JARs may be embedded')
    forbidden_roots = {'saves', 'logs', 'crash-reports', 'screenshots', 'XaeroWaypoints', 'XaeroWorldMap'}
    for name in actual:
        require(name.split('/')[0] not in forbidden_roots, 'Player/runtime data in payload')
        require(PurePosixPath(name).name not in {'options.txt', 'servers.dat', 'usercache.json', '.env'},
                'Private instance configuration in payload')
        if name.endswith(('.json', '.snbt')):
            read(PAYLOAD / name)
        if name.endswith('.toml'):
            tomllib.loads((PAYLOAD / name).read_text(encoding='utf-8'))
        require('craftoria' not in name.casefold(), 'Uncleared upstream-derived file in payload')
    require(read(ROOT / 'manifest.json') == manifest(), 'Manifest is stale; run lock')
    print(f"Validated {len(mods)} pinned third-party mods, {len(authored)} authored mods, {len(actual)} payload files.")
    return {'third_party_mods': len(mods), 'authored_mods': len(authored), 'payload_files': len(actual)}


def lock():
    write(ROOT / 'metadata/payload.sha256.json', payload_hashes())
    write(ROOT / 'manifest.json', manifest())
    print('Updated payload lock and manifest. Review the diff before committing.')


def archive_entries():
    entries = {'manifest.json': (ROOT / 'manifest.json').read_bytes()}
    for path in sorted(PAYLOAD.rglob('*')):
        if path.is_file():
            entries['overrides/' + path.relative_to(PAYLOAD).as_posix()] = path.read_bytes()
    docs = [p.relative_to(ROOT).as_posix() for p in sorted((ROOT / 'docs').glob('*.md'))]
    for name in ['LICENSE', 'THIRD_PARTY_NOTICES.md', 'README.md', 'MODLIST.md', 'CHANGELOG.md',
                 'assets/banner.svg', 'assets/campaign-route.svg', 'assets/campaign-ae2.svg',
                 'downloads/README.md', 'reports/README.md', 'reports/verification-0.4.7.md',
                 'reports/verification-0.4.8.md', *docs]:
        if (ROOT / name).is_file():
            require(not (ROOT / name).is_symlink(), 'Package documentation symlink rejected')
            entries[name] = (ROOT / name).read_bytes()
    return entries


def zip_bytes(entries, target):
    with ZipFile(target, 'w', compression=ZIP_DEFLATED, compresslevel=9) as archive:
        for name, data in sorted(entries.items()):
            safe_name(name)
            info = ZipInfo(name, date_time=(2026, 1, 1, 0, 0, 0))
            info.compress_type = ZIP_DEFLATED
            info.create_system = 3
            info.external_attr = 0o100644 << 16
            archive.writestr(info, data, compresslevel=9)


def build():
    check()
    target = package_path()
    target.parent.mkdir(exist_ok=True)
    zip_bytes(archive_entries(), target)
    (target.parent / 'SHA256SUMS').write_text(f'{digest(target)}  {target.name}\n', encoding='utf-8')
    print(f'Built {target.relative_to(ROOT)} ({target.stat().st_size:,} bytes)')


def check_package():
    check()
    target = package_path()
    entries = archive_entries()
    with ZipFile(target) as archive:
        require(archive.testzip() is None, 'ZIP CRC failure')
        require(len(archive.namelist()) == len(set(archive.namelist())), 'Duplicate ZIP paths')
        require(set(archive.namelist()) == set(entries), 'Unexpected package contents')
        for name in archive.namelist():
            safe_name(name)
            require(archive.read(name) == entries[name], f'Package mismatch: {name}')
        require(json.loads(archive.read('manifest.json')) == manifest(), 'Wrong packaged manifest')
    import io
    rebuilt = io.BytesIO()
    zip_bytes(entries, rebuilt)
    require(hashlib.sha256(rebuilt.getvalue()).hexdigest() == digest(target),
            'Package is not reproducible on this Python/zlib runtime')
    print(f'Package contents, CRC, hashes and deterministic rebuild passed: {digest(target)}')


def verify_downloads():
    """Audit exact public pinned files locally. Downloads are never packaged."""
    check()
    _, mods = metadata()
    cache = ROOT / '.cache/mods'
    cache.mkdir(parents=True, exist_ok=True)

    def fetch(row):
        dst = cache / row['filename']
        require(not dst.is_symlink(), 'Cached mod symlink rejected')
        if dst.is_file():
            require(digest(dst) == row['sha256'] and dst.stat().st_size == row['bytes'],
                    f"Cached mod hash mismatch: {row['filename']}")
            return {'filename': row['filename'], 'sha256': row['sha256'], 'bytes': row['bytes']}
        file_id = str(row['file_id'])
        from urllib.parse import quote
        url = f"https://mediafilez.forgecdn.net/files/{file_id[:-3]}/{int(file_id[-3:])}/{quote(row['filename'])}"
        part = dst.with_suffix('.part')
        for attempt in range(3):
            try:
                request = Request(url, headers={'User-Agent': 'SOQM-local-audit/0.1 (github.com/lsanarchist/SOQM)'})
                with urlopen(request, timeout=60) as source, part.open('wb') as output:
                    size = 0
                    while chunk := source.read(1024 * 1024):
                        size += len(chunk)
                        require(size <= row['bytes'], 'Download exceeded pinned size')
                        output.write(chunk)
                require(part.stat().st_size == row['bytes'] and digest(part) == row['sha256'],
                        f"Downloaded mod hash mismatch: {row['filename']}")
                with ZipFile(part) as jar:
                    require(jar.testzip() is None, 'JAR CRC failure')
                part.replace(dst)
                return {'filename': row['filename'], 'sha256': row['sha256'], 'bytes': row['bytes']}
            except Exception:
                part.unlink(missing_ok=True)
                if attempt == 2:
                    raise
                time.sleep(attempt + 1)

    results, failures = [], []
    with ThreadPoolExecutor(max_workers=4) as pool:
        futures = {pool.submit(fetch, row): row['filename'] for row in mods}
        for future in as_completed(futures):
            try:
                results.append(future.result())
            except Exception as exc:
                # Keep URLs and host details out of reports; no credentials used.
                failures.append({'filename': futures[future], 'error': type(exc).__name__})
            done = len(results) + len(failures)
            if done % 10 == 0 or done == len(mods):
                print(f'Checked {done}/{len(mods)}', flush=True)
    report = {'checked_at_utc': datetime.now(timezone.utc).isoformat(),
              'scope': 'Public pinned file availability, SHA256, size and ZIP CRC; no game launch.',
              'passed': not failures, 'mods_lock_sha256': digest(ROOT / 'metadata/mods.lock.json'),
              'verified': sorted(results, key=lambda row: row['filename']), 'failures': failures}
    write(ROOT / 'reports/downloads.json', report)
    require(not failures, f'{len(failures)} pinned files unavailable or invalid; see reports/downloads.json')
    print(f'All {len(results)} exact third-party files verified; no JARs added to the release.')


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('command', choices=['check', 'lock', 'build', 'check-package', 'verify-downloads'])
    command = parser.parse_args().command
    try:
        {'check': check, 'lock': lock, 'build': build, 'check-package': check_package,
         'verify-downloads': verify_downloads}[command]()
    except (ValueError, OSError, KeyError, json.JSONDecodeError) as exc:
        print(f'SOQM: {exc}', file=sys.stderr)
        return 1
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
