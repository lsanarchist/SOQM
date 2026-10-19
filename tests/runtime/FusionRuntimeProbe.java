package soqm.validation;

import java.util.Map;
import mekanism.generators.common.content.fusion.FusionReactorMultiblockData;
import mekanism.generators.common.tile.fusion.TileEntityFusionReactorBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.EntityBlock;
import qm.questauto.QmQuestAutomation;

/** Native data/predicate fixture; no reactor structure or real player is edited. */
public final class FusionRuntimeProbe {
    private FusionRuntimeProbe() {}

    public static void run(MinecraftServer server, Map<String, Object> results) {
        var state = BuiltInRegistries.BLOCK.get(ResourceLocation.parse("mekanismgenerators:fusion_reactor_controller"))
                .defaultBlockState();
        var entity = ((EntityBlock) state.getBlock()).newBlockEntity(new BlockPos(80, 200, 80), state);
        check(entity instanceof TileEntityFusionReactorBlock, "Native fusion controller fixture resolves");
        var controller = (TileEntityFusionReactorBlock) entity;
        controller.setLevel(server.overworld());
        var data = new FusionReactorMultiblockData(controller);
        data.setFormedForce(true);
        data.setBurning(true);
        data.heatCapacitor.setHeat(1_000_000);
        data.updateTemperatures();
        data.energyContainer.setEnergy(0);
        check(data.getPassiveGeneration(false, true) > 0 && data.energyContainer.getEnergy() == 0,
                "Native reactor has positive current generation and an empty output buffer");
        check(QmQuestAutomation.fusionGenerating(data), "Exported output must not hide an operating reactor");

        var clock = new QmQuestAutomation.ReactorClock();
        for (int tick = 0; tick <= 200; tick += 20) {
            check(clock.sample(tick, QmQuestAutomation.fusionGenerating(data), false, true) == (tick == 200),
                    "Native empty-buffer state must complete only after all 200 observed ticks");
        }
        results.put("fusion_native_zero_buffer_positive_generation", true);
        results.put("fusion_native_200_tick_continuity", true);

        data.energyContainer.setEnergy(1_000_000);
        data.setBurning(false);
        check(!QmQuestAutomation.fusionGenerating(data), "Residual energy must not count as active burning");
        data.setBurning(true);
        data.setFormedForce(false);
        check(!QmQuestAutomation.fusionGenerating(data), "Unformed reactor must not count");
        data.setFormedForce(true);
        data.heatCapacitor.setHeat(0);
        data.updateTemperatures();
        check(data.getPassiveGeneration(false, true) == 0 && !QmQuestAutomation.fusionGenerating(data),
                "Zero native generation must not count even with energy in the buffer");
        results.put("fusion_native_stopped_unformed_zero_generation_rejected", true);
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
