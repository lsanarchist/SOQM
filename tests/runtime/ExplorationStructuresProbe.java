package soqm.validation;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import java.io.InputStreamReader;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

/** Native data codec / chest load checks without starting a server or a world. */
public final class ExplorationStructuresProbe {
    private static final String MOON_LOOT = "ad_astra:chests/dungeon/moon/dungeon_chest";

    private ExplorationStructuresProbe() {}

    public static void main(String[] args) throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        System.out.println(run(Path.of(args[0])));
    }

    public static Map<String, Object> run(Path repository) throws Exception {
        Path data = repository.resolve("overrides/kubejs/data");
        JsonObject definition = json(data.resolve("qm/worldgen/processor_list/moon_tower_chest_loot.json"));
        var processors = StructureProcessorType.DIRECT_CODEC.parse(JsonOps.INSTANCE, definition).getOrThrow().list();
        check(processors.size() == 1, "The tower has one data-only processor");
        StructureProcessor processor = processors.getFirst();
        var registries = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        LevelReader level = (LevelReader) Proxy.newProxyInstance(LevelReader.class.getClassLoader(),
                new Class<?>[]{LevelReader.class}, (proxy, method, arguments) -> {
                    if (method.getName().equals("getBlockState")) return Blocks.AIR.defaultBlockState();
                    throw new AssertionError("Unexpected world access: " + method.getName());
                });
        CompoundTag prefilled = new CompoundTag();
        prefilled.putString("id", "minecraft:chest");
        var items = NonNullList.withSize(27, ItemStack.EMPTY);
        for (int slot = 0; slot < items.size(); slot++) items.set(slot, new ItemStack(Items.DIAMOND, 64));
        ContainerHelper.saveAllItems(prefilled, items, registries);

        // First prove the fixture loads valuable items without the processor.
        ProbeChest original = new ProbeChest(Blocks.CHEST.defaultBlockState());
        original.loadWithComponents(prefilled, registries);
        check(original.itemCount() == 27 * 64, "The control chest really contains 27 stacks");
        int facings = 0;
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            BlockState state = Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, facing);
            var info = new StructureBlockInfo(BlockPos.ZERO, state, prefilled);
            var result = processor.processBlock(level, BlockPos.ZERO, BlockPos.ZERO, info, info, new StructurePlaceSettings());
            check(result.state() == state, "The processor retains chest facing and single/dry state");
            check(MOON_LOOT.equals(result.nbt().getString("LootTable")), "The fixed chest gets deferred moon loot");
            check(!prefilled.contains("LootTable"), "AppendLoot does not mutate the original template NBT");
            ProbeChest loaded = new ProbeChest(result.state());
            loaded.loadWithComponents(result.nbt(), registries);
            check(loaded.itemCount() == 0, "Native chest loading ignores prefilled Items once LootTable is set");
            check(loaded.getLootTable() != null && loaded.getLootTable().location().toString().equals(MOON_LOOT),
                    "Native chest loading retains the deferred table");
            CompoundTag saved = loaded.saveWithFullMetadata(registries);
            check(!saved.contains("Items") && saved.contains("LootTable"),
                    "A save also drops the old prefilled Items instead of persisting a hidden duplicate");
            // The five existing randomized tower chests keep the same table.
            CompoundTag randomChest = new CompoundTag();
            randomChest.putString("LootTable", MOON_LOOT);
            var randomInfo = new StructureBlockInfo(BlockPos.ZERO, state, randomChest);
            var randomResult = processor.processBlock(level, BlockPos.ZERO, BlockPos.ZERO,
                    randomInfo, randomInfo, new StructurePlaceSettings());
            check(MOON_LOOT.equals(randomResult.nbt().getString("LootTable")), "Existing random moon chests retain their table");
            facings++;
        }
        for (BlockState state : new BlockState[]{Blocks.BARREL.defaultBlockState(), Blocks.STONE.defaultBlockState(),
                Blocks.CHEST.defaultBlockState().setValue(ChestBlock.TYPE, ChestType.LEFT),
                Blocks.CHEST.defaultBlockState().setValue(ChestBlock.WATERLOGGED, true)}) {
            var info = new StructureBlockInfo(BlockPos.ZERO, state, prefilled);
            check(processor.processBlock(level, BlockPos.ZERO, BlockPos.ZERO, info, info,
                    new StructurePlaceSettings()) == info, "Unmatched blocks are untouched");
        }
        var pool = json(data.resolve("ad_astra_more_structures/worldgen/template_pool/moon_tower_dungeon.json"));
        var element = pool.getAsJsonArray("elements").get(0).getAsJsonObject().getAsJsonObject("element");
        check(element.get("location").getAsString().equals("ad_astra_more_structures:moon_tower_dungeon")
                        && element.get("processors").getAsString().equals("qm:moon_tower_chest_loot"),
                "The policy is attached only to the pinned moon tower template");
        int placements = 0;
        for (String name : new String[]{"jail", "jungle_main_room", "library1", "mainroom1", "undead_main_room"}) {
            var placement = json(data.resolve("underground_rooms/worldgen/structure_set/" + name + ".json"))
                    .getAsJsonObject("placement");
            StructurePlacement.CODEC.parse(JsonOps.INSTANCE, placement).getOrThrow();
            placements++;
        }
        // This replacement is all vanilla, so decode the real table against
        // the real item registry rather than substituting modded stand-ins.
        var undeadLoot = LootTable.DIRECT_CODEC.parse(RegistryOps.create(JsonOps.INSTANCE, registries),
                json(data.resolve("underground_rooms/loot_table/chests/undead_dungeon_loot.json"))).getOrThrow();
        check(undeadLoot.getParamSet() == LootContextParamSets.CHEST, "The undead replacement is a native chest table");
        int trimItems = 0;
        try (var resource = ExplorationStructuresProbe.class.getResourceAsStream("/data/minecraft/tags/item/trim_templates.json")) {
            check(resource != null, "The referenced trim_templates tag exists in native 1.21.1 resources");
            var tag = JsonParser.parseReader(new InputStreamReader(resource, StandardCharsets.UTF_8)).getAsJsonObject();
            for (var entry : tag.getAsJsonArray("values")) {
                check(BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(entry.getAsString())),
                        "Every native trim template resolves to a real item");
                trimItems++;
            }
            check(trimItems > 0, "The trim pool has native members");
        }
        Map<String, Object> results = new LinkedHashMap<>();
        results.put("scope", "Vanilla 1.21.1 native data codecs and chest objects; no world generation or client launch");
        results.put("moon_processor_facings_preserved", facings);
        results.put("prefilled_stacks_ignored_and_dropped_on_save", 27);
        results.put("unmatched_blocks_untouched", 4);
        results.put("native_structure_placements_decoded", placements);
        results.put("native_undead_loot_tables_decoded", 1);
        results.put("native_trim_template_items", trimItems);
        return results;
    }

    private static JsonObject json(Path path) throws Exception {
        return JsonParser.parseString(Files.readString(path)).getAsJsonObject();
    }

    private static void check(boolean valid, String message) {
        if (!valid) throw new AssertionError(message);
    }

    private static final class ProbeChest extends ChestBlockEntity {
        ProbeChest(BlockState state) { super(BlockPos.ZERO, state); }
        int itemCount() { return getItems().stream().mapToInt(ItemStack::getCount).sum(); }
    }
}
