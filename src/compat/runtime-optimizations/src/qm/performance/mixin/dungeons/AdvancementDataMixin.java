package qm.performance.mixin.dungeons;

import com.google.gson.JsonElement;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qm.performance.dungeons.DungeonsDataOptimizations;

@Mixin(ServerAdvancementManager.class)
public abstract class AdvancementDataMixin {
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
        at = @At("HEAD"), require = 1, allow = 1, remap = false)
    private void soqm$eventDrivenRoot(Map<ResourceLocation, JsonElement> data, ResourceManager manager,
                                     ProfilerFiller profiler, CallbackInfo callback) {
        DungeonsDataOptimizations.advancements(data);
    }
}
