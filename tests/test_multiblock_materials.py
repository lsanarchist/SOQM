"""Construction quantities, geometry and save-safe additive objective regressions."""

from collections import Counter
from copy import deepcopy
from itertools import product
import json
from pathlib import Path
import sys
import unittest

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "scripts"))
import campaign_multiblocks as multiblocks


def rounded_shell(rows):
    """Independent six-face enumeration of Mekanism's reviewed ALLOWED_GRID."""
    size = len(rows)
    positions = {}
    for axis in range(3):
        for face in (0, size - 1):
            for u, v in product(range(size), repeat=2):
                kind = int(rows[u][v])
                if not kind:
                    continue
                coordinates = [u, v]
                coordinates.insert(axis, face)
                position = tuple(coordinates)
                if position in positions:
                    assert positions[position] == kind
                positions[position] = kind
    return Counter(positions.values())


class MultiblockMaterialsTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.data = multiblocks.load()
        cls.entries = {e["quest_id"]: e for e in cls.data["quests"]}
        # Always exercise the pre-migration objectives, even after regeneration.
        cls.baseline = [{"quests": [
            {"id": ident, "tasks": deepcopy(tasks), "rewards": []}
            for ident, tasks in cls.data["migration"]["original_tasks"].items()
        ]}]
        cls.chapters = deepcopy(cls.baseline)
        cls.report = multiblocks.apply_structure(cls.chapters)
        cls.quests = {q["id"]: q for c in cls.chapters for q in c["quests"]}

    def kit(self, ident):
        return {m["item"]: m["count"] for m in self.entries[ident]["materials"]}

    def test_all_new_tasks_are_additive_mandatory_non_consuming(self):
        self.assertEqual(self.report, {
            "tasks_added": 138, "quests_with_kits": 35, "counts_corrected": 14,
        })
        old_ids = {t["id"] for q in self.baseline[0]["quests"] for t in q["tasks"]}
        tasks = [t for q in self.quests.values() for t in q["tasks"]]
        ids = [t["id"] for t in tasks]
        self.assertEqual(len(ids), len(set(ids)))
        self.assertTrue(old_ids <= set(ids))
        self.assertEqual(set(ids) - old_ids, multiblocks.added_task_ids())
        for quest in self.quests.values():
            self.assertFalse(quest.get("optional", False))
            self.assertFalse(quest.get("min_required_tasks", 0))
            for task in quest["tasks"]:
                if task["type"] == "item":
                    self.assertFalse(task["consume_items"])
                    self.assertFalse(task["only_from_crafting"])
                    self.assertGreater(task["count"], 0)

    def test_native_checks_and_original_task_ids_are_retained(self):
        for original in self.baseline[0]["quests"]:
            current = {t["id"]: t for t in self.quests[original["id"]]["tasks"]}
            for task in original["tasks"]:
                with self.subTest(quest=original["id"], task=task["id"]):
                    self.assertIn(task["id"], current)
                    if task["type"] != "item":
                        self.assertEqual(current[task["id"]], task)

    def test_count_changes_match_the_explicit_reviewed_inventory(self):
        expected = {
            row["task_id"]: (row["before_count"], row["after_count"])
            for row in self.data["migration"]["changed_counts"]
        }
        actual = {}
        for original in self.baseline[0]["quests"]:
            current = {t["id"]: t for t in self.quests[original["id"]]["tasks"]}
            for old in original["tasks"]:
                new = current[old["id"]]
                if old.get("count", 1) != new.get("count", 1):
                    actual[old["id"]] = (old.get("count", 1), new.get("count", 1))
        self.assertEqual(actual, expected)

    def test_structure_and_both_translations_are_idempotent(self):
        once = deepcopy(self.chapters)
        report = multiblocks.apply_structure(self.chapters)
        self.assertEqual(report, {"quests_with_kits": 35})
        self.assertEqual(once, self.chapters)
        for language in ("uk_ua", "en_us"):
            locale = {}
            multiblocks.apply_locale(self.chapters, locale, language)
            before = deepcopy(locale)
            multiblocks.apply_locale(self.chapters, locale, language)
            self.assertEqual(before, locale)
            for ident in self.quests:
                self.assertTrue(locale[f"quest.{ident}.title"])
                self.assertTrue(locale[f"quest.{ident}.quest_desc"])

    def test_evidence_matches_pinned_archives(self):
        mods = json.loads((ROOT / "metadata/mods.lock.json").read_text())["mods"]
        archives = {mod["filename"]: mod for mod in mods}
        for entry in self.entries.values():
            self.assertTrue(entry["sources"], entry["quest_id"])
            for source in entry["sources"]:
                if "archive" in source:
                    self.assertEqual(source["sha256"], archives[source["archive"]]["sha256"])
                    self.assertEqual(source["version"], archives[source["archive"]]["version"])

    def test_two_mods_do_not_share_the_same_brick_quantities(self):
        self.assertEqual(self.kit("51301157FF888779")["immersiveengineering:cokebrick"], 27)
        self.assertEqual(self.kit("513011AE9CF26AB0")["immersiveengineering:blastbrick"], 27)
        self.assertEqual(self.kit("5130118DB31A46A1")["railcraft:coke_oven_bricks"], 26)
        self.assertEqual(self.kit("5130110CBEF3D218")["railcraft:blast_furnace_bricks"], 34)

    def test_sps_and_fusion_count_placed_casings_not_crafting_inputs(self):
        fusion = rounded_shell(["00100", "01210", "12221", "01210", "00100"])
        sps = rounded_shell(["0011100", "0122210", "1222221", "1222221", "1222221", "0122210", "0011100"])
        self.assertEqual(fusion, {1: 36, 2: 30})
        self.assertEqual(sps, {1: 60, 2: 126})
        f = self.kit("514D113E1E8CF8B1")
        self.assertEqual(f["mekanismgenerators:fusion_reactor_frame"], fusion[1])
        self.assertEqual(sum(f.values()), sum(fusion.values()))
        s = self.kit("514D114878C1E1BB")
        self.assertEqual(s["mekanism:sps_casing"], sps[1])
        self.assertEqual(s["mekanism:sps_port"] + s["mekanism:structural_glass"], sps[2])
        self.assertEqual(s["mekanism:supercharged_coil"], 1)

    def test_fission_matrix_and_turbine_have_complete_shells(self):
        f = self.kit("514D11CD9798EC9F")
        self.assertEqual(f["mekanismgenerators:fission_reactor_casing"] + f["mekanismgenerators:fission_reactor_port"], 3 * 4 * 3 - 2)
        m = self.kit("513011ED63C6B222")
        self.assertEqual(m["mekanism:induction_casing"] + m["mekanism:induction_port"] + m["mekanism:structural_glass"], 3 * 4 * 3 - 2)
        t = self.kit("514D111250CFCDBB")
        self.assertEqual(t["mekanismgenerators:turbine_casing"] + t["mekanismgenerators:turbine_valve"] + t["mekanismgenerators:turbine_vent"] + t["mekanism:structural_glass"], 5 ** 3 - 3 ** 3)
        self.assertEqual(t["mekanism:pressure_disperser"], 3 ** 2 - 1)

    def test_extreme_reactors_layout_has_continuous_axis_and_separate_layers(self):
        entry = self.entries["51301182A048FC8B"]
        layout = entry["layout"]
        shape = entry["dimensions_xyz"]
        shell = {p for p in product(*(range(n) for n in shape))
                 if any(v in (0, n - 1) for v, n in zip(p, shape))}
        self.assertEqual(len(shell), 44)
        devices = [tuple(layout[key]) for key in ("bearing", "controller", "steam_input", "water_output", "power_output")]
        self.assertEqual(len(set(devices)), 5)
        for point in devices:
            self.assertIn(point, shell)
            self.assertEqual(sum(v in (0, n - 1) for v, n in zip(point, shape)), 1)
        self.assertEqual(self.kit(entry["quest_id"])["bigreactors:reinforced_turbinecasing"], len(shell) - len(devices))
        axis = tuple(layout["bearing"])
        direction = layout["direction"]
        for shaft in layout["shafts"]:
            axis = tuple(v + d for v, d in zip(axis, direction))
            self.assertEqual(axis, tuple(shaft))
            self.assertNotIn(axis, shell)
        self.assertEqual(tuple(v + d for v, d in zip(axis, direction)), tuple(layout["rotor_end_casing"]))
        self.assertNotEqual(layout["blades"][0][1], layout["coils"][0][1])
        for shaft, adjacent in zip(layout["shafts"], layout["blades"] + layout["coils"]):
            self.assertEqual(sum(abs(a - b) for a, b in zip(shaft, adjacent)), 1)
            self.assertNotIn(tuple(adjacent), shell)

    def test_quantum_bridge_requires_both_endpoints_and_a_pair(self):
        self.assertEqual(self.kit("513011C1618FC593"), {
            "ae2:quantum_entangled_singularity": 2,
            "ae2:quantum_ring": 16, "ae2:quantum_link": 2,
        })


if __name__ == "__main__":
    unittest.main()
