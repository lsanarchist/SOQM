package qm.villageguard.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.millenaire.building.BuildingInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qm.villageguard.GeometryChangeSource;
import qm.villageguard.GeometryListeners;

@Mixin(value = BuildingInstance.class, remap = false)
public abstract class BuildingGeometryMixin implements GeometryChangeSource {
    @Unique private final GeometryListeners qm$geometryListeners = new GeometryListeners();

    @Override public void qm$addGeometryListener(Runnable listener) { qm$geometryListeners.add(listener); }
    @Override public void qm$removeGeometryListener(Runnable listener) { qm$geometryListeners.remove(listener); }
    @Override public int qm$geometryListenerCount() { return qm$geometryListeners.size(); }

    @Inject(method = "setOrigin(Lnet/minecraft/core/BlockPos;)V", at = @At("RETURN"), require = 1)
    private void qm$originChanged(BlockPos origin, CallbackInfo ci) {
        qm$geometryListeners.fire();
    }

    @Inject(method = "startUpgrade(Lnet/minecraft/resources/ResourceLocation;I)V", at = @At("RETURN"), require = 1)
    private void qm$planChanged(ResourceLocation plan, int level, CallbackInfo ci) {
        qm$geometryListeners.fire();
    }
}
