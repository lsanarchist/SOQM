package qm.villageguard.mixin;

import net.minecraft.resources.ResourceLocation;
import org.millenaire.building.BuildingPlan;
import org.millenaire.culture.ModCultures;
import org.millenaire.culture.VillageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qm.villageguard.VillageProtectionIndex;

@Mixin(value = ModCultures.class, remap = false)
public abstract class CultureGeometryMixin {
    @Inject(method = "registerBuildingPlan(Lorg/millenaire/building/BuildingPlan;)V", at = @At("RETURN"), require = 1)
    private static void qm$planRegistered(BuildingPlan plan, CallbackInfo ci) {
        VillageProtectionIndex.invalidateAll();
    }

    @Inject(method = "registerVillageType(Lorg/millenaire/culture/VillageType;)V", at = @At("RETURN"), require = 1)
    private static void qm$typeRegistered(VillageType type, CallbackInfo ci) {
        VillageProtectionIndex.invalidateAll();
    }

    @Inject(method = {"unregisterBuildingPlan(Lnet/minecraft/resources/ResourceLocation;)V",
            "unregisterVillageType(Lnet/minecraft/resources/ResourceLocation;)V",
            "unregisterCultureContent(Lnet/minecraft/resources/ResourceLocation;)V"}, at = @At("RETURN"), require = 1)
    private static void qm$contentRemoved(ResourceLocation id, CallbackInfo ci) {
        if (id != null) VillageProtectionIndex.invalidateAll();
    }

    @Inject(method = "clear()V", at = @At("RETURN"), require = 1)
    private static void qm$contentCleared(CallbackInfo ci) {
        VillageProtectionIndex.invalidateAll();
    }
}
