package qm.villageguard.mixin;

import org.millenaire.village.Village;
import org.millenaire.village.VillageId;
import org.millenaire.village.VillageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qm.villageguard.VillageIndexAccess;
import qm.villageguard.VillageProtectionIndex;

@Mixin(value = VillageManager.class, remap = false)
public abstract class VillageManagerIndexMixin implements VillageIndexAccess {
    @Unique private VillageProtectionIndex qm$protectionIndex;

    @Override
    public VillageProtectionIndex qm$getVillageProtectionIndex() {
        if (qm$protectionIndex == null) {
            qm$protectionIndex = new VillageProtectionIndex((VillageManager) (Object) this);
        }
        return qm$protectionIndex;
    }

    @Override
    public VillageProtectionIndex qm$peekVillageProtectionIndex() {
        return qm$protectionIndex;
    }

    @Override
    public void qm$unloadVillageProtectionIndex() {
        if (qm$protectionIndex != null) {
            qm$protectionIndex.close();
            qm$protectionIndex = null;
        }
    }

    @Inject(method = "addVillage(Lorg/millenaire/village/Village;)V", at = @At("RETURN"), require = 1)
    private void qm$villageAdded(Village village, CallbackInfo ci) {
        if (qm$protectionIndex != null) qm$protectionIndex.villageAdded(village);
    }

    @Inject(method = "removeVillage(Lorg/millenaire/village/VillageId;)V", at = @At("RETURN"), require = 1)
    private void qm$villageRemoved(VillageId id, CallbackInfo ci) {
        if (qm$protectionIndex != null) qm$protectionIndex.villageRemoved(id);
    }

    @Inject(method = "clear()V", at = @At("RETURN"), require = 1)
    private void qm$managerCleared(CallbackInfo ci) {
        if (qm$protectionIndex != null) qm$protectionIndex.managerCleared();
    }
}
