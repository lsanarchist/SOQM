"""Equipment progression must teach real slots and retain optional upgrade goals."""

import copy
import json
from pathlib import Path
import sys
import unittest
import zipfile

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "scripts"))
import campaign_branches as branches
import campaign_guidance as guidance
import campaign_layout as layout
import campaign_lessons as lessons
import campaign_locales as locales
import campaign_progression as progression

EQUIPMENT = "qm_04_tools"
NUCLEAR = "qm_03_nuclear"
STATION = "51301138646C9874"
BASE = "513011C521E5B4DD"
FULL_SET = "514C110000000003"
MAXED = "514C11000000000F"
SUBSTRATE = "514C110000000001"
PELLET = "514C110000000002"
SHEET = "514D11E17AB94DF9"
PLASTIC = "513011DB5BB9FB5B"
DRY_RUBBER = "51301137DA4E3B80"
BOX = "514A110000000001"
SAWMILL = "513011B3BB1DF1D9"
GEAR = {
    "mekanism:mekasuit_helmet": "513011CDE1D10A76",
    "mekanism:mekasuit_bodyarmor": "51301111493CFC21",
    "mekanism:mekasuit_pants": "51301181BF058BAE",
    "mekanism:mekasuit_boots": "513011087C06E9D8",
    "mekanism:meka_tool": "5130112F15810A47",
}


def read(path):
    return json.loads(path.read_text(encoding="utf-8"))


def task_item(task):
    item = task.get("item", {})
    return item.get("id") if isinstance(item, dict) else item


class EquipmentWorkshopTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        # Exercise the authored graph and text, including before global generation.
        cls.chapters = copy.deepcopy(locales.read_chapters())
        cls.uk = locales.load("uk_ua")
        cls.progression_report = progression.apply(cls.chapters, cls.uk)
        lessons.apply_locale(cls.uk, "uk_ua")
        layout.apply(cls.chapters, cls.uk)
        guidance.apply(cls.chapters, cls.uk, "uk_ua")
        cls.en = locales.load("en_us")
        branches.apply_locale(cls.en, "en_us")
        lessons.apply_locale(cls.en, "en_us")
        guidance.apply(cls.chapters, cls.en, "en_us")
        cls.by_chapter = {c["filename"]: c for c in cls.chapters}
        cls.quests = {q["id"]: q for c in cls.chapters for q in c["quests"]}
        cls.owner = {q["id"]: c["filename"] for c in cls.chapters for q in c["quests"]}
        cls.items = {task_item(t): q for q in cls.quests.values()
                     for t in q["tasks"] if t["type"] == "item"}
        cls.specs = layout.blueprint()

    def ancestors(self, ident):
        return layout.ancestors(self.quests, ident)

    def test_equipment_is_visible_and_does_not_gate_reactors_or_main_route(self):
        chapter = self.by_chapter[EQUIPMENT]
        self.assertEqual(chapter["icon"]["id"], "mekanism:mekasuit_bodyarmor")
        self.assertEqual(self.progression_report["warnings"], [])
        equipment = set(GEAR.values()) | {STATION, BASE, FULL_SET, MAXED}
        for ident in equipment:
            self.assertEqual(self.owner[ident], EQUIPMENT)
            self.assertTrue(self.quests[ident]["optional"])
        for quest in self.by_chapter[NUCLEAR]["quests"] + [self.quests[layout.FINAL]]:
            self.assertFalse(equipment & self.ancestors(quest["id"]), quest["id"])
        for ident in GEAR.values():
            self.assertFalse((set(GEAR.values()) - {ident}) & self.ancestors(ident), ident)

    def test_loose_modules_teach_the_correct_equipment_slot(self):
        # In particular, Dosimeter/Geiger belong to bodyarmor and Magnetic to boots.
        expected_slots = {
            "module_charge_distribution_unit": "mekasuit_bodyarmor",
            "module_dosimeter_unit": "mekasuit_bodyarmor",
            "module_geiger_unit": "mekasuit_bodyarmor",
            "module_jetpack_unit": "mekasuit_bodyarmor",
            "module_elytra_unit": "mekasuit_bodyarmor",
            "module_gravitational_modulating_unit": "mekasuit_bodyarmor",
            "module_electrolytic_breathing_unit": "mekasuit_helmet",
            "module_vision_enhancement_unit": "mekasuit_helmet",
            "module_inhalation_purification_unit": "mekasuit_helmet",
            "module_locomotive_boosting_unit": "mekasuit_pants",
            "module_hydraulic_propulsion_unit": "mekasuit_boots",
            "module_magnetic_attraction_unit": "mekasuit_boots",
            "module_silk_touch_unit": "meka_tool",
            "module_fortune_unit": "meka_tool",
            "module_excavation_escalation_unit": "meka_tool",
            "module_vein_mining_unit": "meka_tool",
        }
        for module, slot in expected_slots.items():
            with self.subTest(module=module):
                quest = self.items[f"mekanism:{module}"]
                self.assertEqual(self.owner[quest["id"]], EQUIPMENT)
                prerequisites = self.ancestors(quest["id"])
                self.assertTrue({STATION, BASE, GEAR[f"mekanism:{slot}"]} <= prerequisites)
                task = next(t for t in quest["tasks"] if task_item(t) == f"mekanism:{module}")
                self.assertFalse(task.get("consume_items", False))
                self.assertEqual(task["match_components"], "none")
                text = " ".join(self.en[f"quest.{quest['id']}.quest_desc"])
                self.assertIn("Modification Station", text)
                self.assertIn("does not prove installation", text)

    def test_maximum_loadout_uses_native_installed_module_detection(self):
        for ident, advancement in [(FULL_SET, "mekanism:mekasuit"),
                                   (MAXED, "mekanism:upgraded_mekasuit")]:
            task, = self.quests[ident]["tasks"]
            self.assertEqual(task["type"], "advancement")
            self.assertEqual(task["advancement"], advancement)
            self.assertEqual(task.get("criterion", ""), "")
            self.assertTrue(set(GEAR.values()) <= self.ancestors(ident))
        self.assertEqual(layout.stage(self.quests[MAXED]), 6)
        self.assertIn("installed-copy counts", " ".join(self.en[f"quest.{MAXED}.quest_desc"]))

    def test_hdpe_is_available_before_polonium_and_plastic_routes_are_alternatives(self):
        polonium = "514D1127DFAD1A15"
        for ident in (SUBSTRATE, PELLET, SHEET):
            self.assertEqual(layout.stage(self.quests[ident]), 2)
            self.assertNotIn(polonium, self.ancestors(ident))
        self.assertIn(SUBSTRATE, self.ancestors(PELLET))
        self.assertIn(PELLET, self.ancestors(SHEET))
        self.assertIn("5130113316FAEC33", self.ancestors(BASE))  # Elite circuit in the new base recipe.
        self.assertEqual(layout.stage(self.quests[BASE]), 3)
        self.assertFalse({DRY_RUBBER, SHEET} & self.ancestors(PLASTIC))
        text = " ".join(self.en[f"quest.{PLASTIC}.quest_desc"])
        self.assertIn("Dry Rubber", text)
        self.assertIn("3 Plastic", text)
        latex = " ".join(self.en["quest.5130111B4AB75BE7.quest_desc"])
        self.assertIn("750", latex)
        self.assertIn("500", latex)
        self.assertIn("produces Dry Rubber directly", latex)
        self.assertFalse(any(task_item(t) == "industrialforegoing:tinydryrubber"
                             for q in self.by_chapter["qm_v030_10_industrial"]["quests"]
                             for t in q["tasks"]))

    def test_box_keeps_its_identity_and_has_an_early_named_caption(self):
        quest = self.quests[BOX]
        self.assertEqual(self.owner[BOX], "qm_02_mek")
        self.assertEqual(quest["tasks"][0]["id"], "514A120000000001")
        self.assertEqual(quest["rewards"][0]["id"], "514A130000000001")
        self.assertIn(SAWMILL, quest["dependencies"])
        self.assertEqual(self.uk[f"quest.{BOX}.title"], "Mekanism: Картонна коробка")
        spec = self.specs["qm_02_mek"]
        self.assertEqual(spec["captions"][BOX], "514A140000000001")
        section = next(s for s in spec["sections"] if BOX in s["quests"])
        self.assertIn(SAWMILL, section["quests"])
        self.assertEqual(spec["sections"].index(section), 2)

    def test_pinned_native_advancements_really_check_all_five_items(self):
        path = ROOT / ".cache/mods/Mekanism-1.21.1-10.7.19.85.jar"
        if not path.is_file():
            self.skipTest("Pinned Mekanism archive is not cached; run download-mods for native evidence checks")
        with zipfile.ZipFile(path) as jar:
            full = json.loads(jar.read("data/mekanism/advancement/mekasuit.json"))["criteria"]["full_set"]
            maximum = json.loads(jar.read("data/mekanism/advancement/upgraded_mekasuit.json"))["criteria"]["maxed_gear"]
            self.assertEqual(full["trigger"], "minecraft:inventory_changed")
            self.assertEqual({p["items"] for p in full["conditions"]["items"]}, set(GEAR))
            self.assertEqual(maximum["trigger"], "minecraft:inventory_changed")
            self.assertEqual({p["predicates"]["mekanism:maxed_module_container"]["item"]
                              for p in maximum["conditions"]["items"]}, set(GEAR))
            sheet = json.loads(jar.read("data/mekanism/recipe/enriching/hdpe_sheet.json"))
            self.assertEqual(sheet["input"], {"count": 3, "item": "mekanism:hdpe_pellet"})
            alloy = json.loads(jar.read("data/mekanism/recipe/metallurgic_infusing/alloy/infused.json"))
            self.assertEqual(alloy["item_input"]["tag"], "c:ingots/copper")
        infuser = " ".join(self.en["quest.514D11B65A3EE8C0.quest_desc"])
        self.assertIn("copper and redstone for Infused Alloy", infuser)


if __name__ == "__main__":
    unittest.main()
