"""Removal must leave no live dependency, and the offline audit must be safe."""

from copy import deepcopy
import gzip
import hashlib
import json
from pathlib import Path
import struct
import sys
import tempfile
import unittest
from unittest.mock import patch
import zlib

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "scripts"))
import audit_storage_upgrade as audit
import campaign_progression as progression
import campaign_storage as storage


def nbt(value: str) -> bytes:
    data = value.encode()
    return b"\x0a\x00\x00\x08\x00\x02id" + len(data).to_bytes(2, "big") + data + b"\x00"


def named(kind: int, payload: bytes, name: bytes = b"x") -> bytes:
    return bytes([kind]) + len(name).to_bytes(2, "big") + name + payload


def region(path: Path, payload: bytes, codec: int = 2, external: bool = False):
    compressed = gzip.compress(payload) if codec == 1 else zlib.compress(payload) if codec == 2 else payload
    if external:
        path.with_name("c.0.0.mcc").write_bytes(compressed)
        compressed = b""
        codec |= 128
    chunk = struct.pack(">I", len(compressed) + 1) + bytes([codec]) + compressed
    sectors = (len(chunk) + 4095) // 4096
    header = ((2 << 8) | sectors).to_bytes(4, "big") + bytes(8192 - 4)
    path.write_bytes(header + chunk.ljust(sectors * 4096, b"\x00"))


class StorageUpgradeTests(unittest.TestCase):
    def test_removed_mod_has_no_live_recipe_quest_reward_or_emc_reference(self):
        mods = json.loads((ROOT / "metadata/mods.lock.json").read_text())["mods"]
        self.assertNotIn(228756, {row["project_id"] for row in mods})
        self.assertIn(619320, {row["project_id"] for row in mods})
        for path in (ROOT / "overrides").rglob("*"):
            if path.is_file() and path.suffix in {".json", ".snbt", ".js", ".toml"}:
                self.assertFalse("ironchest:" in path.read_text(), str(path.relative_to(ROOT)))

    def test_recipe_dependencies_use_inventory_free_upgrades(self):
        rows = {row["id"]: row["recipe"] for row in progression.recipe_rows()}
        for recipe, wanted in {
            "kubejs:expert/ae2/network/blocks/storage_drive": "sophisticatedstorage:iron_to_gold_tier_upgrade",
            "kubejs:expert_v043/ae2/network/blocks/io_condenser": "sophisticatedstorage:gold_to_diamond_tier_upgrade",
        }.items():
            inputs = progression.ingredient_items(rows[recipe])
            self.assertIn(wanted, inputs)
            self.assertFalse(any(item.startswith("sophisticatedstorage:") and item.endswith("_chest") for item in inputs),
                             "An ordinary machine recipe must not consume a storage item carrying contents")
        for row in progression.recipe_rows():
            if row["id"].startswith("kubejs:expert_v042/sophisticatedstorage/"):
                output = progression.recipe_output(row["recipe"])
                if output in {"sophisticatedstorage:gold_chest", "sophisticatedstorage:diamond_chest", "sophisticatedstorage:netherite_chest"}:
                    self.assertTrue(row["recipe"]["type"].startswith("sophisticatedstorage:"),
                                    "Native container-data-preserving serializers must remain intact")

    def test_storage_objectives_keep_task_and_personal_reward_ids(self):
        chapter = json.loads((ROOT / "overrides/config/ftbquests/quests/chapters/qm_v030_11_storage.snbt").read_text())
        before = deepcopy(chapter)
        by_id = {q["id"]: q for q in chapter["quests"]}
        for row in storage.load()["quests"]:
            next(task for task in by_id[row["quest_id"]]["tasks"] if task["id"] == row["task_id"])["item"]["id"] = row["before_item"]
        result = storage.apply_structure([chapter])
        self.assertEqual(result["retargeted_objectives"], 6)
        self.assertEqual(storage.apply_structure([chapter])["retargeted_objectives"], 0)
        for previous, current in zip(before["quests"], chapter["quests"]):
            self.assertEqual([t["id"] for t in previous["tasks"]], [t["id"] for t in current["tasks"]])
            self.assertEqual(previous["rewards"], current["rewards"])

    def test_audit_finds_player_and_dimension_data_without_writing(self):
        with tempfile.TemporaryDirectory() as directory:
            world = Path(directory)
            (world / "level.dat").write_bytes(gzip.compress(nbt("minecraft:overworld")))
            (world / "playerdata").mkdir()
            (world / "playerdata/player.dat").write_bytes(gzip.compress(nbt("ironchest:gold_chest")))
            target = world / "dimensions/example/deep/region"
            target.mkdir(parents=True)
            region(target / "r.0.0.mca", nbt("ironchest:diamond_chest"))
            before = {p: hashlib.sha256(p.read_bytes()).hexdigest() for p in world.rglob("*") if p.is_file()}
            result = audit.audit(world)
            self.assertTrue(result["complete"])
            self.assertFalse(result["ready_for_removal"])
            self.assertEqual(len(result["references"]), 2)
            self.assertEqual(before, {p: hashlib.sha256(p.read_bytes()).hexdigest() for p in before})

    def test_audit_decodes_external_chunks_and_fails_closed_on_unknown_compression(self):
        with tempfile.TemporaryDirectory() as directory:
            world = Path(directory)
            (world / "level.dat").write_bytes(gzip.compress(nbt("minecraft:overworld")))
            target = world / "region"
            target.mkdir()
            path = target / "r.0.0.mca"
            region(path, nbt("ironchest:iron_chest"), external=True)
            self.assertTrue(audit.audit(world)["legacy_references_found"])
            region(path, nbt("minecraft:chest"), codec=4)
            self.assertFalse(audit.audit(world)["complete"])
            self.assertFalse(audit.audit(world)["ready_for_removal"])

    def test_clean_world_and_malformed_region_have_different_results(self):
        with tempfile.TemporaryDirectory() as directory:
            world = Path(directory)
            (world / "level.dat").write_bytes(nbt("minecraft:overworld"))
            path = world / "r.0.0.mca"
            region(path, nbt("sophisticatedstorage:gold_chest"), codec=1)
            self.assertTrue(audit.audit(world)["ready_for_removal"])
            path.write_bytes(b"truncated")
            self.assertFalse(audit.audit(world)["complete"])

    def test_native_zero_byte_region_is_empty_but_partial_headers_fail_closed(self):
        with tempfile.TemporaryDirectory() as directory:
            world = Path(directory)
            (world / "level.dat").write_bytes(nbt("minecraft:overworld"))
            (world / "entities").mkdir()
            path = world / "entities/r.-1.0.mca"
            path.write_bytes(b"")
            result = audit.audit(world)
            self.assertTrue(result["complete"])
            self.assertTrue(result["ready_for_removal"])
            self.assertEqual(result["nbt_records"], 1)
            self.assertEqual(path.stat().st_size, 0, "Reading an empty region must not initialize its header")
            for length in (1, 4096, 8191):
                with self.subTest(length=length):
                    path.write_bytes(bytes(length))
                    result = audit.audit(world)
                    self.assertFalse(result["complete"])
                    self.assertFalse(result["ready_for_removal"])
                    self.assertTrue(any("Truncated region file" in error["error"] for error in result["errors"]))

    def test_full_nbt_validation_accepts_every_payload_type_and_modified_utf8(self):
        text = b"ironchest:gold_chest"
        compound = named(8, len(text).to_bytes(2, "big") + text, b"id") + b"\x00"
        payloads = [
            named(1, b"\xff"), named(2, struct.pack(">h", -2)), named(3, struct.pack(">i", -3)),
            named(4, struct.pack(">q", -4)), named(5, struct.pack(">f", 1.5)), named(6, struct.pack(">d", 2.5)),
            named(7, struct.pack(">i", 3) + b"\x01\x02\x03"),
            named(8, b"\x00\x08\xc0\x80\xed\xa0\xbd\xed\xb8\x80"),  # NUL plus a UTF-16 surrogate pair.
            named(9, b"\x03" + struct.pack(">iii", 2, 17, 19)),
            named(9, b"\x00\x00\x00\x00\x00"),  # Empty TAG_End-typed list.
            named(9, b"\x0a" + struct.pack(">i", 2) + compound + b"\x00"),
            named(10, compound),
            named(11, struct.pack(">iii", 2, 7, 8)),
            named(12, struct.pack(">iqq", 2, 9, 10)),
        ]
        self.assertEqual(audit.references(b"\x0a\x00\x00" + b"".join(payloads) + b"\x00"),
                         ["ironchest:gold_chest"])
        self.assertEqual(audit.references(nbt("Українська назва")), [])

    def test_malformed_nbt_roots_lengths_ends_and_trailing_data_fail_closed(self):
        root = b"\x0a\x00\x00"
        malformed = {
            "root type alone": b"\x0a",
            "root without END": root,
            "root name truncated": b"\x0a\x00\x05ab",
            "trailing bytes": nbt("minecraft:chest") + b"\x00",
            "unknown tag": root + b"\x0d\x00\x00\x00",
            "negative list length": root + named(9, b"\x01" + struct.pack(">i", -1)) + b"\x00",
            "nonempty END list": root + named(9, b"\x00" + struct.pack(">i", 1)) + b"\x00",
            "unknown empty list type": root + named(9, b"\x0d" + struct.pack(">i", 0)) + b"\x00",
            "short list payload": root + named(9, b"\x03" + struct.pack(">ii", 2, 1)) + b"\x00",
            "negative byte array": root + named(7, struct.pack(">i", -1)) + b"\x00",
            "short int array": root + named(11, struct.pack(">ii", 2, 1)) + b"\x00",
            "huge long array": root + named(12, struct.pack(">i", 2_147_483_647)) + b"\x00",
            "short string": root + named(8, b"\x00\x05ab") + b"\x00",
            "unclosed outer compound": root + named(10, b"\x00"),
            "invalid UTF continuation": root + named(8, b"\x00\x01\x80") + b"\x00",
            "overlong registry letter": root + named(8, b"\x00\x02\xc1\xa9") + b"\x00",
        }
        with tempfile.TemporaryDirectory() as directory:
            world = Path(directory)
            for name, data in malformed.items():
                with self.subTest(name=name):
                    (world / "level.dat").write_bytes(data)
                    result = audit.audit(world)
                    self.assertFalse(result["complete"])
                    self.assertFalse(result["ready_for_removal"])
                    self.assertTrue(result["errors"])
            (world / "level.dat").write_bytes(nbt("minecraft:overworld"))
            region(world / "r.0.0.mca", root)
            self.assertFalse(audit.audit(world)["ready_for_removal"], "Region records use the same full validator")

    def test_nbt_validation_bounds_nesting_and_structural_work(self):
        deep = b"\x0a\x00\x00" + named(10, named(10, named(10, b"\x00") + b"\x00") + b"\x00") + b"\x00"
        with patch.object(audit, "NBT_DEPTH_LIMIT", 2):
            with self.assertRaisesRegex(ValueError, "nesting or structural work"):
                audit.references(deep)
        with patch.object(audit, "NBT_WORK_LIMIT", 2):
            with self.assertRaisesRegex(ValueError, "nesting or structural work"):
                audit.references(nbt("minecraft:chest"))

    def test_symlink_directory_is_reported_instead_of_silently_skipped(self):
        with tempfile.TemporaryDirectory() as directory:
            parent = Path(directory)
            world, outside = parent / "world", parent / "playerdata"
            world.mkdir()
            outside.mkdir()
            (world / "level.dat").write_bytes(nbt("minecraft:overworld"))
            (outside / "player.dat").write_bytes(nbt("ironchest:gold_chest"))
            (world / "playerdata").symlink_to(outside, target_is_directory=True)
            result = audit.audit(world)
            self.assertFalse(result["complete"])
            self.assertFalse(result["ready_for_removal"])
            self.assertTrue(any(error["file"] == "playerdata" and "Symlink" in error["error"]
                                for error in result["errors"]))

    def test_unreadable_subtree_fails_closed(self):
        with tempfile.TemporaryDirectory() as directory:
            world = Path(directory)
            (world / "level.dat").write_bytes(nbt("minecraft:overworld"))
            unreadable = world / "dimensions"
            unreadable.mkdir()
            real_scandir = audit.os.scandir

            def scandir(path):
                if Path(path) == unreadable:
                    raise PermissionError("fixture: unreadable world subtree")
                return real_scandir(path)

            # A simulated OS error is reliable even when tests run as root.
            with patch.object(audit.os, "scandir", side_effect=scandir):
                result = audit.audit(world)
            self.assertFalse(result["complete"])
            self.assertFalse(result["ready_for_removal"])
            self.assertTrue(any(error["file"] == "dimensions" and "unreadable" in error["error"]
                                for error in result["errors"]))

    def test_new_file_during_scan_invalidates_the_world_snapshot(self):
        with tempfile.TemporaryDirectory() as directory:
            world = Path(directory)
            (world / "level.dat").write_bytes(nbt("minecraft:overworld"))
            read_references = audit.references

            def changed_world(data):
                found = read_references(data)
                (world / "new-player.dat").write_bytes(nbt("ironchest:iron_chest"))
                return found

            with patch.object(audit, "references", side_effect=changed_world):
                result = audit.audit(world)
            self.assertFalse(result["complete"])
            self.assertFalse(result["ready_for_removal"])
            self.assertTrue(any(error["file"] == "new-player.dat" and "changed" in error["error"]
                                for error in result["errors"]))


if __name__ == "__main__":
    unittest.main()
