package qm.lucky;

import java.util.ArrayList;
import java.util.List;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.common.Mod;

/** Exact native milestones and bounded personal history; no random machine registry. */
@Mod(QmLuckySupport.ID)
public final class QmLuckySupport {
    public static final String ID = "qm_lucky_support";
    private static final String KEY = "qm_lucky_v042";
    private static final long[] QUESTS = {0L, 0x513011E734464E5DL, 0x5130115E1ED084F6L,
        0x513011AA869D0649L, 0x5130117F2402AE8AL, 0x513011AEA75E4401L, 0x5130113398623BDEL};
    private static final String[] ITEMS = {"", "kubejs:industrial_chassis", "kubejs:precision_mechanism",
        "kubejs:dimensional_matrix", "kubejs:stellar_alloy", "kubejs:mythic_circuit", "kubejs:emc_core"};
    private static final String[] NAMES = {"Початок", "Промислове шасі", "Точний механізм",
        "Міжвимірна матриця", "Зоряний сплав", "Міфічний контур", "Ядро EMC"};

    public QmLuckySupport() {}

    /** Typed dispatch avoids Rhino's BlockPos/Vec3/ChunkPos overload ambiguity. */
    public static boolean openingSpace(ServerLevel level, BlockPos position) {
        if (position.getY() - 12 < level.getMinBuildHeight()
                || position.getY() + 23 >= level.getMaxBuildHeight()) return false;
        BlockPos low = position.offset(-10, -12, -10);
        BlockPos high = position.offset(10, 23, 10);
        return level.getWorldBorder().isWithinBounds(low)
                && level.getWorldBorder().isWithinBounds(high) && level.hasChunksAt(low, high);
    }

    /** Native Commands.performPrefixedCommand is void in 1.21.1. Brigadier needs an int. */
    public static int runEvent(CommandSourceStack source, int tier, String family, int variant) {
        if (tier < 0 || tier > 6 || variant < 0 || variant > 3
                || family == null || !family.matches("[a-z][a-z0-9_]{0,31}")) return 0;
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("qm", "mayhem/v042/t"
                + tier + "/" + family + "_v" + variant);
        if (source.getServer().getFunctions().get(id).isEmpty()) return 0;
        source.getServer().getCommands().performPrefixedCommand(
                source.withPermission(2).withSuppressedOutput(), "function " + id);
        return 1;
    }

    private static CompoundTag data(ServerPlayer player) {
        CompoundTag root = player.getPersistentData();
        if (!root.contains(Player.PERSISTED_NBT_TAG, 10)) root.put(Player.PERSISTED_NBT_TAG, new CompoundTag());
        CompoundTag persisted = root.getCompound(Player.PERSISTED_NBT_TAG);
        if (!persisted.contains(KEY, 10)) persisted.put(KEY, new CompoundTag());
        return persisted.getCompound(KEY);
    }

    public static int completedMilestoneTier(ServerQuestFile file, TeamData team) {
        for (int tier = 6; tier >= 1; tier--) {
            Quest quest = file == null ? null : file.getQuest(QUESTS[tier]);
            if (team != null && quest != null && team.isCompleted(quest)) return tier;
        }
        return 0;
    }

    public static int highestUnlocked(ServerPlayer player) {
        CompoundTag saved = data(player);
        int unlocked = Math.max(0, Math.min(6, saved.getInt("earnedTier")));
        ServerQuestFile file = ServerQuestFile.INSTANCE;
        TeamData team = file == null ? null : file.getTeamData(player).orElse(null);
        unlocked = Math.max(unlocked, completedMilestoneTier(file, team));
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (stack.isEmpty()) continue;
            String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
            for (int tier = 6; tier > unlocked; tier--) {
                if (ITEMS[tier].equals(id)) { unlocked = tier; break; }
            }
        }
        if (saved.getInt("earnedTier") < unlocked) saved.putInt("earnedTier", unlocked);
        return unlocked;
    }

    public static boolean canOpen(ServerPlayer player, int tier) {
        return tier >= 0 && tier <= 6 && highestUnlocked(player) >= tier;
    }
    public static String requirement(int tier) {
        if (tier < 1 || tier > 6) return "";
        return "Куб етапу " + tier + " зачекає: спочатку «" + NAMES[tier] + "» (" + ITEMS[tier]
            + "). Куб цілий; збережи його на потім.";
    }
    public static long stageQuestId(int tier) { return tier >= 0 && tier <= 6 ? QUESTS[tier] : 0L; }
    public static String stageItemId(int tier) { return tier >= 0 && tier <= 6 ? ITEMS[tier] : ""; }
    public static String recentFamilies(ServerPlayer player) { return data(player).getString("recentFamilies"); }
    public static void recordFamily(ServerPlayer player, String family) {
        if (family == null || !family.matches("[a-z][a-z0-9_]{0,31}")) return;
        List<String> recent = new ArrayList<>(); recent.add(family);
        for (String old : recentFamilies(player).split("\\|")) {
            if (recent.size() >= 4) break;
            if (!old.equals(family) && old.matches("[a-z][a-z0-9_]{0,31}")) recent.add(old);
        }
        data(player).putString("recentFamilies", String.join("|", recent));
    }
}
