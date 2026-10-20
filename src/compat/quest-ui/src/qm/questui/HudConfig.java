package qm.questui;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Common-side definition only; rendering classes remain client-only mixins. */
public final class HudConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue ENABLED;
    static {
        var builder = new ModConfigSpec.Builder();
        ENABLED = builder.comment("Coordinate the pack's minimap, suit, mining and quest HUDs.",
                "Applies at render time, including existing FTB local/ overrides.",
                "Disable to restore the individual mods' saved positions and scales.")
                .define("coordinateHud", true);
        SPEC = builder.build();
    }
    private HudConfig() {}
    public static boolean enabled() { return SPEC.isLoaded() && ENABLED.get(); }
}
