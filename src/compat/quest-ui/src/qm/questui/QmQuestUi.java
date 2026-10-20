package qm.questui;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;

/** The common entry point deliberately has no client class references. */
@Mod("qm_quest_ui")
public final class QmQuestUi {
    public QmQuestUi(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, HudConfig.SPEC, "soqm-hud-client.toml");
    }
}
