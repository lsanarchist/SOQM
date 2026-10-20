"""Material parity, alternative closure and bootstrap regressions for the workshop.

The small native snapshot makes the bootstrap test network-independent. When the
pinned cache is available, source hashes and alternative craft outputs are also
checked against the actual archives. Native inventory behavior belongs to the
separate HardcoreWorkshopProbe, not to a Python approximation of serializers.
"""

from collections import Counter
import hashlib
import json
from pathlib import Path
import re
import subprocess
import sys
import unittest
import zipfile

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT / "scripts"))
import hardcore_workshop as workshop
import campaign_progression as progression

PREFIX = "kubejs:hardcore_workshop/"
NATIVE = json.loads((ROOT / "tests/fixtures/hardcore_workshop_native.json").read_text())["recipes"]


def token(value):
    if isinstance(value, str):
        return value
    if isinstance(value, list):
        # A positive witness needs one accepted alternative, not every material.
        return token(value[0]) if value else "#unknown"
    if "basePredicate" in value:
        return token(value["basePredicate"])
    if value.get("type") == "immersiveengineering:fluid_stack":
        return "fluid:" + value["tag"]
    return value.get("item", value.get("id", "#" + value.get("tag", "unknown")))


def inputs(recipe):
    if "pattern" in recipe:
        return Counter(token(recipe["key"][char]) for line in recipe["pattern"] for char in line if char != " ")
    if "ingredients" in recipe:
        return Counter(token(value) for value in recipe["ingredients"])
    return Counter({token(recipe[key]): recipe[key].get("count", 1)
                    for key in ("input", "item_input", "chemical_input") if key in recipe})


def sha_recipe(recipe):
    return hashlib.sha256(json.dumps(recipe, sort_keys=True, separators=(",", ":")).encode()).hexdigest()


class HardcoreWorkshopTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.policy = workshop.load_policy()
        cls.rows = {row["id"]: row for row in cls.policy["recipes"]}
        cls.active = {row["id"]: row for row in progression.recipe_rows()}
        cls.native = {row["id"]: row for row in NATIVE}

    def recipe(self, suffix):
        return self.rows[PREFIX + suffix]["recipe"]

    def test_exact_late_replacement_is_active_and_reproducible(self):
        self.assertEqual(workshop.SCRIPT.read_text(), workshop.render(self.policy))
        self.assertEqual(len(self.rows), 63)
        self.assertTrue(self.rows.keys() <= self.active.keys())
        self.assertFalse(set(self.policy["remove_ids"]) & self.active.keys())
        self.assertIn("kubejs:expert/kubejs/industrial_chassis", self.active)
        self.assertIn("kubejs:expert/ae2/network/crafting/molecular_assembler", self.active)
        self.assertFalse(self.policy["food_recipe_changes"])
        source = workshop.SCRIPT.read_text()
        self.assertNotIn("remove({output", source)
        fixture = (ROOT / "tests/runtime/HardcoreWorkshopProbe.java").read_text()
        removed = fixture.split("REMOVED = Set.of(", 1)[1].split(");", 1)[0]
        self.assertEqual(set(re.findall(r'"([a-z0-9_:/.+-]+)"', removed)), set(self.policy["remove_ids"]))
        self.assertIn(f"EXPECTED_RECIPES = {len(self.rows)};", fixture)

    def test_runtime_script_removes_native_originals_and_queued_authored_recipes(self):
        """Regress KubeJS's separate original/added collections, using the real JS.

        The first native run exposed that event.remove alone leaves every
        earlier custom addition active. This model deliberately gives remove
        that pinned behavior, rather than making it delete from both stores.
        """
        harness = r'''
const fs = require('node:fs');
const vm = require('node:vm');
const {policy, source} = JSON.parse(fs.readFileSync(0, 'utf8'));
function recipe(id) {
  return {_id: id, removed: false, getId() { return this._id; }, remove() { this.removed = true; }};
}
const originals = new Map(policy.remove_ids.filter(id => !id.startsWith('kubejs:')).map(id => [id, recipe(id)]));
const added = policy.remove_added_ids.map(recipe);
const preserved = recipe('kubejs:fixture/unrelated');
added.push(preserved);
const event = {
  addedRecipes: added,
  remove({id}) { originals.get(id)?.remove(); },
  custom(json) {
    const value = recipe(null);
    value.json = json;
    value.id = function(id) { this._id = id; return this; };
    added.push(value);
    return value;
  }
};
vm.runInNewContext(source, {ServerEvents: {recipes(callback) { callback(event); }}});
const live = added.filter(row => !row.removed).map(row => row.getId());
if ([...originals.values()].some(row => !row.removed)) throw new Error('Native original removal failed');
if (policy.remove_added_ids.some(id => live.includes(id))) throw new Error('Queued cheap authored recipe survived');
if (!live.includes(preserved.getId())) throw new Error('Unrelated scripted recipe was removed');
if (live.filter(id => id.startsWith('kubejs:hardcore_workshop/')).length !== policy.recipes.length) {
  throw new Error('Replacement batch is incomplete');
}
console.log('native originals and queued additions removed independently');
'''
        result = subprocess.run(["node", "-e", harness], input=json.dumps({
            "policy": self.policy, "source": workshop.SCRIPT.read_text()
        }), capture_output=True, text=True, check=False)
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)

    def test_storage_keeps_native_serializers_and_all_single_double_variants(self):
        storage = [r for r in self.rows.values() if r["recipe"]["type"].startswith("sophisticatedstorage:")]
        self.assertEqual(len(storage), 24)
        self.assertEqual(Counter(r["recipe"]["type"] for r in storage), {
            "sophisticatedstorage:storage_tier_upgrade": 21,
            "sophisticatedstorage:double_chest_tier_upgrade": 3,
        })
        for row in storage:
            self.assertEqual(row["recipe"]["type"], row["native_evidence"]["serializer"])
            self.assertIn("neoforge:conditions", row["recipe"])
        for suffix in ("chemical_infuser", "rotary_condensentrator"):
            self.assertEqual(self.recipe("mekanism/" + suffix)["type"], "mekanism:mek_data")

    def test_all_storage_paths_pay_full_copper_and_iron_tiers(self):
        producing = {}
        for row in self.rows.values():
            output = row["recipe"]["result"]["id"]
            producing.setdefault(output, []).append(row)

        def cost(ingredient, visiting=()):
            if ingredient in {"#c:storage_blocks/copper", "minecraft:copper_block"}:
                return Counter(copper=9)
            if ingredient in {"#c:storage_blocks/iron", "minecraft:iron_block"}:
                return Counter(iron=9)
            if ingredient == "minecraft:iron_ingot":
                return Counter(iron=1)
            if ingredient not in producing or not ingredient.startswith("sophisticatedstorage:"):
                return Counter()
            self.assertNotIn(ingredient, visiting)
            alternatives = [row for row in producing[ingredient] if "/double_" not in row["id"]]
            prices = [row_cost(row, (*visiting, ingredient)) for row in alternatives]
            self.assertTrue(all(price == prices[0] for price in prices), ingredient)
            return prices[0]

        def row_cost(row, visiting=()):
            total = Counter()
            for ingredient, count in inputs(row["recipe"]).items():
                # A native double chest carries two already-paid single capacities.
                if "/double_" in row["id"] and ingredient in {
                    "sophisticatedstorage:chest", "sophisticatedstorage:copper_chest"
                }:
                    count *= 2
                for metal, amount in cost(ingredient, visiting).items():
                    total[metal] += count * amount
            return total

        for row in self.rows.values():
            if not row["recipe"]["type"].startswith("sophisticatedstorage:"):
                continue
            doubled = "/double_" in row["id"]
            expected = Counter(copper=144 if doubled else 72)
            if "iron" in row["recipe"]["result"]["id"]:
                expected["iron"] = 144 if doubled else 72
            self.assertEqual(row_cost(row), expected, row["id"])
        self.assertEqual(row_cost(self.rows[PREFIX + "minecraft/hopper"]), Counter(copper=72, iron=77))
        self.assertIn("sophisticatedstorage:basic_to_iron_tier_from_basic_to_copper_tier", self.policy["remove_ids"])

    def test_manual_and_powered_workshop_have_distinct_costs(self):
        for metal in ("copper", "iron", "gold", "steel"):
            recipe = self.recipe("manual/plate_" + metal)
            self.assertEqual(inputs(recipe), Counter({"#c:ingots/" + metal: 2, "immersiveengineering:hammer": 1}))
            press = self.native["immersiveengineering:metalpress/plate_" + metal]["recipe"]
            self.assertEqual(press["input"].get("count", 1), 1)
            self.assertNotIn("immersiveengineering:metalpress/plate_" + metal, self.policy["remove_ids"])
        for suffix in ("iron_furnace", "iron_furnace2", "upgrade_iron", "upgrade_iron2"):
            self.assertEqual(inputs(self.recipe("ironfurnaces/" + suffix))["#c:storage_blocks/iron"], 8)
        for suffix in ("copper_furnace", "upgrade_copper"):
            self.assertEqual(inputs(self.recipe("ironfurnaces/" + suffix))["#c:storage_blocks/copper"], 8)
        for row in self.rows.values():
            if "/gears/" in row["id"] and row["recipe"]["result"]["id"].endswith(("iron_gear", "gear_iron")):
                self.assertEqual(inputs(row["recipe"])["#c:plates/iron"], 4, row["id"])
        hopper = inputs(self.recipe("minecraft/hopper"))
        self.assertEqual(hopper["minecraft:chest"], 1)
        self.assertEqual(hopper["sophisticatedstorage:basic_to_iron_tier_upgrade"], 1)

    def test_first_workshop_and_mekanism_are_reachable_without_powered_plate_press(self):
        """Positive native recipe witness, starting from obtainable basic resources.

        Multiblock operation has explicit brick/hammer/fuel requirements. This
        checks dependency reachability, not mining time, power rates or quantities.
        """
        tags = {
            "minecraft:planks": "minecraft:oak_planks", "minecraft:wooden_slabs": "minecraft:oak_slab",
            "minecraft:wooden_buttons": "minecraft:oak_button", "minecraft:stone_crafting_materials": "minecraft:cobblestone",
            "c:rods/wooden": "minecraft:stick", "c:strings": "minecraft:string", "c:clay": "minecraft:clay_ball",
            "c:bricks": "minecraft:brick", "c:bricks/nether": "minecraft:nether_brick",
            "c:sandstone/blocks": "minecraft:sandstone", "c:coal_coke": "immersiveengineering:coal_coke",
            "c:wires/copper": "immersiveengineering:wire_copper", "c:glass_blocks/cheap": "minecraft:glass",
            "c:glass_blocks": "minecraft:glass", "c:glass_blocks/colorless": "minecraft:glass",
            "mekanism:alloys/basic": "minecraft:redstone", "mekanism:alloys/infused": "mekanism:alloy_infused",
            "mekanism:redstone": "chemical:mekanism:redstone", "c:dusts/redstone": "minecraft:redstone",
            "c:circuits/basic": "mekanism:basic_control_circuit",
        }
        for metal, ingot, block in (
            ("iron", "minecraft:iron_ingot", "minecraft:iron_block"),
            ("copper", "minecraft:copper_ingot", "minecraft:copper_block"),
            ("gold", "minecraft:gold_ingot", "minecraft:gold_block"),
            ("steel", "immersiveengineering:ingot_steel", "immersiveengineering:storage_steel"),
            ("osmium", "mekanism:ingot_osmium", "mekanism:block_osmium"),
        ):
            tags["c:ingots/" + metal] = ingot
            tags["c:storage_blocks/" + metal] = block
            tags["c:plates/" + metal] = "immersiveengineering:plate_" + metal
        tags["c:storage_blocks/redstone"] = "minecraft:redstone_block"
        available = {"minecraft:" + item for item in (
            "oak_planks", "stick", "string", "cobblestone", "stone", "iron_ingot", "copper_ingot", "gold_ingot",
            "redstone", "glass", "coal", "brick", "clay_ball", "sandstone", "nether_brick", "magma_block"
        )} | {"mekanism:ingot_osmium"}
        recipes = {row["id"]: row for row in NATIVE if row["recipe"]["type"] != "immersiveengineering:metal_press"}
        recipes.update(self.active)
        requires = {
            "immersiveengineering:cokeoven/coke": {"immersiveengineering:cokebrick", "immersiveengineering:hammer"},
            "immersiveengineering:blastfurnace/steel": {
                "immersiveengineering:blastbrick", "immersiveengineering:hammer", "immersiveengineering:coal_coke"
            },
        }
        permitted = {"minecraft:crafting_shaped", "minecraft:crafting_shapeless", "mekanism:mek_data",
                     "sophisticatedstorage:storage_tier_upgrade", "immersiveengineering:shaped_fluid"}
        witnessed = {}

        def resolve(value):
            return tags.get(value[1:], value) if value.startswith("#") else value

        for _ in range(len(recipes)):
            changed = False
            for ident, row in recipes.items():
                recipe = row["recipe"]
                kind = recipe["type"]
                if kind not in permitted and ident not in self.native:
                    continue
                requirements = set(requires.get(ident, ()))
                if kind in {"mekanism:metallurgic_infusing", "mekanism:chemical_conversion"}:
                    requirements |= {"mekanism:metallurgic_infuser", "mekanismgenerators:heat_generator"}
                if kind == "immersiveengineering:shaped_fluid":
                    requirements.add("minecraft:bucket")
                if not requirements <= available or not {resolve(value) for value in inputs(recipe)} <= available:
                    continue
                result = recipe.get("result", recipe.get("output"))
                if not isinstance(result, dict):
                    continue
                output = resolve(token(result))
                if kind == "mekanism:chemical_conversion":
                    output = "chemical:" + output
                if output not in available:
                    available.add(output)
                    witnessed[output] = ident
                    changed = True
                if kind == "immersiveengineering:coke_oven":
                    available.add("fluid:c:creosote")
            if not changed:
                break
        for target in (*self.policy["bootstrap_targets"], "mekanismgenerators:heat_generator",
                       "ae2:inscriber", "mekanism:advanced_chemical_tank", "mekanism:advanced_fluid_tank"):
            self.assertIn(target, available, (target, sorted(witnessed)))
        self.assertEqual(witnessed["immersiveengineering:ingot_steel"], "immersiveengineering:blastfurnace/steel")
        self.assertFalse(any(ident.startswith("immersiveengineering:metalpress/") for ident in witnessed.values()))

    def test_circuit_scaling_and_module_base_do_not_require_their_own_tier(self):
        for tier, previous in (("advanced", "basic"), ("elite", "advanced"), ("ultimate", "elite")):
            recipe = self.recipe("mekanism/control_circuit/" + tier)
            self.assertEqual(recipe["result"]["count"], 1)
            self.assertEqual(inputs(recipe)["mekanism:" + previous + "_control_circuit"], 2)
            self.assertNotIn("mekanism:control_circuit/basic", self.policy["remove_ids"])
            self.assertIn("mekanism:control_circuit/infused_" + tier, self.policy["remove_ids"])
        module = self.recipe("mekanism/module_base")
        self.assertEqual(module["result"]["count"], 1)
        self.assertEqual(inputs(module)["mekanism:hdpe_sheet"], 2)
        self.assertEqual(inputs(module)["mekanism:elite_control_circuit"], 1)
        producing = {}
        for row in self.active.values():
            output = progression.recipe_output(row["recipe"])
            if output:
                producing.setdefault(output, set()).update(progression.ingredient_items(row["recipe"]))
        for output in ("mekanism:advanced_control_circuit", "mekanism:elite_control_circuit",
                       "mekanism:ultimate_control_circuit", "mekanism:module_base"):
            visited, pending = set(), list(producing[output])
            while pending:
                item = pending.pop()
                self.assertNotEqual(item, output, "A new circuit/module dependency loops through " + item)
                if item not in visited:
                    visited.add(item)
                    pending.extend(producing.get(item, ()))

    def test_native_evidence_matches_pinned_source_when_cached(self):
        cache = ROOT / ".cache/mods"
        if not cache.is_dir():
            self.skipTest("Pinned native mod cache is optional for the network-independent authoring checks")
        sources = [row["native_evidence"] for row in self.rows.values() if "native_evidence" in row]
        sources += [{"jar": row["jar"], "path": row["path"], "sha256": row["sha256"]}
                    for row in NATIVE if row["jar"] != "Minecraft 1.21.1"]
        for evidence in sources:
            archive = cache / evidence["jar"]
            if not archive.is_file():
                self.skipTest("Not all pinned native mod archives are cached")
            with zipfile.ZipFile(archive) as native:
                actual = json.loads(native.read(evidence["path"]))
            self.assertEqual(sha_recipe(actual), evidence["sha256"], evidence["path"])
        for row in NATIVE:
            self.assertEqual(sha_recipe(row["recipe"]), row["sha256"], row["id"])

    def test_pinned_recipe_alternatives_cannot_bypass_new_output_costs(self):
        lock = json.loads((ROOT / "metadata/mods.lock.json").read_text())
        pins = {row["filename"]: row for row in lock["mods"]}
        for native in self.policy["native_mods"]:
            self.assertEqual(native["sha256"], pins[native["filename"]]["sha256"])
            self.assertEqual(native["version"], pins[native["filename"]]["version"])
        cache = ROOT / ".cache/mods"
        if not all((cache / filename).is_file() for filename in pins):
            self.skipTest("Full pinned cache is optional; alternative closure was audited during authoring")
        outputs = {row["recipe"]["result"]["id"] for row in self.rows.values()}
        allowed = set(self.policy["remove_ids"]) | self.policy["allowed_native_output_alternatives"].keys()
        processes = self.policy["allowed_native_tag_output_processes"]
        output_tags = {row["output_tag"] for row in processes.values()}
        unexpected = []
        for filename in pins:
            with zipfile.ZipFile(cache / filename) as archive:
                for path in archive.namelist():
                    match = re.fullmatch(r"data/([^/]+)/recipe/(.+)\.json", path)
                    if not match:
                        continue
                    recipe = json.loads(archive.read(path))
                    output = progression.recipe_output(recipe)
                    ident = match[1] + ":" + match[2]
                    if output in outputs and ident not in allowed:
                        unexpected.append(ident)
                    result = recipe.get("result", recipe.get("output"))
                    tag = result.get("tag") if isinstance(result, dict) else None
                    if tag in output_tags:
                        if ident not in processes or recipe["type"] != processes[ident]["serializer"]:
                            unexpected.append(ident)
        self.assertEqual(unexpected, [], "A newly pinned native output needs an explicit cost/contents review")


if __name__ == "__main__":
    unittest.main()
