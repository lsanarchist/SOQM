#!/usr/bin/env python3
"""Compile personal, production-gated reward packs from the reviewed catalogue.

FTB Quests owns the existing claim IDs. The generated policy describes themed
prizes; native production evidence determines eligibility at personal claim.
"""
from __future__ import annotations
import argparse
from collections import Counter, defaultdict
from fractions import Fraction
import hashlib
import json
from pathlib import Path
import re

import campaign_refunds

ROOT = Path(__file__).resolve().parents[1]
QUESTS = ROOT / 'overrides/config/ftbquests/quests'
POLICY_PATH = ROOT / 'metadata/reward_pools.json'
RUNTIME_PATH = ROOT / 'overrides/kubejs/config/soqm_rewards.json'
TABLES_PATH = QUESTS / 'reward_tables'
BOOK_QUEST = '514D010000000001'
ID_PATTERN = re.compile(r'[0-9A-F]{16}\Z')
ITEM_PATTERN = re.compile(r'[a-z0-9_.-]+:[a-z0-9_./-]+\Z')
RARITIES = ('common', 'uncommon', 'rare', 'epic')
GATES = ('kubejs:industrial_chassis', 'kubejs:precision_mechanism',
         'kubejs:dimensional_matrix', 'kubejs:stellar_alloy',
         'kubejs:mythic_circuit', 'kubejs:emc_core')
PROGRESSIVE_TAG = 'soqm:progressive_reward'
DEFAULT_ICONS = {
    'survival': 'minecraft:bundle', 'workshop': 'minecraft:anvil',
    'engineering': 'mekanism:basic_control_circuit', 'logistics': 'minecraft:barrel',
    'processing': 'minecraft:furnace', 'scholar': 'minecraft:book',
    'arcane': 'botania:mana_tablet', 'equipment': 'minecraft:iron_chestplate',
    'expedition': 'minecraft:compass', 'hunting': 'minecraft:bow',
    'settlement': 'minecraft:bell', 'power': 'minecraft:redstone',
    'space': 'ad_astra:space_helmet', 'masterwork': 'kubejs:mythic_circuit',
}


def serialize(value) -> str:
    return json.dumps(value, ensure_ascii=False, indent=2) + '\n'


def chapters_from_disk() -> list[dict]:
    return [json.loads(p.read_text(encoding='utf-8'))
            for p in sorted((QUESTS / 'chapters').glob('*.snbt'))]


def load_policy() -> dict:
    policy = json.loads(POLICY_PATH.read_text(encoding='utf-8'))
    validate_policy(policy)
    return policy


def eligible_entries(pool: dict, level: int, produced: set[str]) -> list[dict]:
    return [e for e in pool['entries'] if e.get('min_level', 0) <= level
            and set(e.get('requires_craft', [])) <= produced]


def validate_policy(policy: dict) -> None:
    if policy.get('schema') != 2:
        raise ValueError('Unknown progressive reward policy schema')
    if [r['level'] for r in policy['levels']] != list(range(1, 7)) or [r['item'] for r in policy['levels']] != list(GATES):
        raise ValueError('Production levels must be the six ordered campaign milestones')
    if policy['draws_by_level'] != [3, 3, 4, 4, 4, 5, 5]:
        raise ValueError('Review changed personal reward quantities')
    if policy.get('guaranteed_quality_draws') != 1:
        raise ValueError('Every pack reserves one draw for an unlocked uncommon-or-better prize')
    weights = policy['rarity_weights_by_level']
    if len(weights) != 7:
        raise ValueError('Every production level needs rarity weights')
    for row in weights:
        if set(row) != set(RARITIES) or any(type(v) is not int or v <= 0 for v in row.values()):
            raise ValueError('Rarity weights must be strictly positive integers')
        if sum(row.values()) != 100:
            raise ValueError('Rarity weights must sum to 100')
    if any(weights[i]['rare'] + weights[i]['epic'] > weights[i + 1]['rare'] + weights[i + 1]['epic'] for i in range(6)):
        raise ValueError('Higher production levels must improve rare-prize weights')
    pools = policy['pools']
    keys = {p['key'] for p in pools}
    if len(pools) != len(keys) or len(keys) < 14 or 'survival' not in keys:
        raise ValueError('The campaign needs distinct, complete themed pools')
    for pool in pools:
        if not re.fullmatch(r'[a-z_]+', pool['key']) or pool.get('fallback', 'survival') not in keys:
            raise ValueError('Unsafe pool key or unknown fallback')
        if set(pool['title']) != {'uk_ua', 'en_us'} or not all(pool['title'].values()):
            raise ValueError('Pool titles must be bilingual')
        if not 16 <= len(pool['entries']) <= 40:
            raise ValueError('Each theme needs 16–40 distinct prizes')
        if len({e['item'] for e in pool['entries']}) != len(pool['entries']):
            raise ValueError('Duplicate item hides its combined probability')
        for entry in pool['entries']:
            item = entry['item']
            if not ITEM_PATTERN.fullmatch(item):
                raise ValueError('Invalid reward item ID')
            if any(word in item for word in ('plastic', 'hdpe', 'mayhem_cube')):
                raise ValueError(f'Plastic and nested cubes remain excluded: {item}')
            if type(entry['count']) is not int or not 1 <= entry['count'] <= 16:
                raise ValueError('Each draw awards one reviewed stack of 1–16 items')
            if type(entry['weight']) is not int or entry['weight'] <= 0:
                raise ValueError('Entry weights must be positive integers')
            if entry['rarity'] not in RARITIES:
                raise ValueError('Unknown prize rarity')
            minimum = entry.get('min_level', 0)
            if type(minimum) is not int or not 0 <= minimum <= 6:
                raise ValueError('Invalid production level')
            required = entry.get('requires_craft', [])
            if not isinstance(required, list) or len(set(required)) != len(required) or not all(isinstance(x, str) and ITEM_PATTERN.fullmatch(x) for x in required):
                raise ValueError('Production conditions must be distinct exact item IDs')
            evidence = entry.get('evidence')
            if not isinstance(evidence, dict) or evidence.get('method') not in {'starter_supply', 'crafting', 'ae2_inscriber', 'mekanism_infusing'}:
                raise ValueError(f'Record a supported production method: {item}')
            if evidence['method'] != 'starter_supply' and not evidence.get('recipe_ids'):
                raise ValueError(f'Manufactured prizes need exact recipe evidence: {item}')
            manufactured = (evidence['method'] != 'starter_supply'
                            or item.startswith(('kubejs:', 'sophisticatedstorage:', 'sophisticatedbackpacks:'))
                            or item in {'ae2:logic_processor', 'ae2:calculation_processor', 'ae2:engineering_processor'}
                            or (not item.startswith('minecraft:') and entry['rarity'] in {'rare', 'epic'}))
            if manufactured and item not in required:
                raise ValueError(f'Manufactured prizes require proof of that same item: {item}')
            if (item.startswith('kubejs:') or entry['rarity'] in {'rare', 'epic'}) and entry['count'] != 1:
                raise ValueError('Rare prizes and custom progression components are single-item prizes')
            if set(entry.get('name', {})) != {'uk_ua', 'en_us'} or not all(entry['name'].values()):
                raise ValueError('Every prize needs bilingual names')
        if len(eligible_entries(pool, 0, set())) < 5:
            raise ValueError('Every theme needs at least five guaranteed starter options')
    if set(policy['chapter_pools'].values()) != keys:
        raise ValueError('Every theme must be assigned and every chapter theme must exist')
    for qid, milestone in policy['milestones'].items():
        if not ID_PATTERN.fullmatch(qid) or not 0 <= milestone['tier'] <= 6:
            raise ValueError('Invalid fixed milestone reward')


def entry_id(pool_key: str, item_id: str) -> str:
    """Stable catalogue identity, never a persistent reward claim."""
    return '5A52' + hashlib.sha256(f'soqm:reward:{pool_key}:{item_id}'.encode()).hexdigest()[:12].upper()


def quest_stage(quest: dict) -> int:
    return max((int(t[6:]) for t in quest.get('tags', []) if re.fullmatch(r'stage_[0-6]', t)), default=0)


def choose_pool(chapter: dict, quest: dict, policy: dict) -> dict:
    """Theme belongs to the chapter; quality is evaluated at personal claim."""
    key = policy['chapter_pools'][chapter['filename']]
    return next(p for p in policy['pools'] if p['key'] == key)


def production_level(produced: set[str], policy: dict) -> int:
    return max((r['level'] for r in policy['levels'] if r['item'] in produced), default=0)


def probabilities(pool: dict, level: int, produced: set[str], policy: dict) -> dict[str, Fraction]:
    """Exact probability per independent draw, normalized over available groups."""
    groups = defaultdict(list)
    for entry in eligible_entries(pool, level, produced):
        groups[entry['rarity']].append(entry)
    if not groups:
        return {}
    weights = policy['rarity_weights_by_level'][level]
    total = sum(weights[g] for g in groups)
    return {entry['item']: Fraction(weights[group], total) * Fraction(entry['weight'], sum(e['weight'] for e in entries))
            for group, entries in groups.items() for entry in entries}


def quality_probabilities(pool: dict, level: int, produced: set[str], policy: dict) -> dict[str, Fraction]:
    """Odds for the reserved draw; unavailable premium prizes stay locked.

    With no eligible uncommon-or-better prize, the draw uses the normal pool.
    It never substitutes a locked item to satisfy the quality floor.
    """
    premium = {**pool, 'entries': [e for e in pool['entries'] if e['rarity'] != 'common']}
    return probabilities(premium, level, produced, policy) or probabilities(pool, level, produced, policy)


def persistent_ids(chapters: list[dict]) -> list[str]:
    ids = []
    for chapter in chapters:
        ids.append(chapter['id'])
        ids.extend(i['id'] for i in chapter.get('images', []) if 'id' in i)
        for quest in chapter['quests']:
            ids.append(quest['id'])
            ids.extend(t['id'] for t in quest['tasks'])
            ids.extend(r['id'] for r in quest['rewards'])
    return ids


def locale_entries(language: str = 'uk_ua', policy: dict | None = None, chapters: list[dict] | None = None) -> dict:
    if language not in {'uk_ua', 'en_us'}:
        raise ValueError('Unreviewed language')
    policy = load_policy() if policy is None else policy
    chapters = chapters_from_disk() if chapters is None else chapters
    refunds = campaign_refunds.load(chapters)
    names = campaign_refunds.item_names()
    result = {}
    for chapter in chapters:
        for quest in chapter['quests']:
            refund = refunds.get(quest['id'])
            milestone = policy['milestones'].get(quest['id'])
            if milestone and not refund:
                continue
            for reward in quest['rewards']:
                if reward.get('item', {}).get('id') == 'ftbquests:book':
                    continue
                title = (('Куб Mayhem' if language == 'uk_ua' else 'Mayhem Cube') + f" T{milestone['tier']}"
                         if milestone else choose_pool(chapter, quest, policy)['title'][language])
                if refund:
                    title += ' + ' + ', '.join(f"{p['count']} × {names[p['item']][language]}" for p in refund['items'])
                result[f"reward.{reward['id']}.title"] = title
    return result


def runtime_policy(chapters: list[dict], policy: dict | None = None) -> dict:
    policy = load_policy() if policy is None else policy
    refunds = campaign_refunds.load(chapters)
    mappings = {}
    for chapter in chapters:
        for quest in chapter['quests']:
            for reward in quest['rewards']:
                if reward.get('type') == 'custom' and PROGRESSIVE_TAG in reward.get('tags', []):
                    mappings[reward['id']] = {'pool': choose_pool(chapter, quest, policy)['key'], 'quest_id': quest['id']}
                    if quest['id'] in refunds:
                        if campaign_refunds.TAG not in reward.get('tags', []):
                            raise ValueError('Apply the questbook refresh before compiling recipe refunds')
                        mappings[reward['id']]['refund'] = campaign_refunds.runtime(refunds[quest['id']])
                        if quest['id'] in policy['milestones']:
                            mappings[reward['id']]['cube'] = f"kubejs:mayhem_cube_t{policy['milestones'][quest['id']]['tier']}"
    mapped_refunds = {m['quest_id'] for m in mappings.values() if 'refund' in m}
    if mapped_refunds != set(refunds):
        raise ValueError('Every recipe refund needs its retained custom reward slot')
    produced = set(GATES)
    for pool in policy['pools']:
        for entry in pool['entries']:
            produced.update(entry.get('requires_craft', []))
    produced.update(row['output'] for row in refunds.values())
    pools = [{'key': p['key'], 'title': p['title'], 'fallback': p.get('fallback', 'survival'),
              'entries': [{k: v for k, v in e.items() if k != 'evidence'} for e in p['entries']]} for p in policy['pools']]
    return {'schema': 2, 'version': policy.get('version', '2.0'), 'levels': policy['levels'],
            'draws_by_level': policy['draws_by_level'], 'guaranteed_quality_draws': policy['guaranteed_quality_draws'],
            'rarity_weights_by_level': policy['rarity_weights_by_level'],
            'pools': pools, 'production_items': sorted(produced), 'reward_mappings': dict(sorted(mappings.items()))}


def apply(chapters: list[dict], locale: dict) -> dict:
    policy = load_policy()
    refunds = campaign_refunds.load(chapters)
    before = persistent_ids(chapters)
    by_id = {quest['id']: quest for chapter in chapters for quest in chapter['quests']}
    if set(policy['milestones']) - set(by_id):
        raise ValueError('Lucky-cube milestone points to an unknown quest')
    for ident, milestone in policy['milestones'].items():
        if milestone['tier'] > quest_stage(by_id[ident]):
            raise ValueError(f'Lucky-cube tier exceeds its quest progression: {ident}')
    usage = Counter()
    books = cubes = refund_cubes = 0
    for chapter in chapters:
        for quest in chapter['quests']:
            pool = choose_pool(chapter, quest, policy)
            for reward in quest['rewards']:
                if quest['id'] == BOOK_QUEST and reward.get('item', {}).get('id') == 'ftbquests:book':
                    if reward.get('team_reward', True):
                        raise ValueError('The original introductory book must remain personal')
                    books += 1
                    continue
                if reward.get('auto', 'disabled') != 'disabled':
                    raise ValueError('Review non-manual reward before migration')
                kept = {k: reward[k] for k in ('id', 'exclude_from_claim_all', 'ignore_reward_blocking', 'hide_tooltip') if k in reward}
                tags = [t for t in reward.get('tags', []) if t not in {PROGRESSIVE_TAG, campaign_refunds.TAG}
                        and not t.startswith('soqm:reward_pool:')]
                reward.clear()
                reward.update(kept)
                reward.update({'team_reward': False, 'auto': 'disabled'})
                milestone = policy['milestones'].get(quest['id'])
                refund = refunds.get(quest['id'])
                if milestone and not refund:
                    reward.update({'type': 'item', 'item': {'id': f"kubejs:mayhem_cube_t{milestone['tier']}", 'count': 1}, 'count': 1, 'random_bonus': 0})
                    # The old custom pack's localized title must not mask the
                    # cube's real item name after changing this existing slot.
                    locale.pop(f"reward.{reward['id']}.title", None)
                    if tags:
                        reward['tags'] = tags
                    cubes += 1
                else:
                    icon = f"kubejs:mayhem_cube_t{milestone['tier']}" if milestone else pool.get('icon', DEFAULT_ICONS[pool['key']])
                    reward.update({'type': 'custom', 'icon': icon,
                                   'tags': tags + [PROGRESSIVE_TAG, 'soqm:reward_pool:' + pool['key']]
                                           + ([campaign_refunds.TAG] if refund else [])})
                    if milestone:
                        cubes += 1
                        refund_cubes += 1
                    else:
                        usage[pool['key']] += 1
    if persistent_ids(chapters) != before:
        raise AssertionError('Reward refresh changed a saved campaign object ID')
    for key in list(locale):
        if key.startswith('reward_table.000000006A51'):
            del locale[key]
    locale.update(locale_entries('uk_ua', policy, chapters))
    entries = [e for p in policy['pools'] for e in p['entries']]
    return {'personal_rewards': sum(len(q['rewards']) for c in chapters for q in c['quests']),
            'progressive_rewards': sum(usage.values()), 'milestone_cubes': cubes, 'personal_books': books,
            'recipe_refunds': len(refunds), 'refund_cube_bundles': refund_cubes,
            'custom_rewards': sum(usage.values()) + refund_cubes,
            'pools': len(policy['pools']), 'catalogue_entries': len(entries), 'distinct_items': len({e['item'] for e in entries}),
            'modded_items': len({e['item'] for e in entries if not e['item'].startswith('minecraft:')}),
            'production_gated_entries': sum(bool(e.get('requires_craft')) for e in entries),
            'pool_usage': dict(sorted(usage.items())), 'draws_by_level': policy['draws_by_level']}


def obsolete_tables() -> list[Path]:
    return sorted(TABLES_PATH.glob('000000006A51*.snbt'))


def expected_outputs(chapters: list[dict]) -> dict[Path, str]:
    return {RUNTIME_PATH: serialize(runtime_policy(chapters))}


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--write', action='store_true', help='write the runtime policy')
    parser.add_argument('--check', action='store_true', help='check the generated runtime policy')
    args = parser.parse_args()
    if args.write and args.check:
        parser.error('Choose --write or --check')
    outputs = expected_outputs(chapters_from_disk())
    stale = [str(p.relative_to(ROOT)) for p, content in outputs.items() if not p.exists() or p.read_text(encoding='utf-8') != content]
    stale.extend(str(p.relative_to(ROOT)) for p in obsolete_tables())
    if args.check and stale:
        parser.exit(1, 'Reward policy is stale; run campaign_refresh.py\n' + '\n'.join(stale) + '\n')
    if args.write:
        for path, content in outputs.items():
            path.parent.mkdir(parents=True, exist_ok=True)
            path.write_text(content, encoding='utf-8')
        for path in obsolete_tables():
            path.unlink()
    print(json.dumps({'mode': 'write' if args.write else 'check' if args.check else 'inspect', 'changed_files': len(stale)}))
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
