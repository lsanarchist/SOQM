package qm.questauto;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

/** Capped evidence only. FTB Quests remains the authoritative completion store. */
public final class AutomationState extends SavedData {
    public static final Factory<AutomationState> FACTORY = new Factory<>(AutomationState::new, AutomationState::load, null);
    private final Map<UUID, Map<String, Integer>> teams = new HashMap<>();

    public static AutomationState get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(FACTORY, "qm_quest_automation");
    }

    public static int cap(String signal) {
        return switch (signal) {
            case "mill_building_64" -> 64;
            case "mek_iron_32" -> 32;
            default -> 1;
        };
    }

    public int get(UUID team, String signal) {
        Map<String, Integer> row = teams.get(team);
        return row == null ? 0 : row.getOrDefault(signal, 0);
    }

    public boolean record(UUID team, String signal, int amount) {
        if (amount <= 0) return false;
        int previous = get(team, signal);
        int next = (int) Math.min(cap(signal), (long) previous + amount);
        if (next == previous) return false;
        teams.computeIfAbsent(team, ignored -> new HashMap<>()).put(signal, next);
        setDirty();
        return true;
    }

    public static AutomationState load(CompoundTag tag, HolderLookup.Provider registries) {
        AutomationState state = new AutomationState();
        CompoundTag all = tag.getCompound("teams");
        for (String key : all.getAllKeys()) {
            UUID id;
            try { id = UUID.fromString(key); } catch (IllegalArgumentException invalid) { continue; }
            CompoundTag row = all.getCompound(key);
            Map<String, Integer> counters = new HashMap<>();
            for (String signal : row.getAllKeys()) {
                if (!QmQuestAutomation.SIGNAL_NAMES.contains(signal)) continue;
                int value = Math.clamp(row.getInt(signal), 0, cap(signal));
                if (value > 0) counters.put(signal, value);
            }
            if (!counters.isEmpty()) state.teams.put(id, counters);
        }
        return state;
    }

    @Override public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag all = new CompoundTag();
        teams.forEach((id, counters) -> {
            CompoundTag row = new CompoundTag();
            counters.forEach(row::putInt);
            all.put(id.toString(), row);
        });
        tag.putInt("version", 1);
        tag.put("teams", all);
        return tag;
    }
}
