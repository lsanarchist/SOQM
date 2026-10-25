package soqm.validation.encounters;

import com.catastrophe573.dimdungeons.DimDungeons;
import com.catastrophe573.dimdungeons.item.BaseItemKey;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.loot.LootController;
import dev.shadowsoffire.apotheosis.loot.RarityRegistry;
import dev.shadowsoffire.apotheosis.socket.gem.GemInstance;
import dev.shadowsoffire.apotheosis.tiers.GenContext;
import dev.shadowsoffire.apotheosis.tiers.WorldTier;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.data.inventory.ILootrInventory;
import qm.chests.SparseChestLoot;

/** Exercises native Apotheosis codecs and Lootr's personal first-fill path. */
final class DungeonRewardProbe {
    private static final String MARKER = "qm_dungeon_reward";
    private static final String FOREIGN = "soqm_probe_foreign_data";
    private static final Map<String, Integer> CHESTS = Map.ofEntries(
        Map.entry("dimdungeons:chest/chestloot_basic_easy", 0),
        Map.entry("dimdungeons:chest/chestloot_basic_hard", 1),
        Map.entry("dimdungeons:chest/chestloot_advanced_easy", 1),
        Map.entry("dimdungeons:chest/chestloot_advanced_hard", 1),
        Map.entry("dimdungeons:chest/chestloot_lucky", 0),
        Map.entry("dimdungeons:chest/chestloot_crazy", 1),
        Map.entry("dimdungeons:chests/chestloot_2", 1),
        Map.entry("dimdungeons:chests/chestloot_basic_easy", 0),
        Map.entry("dimdungeons:chests/chestloot_basic_hard", 1),
        Map.entry("dimdungeons:chests/chestloot_advanced_hard", 1),
        Map.entry("dimdungeons:chests/chestloot_lucky", 0),
        Map.entry("qm:chest/dimensional_dungeons/citadel_supplies", 0),
        Map.entry("qm:chest/dimensional_dungeons/citadel_treasure", 2),
        Map.entry("qm:chest/dimensional_dungeons/abyss_supplies", 0),
        Map.entry("qm:chest/dimensional_dungeons/abyss_treasure", 2)
    );
    private static int assertions;
    private static final List<PersonalChest> PERSONAL = new ArrayList<>();
    private static DungeonTestPlayers persistentPlayers;
    private record PersonalChest(ILootrBlockEntity provider, ServerPlayer player,
                                 ILootrInventory inventory, CompoundTag saved) { }
    private DungeonRewardProbe() { }

    static Map<String, Object> run(MinecraftServer server) throws Exception {
        assertions = 0;
        ServerLevel level = server.getLevel(DimDungeons.DUNGEON_DIMENSION);
        check(level != null, "DD dimension loaded for real native combat loot");
        Method finalFill = LootTable.class.getDeclaredMethod("shuffleAndSplitItems",
                ObjectArrayList.class, int.class, RandomSource.class);
        finalFill.setAccessible(true);
        int rolls = 0, prizes = 0, gems = 0, gear = 0, keys = 0;
        Map<String, Integer> sampled = new LinkedHashMap<>();
        Map<String, Integer> keyRolls = new LinkedHashMap<>();
        try (DungeonTestPlayers players = new DungeonTestPlayers()) {
            ServerPlayer player = players.create(level, new BlockPos(0, 80, 0), WorldTier.HAVEN);
            for (WorldTier tier : WorldTier.values()) {
                player.setData(Apoth.Attachments.WORLD_TIER, tier);
                LootParams params = params(level, player, player.position());
                for (var entry : CHESTS.entrySet()) {
                    String name = entry.getKey();
                    LootTable table = table(server, name);
                    int prizeRolls = 0;
                    for (int seed = 1; seed <= 48; seed++) {
                        var output = table.getRandomItems(params, seed);
                        List<ItemStack> expected = new ArrayList<>();
                        for (ItemStack stack : output) {
                            if (marked(stack)) {
                                boolean gem = GemInstance.unsocketed(stack).isValidUnsocketed();
                                validateCombat(stack, tier, player.getLuck());
                                if (gem) gems++; else gear++;
                                CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
                                tag.putString(FOREIGN, "retained");
                                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
                                expected.add(unmarked(stack));
                            }
                            if (stack.getItem() instanceof BaseItemKey key && key.isActivated(stack)) {
                                int theme = key.getDungeonTheme(stack);
                                if (theme >= 4) {
                                    boolean allowed = theme == 4 && (name.equals("dimdungeons:chest/chestloot_advanced_hard")
                                            || name.equals("dimdungeons:chests/chestloot_advanced_hard")
                                            || name.endsWith("citadel_treasure"))
                                        || theme == 5 && (name.endsWith("citadel_treasure") || name.endsWith("abyss_treasure"));
                                    check(allowed, "Earned expedition key cannot escape its difficulty gate: " + name + " -> " + theme);
                                    check(stack.has(DimDungeons.DUNGEON_KEY_DATA), "Earned key has the author's real address component");
                                    keyRolls.merge(name + "/" + theme, 1, Integer::sum);
                                    keys++;
                                }
                            }
                        }
                        check(expected.size() >= entry.getValue(), "Hard treasure rolled its promised native combat prize: " + name);
                        check(expected.size() <= (name.endsWith("_treasure") ? 2 : 1), "Bonus loot is finite per chest: " + name);
                        if (!expected.isEmpty()) prizeRolls++;
                        prizes += expected.size();
                        finalFill.invoke(table, output, 27, RandomSource.create(seed));
                        for (ItemStack wanted : expected) {
                            check(output.stream().anyMatch(actual -> ItemStack.isSameItemSameComponents(wanted, actual)
                                            && actual.getCount() == wanted.getCount()),
                                    "Final fill keeps native affixes/gem identity and unrelated custom data: " + name);
                        }
                        check(output.stream().noneMatch(DungeonRewardProbe::marked), "Temporary combat marker never reaches the inventory");
                        check(output.stream().noneMatch(SparseChestLoot::isBlocked), "Dungeon output preserves technology exclusions");
                        check(output.size() <= 27, "Real final fill respects physical chest capacity");
                        rolls++;
                    }
                    sampled.put(tier + "/" + name, prizeRolls);
                    if (entry.getValue() == 0) check(prizeRolls > 0 && prizeRolls < 48,
                            "Ordinary chests sample both successful and unsuccessful optional combat rolls");
                }
            }
            check(gems > 0 && gear > 0 && keys > 0, "Real roll sample includes native gems, affix gear and earned expedition keys");
            for (String gated : List.of("dimdungeons:chest/chestloot_advanced_hard/4",
                    "dimdungeons:chests/chestloot_advanced_hard/4",
                    "qm:chest/dimensional_dungeons/citadel_treasure/4",
                    "qm:chest/dimensional_dungeons/citadel_treasure/5",
                    "qm:chest/dimensional_dungeons/abyss_treasure/5")) {
                check(keyRolls.getOrDefault(gated, 0) > 0,
                        "Native add_table helper retains the original outer-table context and yields its earned key: " + gated);
            }
            nativeTreasure(server, level, player, finalFill);
        }
        personalLootr(server, level);
        return Map.ofEntries(Map.entry("world_tiers", WorldTier.values().length), Map.entry("outer_tables", CHESTS.size()),
                Map.entry("actual_loaded_rolls", rolls), Map.entry("combat_prizes_preserved", prizes),
                Map.entry("valid_native_gems", gems), Map.entry("valid_native_affix_items", gear), Map.entry("earned_keys", keys),
                Map.entry("personal_lootr_inventories", PERSONAL.size()), Map.entry("assertions", assertions),
                Map.entry("optional_prize_roll_counts", sampled), Map.entry("native_key_roll_counts", keyRolls));
    }

    private static void nativeTreasure(MinecraftServer server, ServerLevel level, ServerPlayer player, Method fill) throws Exception {
        player.setData(Apoth.Attachments.WORLD_TIER, WorldTier.PINNACLE);
        ItemStack sword = LootController.createLootItem(new ItemStack(Items.DIAMOND_SWORD),
                RarityRegistry.INSTANCE.getValue(ResourceLocation.parse("apotheosis:mythic")),
                GenContext.forPlayer(player));
        check(AffixHelper.hasAffixes(sword), "Native treasure fixture is a real affix item");
        for (String name : CHESTS.keySet()) {
            LootTable table = table(server, name);
            for (int seed = 0; seed < 32; seed++) {
                var generated = new ObjectArrayList<ItemStack>();
                generated.add(sword.copy());
                fill.invoke(table, generated, 27, RandomSource.create(seed));
                check(generated.size() == 1 && ItemStack.isSameItemSameComponents(generated.getFirst(), sword),
                        "DD retains an already-generated native valuable without a second chance roll: " + name);
            }
        }
        ResourceLocation outside = ResourceLocation.parse("minecraft:chests/village/village_plains_house");
        check("settlement".equals(SparseChestLoot.profile(outside)), "Ordinary village retains its existing profile");
        int survived = 0;
        for (int seed = 0; seed < 256; seed++) {
            var generated = new ObjectArrayList<ItemStack>(); generated.add(sword.copy());
            SparseChestLoot.compact(outside, generated, 27, RandomSource.create(seed * 71023L));
            if (!generated.isEmpty()) survived++;
        }
        check(survived > 0 && survived < 128, "DD-only retention change does not globally buff settlement treasures");
    }

    private static void personalLootr(MinecraftServer server, ServerLevel level) {
        check(LootrAPI.isReady() && !LootrAPI.isTeamLoot(), "Native Lootr is ready and uses personal inventories");
        persistentPlayers = new DungeonTestPlayers();
        ServerPlayer first = persistentPlayers.create(level, new BlockPos(16, 80, 16), WorldTier.HAVEN);
        ServerPlayer second = persistentPlayers.create(level, new BlockPos(18, 80, 16), WorldTier.PINNACLE);
        BlockPos pos = new BlockPos(16, 79, 18);
        level.getChunkAt(pos); level.setBlock(pos.below(), Blocks.STONE.defaultBlockState(), 2);
        ResourceLocation blockId = ResourceLocation.parse("lootr:lootr_chest");
        check(BuiltInRegistries.BLOCK.containsKey(blockId), "Pinned Lootr chest is registered");
        level.setBlock(pos, BuiltInRegistries.BLOCK.get(blockId).defaultBlockState(), 3);
        var blockEntity = level.getBlockEntity(pos);
        check(blockEntity instanceof RandomizableContainerBlockEntity && blockEntity instanceof ILootrBlockEntity,
                "Fixture uses a real Lootr block entity");
        var container = (RandomizableContainerBlockEntity) blockEntity;
        container.setLootTable(ResourceKey.create(Registries.LOOT_TABLE,
                ResourceLocation.parse("qm:chest/dimensional_dungeons/citadel_treasure")), 123456789L);
        var provider = (ILootrBlockEntity) blockEntity;
        ILootrInventory firstInventory = null;
        for (ServerPlayer player : List.of(first, second)) {
            ILootrInventory inventory = LootrAPI.getInventory(provider, player);
            check(inventory != null && !inventory.isEmpty(), "Real Lootr first-fill creates personal loot");
            if (firstInventory == null) firstInventory = inventory;
            else check(inventory != firstInventory, "Two players never share the same Lootr inventory instance");
            long gems = 0, gear = 0;
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack item = inventory.getItem(i);
                check(!marked(item), "Lootr fill strips the temporary prize marker");
                if (GemInstance.unsocketed(item).isValidUnsocketed()) { validateCombat(item, player.getData(Apoth.Attachments.WORLD_TIER), player.getLuck()); gems++; }
                else if (AffixHelper.getRarity(item).isBound()) { validateCombat(item, player.getData(Apoth.Attachments.WORLD_TIER), player.getLuck()); gear++; }
            }
            check(gems >= 1 && gear >= 1, "Actual Lootr opener receives the guaranteed gem and gear pair");
            CompoundTag saved = inventory.saveToTag(server.registryAccess());
            check(LootrAPI.getInventory(provider, player) == inventory
                    && saved.equals(inventory.saveToTag(server.registryAccess())), "Reopening Lootr does not reroll or duplicate prizes");
            PERSONAL.add(new PersonalChest(provider, player, inventory, saved));
        }
    }

    static Map<String, Object> afterReload(MinecraftServer server) {
        int checked = 0;
        try {
            for (PersonalChest entry : PERSONAL) {
                ILootrInventory inventory = LootrAPI.getInventory(entry.provider(), entry.player());
                check(entry.saved().equals(inventory.saveToTag(server.registryAccess())),
                        "Actual resource reload and reopen preserve personal Lootr contents");
                checked++;
            }
            check(checked == 2, "Both opener inventories survived reload");
            return Map.of("personal_inventories_retained", checked, "rerolls", 0, "assertions", assertions);
        } finally {
            if (persistentPlayers != null) persistentPlayers.close();
            PERSONAL.clear(); persistentPlayers = null;
        }
    }

    static void validateCombat(ItemStack stack, WorldTier tier, float luck) {
        GemInstance gem = GemInstance.unsocketed(stack);
        if (gem.isValidUnsocketed()) {
            check(gem.getGem().weights().getWeight(tier, luck) > 0, "Native gem kind is available at opener World Tier");
            check(gem.purity().weights().getWeight(tier, luck) > 0 || gem.purity() == gem.getGem().getMinPurity(),
                    "Native gem purity obeys World Tier, with its own documented minimum");
        } else {
            var rarity = AffixHelper.getRarity(stack);
            check(rarity.isBound(), "Combat gear has a resolved native Apotheosis rarity");
            check(rarity.get().weights().getWeight(tier, luck) > 0, "Native gear rarity is allowed by opener World Tier");
            check(AffixHelper.hasAffixes(stack), "Combat gear contains actual native affix effects");
        }
    }

    static LootTable table(MinecraftServer server, String name) {
        var id = ResourceLocation.parse(name);
        LootTable table = server.reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, id));
        check(table != LootTable.EMPTY && id.equals(table.getLootTableId()), "Loaded loot table codec succeeds: " + name);
        return table;
    }

    static LootParams params(ServerLevel level, ServerPlayer player, Vec3 origin) {
        return new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, origin)
                .withOptionalParameter(LootContextParams.THIS_ENTITY, player).withLuck(player.getLuck())
                .create(LootContextParamSets.CHEST);
    }

    private static boolean marked(ItemStack item) {
        return item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean(MARKER);
    }
    private static ItemStack unmarked(ItemStack item) {
        ItemStack result = item.copy();
        CompoundTag tag = result.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.remove(MARKER);
        if (tag.isEmpty()) result.remove(DataComponents.CUSTOM_DATA); else result.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return result;
    }
    private static void check(boolean condition, String message) {
        assertions++;
        if (!condition) throw new AssertionError(message);
    }
}
