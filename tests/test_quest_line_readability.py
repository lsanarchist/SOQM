"""Protect dependency semantics while keeping the idle quest map readable."""

import copy
import json
import math
from pathlib import Path
import re
import sys
import unittest
from xml.etree import ElementTree

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "scripts"))
import campaign_branches
import campaign_layout as layout
import campaign_locales
import render_campaign


class QuestLineReadabilityTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        quests = ROOT / "overrides/config/ftbquests/quests"
        cls.chapters = [json.loads(path.read_text()) for path in sorted((quests / "chapters").glob("*.snbt"))]
        campaign_branches.apply_structure(cls.chapters)
        cls.before = copy.deepcopy(cls.chapters)
        locale = campaign_locales.load("uk_ua")
        layout.apply(cls.chapters, locale)
        cls.locale = locale
        cls.specs = layout.blueprint()

    def test_drawing_policy_preserves_tasks_rewards_and_unlock_requirements(self):
        def content(chapters):
            return {quest['id']: {key: quest.get(key) for key in (
                'dependencies', 'dependency_requirement', 'min_required_dependencies',
                'tasks', 'rewards', 'optional', 'can_repeat',
            )} for chapter in chapters for quest in chapter['quests']}
        self.assertEqual(content(self.before), content(self.chapters))

    def test_every_local_prerequisite_precedes_its_dependent(self):
        for chapter in self.chapters:
            spec = self.specs[chapter['filename']]
            qmap = {quest['id']: quest for quest in chapter['quests']}
            sections = {ident: i for i, section in enumerate(layout.sections_for(chapter, spec))
                        for ident in section['quests']}
            for child in qmap.values():
                for ident in child.get('dependencies', []):
                    if ident not in qmap:
                        continue
                    parent = qmap[ident]
                    with self.subTest(chapter=chapter['filename'], parent=ident, child=child['id']):
                        self.assertLessEqual(sections[ident], sections[child['id']])
                        if sections[ident] == sections[child['id']]:
                            self.assertLess((parent['y'], parent['x']), (child['y'], child['x']))

    def test_idle_lines_are_local_short_and_clear_every_caption_including_the_source(self):
        for chapter in self.chapters:
            spec = self.specs[chapter['filename']]
            images = {image['id']: image for image in chapter['images']}
            sections = {ident: i for i, section in enumerate(layout.sections_for(chapter, spec))
                        for ident in section['quests']}
            edges = layout.visible_edges(chapter)
            for parent, child in edges:
                with self.subTest(chapter=chapter['filename'], parent=parent['id'], child=child['id']):
                    spine = chapter['filename'] == layout.ROUTE_CHAPTER and child['id'] in layout.GATES
                    if not spine:
                        self.assertEqual(sections[parent['id']], sections[child['id']])
                        self.assertLessEqual(math.hypot(parent['x'] - child['x'], parent['y'] - child['y']),
                                             layout.MAX_LOCAL_LINE)
                    for steps in (20, 80):
                        points = layout.connection_points(parent, child, steps)
                        for image in images.values():
                            if image.get('text_on_image'):
                                self.assertFalse(layout.path_hits_box(points, image), image['id'])
                        for node in chapter['quests']:
                            if node['id'] in (parent['id'], child['id']):
                                continue
                            icon = dict(x=node['x'], y=node['y'], width=node['size'], height=node['size'])
                            self.assertFalse(layout.path_hits_box(points, icon), node['id'])
            for steps in (20, 80):
                paths = [(parent, child, layout.connection_points(parent, child, steps)) for parent, child in edges]
                for index, path in enumerate(paths):
                    self.assertFalse(any(layout.paths_cross(path, other) for other in paths[index + 1:]),
                                     chapter['filename'])

    def test_shared_endpoint_does_not_excuse_a_second_crossing_outside_its_icon(self):
        source = dict(id='source', x=0.0, y=0.0, size=1.0)
        left = dict(id='left', x=-2.0, y=4.0, size=1.0)
        right = dict(id='right', x=2.0, y=4.0, size=1.0)
        first = (source, left, [source, dict(x=1.5, y=1.5), left])
        second = (source, right, [source, dict(x=-1.5, y=1.5), right])
        self.assertTrue(layout.paths_cross(first, second))
        self.assertFalse(layout.paths_cross((source, left, [source, left]),
                                           (source, right, [source, right])))

    def test_native_curves_are_double_lists_and_match_the_pinned_transform(self):
        theme = (ROOT / 'overrides/kubejs/assets/ftbquests/ftb_quests_theme.txt').read_text()
        self.assertRegex(theme, r'(?m)^quest_spacing:\s*0\.0\s*$')
        curves = 0
        for chapter in self.chapters:
            for child in chapter['quests']:
                for parent_id, stored in child.get('dep_control_pts', {}).items():
                    curves += 1
                    self.assertIn(parent_id, child['dependencies'])
                    self.assertEqual(len(stored), 4)
                    self.assertTrue(all(type(value) is float for value in stored))
                    controls = layout.connection_controls({'id': parent_id}, child)
                    # Independent transcription of QuestButton's map -> widget
                    # conversion with quest_spacing=0, at two native zooms.
                    for zoom in (8, 28):
                        button = zoom * 3 / 2
                        origin = -40
                        for index, control in enumerate(controls):
                            for axis, offset in (('x', 0), ('y', 1)):
                                native = (stored[index * 2 + offset] - origin - child['size'] / 2) * button
                                expected = (control[axis] - origin) * button
                                self.assertAlmostEqual(native, expected)
        self.assertGreater(curves, 20)

    def test_main_material_spine_bends_around_its_captions_and_heading_bars(self):
        chapter = next(c for c in self.chapters if c['filename'] == layout.ROUTE_CHAPTER)
        qmap = {q['id']: q for q in chapter['quests']}
        for parent_id, child_id in zip(layout.GATES, layout.GATES[1:]):
            parent, child = qmap[parent_id], qmap[child_id]
            self.assertFalse(child['hide_dependency_lines'])
            self.assertIn(parent_id, child['dep_control_pts'])
            self.assertLess(min(point['x'] for point in layout.connection_points(parent, child)), -12.5)

    def test_svg_draws_captions_before_connections_and_retains_native_curve_shape(self):
        chapter = next(c for c in self.chapters if c['filename'] == layout.ROUTE_CHAPTER)
        qmap = {quest['id']: quest for c in self.chapters for quest in c['quests']}
        svg = render_campaign.build_svg(chapter, self.locale, self.specs[chapter['filename']], qmap)
        document = ElementTree.fromstring(svg)
        elements = list(document.iter())
        labels = [index for index, element in enumerate(elements) if element.get('data-image-id')]
        links = [(index, element) for index, element in enumerate(elements) if element.get('data-from')]
        nodes = [index for index, element in enumerate(elements) if element.get('data-quest-id')]
        self.assertLess(max(labels), min(index for index, _ in links))
        self.assertLess(max(index for index, _ in links), min(nodes))
        metadata = json.loads(document.find('{http://www.w3.org/2000/svg}metadata').text)
        self.assertTrue(metadata['native_dependency_control_points'])
        for _, element in links:
            parent, child = qmap[element.get('data-from')], qmap[element.get('data-to')]
            values = list(map(float, re.findall(r'-?\d+(?:\.\d+)?', element.get('d'))))
            plotted = list(zip(values[::2], values[1::2]))
            expected = layout.connection_points(parent, child)
            self.assertEqual(len(plotted), len(expected))
            for (x, y), point in zip(plotted, expected):
                self.assertAlmostEqual(x - plotted[0][0], (point['x'] - parent['x']) * render_campaign.SCALE, places=2)
                self.assertAlmostEqual(y - plotted[0][1], (point['y'] - parent['y']) * render_campaign.SCALE, places=2)

    def test_mekanism_keeps_short_chains_but_hides_cross_section_fans(self):
        chapter = next(chapter for chapter in self.chapters if chapter['filename'] == 'qm_02_mek')
        qmap = {quest['id']: quest for quest in chapter['quests']}
        # Carbon -> steel dust remains visibly sequential.
        self.assertIn('513011C1D784F1C8', qmap['513011A22D5322FD']['dependencies'])
        self.assertFalse(qmap['513011A22D5322FD']['hide_dependency_lines'])
        # The buffer's two earlier prerequisites still apply, but no longer
        # run diagonally through the next production panel at idle.
        self.assertTrue(qmap['514D1178A4D44AFB']['hide_dependency_lines'])
        self.assertIn('5130111B9CDAD323', qmap['514D1178A4D44AFB']['dependencies'])
        self.assertIn('514D11EFB7B6F506', qmap['514D1178A4D44AFB']['dependencies'])
        self.assertTrue(chapter['default_hide_dependency_lines'])


if __name__ == '__main__':
    unittest.main()
