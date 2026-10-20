package qm.villageguard.mixin;

import net.minecraft.server.level.ServerLevel;
import org.millenaire.building.BuildingInstance;
import org.millenaire.village.Village;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import qm.villageguard.GeometryChangeSource;
import qm.villageguard.GeometryListeners;

@Mixin(value = Village.class, remap = false)
public abstract class VillageGeometryMixin implements GeometryChangeSource {
    @Unique private final GeometryListeners qm$geometryListeners = new GeometryListeners();

    @Override public void qm$addGeometryListener(Runnable listener) { qm$geometryListeners.add(listener); }
    @Override public void qm$removeGeometryListener(Runnable listener) { qm$geometryListeners.remove(listener); }
    @Override public int qm$geometryListenerCount() { return qm$geometryListeners.size(); }

    @Inject(method = "addBuilding(Lorg/millenaire/building/BuildingInstance;)V", at = @At("RETURN"), require = 1)
    private void qm$buildingAdded(BuildingInstance building, CallbackInfo ci) {
        qm$geometryListeners.fire();
    }

    @Inject(method = "forgetBuilding(Lnet/minecraft/server/level/ServerLevel;Lorg/millenaire/building/BuildingInstance;)Z",
            at = @At("RETURN"), require = 1)
    private void qm$buildingRemoved(ServerLevel level, BuildingInstance building, CallbackInfoReturnable<Boolean> ci) {
        if (ci.getReturnValueZ()) qm$geometryListeners.fire();
    }
}
