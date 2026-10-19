"""Regression guard for the pinned AE2/Mekanism crafting-CPU duplication path.

Mekanism serializes a block before replacing it. AE2's replacement callback
returns an active CPU's inputs to the network, leaving the saved copy intact.
Every native CPU block can become the persisted core, so protecting storage
blocks alone is insufficient. Ordinary AE2 transport must stay available.
"""
import json
from pathlib import Path
import unittest


ROOT = Path(__file__).resolve().parents[1]
BLACKLIST = ROOT / 'overrides/kubejs/data/mekanism/tags/block/cardboard_blacklist.json'


class CardboardTransportTests(unittest.TestCase):
    def test_all_native_cpu_core_candidates_are_protected(self):
        policy = json.loads(BLACKLIST.read_text(encoding='utf-8'))
        # Registry IDs verified in appliedenergistics2-19.2.18.jar. The native
        # CraftingCPUCluster.addBlockEntity accepts any one of these as core.
        cpu_blocks = {
            'ae2:crafting_unit', 'ae2:crafting_accelerator', 'ae2:crafting_monitor',
            *(f'ae2:{size}k_crafting_storage' for size in (1, 4, 16, 64, 256)),
        }
        self.assertEqual(set(policy['values']), cpu_blocks)
        self.assertEqual(len(policy['values']), len(cpu_blocks))

    def test_addition_preserves_native_guards_and_non_cpu_transport(self):
        policy = json.loads(BLACKLIST.read_text(encoding='utf-8'))
        self.assertIs(policy.get('replace'), False)
        for block in (
            'ae2:controller', 'ae2:drive', 'ae2:chest', 'ae2:molecular_assembler',
            'ae2:damaged_budding_quartz', 'ae2:chipped_budding_quartz',
            'ae2:flawed_budding_quartz', 'ae2:flawless_budding_quartz',
            'minecraft:chest', 'minecraft:spawner',
        ):
            with self.subTest(block=block):
                self.assertNotIn(block, policy['values'])
        self.assertFalse(any(value.startswith('#') or '*' in value for value in policy['values']))


if __name__ == '__main__':
    unittest.main()
