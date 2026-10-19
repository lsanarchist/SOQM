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
import campaign_refresh as refresh


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
        cls.groups = load(rewards.QUESTS / 'chapter_groups.snbt')['chapter_groups']

    def test_runtime_policy_exactly_matches_catalogue_and_existing_reward_ownership(self):
        self.assertEqual(self.runtime, rewards.runtime_policy(self.chapters, self.policy))
        self.assertEqual(set(self.policy['chapter_pools']), {c['filename'] for c in self.chapters})
        self.assertEqual(len(self.runtime['reward_mappings']), 1611)
        owners = {r['id']: q['id'] for q in quests(self.chapters) for r in q['rewards']}
        for ident, mapping in self.runtime['reward_mappings'].items():
            self.assertRegex(ident, rewards.ID_PATTERN)
            self.assertEqual(mapping['quest_id'], owners[ident])
            self.assertIn(mapping['pool'], self.by_key)
        self.assertEqual(len(self.by_key), 14)

    def test_only_authorized_claim_scope_changes_without_changing_saved_ownership(self):
        identity = load(ROOT / 'metadata/campaign_identity.json')
        self.assertEqual(refresh.persistent_identity(self.chapters, self.groups), identity['sha256'])
        legacy = deepcopy(self.chapters)
        changes = 0
        for reward in outer_rewards(legacy):
            self.assertFalse(reward['team_reward'])
            if reward.get('item', {}).get('id') != 'ftbquests:book':
                reward['team_reward'] = True
                changes += 1
        self.assertEqual(changes, 1625)
        self.assertEqual(refresh.persistent_identity(legacy, self.groups), identity['previous_claim_scope']['sha256'])
        self.assertEqual(rewards.persistent_ids(legacy), rewards.persistent_ids(self.chapters))
        for quest in quests(self.chapters):
            self.assertFalse(quest.get('can_repeat', False))
            self.assertFalse(quest.get('repeatable', False))

    def test_native_custom_flags_icons_and_localized_titles_are_complete(self):
        uk = load(rewards.QUESTS / 'lang/uk_ua.snbt')
        en = load(rewards.QUESTS / 'lang/en_us.snbt')
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
        self.assertEqual(count, 1611)

    def test_fixed_milestones_and_original_personal_book_are_retained(self):
        cubes = books = 0
        for quest in quests(self.chapters):
            for reward in quest['rewards']:
                if reward['type'] == 'custom':
                    continue
                self.assertEqual(reward['type'], 'item')
                self.assertEqual(reward.get('count', 1), 1)
                if reward['item']['id'] == 'ftbquests:book':
                    books += 1
                    self.assertEqual(quest['id'], rewards.BOOK_QUEST)
                else:
                    cubes += 1
                    self.assertEqual(reward['item']['id'], f"kubejs:mayhem_cube_t{self.policy['milestones'][quest['id']]['tier']}")
        self.assertEqual((cubes, books), (14, 1))

    def test_player_guidance_matches_personal_claims_and_real_reward_types(self):
        uk = load(rewards.QUESTS / 'lang/uk_ua.snbt')
        all_descriptions = '\n'.join(line for key, lines in uk.items() if key.endswith('.quest_desc') for line in lines)
        self.assertNotIn('Нагорода одноразова для команди', all_descriptions)
        self.assertNotIn('Нагорода команді одноразова', all_descriptions)
        for ident in ('5142421100000008', '5142421100000017', '5142421100000019'):
            quest = next(q for q in quests(self.chapters) if q['id'] == ident)
            self.assertTrue(all(r['type'] == 'custom' for r in quest['rewards']))
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
        self.assertEqual(self.policy['draws_by_level'], [2, 2, 3, 3, 3, 4, 4])
        rare = [r['rare'] + r['epic'] for r in self.policy['rarity_weights_by_level']]
        self.assertEqual(rare, sorted(rare))
        self.assertGreater(rare[-1], rare[0])
        self.assertEqual(rewards.production_level(set(), self.policy), 0)
        self.assertEqual(rewards.production_level({'kubejs:precision_mechanism'}, self.policy), 2)

    def test_plastic_hdpe_nested_cubes_and_oversized_stacks_remain_excluded(self):
        for entry in self.entries:
            self.assertFalse(any(x in entry['item'] for x in ('plastic', 'hdpe', 'mayhem_cube')))
            self.assertLessEqual(entry['count'], 4)
        # Even four identical draws cannot recreate a 32/64-item ordinary payout.
        self.assertLessEqual(max(self.policy['draws_by_level']) * max(e['count'] for e in self.entries), 16)

    def test_whitelist_equals_exact_craft_requirements_and_has_no_stale_tables(self):
        expected = set(rewards.GATES)
        for entry in self.entries:
            expected.update(entry.get('requires_craft', []))
        self.assertEqual(self.runtime['production_items'], sorted(expected))
        self.assertEqual(rewards.obsolete_tables(), [])
        self.assertTrue(all('evidence' not in e for p in self.runtime['pools'] for e in p['entries']))

    def test_refresh_is_idempotent_and_keeps_unrelated_locale_and_saved_ids(self):
        chapters = deepcopy(self.chapters)
        locale = {'unrelated.key': 'untouched'}
        report = rewards.apply(chapters, locale)
        self.assertEqual(chapters, self.chapters)
        self.assertEqual(report['personal_rewards'], 1626)
        self.assertEqual(report['progressive_rewards'], 1611)
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
