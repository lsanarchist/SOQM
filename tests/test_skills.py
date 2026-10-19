"""Offline checks of shipped Skills configuration; no Minecraft lifecycle model."""

import json
from pathlib import Path
import unittest


ROOT = Path(__file__).resolve().parents[1]
CATEGORY = ROOT / "overrides/config/puffish_skills/categories/mayhem"
ADVANCEMENTS = ROOT / "overrides/kubejs/data/qm/advancement/skills"
FUNCTIONS = ROOT / "overrides/kubejs/data/qm/function/skills"
BRANCHES = ("guardian", "warrior", "explorer", "engineer")

# An independent statement of the nine existing milestone criteria.
MILESTONES = {
    "root": ("root", "unlocked"),
    "guardian_1": ("guardian", "unlocked"),
    "warrior_1": ("warrior", "unlocked"),
    "explorer_1": ("explorer", "unlocked"),
    "engineer_1": ("engineer", "unlocked"),
    "guardian_6": ("capstone", "guardian"),
    "warrior_6": ("capstone", "warrior"),
    "explorer_6": ("capstone", "explorer"),
    "engineer_6": ("capstone", "engineer"),
}
STAGE_ITEMS = {
    1: "kubejs:industrial_chassis",
    2: "kubejs:precision_mechanism",
    3: "kubejs:dimensional_matrix",
    4: "kubejs:stellar_alloy",
    5: "kubejs:mythic_circuit",
    6: "mekanism:pellet_antimatter",
}


def read_json(path):
    return json.loads(path.read_text(encoding="utf-8"))


def requirements_met(advancement, saved_criteria):
    """Evaluate the JSON's AND-of-OR rule against saved criterion names only."""
    requirements = advancement.get(
        "requirements", [[name] for name in advancement["criteria"]]
    )
    return all(any(name in saved_criteria for name in group) for group in requirements)


class SkillsConfigurationTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.definitions = read_json(CATEGORY / "definitions.json")
        cls.skills = read_json(CATEGORY / "skills.json")
        cls.advancements = {
            path.stem: read_json(path) for path in ADVANCEMENTS.glob("*.json")
        }

    def test_25_skills_keep_their_costs_and_existing_tags(self):
        expected = {"root"} | {
            f"{branch}_{level}" for branch in BRANCHES for level in range(1, 7)
        }
        self.assertEqual(set(self.definitions), expected)
        self.assertEqual(set(self.skills), expected)
        for skill in expected:
            with self.subTest(skill=skill):
                definition = self.definitions[skill]
                self.assertIs(type(definition["cost"]), int)
                self.assertEqual(definition["cost"], 0 if skill == "root" else 1)
                self.assertEqual(self.skills[skill]["definition"], skill)
                self.assertEqual(self.skills[skill]["root"], skill == "root")
                tags = [
                    reward for reward in definition["rewards"]
                    if reward["type"] == "puffish_skills:tag"
                ]
                self.assertEqual(tags, [{
                    "type": "puffish_skills:tag",
                    "data": {"tag": f"qm_skill_{skill}"},
                }])

    def test_nine_unlock_commands_guard_completion_and_grant_one_criterion(self):
        for skill, definition in self.definitions.items():
            with self.subTest(skill=skill):
                commands = [
                    reward for reward in definition["rewards"]
                    if reward["type"] == "puffish_skills:command"
                ]
                if skill not in MILESTONES:
                    self.assertEqual(commands, [], "Levels 2-5 must not grant milestones")
                    continue
                advancement, criterion = MILESTONES[skill]
                ident = f"qm:skills/{advancement}"
                self.assertEqual(commands, [{
                    "type": "puffish_skills:command",
                    "data": {
                        "unlock_command": (
                            f"execute unless entity @s[advancements={{{ident}=true}}] "
                            f"run advancement grant @s only {ident} {criterion}"
                        ),
                    },
                }], "Use a guarded unlock_command, with no action or lock command")
                self.assertIn(criterion, self.advancements[advancement]["criteria"])

    def test_existing_milestone_ids_have_no_tick_or_nbt_predicates(self):
        expected = {
            "root": {"unlocked"},
            "guardian": {"unlocked"},
            "warrior": {"unlocked"},
            "explorer": {"unlocked"},
            "engineer": {"unlocked"},
            "capstone": set(BRANCHES),
        }
        stage_names = {f"points_stage_{stage}" for stage in STAGE_ITEMS}
        self.assertEqual(set(self.advancements), set(expected) | stage_names)
        for name, criteria in expected.items():
            with self.subTest(advancement=name):
                advancement = self.advancements[name]
                self.assertEqual(advancement["criteria"], {
                    criterion: {"trigger": "minecraft:impossible"}
                    for criterion in criteria
                })
                self.assertNotIn("rewards", advancement, "Milestones do not issue skill points")
                if name != "capstone":
                    self.assertNotIn("requirements", advancement)

    def test_or_capstone_recognizes_saved_criterion_history(self):
        capstone = self.advancements["capstone"]
        self.assertEqual(capstone["requirements"], [list(BRANCHES)])
        self.assertFalse(requirements_met(capstone, {}))
        self.assertFalse(requirements_met(capstone, {"unlocked": "2026-10-18 12:00:00 +0000"}))
        for branch in BRANCHES:
            with self.subTest(saved_capstone_criterion=branch):
                # Only one named criterion is needed, including an old earned one.
                saved = {branch: "2026-10-18 12:00:00 +0000"}
                self.assertTrue(requirements_met(capstone, saved))
        for name in ("root", *BRANCHES):
            with self.subTest(saved_advancement=name):
                advancement = self.advancements[name]
                self.assertFalse(requirements_met(advancement, {}))
                self.assertTrue(requirements_met(
                    advancement, {"unlocked": "2026-10-18 12:00:00 +0000"}
                ))

    def test_six_point_stages_keep_inventory_triggers_and_source_specific_sets(self):
        self.assertEqual(
            {path.stem for path in FUNCTIONS.glob("points_stage_*.mcfunction")},
            {f"points_stage_{stage}" for stage in STAGE_ITEMS},
        )
        for stage, item in STAGE_ITEMS.items():
            with self.subTest(stage=stage):
                name = f"points_stage_{stage}"
                self.assertEqual(self.advancements[name], {
                    "criteria": {
                        "stage_component": {
                            "trigger": "minecraft:inventory_changed",
                            "conditions": {
                                "items": [{"items": [item], "count": {"min": 1}}],
                            },
                        },
                    },
                    "rewards": {"function": f"qm:skills/{name}"},
                })
                lines = (FUNCTIONS / f"{name}.mcfunction").read_text(encoding="utf-8").splitlines()
                commands = [
                    line.strip() for line in lines
                    if line.strip() and not line.lstrip().startswith("#")
                ]
                self.assertEqual(commands, [
                    f"puffish_skills points set @s puffish_skills:mayhem 2 qm:stage_{stage}"
                ], "Each source grants two points without milestone grants or additive replay")


if __name__ == "__main__":
    unittest.main()
