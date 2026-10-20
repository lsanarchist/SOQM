package qm.villageguard.mixin;

import java.util.Map;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = DimensionDataStorage.class, remap = false)
public interface VillageSavedDataCacheAccess {
    @Accessor("cache")
    Map<String, SavedData> qm$villageGuardSavedDataCache();
}
