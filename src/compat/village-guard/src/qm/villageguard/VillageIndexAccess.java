package qm.villageguard;

/** The native manager owns its index; no static registry retains worlds. */
public interface VillageIndexAccess {
    VillageProtectionIndex qm$getVillageProtectionIndex();
    VillageProtectionIndex qm$peekVillageProtectionIndex();
    void qm$unloadVillageProtectionIndex();
}
