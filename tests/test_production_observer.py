"""Exercise actual output filters/capture callbacks with counted native stand-ins.

The methods and policy snapshot are read unchanged from production Java. The
native full-pack fixture separately checks every registered item and real
manufacture/ownership paths; this harness measures unnecessary observer work.
"""
from pathlib import Path
import shutil
import subprocess
import tempfile
import unittest


ROOT = Path(__file__).resolve().parents[1]
MODULE = ROOT / "src/compat/progressive-rewards/src/qm/rewards"


def declaration(source, marker):
    start = source.index(marker)
    opening = source.index("{", start)
    depth = 1
    for end in range(opening + 1, len(source)):
        depth += (source[end] == "{") - (source[end] == "}")
        if depth == 0:
            return source[start:end + 1]
    raise AssertionError("Unclosed Java declaration: " + marker)


class ProductionObserverTests(unittest.TestCase):
    @unittest.skipUnless(shutil.which("java"), "Java 17+ source launcher required for observer checks")
    def test_furnace_upgrade_requires_consumption_replacement_and_real_actor(self):
        evidence = (MODULE / "ProductionEvidence.java").read_text(encoding="utf-8")
        upgrade = (MODULE / "mixin/IronFurnacesUpgradeProductionMixin.java").read_text(encoding="utf-8")
        harness = r'''import java.util.*;
class FurnaceUpgradeObserverRegression {
    static final Item AIR = new Item(), OLD = new Item(), OUTPUT = new Item(), WRONG = new Item();
    static final Block BEFORE = new Block(OLD), AFTER = new Block(OUTPUT), OTHER = new Block(WRONG);
    static final BlockPos POS = new BlockPos();
    static int committed, ownershipReads;
    static class Item {}
    static class ItemStack {
        final Item item;
        int count;
        boolean consume = true;
        ItemStack(Item item) { this(item, 1); }
        ItemStack(Item item, int count) { this.item = item; this.count = count; }
        int getCount() { return count; }
        Item getItem() { return count <= 0 ? AIR : item; }
        void shrink(int amount) { if (consume) count = Math.max(0, count - amount); }
        boolean is(Item other) { return getItem() == other; }
    }
    record Block(Item item) {
        Item asItem() { return item; }
        BlockState defaultBlockState() { return new BlockState(this); }
    }
    record BlockState(Block block) {
        Block getBlock() { return block; }
        boolean is(Block other) { return block == other; }
    }
    static class BlockPos {}
    static class BlockIronFurnaceTileBase {}
    static class Level {
        Block block = AFTER;
        Object blockEntity = new BlockIronFurnaceTileBase();
        BlockState getBlockState(BlockPos pos) { return block.defaultBlockState(); }
        Object getBlockEntity(BlockPos pos) { return blockEntity; }
    }
    static class ServerLevel extends Level { Object getServer() { return this; } }
    static class Player {
        boolean creative, spectator;
        boolean isCreative() { return creative; }
        boolean isSpectator() { return spectator; }
        UUID getUUID() { return new UUID(0, 1); }
    }
    static class ServerPlayer extends Player {}
    static class FakePlayer extends ServerPlayer {}
    record UseOnContext(Player player, Level level) {
        Player getPlayer() { return player; }
        Level getLevel() { return level; }
        BlockPos getClickedPos() { return POS; }
    }
    static class Team { UUID getId() { return new UUID(0, 2); } }
    static class TeamManager {
        Optional<Team> getTeamForPlayerID(UUID id) { ownershipReads++; return Optional.of(new Team()); }
    }
    static class ProductionEvidence {
        static TeamManager manager() { return new TeamManager(); }
        static boolean isTrackedOutput(ItemStack stack) { return stack.is(OUTPUT); }
        static void rememberActor(ServerPlayer player, boolean allowed) {}
        static void record(Object server, UUID player, UUID team, ItemStack output) {
            check(output.is(OUTPUT), "Only the actual registered output is recorded"); committed++;
        }
        __EVIDENCE__
    }
    static class Upgrade extends Item {
        Block from = BEFORE, to = AFTER;
        __UPGRADE__
    }
    static void check(boolean value, String message) { if (!value) throw new AssertionError(message); }
    static void rejected(Upgrade upgrade, ItemStack stack, int count, UseOnContext context, String message) {
        int prior = committed;
        upgrade.qm$afterConsumed(stack, count, context);
        check(committed == prior, message);
    }
    public static void main(String[] args) {
        Upgrade upgrade = new Upgrade();
        ServerLevel level = new ServerLevel();
        ServerPlayer player = new ServerPlayer();
        UseOnContext context = new UseOnContext(player, level);
        ItemStack successful = new ItemStack(upgrade);
        upgrade.qm$afterConsumed(successful, 1, context);
        check(successful.getCount() == 0 && committed == 1, "Final consumed upgrade retains pre-consumption identity");

        rejected(upgrade, new ItemStack(upgrade, 0), 1, context, "Empty held stack");
        rejected(upgrade, new ItemStack(WRONG), 1, context, "Consumed wrong item");
        rejected(upgrade, new ItemStack(upgrade), 0, context, "Zero consumption request");
        rejected(upgrade, new ItemStack(upgrade, 2), 2, context, "Unexpected native consumption amount");
        ItemStack unchanged = new ItemStack(upgrade); unchanged.consume = false;
        rejected(upgrade, unchanged, 1, context, "Requested consumption without actual delta");

        level.block = BEFORE;
        rejected(upgrade, new ItemStack(upgrade), 1, context, "Failed replacement retains old block");
        level.block = OTHER;
        rejected(upgrade, new ItemStack(upgrade), 1, context, "Replacement created a different block");
        level.block = AFTER; level.blockEntity = null;
        rejected(upgrade, new ItemStack(upgrade), 1, context, "No native furnace block entity");
        level.blockEntity = new Object();
        rejected(upgrade, new ItemStack(upgrade), 1, context, "Wrong block entity type");
        level.blockEntity = new BlockIronFurnaceTileBase();
        upgrade.from = AFTER;
        rejected(upgrade, new ItemStack(upgrade), 1, context, "Same from/to block");
        upgrade.from = new Block(OUTPUT);
        rejected(upgrade, new ItemStack(upgrade), 1, context, "Different blocks exposing the same output item");
        upgrade.from = BEFORE;
        player.creative = true;
        rejected(upgrade, new ItemStack(upgrade), 1, context, "Creative player");
        player.creative = false; player.spectator = true;
        rejected(upgrade, new ItemStack(upgrade), 1, context, "Spectator player");
        player.spectator = false;
        rejected(upgrade, new ItemStack(upgrade), 1, new UseOnContext(new FakePlayer(), level), "FakePlayer");
        rejected(upgrade, new ItemStack(upgrade), 1, new UseOnContext(new Player(), level), "Non-server player");
        rejected(upgrade, new ItemStack(upgrade), 1, new UseOnContext(null, level), "Absent player");
        rejected(upgrade, new ItemStack(upgrade), 1, new UseOnContext(player, new Level()), "Client/non-server level");
        upgrade.to = OTHER; level.block = OTHER;
        rejected(upgrade, new ItemStack(upgrade), 1, context, "Untracked actual output");
        check(committed == 1 && ownershipReads == 1, "Every denied path avoids the ledger and ownership lookup");
        System.out.println("PASS: exact native consumption, replacement, output identity and real survival actor required");
    }
}
'''
        harness = harness.replace("__EVIDENCE__", "\n".join(declaration(evidence, marker) for marker in (
            "static boolean realSurvivalPlayer(", "public static void storageUpgraded(", "public static void furnaceUpgraded(",
        )))
        harness = harness.replace("__UPGRADE__", declaration(upgrade, "private void qm$afterConsumed("))
        with tempfile.TemporaryDirectory(prefix="soqm-furnace-observer-") as temporary:
            path = Path(temporary) / "FurnaceUpgradeObserverRegression.java"
            path.write_text(harness, encoding="utf-8")
            result = subprocess.run([shutil.which("java"), "-XX:-UsePerfData", "--source", "17", str(path)],
                                    capture_output=True, text=True, timeout=30)
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        self.assertIn("PASS: exact native consumption", result.stdout)

    @unittest.skipUnless(shutil.which("java"), "Java 17+ source launcher required for observer checks")
    def test_untracked_outputs_skip_work_without_changing_tracked_manufacture(self):
        policy = (MODULE / "ProductionPolicy.java").read_text(encoding="utf-8")
        evidence = (MODULE / "ProductionEvidence.java").read_text(encoding="utf-8")
        assembler = (MODULE / "mixin/AssemblerProductionMixin.java").read_text(encoding="utf-8")
        inscriber = (MODULE / "mixin/InscriberProductionMixin.java").read_text(encoding="utf-8")
        policy_methods = policy[policy.index("private record Policy("):policy.index("static Set<String> read()")]
        evidence_methods = "\n".join(declaration(evidence, marker) for marker in (
            "public static boolean isTrackedOutput(",
            "public static boolean isManufacture(ItemStack output, Container input)",
            "public static boolean isManufacture(ItemStack output, CraftingInput input)",
            "public static void infuserFinished(",
        ))
        harness = r'''import java.util.*;
class ProductionObserverRegression {
    static final Item TRACKED = new Item("test:tracked");
    static final Item OTHER = new Item("test:other");
    static final Item INPUT = new Item("test:input");
    static final UUID OWNER = UUID.fromString("f898e4bd-2401-4e4c-835f-f29f79b8503c");
    static int copies, committed;
    record Item(String id) {}
    record ResourceLocation(String id) {
        static ResourceLocation parse(String id) { return new ResourceLocation(id); }
    }
    static final class Registry {
        final Map<ResourceLocation, Item> items = new HashMap<>();
        int reads;
        boolean containsKey(ResourceLocation id) { reads++; return items.containsKey(id); }
        Item get(ResourceLocation id) { reads++; return items.get(id); }
    }
    static final class BuiltInRegistries { static final Registry ITEM = new Registry(); }
    static final class Logger {
        int errors;
        void error(String message, Object... args) { errors++; }
        void info(String message, Object... args) {}
    }
    static final class ItemStack {
        static final ItemStack EMPTY = new ItemStack(null);
        final Item item;
        ItemStack(Item item) { this.item = item; }
        boolean isEmpty() { return item == null; }
        Item getItem() { return item; }
        boolean is(Item other) { return item == other; }
        ItemStack copy() { copies++; return new ItemStack(item); }
    }
    static class Container {
        final ItemStack[] input;
        int reads;
        Container(ItemStack... input) { this.input = input; }
        int getContainerSize() { return input.length; }
        ItemStack getItem(int slot) { reads++; return input[slot]; }
    }
    static final class CraftingInput extends Container {
        CraftingInput(ItemStack... input) { super(input); }
        int size() { return input.length; }
    }
    // Java 17 launcher stand-in for the Java 21 List.getFirst native signature.
    static final class List<E> extends ArrayList<E> {
        E getFirst() { return get(0); }
        @SafeVarargs static <E> List<E> of(E... entries) {
            List<E> result = new List<>(); Collections.addAll(result, entries); return result;
        }
    }
    static final class Ingredient {
        final List<ItemStack> representations;
        int reads;
        Ingredient(ItemStack... stacks) { representations = List.of(stacks); }
        List<ItemStack> getRepresentations() { reads++; return representations; }
    }
    static final class ItemStackChemicalToItemStackRecipe {
        final List<ItemStack> outputs;
        final Ingredient input;
        int ingredientReads;
        ItemStackChemicalToItemStackRecipe(Ingredient input, ItemStack... outputs) {
            this.input = input; this.outputs = List.of(outputs);
        }
        List<ItemStack> getOutputDefinition() { return outputs; }
        Ingredient getItemInput() { ingredientReads++; return input; }
    }
    static final class Security {
        int reads;
        UUID getOwnerUUID() { reads++; return OWNER; }
    }
    static final class TileEntityMekanism {
        boolean supported = true;
        final Security security = new Security();
        Security getSecurity() { return security; }
    }
    static final class Level {}
    static final class IGridNode {}
    static final class TickRateModulation {}
    static final class CallbackInfoReturnable<T> {}
    static final class IMolecularAssemblerSupportedPattern {
        final ItemStack output;
        int calls;
        IMolecularAssemblerSupportedPattern(ItemStack output) { this.output = output; }
        ItemStack assemble(CraftingInput input, Level level) { calls++; return output; }
    }
    static final class AppEngInternalInventory {
        ItemStack stack = new ItemStack(INPUT);
        int reads;
        ItemStack getStackInSlot(int slot) { reads++; return stack; }
    }
    static final class InscriberRecipe {
        ItemStack output;
        InscriberRecipe(ItemStack output) { this.output = output; }
        ItemStack getResultItem() { return output; }
    }
    static class InscriberBlockEntity {
        InscriberRecipe recipe;
        boolean isSmash() { return true; }
        InscriberRecipe getTask() { return recipe; }
    }
    static final class ProductionPolicy {
        static final Logger LOGGER = new Logger();
        __POLICY__
    }
    static final class ProductionEvidence {
        static boolean supportedMachine(TileEntityMekanism machine) { return machine.supported; }
        static void machineFinished(TileEntityMekanism machine, UUID owner, ItemStack output) {
            check(owner.equals(OWNER), "Native owner is retained"); committed++;
        }
        __EVIDENCE__
    }
    static final class AssemblerCapture {
        ItemStack qm$pendingProduction = ItemStack.EMPTY;
        __ASSEMBLER__
    }
    static final class InscriberCapture extends InscriberBlockEntity {
        private int finalStep = 7;
        private final AppEngInternalInventory sideItemHandler = new AppEngInternalInventory();
        private final AppEngInternalInventory topItemHandler = new AppEngInternalInventory();
        private final AppEngInternalInventory bottomItemHandler = new AppEngInternalInventory();
        private ItemStack qm$pendingProduction = ItemStack.EMPTY;
        __INSCRIBER__
    }
    static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
    public static void main(String[] args) {
        Registry registry = BuiltInRegistries.ITEM;
        registry.items.put(ResourceLocation.parse(TRACKED.id()), TRACKED);
        registry.items.put(ResourceLocation.parse(OTHER.id()), OTHER);
        registry.items.put(ResourceLocation.parse(INPUT.id()), INPUT);
        Set<String> mutable = new HashSet<>(Set.of(TRACKED.id()));
        ProductionPolicy.install(mutable);
        mutable.add(OTHER.id());
        check(ProductionPolicy.allowedItems().equals(Set.of(TRACKED.id())), "No mutable policy input escapes");
        check(ProductionPolicy.allows(TRACKED) && ProductionPolicy.allows(TRACKED.id()), "Both policy views agree");
        check(!ProductionPolicy.allows(new Item(TRACKED.id())), "Registered Item identity is required");
        check(!ProductionEvidence.isTrackedOutput(ItemStack.EMPTY), "Empty results are rejected");

        registry.reads = 0;
        ItemStack tracked = new ItemStack(TRACKED), other = new ItemStack(OTHER), ingredient = new ItemStack(INPUT);
        CraftingInput grid = new CraftingInput(ingredient, ingredient, ingredient, ingredient, ingredient,
                ingredient, ingredient, ingredient, ingredient);
        var capture = new AssemblerCapture();
        var pattern = new IMolecularAssemblerSupportedPattern(other);
        for (int i = 0; i < 1000; i++) {
            check(capture.qm$capture(pattern, grid, new Level()) == other, "Native recipe output is returned unchanged");
        }
        check(pattern.calls == 1000, "Untracked native crafting still executes every time");
        check(grid.reads == 0 && copies == 0 && capture.qm$pendingProduction.isEmpty(),
                "Untracked assembler results skip all input reads and pending copies");
        System.out.println("PASS: 1000 untracked assembler results: 0 input reads, 0 pending copies");

        var machine = new TileEntityMekanism();
        var input = new Ingredient(ingredient);
        var untrackedRecipe = new ItemStackChemicalToItemStackRecipe(input, other);
        for (int i = 0; i < 1000; i++) ProductionEvidence.infuserFinished(machine, untrackedRecipe);
        check(untrackedRecipe.ingredientReads == 0 && input.reads == 0 && machine.security.reads == 0 && committed == 0,
                "Untracked infuser results skip ingredient representations and ownership");
        System.out.println("PASS: 1000 untracked infuser results: 0 ingredient reads, 0 owner reads");

        var press = new InscriberCapture();
        press.recipe = new InscriberRecipe(other);
        for (int i = 0; i < 1000; i++) press.qm$capture(null, 1, null);
        check(press.sideItemHandler.reads + press.topItemHandler.reads + press.bottomItemHandler.reads == 0
                && copies == 0 && press.qm$pendingProduction.isEmpty(), "Untracked Inscriber skips slots and copies");
        check(registry.reads == 0, "All hot output filters avoid registry lookups");
        System.out.println("PASS: 1000 untracked inscriber results: 0 input reads, 0 pending copies; 0 filter registry reads");

        capture.qm$capture(new IMolecularAssemblerSupportedPattern(tracked), grid, new Level());
        check(grid.reads == 9 && copies == 1 && capture.qm$pendingProduction.is(TRACKED),
                "Tracked assembler output still scans all ingredients and retains a copy");
        capture.qm$pendingProduction = ItemStack.EMPTY;
        capture.qm$capture(new IMolecularAssemblerSupportedPattern(tracked), new CraftingInput(tracked), new Level());
        check(capture.qm$pendingProduction.isEmpty() && copies == 1, "Same-item assembler transform stays rejected");
        check(!ProductionEvidence.isManufacture(tracked, new Container(tracked)), "Container reset stays rejected");
        check(!ProductionEvidence.isManufacture(tracked, new CraftingInput(ItemStack.EMPTY)), "Empty craft stays rejected");

        ProductionEvidence.infuserFinished(machine, new ItemStackChemicalToItemStackRecipe(input, tracked));
        check(committed == 1 && input.reads == 1 && machine.security.reads == 1, "Tracked infuser still reaches native owner");
        ProductionEvidence.infuserFinished(machine,
                new ItemStackChemicalToItemStackRecipe(new Ingredient(ingredient, tracked), tracked));
        ProductionEvidence.infuserFinished(machine, new ItemStackChemicalToItemStackRecipe(input, tracked, other));
        check(committed == 1 && machine.security.reads == 1, "Same-item alternatives and ambiguous outputs stay rejected");
        press.recipe = new InscriberRecipe(tracked);
        press.qm$capture(null, 1, null);
        check(press.qm$pendingProduction.is(TRACKED) && copies == 2, "Tracked Inscriber output still gets captured");
        press.sideItemHandler.stack = tracked;
        press.qm$capture(null, 1, null);
        check(press.qm$pendingProduction.isEmpty() && copies == 2, "Same-item Name Press stays rejected");

        ProductionPolicy.install(Set.of(OTHER.id()));
        check(!ProductionEvidence.isTrackedOutput(tracked) && ProductionEvidence.isTrackedOutput(other),
                "Reload removes old Item identities and immediately installs new ones");
        check(ProductionPolicy.allowedItems().equals(Set.of(OTHER.id())), "String policy changes with identity policy");
        ProductionPolicy.install(Set.of(OTHER.id(), "test:missing"));
        check(ProductionPolicy.allowedItems().isEmpty() && !ProductionPolicy.allows(OTHER)
                && ProductionPolicy.LOGGER.errors == 1, "Unknown registry item clears the whole policy");
        ProductionPolicy.install(Set.of(TRACKED.id()));
        ProductionPolicy.clear();
        check(ProductionPolicy.allowedItems().isEmpty() && !ProductionEvidence.isTrackedOutput(tracked),
                "Server stop clears both policy views");
        System.out.println("PASS: tracked manufacture, same-item rejection, immutable identity policy, reload and fail-closed clear");
    }
}
'''
        harness = harness.replace("__POLICY__", policy_methods + declaration(policy, "private static void install("))
        harness = harness.replace("__EVIDENCE__", evidence_methods)
        harness = harness.replace("__ASSEMBLER__", declaration(assembler, "private ItemStack qm$capture("))
        harness = harness.replace("__INSCRIBER__", declaration(inscriber, "private void qm$capture("))
        with tempfile.TemporaryDirectory(prefix="soqm-production-observer-") as temporary:
            path = Path(temporary) / "ProductionObserverRegression.java"
            path.write_text(harness, encoding="utf-8")
            result = subprocess.run([shutil.which("java"), "-XX:-UsePerfData", "--source", "17", str(path)],
                                    capture_output=True, text=True, timeout=30)
        self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
        self.assertIn("PASS: tracked manufacture", result.stdout)
        self.assertIn("0 filter registry reads", result.stdout)


if __name__ == "__main__":
    unittest.main()
