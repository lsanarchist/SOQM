package qm.dungeonencounters;

import java.util.List;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Server-authoritative selection and bounded additions; existing encounters keep their saved roll. */
public final class EncounterConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue ENABLE_DIVINE;
    public static final ModConfigSpec.BooleanValue ENABLE_DIVINE_BOSSES;
    public static final ModConfigSpec.BooleanValue ENABLE_DUNGEONS;
    public static final ModConfigSpec.DoubleValue DIVINE_CHANCE;
    public static final ModConfigSpec.DoubleValue DIVINE_BOSS_CHANCE;
    public static final ModConfigSpec.DoubleValue DUNGEON_CHANCE;
    public static final ModConfigSpec.DoubleValue HEALTH_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue DAMAGE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue ADVANCED_HEALTH_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue ADVANCED_DAMAGE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue CITADEL_HEALTH_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue CITADEL_DAMAGE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue ABYSS_HEALTH_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue ABYSS_DAMAGE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue BOSS_HEALTH_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue BOSS_DAMAGE_MULTIPLIER;
    public static final ModConfigSpec.IntValue ACTIVATION_RADIUS;
    public static final ModConfigSpec.IntValue ACTIVATION_BUDGET;
    public static final ModConfigSpec.IntValue ACTIVATION_INTERVAL;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> AFFIXES;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> EXCLUDED_ENTITY_IDS;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment("New conversions only. Existing encounters retain their saved ability and tier.").push("selection");
        ENABLE_DIVINE = builder.define("enableDivineElites", true);
        ENABLE_DIVINE_BOSSES = builder.define("enableDivineBossElites", true);
        ENABLE_DUNGEONS = builder.define("enableDungeonElites", true);
        DIVINE_CHANCE = builder.comment("Chance for a newly spawned ordinary hostile DivineRPG mob in every dimension. Bosses use their separate chance; pets and NPCs are excluded.")
            .defineInRange("divineEliteChance", 0.12, 0.0, 0.35);
        DIVINE_BOSS_CHANCE = builder.comment("Separate chance for native DivineRPG bosses outside DD, including flying and aquatic bosses, in every dimension.")
            .defineInRange("divineBossEliteChance", 0.85, 0.0, 1.0);
        DUNGEON_CHANCE = builder.comment("Chance for an eligible hostile placed by Dimensional Dungeons, after its own scaling.")
            .defineInRange("dungeonEliteChance", 1.0, 0.0, 1.0);
        EXCLUDED_ENTITY_IDS = builder.defineListAllowEmpty("excludedEntityIds",
            List.of("minecraft:wither", "minecraft:warden", "minecraft:elder_guardian", "artifacts:mimic"),
            value -> value instanceof String text && text.contains(":"));
        builder.pop();

        builder.comment("Additions use named permanent modifiers. Original bases, equipment, AI and drops are preserved.").push("strength");
        HEALTH_MULTIPLIER = builder.defineInRange("healthMultiplier", 1.5, 1.0, 4.0);
        DAMAGE_MULTIPLIER = builder.defineInRange("damageMultiplier", 1.15, 1.0, 2.0);
        ADVANCED_HEALTH_MULTIPLIER = builder.defineInRange("advancedHealthMultiplier", 2.0, 1.0, 4.0);
        ADVANCED_DAMAGE_MULTIPLIER = builder.defineInRange("advancedDamageMultiplier", 1.25, 1.0, 2.0);
        CITADEL_HEALTH_MULTIPLIER = builder.defineInRange("citadelHealthMultiplier", 2.5, 1.0, 4.0);
        CITADEL_DAMAGE_MULTIPLIER = builder.defineInRange("citadelDamageMultiplier", 1.35, 1.0, 2.0);
        ABYSS_HEALTH_MULTIPLIER = builder.defineInRange("abyssHealthMultiplier", 3.0, 1.0, 4.0);
        ABYSS_DAMAGE_MULTIPLIER = builder.defineInRange("abyssDamageMultiplier", 1.5, 1.0, 2.0);
        BOSS_HEALTH_MULTIPLIER = builder.defineInRange("bossHealthMultiplier", 1.25, 1.0, 4.0);
        BOSS_DAMAGE_MULTIPLIER = builder.defineInRange("bossDamageMultiplier", 1.10, 1.0, 2.0);
        AFFIXES = builder.comment("One real Elite Mobs ability per selected mob. Default pool avoids explosions, lightning, teleportation and summoned hordes in puzzle rooms.")
            .defineListAllowEmpty("affixes", List.of("brutal", "infernal_touch", "vampiric", "withering",
                "shielded", "reflective", "regenerating", "frostbound", "abyssal"),
                value -> value instanceof String text && !text.isBlank());
        builder.pop();

        builder.comment("Only nearby loaded entities are queried, once per player interval; no global entity scan or chunk loading.").push("activation");
        ACTIVATION_RADIUS = builder.defineInRange("radius", 48, 16, 96);
        ACTIVATION_BUDGET = builder.defineInRange("perPlayerPass", 8, 1, 32);
        ACTIVATION_INTERVAL = builder.defineInRange("intervalTicks", 20, 10, 200);
        builder.pop();
        SPEC = builder.build();
    }

    private EncounterConfig() {}
}
