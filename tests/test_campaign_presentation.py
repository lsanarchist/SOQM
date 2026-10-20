"""Guard save compatibility and the readable, bilingual campaign map."""

import copy
import json
from pathlib import Path
import re
import sys
import unittest

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "scripts"))
import campaign_layout as layout
import campaign_compatibility as compatibility
import campaign_refresh as refresh
import localize_english

QUESTS = ROOT / "overrides/config/ftbquests/quests"


def load(path):
    return json.loads(path.read_text(encoding="utf-8"))


class CampaignPresentationTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.chapters = [load(p) for p in sorted((QUESTS / "chapters").glob("*.snbt"))]
        cls.by_filename = {c['filename']: c for c in cls.chapters}
        cls.quests = {q['id']: q for c in cls.chapters for q in c['quests']}
        cls.groups = load(QUESTS / "chapter_groups.snbt")["chapter_groups"]
        cls.uk = load(QUESTS / "lang/uk_ua.snbt")
        cls.en = load(QUESTS / "lang/en_us.snbt")
        cls.blueprint = layout.blueprint()

    def test_original_save_identities_and_claim_scope_are_preserved(self):
        expected = load(ROOT / "metadata/campaign_identity.json")
        self.assertEqual(expected['base_commit'], 'e71db8398ddc9f07e85981fac942f52dd23f447d')
        self.assertEqual(refresh.persistent_identity(self.chapters, self.groups), expected['sha256'])
        before_update = compatibility.previous_questbook(self.chapters)
        self.assertEqual(refresh.persistent_identity(before_update, self.groups),
                         expected['previous_questbook']['sha256'])
        # Check the historical hash as well: accepting a new full-campaign hash
        # alone could hide an accidental ID, owner or claim-scope change.
        previous = expected['previous_content']
        self.assertEqual(previous['added_quests'], ['514A110000000001'])
        self.assertEqual(previous['added_images'], ['514A140000000001'])
        old = copy.deepcopy(before_update)
        for chapter in old:
            chapter['quests'] = [q for q in chapter['quests'] if q['id'] not in previous['added_quests']]
            chapter['images'] = [i for i in chapter['images'] if i['id'] not in previous['added_images']]
        self.assertEqual(refresh.persistent_identity(old, self.groups), previous['sha256'])
        # A move to another quest would keep the set of IDs but lose progress.
        moved = copy.deepcopy(self.chapters)
        first, second = moved[0]['quests'][:2]
        first['tasks'][0]['id'], second['tasks'][0]['id'] = second['tasks'][0]['id'], first['tasks'][0]['id']
        self.assertNotEqual(refresh.persistent_identity(moved, self.groups), expected['sha256'])

    def test_campaign_regeneration_is_stable(self):
        chapters, locale = copy.deepcopy(self.chapters), copy.deepcopy(self.uk)
        refresh.refresh(chapters, locale, self.groups)
        self.assertEqual(chapters, self.chapters)
        self.assertEqual(locale, self.uk)
        english, _ = localize_english.generate()
        self.assertEqual(json.loads(english), self.en)

    def test_main_route_is_first_with_a_visible_material_spine(self):
        route = self.by_filename[layout.ROUTE_CHAPTER]
        self.assertEqual(route['group'], self.groups[0]['id'])
        self.assertLess(route['order_index'], min(c['order_index'] for c in self.chapters
                                                if c['group'] == route['group'] and c != route))
        self.assertEqual(self.uk[f"chapter.{route['id']}.title"], '00 · Головний маршрут')
        self.assertEqual(self.en[f"chapter.{route['id']}.title"], '00 · Main Route')
        self.assertEqual(len({self.quests[q]['x'] for q in layout.GATES}), 1)
        self.assertEqual([self.quests[q]['y'] for q in layout.GATES],
                         sorted(self.quests[q]['y'] for q in layout.GATES))
        for previous, current in zip(layout.GATES, layout.GATES[1:]):
            self.assertIn(previous, self.quests[current]['dependencies'])
        for ident in layout.GATES:
            self.assertFalse(self.quests[ident]['hide_dependency_lines'])
            self.assertEqual(self.quests[ident]['shape'], 'hexagon')

    def test_shapes_explain_actual_graph_roles(self):
        critical = layout.ancestors(self.quests, layout.FINAL) | {layout.FINAL} | set(layout.GATES)
        for ident, quest in self.quests.items():
            with self.subTest(quest=ident):
                role = layout.role(quest)
                if ident in critical:
                    self.assertEqual(role, 'route')
                elif quest.get('optional'):
                    self.assertEqual(role, 'optional')
                    self.assertEqual(quest['shape'], 'circle')
                elif any(t['type'] != 'item' for t in quest['tasks']):
                    self.assertEqual(role, 'practice')
                for locale in (self.uk, self.en):
                    subtitle = locale[f"quest.{ident}.quest_subtitle"]
                    self.assertTrue(subtitle.strip())
                    self.assertNotRegex(subtitle, r'(?i)(?:Етап|Stage)\s+\d')
                    self.assertNotRegex(subtitle, r'(?i)(?:Розвиток мода|Mod development)')

    def test_reported_tooltips_explain_the_actual_device(self):
        self.assertIn('Rainbow Furnace', self.uk['quest.514011907290DE48.quest_subtitle'])
        self.assertIn('динамо', self.uk['quest.5130114CEA71331A.quest_subtitle'])
        for locale in (self.uk, self.en):
            self.assertIn('27', locale['quest.513011AE9CF26AB0.quest_subtitle'])

    def test_last_skill_bonus_names_its_actual_antimatter_requirement(self):
        # The skill tree's sixth material is antimatter, while the campaign's
        # sixth main material is EMC Core. They must not share a guessed label.
        quest = self.quests['51401114099D7F18']
        task = next(t for t in quest['tasks'] if t['id'] == '5140126DEF5CDC49')
        advancement = load(ROOT / 'overrides/kubejs/data/qm/advancement/skills/points_stage_6.json')
        self.assertEqual(task['advancement'], 'qm:skills/points_stage_6')
        self.assertEqual(advancement['criteria']['stage_component']['conditions']['items'][0]['items'],
                         ['mekanism:pellet_antimatter'])
        self.assertIn('Antimatter', self.en['quest.51401114099D7F18.title'])
        self.assertIn('Antimatter Pellet', self.en['task.5140126DEF5CDC49.title'])
        self.assertIn('антиматері', self.uk['quest.51401114099D7F18.title'])
        self.assertNotIn('EMC', self.uk['task.5140126DEF5CDC49.title'])

    def test_every_caption_stays_below_its_node_inside_its_panel(self):
        for chapter in self.chapters:
            spec = self.blueprint[chapter['filename']]
            images = {image['id']: image for image in chapter['images']}
            panels = []
            for section in layout.sections_for(chapter, spec):
                panel = images[section['panel']]
                panels.append(panel)
                for ident in section['quests']:
                    quest = self.quests[ident]
                    label = images[spec['captions'][ident]]
                    with self.subTest(chapter=chapter['filename'], quest=ident):
                        self.assertAlmostEqual(quest['x'], label['x'])
                        self.assertGreater(label['y'] - label['height'] / 2,
                                           quest['y'] + quest['size'] / 2 + .05)
                        for box in (label, {'x': quest['x'], 'y': quest['y'], 'width': quest['size'], 'height': quest['size']}):
                            self.assertLessEqual(abs(box['x'] - panel['x']) + box['width'] / 2, panel['width'] / 2 + .001)
                            self.assertLessEqual(abs(box['y'] - panel['y']) + box['height'] / 2, panel['height'] / 2 + .001)
            for index, a in enumerate(panels):
                for b in panels[index + 1:]:
                    overlap_x = abs(a['x'] - b['x']) < (a['width'] + b['width']) / 2
                    overlap_y = abs(a['y'] - b['y']) < (a['height'] + b['height']) / 2
                    self.assertFalse(overlap_x and overlap_y, chapter['filename'])

    def test_editorial_cleanup_keeps_useful_mechanics_and_names_sections(self):
        for ident, quest in self.quests.items():
            self.assertNotIn('humor', quest.get('tags', []))
            lines = self.uk[f'quest.{ident}.quest_desc']
            self.assertFalse(any(line.startswith(refresh.OBSOLETE_PREFIXES) for line in lines), ident)
            self.assertFalse(any(re.search(r'глав[ауиі] 27', line, re.I) for line in lines), ident)
        cinderslime = '\n'.join(self.uk['quest.513011B07B76EA2C.quest_desc'])
        for detail in ('90 mB', '250 mB', '1050°C', 'Overburn', 'Overslime'):
            self.assertIn(detail, cinderslime)
        for chapter in self.chapters:
            for section in self.blueprint[chapter['filename']]['sections']:
                self.assertNotRegex(self.en[f"image.{section['heading']}.title"], r'^Section \d+$')
        for image_id in ('5141041000000324', '5141041000000325'):
            self.assertRegex(self.uk[f'image.{image_id}.title'].lower(), r'(?:флюїкс|fluix)')
            self.assertIn('Fluix', self.en[f'image.{image_id}.title'])
        self.assertEqual(set(self.uk), set(self.en))

    def test_first_processor_and_main_workshop_edges_do_not_cross_other_icons(self):
        examples = (
            (layout.ROUTE_CHAPTER, '5130115E1ED084F6', '51301130F8B93E94'),
            ('qm_v030_12_ae2', '5130114314B68D20', '513011E6243A5B01'),
        )
        for filename, parent, child in examples:
            self.assertIn(parent, self.quests[child]['dependencies'])
            if self.quests[child]['hide_dependency_lines']:
                continue
            points = layout.connection_points(self.quests[parent], self.quests[child])
            for node in self.by_filename[filename]['quests']:
                if node['id'] not in (parent, child):
                    icon = dict(x=node['x'], y=node['y'], width=node['size'], height=node['size'])
                    self.assertFalse(layout.path_hits_box(points, icon), node['id'])


if __name__ == '__main__':
    unittest.main()
