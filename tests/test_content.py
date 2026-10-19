"""Offline integrity checks for campaign content and recovered compatibility code."""

from collections import Counter, defaultdict, deque
import hashlib
import json
from pathlib import Path
import re
import unittest


ROOT = Path(__file__).resolve().parents[1]
QUESTS = ROOT / "overrides/config/ftbquests/quests"
INTRO_ID = "514D010000000001"
ID_PATTERN = re.compile(r"[0-9A-F]{16}\Z")


def read_json(path):
    return json.loads(path.read_text(encoding="utf-8"))


class CampaignContentTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.chapters = [
            read_json(path) for path in sorted((QUESTS / "chapters").glob("*.snbt"))
        ]
        cls.quest_list = [quest for chapter in cls.chapters for quest in chapter["quests"]]
        cls.quests = {quest["id"]: quest for quest in cls.quest_list}
        cls.groups = read_json(QUESTS / "chapter_groups.snbt")["chapter_groups"]
        cls.locale = read_json(QUESTS / "lang/uk_ua.snbt")

    def test_campaign_not_truncated(self):
        """A release cannot silently lose chapters, quests, tasks or rewards."""
        self.assertEqual(len(self.chapters), 37)
        self.assertEqual(len(self.quest_list), 1625)
        self.assertEqual(sum(len(q["tasks"]) for q in self.quest_list), 1772)
        self.assertEqual(sum(len(q["rewards"]) for q in self.quest_list), 1626)

    def test_all_persistent_ids_are_unique(self):
        objects = [("group", group["id"]) for group in self.groups]
        for chapter in self.chapters:
            objects.append(("chapter", chapter["id"]))
            objects.extend(("image", image["id"]) for image in chapter.get("images", []) if "id" in image)
            for quest in chapter["quests"]:
                objects.append(("quest", quest["id"]))
                objects.extend(("task", task["id"]) for task in quest["tasks"])
                objects.extend(("reward", reward["id"]) for reward in quest["rewards"])
        for path in sorted((QUESTS / "reward_tables").glob("*.snbt")):
            table = read_json(path)
            objects.append(("reward_table", table["id"]))
            objects.extend(("table_reward", reward["id"]) for reward in table["rewards"])
        invalid = [(kind, ident) for kind, ident in objects if not ID_PATTERN.fullmatch(ident)]
        self.assertEqual(invalid, [], "Persistent IDs must be uppercase 16-digit hexadecimal values")
        duplicates = [ident for ident, count in Counter(ident for _, ident in objects).items() if count > 1]
        self.assertEqual(duplicates, [], "Duplicate IDs can corrupt quest progress or reward claims")
        groups = {group["id"] for group in self.groups}
        self.assertTrue(all(chapter["group"] in groups for chapter in self.chapters))

    def test_dependencies_form_a_reachable_acyclic_campaign(self):
        """Detect dead ends caused by broken links, cycles and disconnected roots."""
        children = defaultdict(list)
        indegree = {}
        for ident, quest in self.quests.items():
            dependencies = quest.get("dependencies", [])
            self.assertEqual(len(dependencies), len(set(dependencies)), ident)
            self.assertNotIn(ident, dependencies, ident)
            for dependency in dependencies:
                self.assertIn(dependency, self.quests, f"{ident} depends on missing {dependency}")
                children[dependency].append(ident)
            indegree[ident] = len(dependencies)
        roots = [ident for ident, degree in indegree.items() if degree == 0]
        self.assertEqual(roots, [INTRO_ID])
        queue = deque(roots)
        visited = set()
        while queue:
            ident = queue.popleft()
            visited.add(ident)
            for child in children[ident]:
                indegree[child] -= 1
                if indegree[child] == 0:
                    queue.append(child)
        self.assertEqual(set(self.quests) - visited, set(), "Dependency cycle or unreachable quests")

    def test_ukrainian_campaign_is_complete_and_is_the_fallback(self):
        required = [f"chapter_group.{group['id']}.title" for group in self.groups]
        for chapter in self.chapters:
            required.append(f"chapter.{chapter['id']}.title")
            for quest in chapter["quests"]:
                required.extend((f"quest.{quest['id']}.title", f"quest.{quest['id']}.quest_desc"))
                required.extend(f"task.{task['id']}.title" for task in quest["tasks"])
        missing = [key for key in required if key not in self.locale or not self.locale[key]]
        self.assertEqual(missing, [], "Every visible campaign object needs Ukrainian text")
        for key, value in self.locale.items():
            with self.subTest(key=key):
                self.assertIsInstance(value, (str, list))
                lines = [value] if isinstance(value, str) else value
                self.assertTrue(lines)
                self.assertTrue(all(isinstance(line, str) for line in lines))
                self.assertTrue(any(line.strip() for line in lines))
        self.assertEqual(read_json(QUESTS / "data.snbt")["fallback_locale"], "uk_ua")

    def test_progress_and_reward_safety(self):
        defaults = read_json(QUESTS / "data.snbt")
        self.assertFalse(defaults["default_consume_items"])
        self.assertEqual(defaults["default_autoclaim_rewards"], "disabled")
        for quest in self.quest_list:
            with self.subTest(quest=quest["id"]):
                self.assertTrue(quest["tasks"], "Every quest needs a completion condition")
                self.assertFalse(quest.get("can_repeat", False))
                self.assertFalse(quest.get("repeatable", False))
                for reward in quest["rewards"]:
                    self.assertIn(reward["type"], {"item", "custom"})
                    self.assertEqual(reward.get("auto", "disabled"), "disabled")
                    self.assertFalse(reward.get("team_reward", True))
                    if reward["type"] == "custom":
                        self.assertIn("soqm:progressive_reward", reward["tags"])
                        self.assertIn(f"reward.{reward['id']}.title", self.locale)
                        self.assertIsInstance(reward["icon"], str)
                        self.assertNotIn("table_id", reward)
                        self.assertNotIn("item", reward)
                        self.assertNotIn("count", reward)
                    else:
                        self.assertIs(type(reward.get("count", 1)), int)
                        self.assertEqual(reward.get("count", 1), 1)
                        self.assertRegex(reward["item"]["id"], r"^(?:ftbquests:book|kubejs:mayhem_cube_t[0-6])$")

    def test_no_craftoria_runtime_adapters(self):
        """Craftoria is a quality reference, not a bundled runtime dependency."""
        pins = read_json(ROOT / "metadata/mods.lock.json")["mods"]
        for pin in pins:
            with self.subTest(mod=pin["filename"]):
                self.assertNotIn("craftoria", pin["filename"].casefold())
                for mod in pin.get("metadata", {}).get("mods", []):
                    self.assertNotIn("craftoria", mod.get("modId", "").casefold())
        for path in (ROOT / "overrides/mods").glob("*.jar"):
            self.assertNotIn("craftoria", path.name.casefold())


class CompatibilityProvenanceTests(unittest.TestCase):
    def test_recovered_sources_and_pinned_binaries_match_provenance(self):
        directory = ROOT / "src/compat"
        provenance = read_json(directory / "provenance.json")
        authored = read_json(ROOT / "metadata/authored-mods.json")
        modules = provenance["modules"]
        self.assertEqual(len(modules), 11)
        self.assertEqual({row["production_jar"] for row in modules}, {row["filename"] for row in authored})
        for module in modules:
            with self.subTest(module=module["module"]):
                source = directory / module["module"]
                self.assertIn("LICENSE", module["files_sha256"])
                self.assertTrue(any(name.endswith(".java") for name in module["files_sha256"]))
                actual = {
                    path.relative_to(source).as_posix(): hashlib.sha256(path.read_bytes()).hexdigest()
                    for path in source.rglob("*") if path.is_file()
                }
                self.assertEqual(actual, module["files_sha256"], "Recovered source changed without provenance review")
                jar = ROOT / "overrides/mods" / module["production_jar"]
                self.assertEqual(hashlib.sha256(jar.read_bytes()).hexdigest(), module["production_jar_sha256"])


if __name__ == "__main__":
    unittest.main()
