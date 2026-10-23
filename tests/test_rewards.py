"""Regressions for progressive, personal reward policy and saved identities."""
from copy import deepcopy
from fractions import Fraction
import json
from pathlib import Path
import sys
import unittest

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / 'scripts'))
import campaign_rewards as rewards
import campaign_locales
import campaign_refresh as refresh
import campaign_compatibility as compatibility
import campaign_refunds as refunds


def load(path):
    return json.loads(path.read_text(encoding='utf-8'))


def quests(chapters):
    return [q for chapter in chapters for q in chapter['quests']]


def outer_rewards(chapters):
    return [r for q in quests(chapters) for r in q['rewards']]


class ProgressiveRewardPolicyTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.policy = rewards.load_policy()
        cls.chapters = rewards.chapters_from_disk()
        cls.by_key = {p['key']: p for p in cls.policy['pools']}
        cls.runtime = load(rewards.RUNTIME_PATH)
        cls.all_produced = set(cls.runtime['production_items'])
        cls.entries = [e for p in cls.policy['pools'] for e in p['entries']]
        cls.refunds = refunds.load(cls.chapters)
        cls.groups = load(rewards.QUESTS / 'chapter_groups.snbt')['chapter_groups']

    def test_runtime_policy_exactly_matches_catalogue_and_existing_reward_ownership(self):
        self.assertEqual(self.runtime, rewards.runtime_policy(self.chapters, self.policy))
        self.assertEqual(set(self.policy['chapter_pools']), {c['filename'] for c in self.chapters})
        self.assertEqual(len(self.runtime['reward_mappings']), 1458)
        owners = {r['id']: q['id'] for q in quests(self.chapters) for r in q['rewards']}
        for ident, mapping in self.runtime['reward_mappings'].items():
            self.assertRegex(ident, rewards.ID_PATTERN)
            self.assertEqual(mapping['quest_id'], owners[ident])
            self.assertIn(mapping['pool'], self.by_key)
        self.assertEqual(len(self.by_key), 14)

    def test_only_authorized_claim_scope_changes_without_changing_saved_ownership(self):
        identity = load(ROOT / 'metadata/campaign_identity.json')
        self.assertEqual(refresh.persistent_identity(self.chapters, self.groups), identity['sha256'])
        original = compatibility.previous_questbook(self.chapters)
        # The newly added box quest did not exist in the shared-reward release.
        # Retain that release's exact hash and test only its original objects.
        additions = identity['previous_content']
        for chapter in original:
            chapter['quests'] = [q for q in chapter['quests'] if q['id'] not in additions['added_quests']]
            chapter['images'] = [i for i in chapter['images'] if i['id'] not in additions['added_images']]
        legacy = deepcopy(original)
        changes = 0
        for reward in outer_rewards(legacy):
            self.assertFalse(reward['team_reward'])
            if reward.get('item', {}).get('id') != 'ftbquests:book':
                reward['team_reward'] = True
                changes += 1
        self.assertEqual(changes, 1625)
        self.assertEqual(refresh.persistent_identity(legacy, self.groups), identity['previous_claim_scope']['sha256'])
        self.assertEqual(rewards.persistent_ids(legacy), rewards.persistent_ids(original))
        for quest in quests(self.chapters):
            self.assertFalse(quest.get('can_repeat', False))
            self.assertFalse(quest.get('repeatable', False))

    def test_native_custom_flags_icons_and_localized_titles_are_complete(self):
        uk = campaign_locales.load('uk_ua')
        en = campaign_locales.load('en_us')
        count = 0
        for reward in outer_rewards(self.chapters):
            self.assertFalse(reward['team_reward'])
            self.assertEqual(reward.get('auto', 'disabled'), 'disabled')
            if reward['type'] == 'custom':
                count += 1
                self.assertIn(rewards.PROGRESSIVE_TAG, reward['tags'])
                self.assertRegex(reward['icon'], rewards.ITEM_PATTERN)
                self.assertNotIn('table_id', reward)
                self.assertNotIn('command', reward)
                self.assertNotIn('item', reward)
                for locale in (uk, en):
                    self.assertTrue(locale[f"reward.{reward['id']}.title"])
        self.assertEqual(count, 1458)

    def test_fixed_milestones_and_original_personal_book_are_retained(self):
        cubes = books = 0
        bonus = rewards.cube_bonus(self.policy)
        for quest in quests(self.chapters):
            for reward in quest['rewards']:
                if reward['type'] == 'custom':
                    binding = self.runtime['reward_mappings'][reward['id']]
                    if 'cube' in binding:
                        cubes += 1
                        self.assertIn(quest['id'], self.refunds)
                        self.assertEqual(binding['cube'], f"kubejs:mayhem_cube_t{self.policy['milestones'][quest['id']]['tier']}")
                        self.assertEqual(reward['icon'], binding['cube'])
                    continue
                self.assertEqual(reward['type'], 'item')
                if reward['item']['id'] == 'ftbquests:book':
                    self.assertEqual(reward.get('count', 1), 1)
                    books += 1
                    self.assertEqual(quest['id'], rewards.BOOK_QUEST)
                else:
                    expected = rewards.cube_quantity(quest['id'], bonus)
                    self.assertEqual(reward.get('count', 1), expected)
                    self.assertEqual(reward['item'].get('count', 1), expected)
                    self.assertEqual(reward.get('random_bonus', 0), 0)
                    cubes += expected
                    self.assertEqual(reward['item']['id'], f"kubejs:mayhem_cube_t{self.policy['milestones'][quest['id']]['tier']}")
        self.assertEqual((cubes, books), (222, 1))
        self.assertEqual(len(self.policy['milestones']), 193)

    def test_cube_increase_is_fixed_fifteen_percent_without_new_claim_ids(self):
        bonus = rewards.cube_bonus(self.policy)
        self.assertEqual((bonus['baseline_awards'], bonus['bonus_awards']), (193, 29))
        self.assertTrue(Fraction(10, 100) <= Fraction(29, 193) <= Fraction(17, 100))
        before = rewards.persistent_ids(self.chapters)
        copy = deepcopy(self.chapters)
        rewards.apply(copy, {})
        self.assertEqual(rewards.persistent_ids(copy), before)
        selected = {q['id'] for q in quests(copy) if any(r.get('type') == 'item'
                    and r.get('item', {}).get('id', '').startswith('kubejs:mayhem_cube_t')
                    and r.get('count') == 2 for r in q['rewards'])}
        self.assertEqual(selected, set(bonus['bonus_quests']))
        self.assertFalse(selected & set(self.refunds))

    def test_book_preview_lists_every_actual_guarantee_and_possible_pool_item(self):
        preview = rewards.preview_catalogue(self.chapters, self.runtime)
        self.assertEqual(load(rewards.PREVIEW_PATH), preview)
        expected_ids = {r['id'] for r in outer_rewards(self.chapters)}
        self.assertEqual(set(preview['rewards']), expected_ids)
        self.assertEqual({rid for ids in preview['quests'].values() for rid in ids}, expected_ids)
        self.assertEqual(preview['pools'], self.runtime['pools'])
        self.assertEqual(preview['draws_by_level'], self.runtime['draws_by_level'])
        for q in quests(self.chapters):
            for reward in q['rewards']:
                shown = preview['rewards'][reward['id']]
                binding = self.runtime['reward_mappings'].get(reward['id'])
                if binding:
                    expected = []
                    if 'cube' in binding:
                        expected.append({'item': binding['cube'], 'count': 1, 'kind': 'cube'})
                        self.assertIsNone(shown['random_pool'])
                    else:
                        self.assertEqual(shown['random_pool'], binding['pool'])
                    if 'refund' in binding:
                        expected.extend({**entry, 'kind': 'refund'} for entry in binding['refund']['items'])
                        self.assertEqual(shown['requires_manufacture'], binding['refund']['output'])
                    self.assertEqual(shown['guaranteed'], expected)
                else:
                    self.assertEqual(shown['guaranteed'], [{'item': reward['item']['id'],
                                     'count': reward.get('count', 1), 'kind': 'item'}])
                    self.assertIsNone(shown['random_pool'])

    def test_player_guidance_matches_personal_claims_and_real_reward_types(self):
        uk = campaign_locales.load('uk_ua')
        all_descriptions = '\n'.join(line for key, lines in uk.items() if key.endswith('.quest_desc') for line in lines)
        self.assertNotIn('Нагорода одноразова для команди', all_descriptions)
        self.assertNotIn('Нагорода команді одноразова', all_descriptions)
        for ident in ('5142421100000008', '5142421100000017', '5142421100000019'):
            quest = next(q for q in quests(self.chapters) if q['id'] == ident)
            self.assertTrue(all(r['type'] == 'item' and r['item']['id'].startswith('kubejs:mayhem_cube_t') for r in quest['rewards']))
            self.assertNotIn('збережи куб', '\n'.join(uk[f'quest.{ident}.quest_desc']))

    def test_catalogue_is_substantially_larger_and_contains_real_mod_families(self):
        self.assertGreaterEqual(len(self.entries), 14 * 16)
        modded = {e['item'] for e in self.entries if not e['item'].startswith('minecraft:')}
        self.assertGreaterEqual(len(modded), 50)
        items = {e['item'] for e in self.entries}
        required = set(rewards.GATES) | {
            'kubejs:quantum_processor', 'kubejs:radiation_shield', 'kubejs:reinforced_netherite_plate',
            'ae2:logic_processor', 'ae2:calculation_processor', 'ae2:engineering_processor',
            'sophisticatedstorage:barrel', 'sophisticatedstorage:iron_barrel',
            'sophisticatedstorage:gold_barrel', 'sophisticatedstorage:diamond_barrel',
            'sophisticatedstorage:netherite_barrel',
        }
        self.assertFalse(required - items, required - items)
        for entry in self.entries:
            self.assertTrue(entry['evidence'])

    def test_requested_expensive_items_require_production_of_that_exact_output(self):
        requested = set(rewards.GATES) | {'kubejs:quantum_processor', 'kubejs:radiation_shield',
                    'kubejs:reinforced_netherite_plate', 'ae2:logic_processor',
                    'ae2:calculation_processor', 'ae2:engineering_processor'}
        for entry in self.entries:
            if entry['item'] in requested or entry['item'].startswith(('sophisticatedstorage:', 'sophisticatedbackpacks:')):
                self.assertIn(entry['item'], entry['requires_craft'])
                self.assertEqual(entry['count'], 1)

    def test_fresh_team_and_high_level_without_specific_production_keep_items_locked(self):
        for pool in self.policy['pools']:
            fresh = rewards.eligible_entries(pool, 0, set())
            self.assertGreaterEqual(len(fresh), 5)
            self.assertTrue(all(not e.get('requires_craft') for e in fresh))
            all_gates = set(rewards.GATES)
            unlocked = {e['item'] for e in rewards.eligible_entries(pool, 6, all_gates)}
            for entry in pool['entries']:
                if set(entry.get('requires_craft', [])) - all_gates:
                    self.assertNotIn(entry['item'], unlocked)

    def test_logic_processor_and_lower_barrel_do_not_unlock_other_variants(self):
        produced = set(rewards.GATES) | {'ae2:logic_processor', 'sophisticatedstorage:iron_barrel'}
        items = {e['item'] for p in self.policy['pools'] for e in rewards.eligible_entries(p, 6, produced)}
        self.assertIn('ae2:logic_processor', items)
        self.assertIn('sophisticatedstorage:iron_barrel', items)
        for item in ('ae2:engineering_processor', 'ae2:calculation_processor', 'sophisticatedstorage:diamond_barrel'):
            self.assertNotIn(item, items)

    def test_every_catalogue_entry_is_reachable_and_more_evidence_never_relocks_a_prize(self):
        for pool in self.policy['pools']:
            complete = rewards.eligible_entries(pool, 6, self.all_produced)
            self.assertEqual(complete, pool['entries'])
            previous = set()
            produced = set()
            for item in sorted(self.all_produced):
                produced.add(item)
                level = rewards.production_level(produced, self.policy)
                current = {e['item'] for e in rewards.eligible_entries(pool, level, produced)}
                self.assertLessEqual(previous, current)
                previous = current

    def test_exact_probabilities_normalize_at_each_level_with_locked_groups(self):
        for level in range(7):
            produced = set(rewards.GATES[:level])
            self.assertEqual(rewards.production_level(produced, self.policy), level)
            for pool in self.policy['pools']:
                odds = rewards.probabilities(pool, level, produced, self.policy)
                self.assertEqual(sum(odds.values()), Fraction(1))
                self.assertTrue(all(0 < p <= 1 for p in odds.values()))
                self.assertEqual(set(odds), {e['item'] for e in rewards.eligible_entries(pool, level, produced)})

    def test_more_draws_and_rare_group_weights_follow_actual_material_milestones(self):
        self.assertEqual(self.policy['draws_by_level'], [3, 3, 4, 4, 4, 5, 5])
        self.assertEqual(self.policy['guaranteed_quality_draws'], 1)
        rare = [r['rare'] + r['epic'] for r in self.policy['rarity_weights_by_level']]
        self.assertEqual(rare, sorted(rare))
        self.assertGreater(rare[-1], rare[0])
        self.assertEqual(rewards.production_level(set(), self.policy), 0)
        self.assertEqual(rewards.production_level({'kubejs:precision_mechanism'}, self.policy), 2)

    def test_reserved_quality_draw_never_opens_locked_prizes_and_has_exact_odds(self):
        for pool in self.policy['pools']:
            for produced in (set(), set(rewards.GATES), self.all_produced):
                level = rewards.production_level(produced, self.policy)
                ordinary = rewards.probabilities(pool, level, produced, self.policy)
                quality = rewards.quality_probabilities(pool, level, produced, self.policy)
                self.assertEqual(sum(quality.values()), Fraction(1))
                self.assertLessEqual(set(quality), set(ordinary))
                eligible = rewards.eligible_entries(pool, level, produced)
                premium = {e['item'] for e in eligible if e['rarity'] != 'common'}
                self.assertEqual(set(quality), premium if premium else set(ordinary))

    def test_cube_milestones_cover_real_branch_achievements_without_new_claim_ids(self):
        self.assertGreaterEqual(len(self.policy['milestones']), 150)
        self.assertLessEqual(len(self.policy['milestones']), 220)
        eligible_chapters = {c['filename'] for c in self.chapters if c['filename'] != 'qm_v040_36_skills'}
        awarded_chapters = {c['filename'] for c in self.chapters
                            if any(q['id'] in self.policy['milestones'] for q in c['quests'])}
        self.assertLessEqual(eligible_chapters, awarded_chapters)
        for chapter in self.chapters:
            for quest in chapter['quests']:
                if quest['id'] in self.policy['milestones']:
                    self.assertLessEqual(self.policy['milestones'][quest['id']]['tier'], rewards.quest_stage(quest))
        original = deepcopy(self.chapters)
        identities = rewards.persistent_ids(original)
        locale = {f"reward.{reward['id']}.title": 'old pack title'
                  for quest in quests(original) for reward in quest['rewards']}
        rewards.apply(original, locale)
        self.assertEqual(rewards.persistent_ids(original), identities)
        for quest in quests(original):
            if quest['id'] in self.policy['milestones']:
                for reward in quest['rewards']:
                    if quest['id'] in self.refunds:
                        self.assertIn(f"reward.{reward['id']}.title", locale)
                    else:
                        self.assertNotIn(f"reward.{reward['id']}.title", locale)

    def test_plastic_hdpe_nested_cubes_and_oversized_stacks_remain_excluded(self):
        for entry in self.entries:
            self.assertFalse(any(x in entry['item'] for x in ('plastic', 'hdpe', 'mayhem_cube')))
            self.assertLessEqual(entry['count'], 16)
        # Bulk resources can reach useful stack sizes; expensive prizes remain single units.
        self.assertLessEqual(max(self.policy['draws_by_level']) * max(e['count'] for e in self.entries), 80)

    def test_masa_is_removed_from_rewards_and_cannot_be_reintroduced(self):
        for document in (self.policy, self.runtime, load(rewards.PREVIEW_PATH)):
            items = {e['item'] for pool in document['pools'] for e in pool['entries']}
            self.assertNotIn('millenaire:masa', items)
            self.assertIn('projecte:dark_matter', items)
            self.assertIn('projecte:red_matter', items)
        # Use an otherwise valid starter entry: rejection must come from the
        # reward exclusion, not an unrelated rarity or manufacture condition.
        for pool_key in ('survival', 'settlement'):
            policy = deepcopy(self.policy)
            pool = next(p for p in policy['pools'] if p['key'] == pool_key)
            entry = next(e for e in pool['entries'] if e['evidence']['method'] == 'starter_supply')
            entry['item'] = 'millenaire:masa'
            with self.subTest(pool=pool_key), self.assertRaisesRegex(ValueError, 'Masa'):
                rewards.validate_policy(policy)

    def test_whitelist_equals_exact_craft_requirements_and_has_no_stale_tables(self):
        expected = set(rewards.GATES)
        for entry in self.entries:
            expected.update(entry.get('requires_craft', []))
        expected.update(row['output'] for row in self.refunds.values())
        self.assertEqual(self.runtime['production_items'], sorted(expected))
        self.assertEqual(rewards.obsolete_tables(), [])
        self.assertTrue(all('evidence' not in e for p in self.runtime['pools'] for e in p['entries']))

    def test_refresh_is_idempotent_and_keeps_unrelated_locale_and_saved_ids(self):
        chapters = deepcopy(self.chapters)
        locale = {'unrelated.key': 'untouched'}
        report = rewards.apply(chapters, locale)
        self.assertEqual(chapters, self.chapters)
        self.assertEqual(report['personal_rewards'], 1643)
        self.assertEqual(report['progressive_rewards'], 1449)
        self.assertEqual(report['recipe_refunds'], 41)
        self.assertEqual(report['refund_cube_bundles'], 9)
        self.assertEqual(report['custom_rewards'], 1458)
        self.assertEqual(report['milestone_cubes'], 222)
        self.assertEqual(locale['unrelated.key'], 'untouched')
        again = deepcopy(locale)
        self.assertEqual(rewards.apply(chapters, again), report)
        self.assertEqual(again, locale)
        self.assertEqual(set(rewards.locale_entries('uk_ua')), set(rewards.locale_entries('en_us')))

    def test_invalid_conditions_or_quantities_are_rejected_before_publication(self):
        for mutation in ('negative_weight', 'too_many', 'missing_exact_proof', 'plastic'):
            policy = deepcopy(self.policy)
            entry = next(e for p in policy['pools'] for e in p['entries'] if e['item'] == 'kubejs:industrial_chassis')
            if mutation == 'negative_weight': entry['weight'] = -1
            elif mutation == 'too_many': entry['count'] = 64
            elif mutation == 'missing_exact_proof': entry['requires_craft'] = []
            else: entry['item'] = 'mekanism:hdpe_pellet'
            with self.subTest(mutation=mutation), self.assertRaises(ValueError):
                rewards.validate_policy(policy)
        chapters = deepcopy(self.chapters)
        outer_rewards(chapters)[0]['auto'] = 'enabled'
        with self.assertRaisesRegex(ValueError, 'non-manual'):
            rewards.apply(chapters, {})


if __name__ == '__main__':
    unittest.main()
