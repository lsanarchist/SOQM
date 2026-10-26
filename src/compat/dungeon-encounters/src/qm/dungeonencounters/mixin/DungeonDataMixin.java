package qm.dungeonencounters.mixin;

import com.catastrophe573.dimdungeons.dimension.DungeonData;
import com.catastrophe573.dimdungeons.structure.DungeonRoom;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import qm.dungeonencounters.QmExpeditions;

/** DD210 saves room.theme but omits it when loading both room maps. */
@Mixin(value = DungeonData.class, remap = false)
public abstract class DungeonDataMixin {
    @Shadow private ConcurrentHashMap<ChunkPos, DungeonRoom> roomMap;
    @Shadow private ConcurrentHashMap<ChunkPos, DungeonRoom> remainingBuilds;

    @Inject(method = "<init>(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;)V",
        at = @At("RETURN"), remap = false)
    private void qm$restoreAuthoredExpeditionThemes(CompoundTag saved, HolderLookup.Provider registries,
            CallbackInfo callback) {
        qm$restoreThemes(saved.getList("room_data", Tag.TAG_COMPOUND), roomMap);
        qm$restoreThemes(saved.getList("remaining_builds", Tag.TAG_COMPOUND), remainingBuilds);
    }

    @Unique
    private static void qm$restoreThemes(ListTag savedRooms, ConcurrentHashMap<ChunkPos, DungeonRoom> loadedRooms) {
        for (int i = 0; i < savedRooms.size(); i++) {
            CompoundTag saved = savedRooms.getCompound(i);
            if (!saved.contains("theme", Tag.TAG_INT)) continue;
            int theme = saved.getInt("theme");
            if (!QmExpeditions.isExpedition(theme)) continue;
            String structure = saved.getString("structure");
            String expectedPrefix = "qm:dimensional_dungeons/"
                + (theme == QmExpeditions.CITADEL ? "citadel/" : "abyss/");
            if (!structure.startsWith(expectedPrefix)) continue;
            DungeonRoom room = loadedRooms.get(new ChunkPos(saved.getInt("x"), saved.getInt("z")));
            if (room != null && structure.equals(room.structure)) room.theme = theme;
        }
    }
}
