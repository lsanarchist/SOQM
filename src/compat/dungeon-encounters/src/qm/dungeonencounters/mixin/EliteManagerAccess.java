package qm.dungeonencounters.mixin;

import java.util.Map;
import java.util.UUID;
import net.tonicarter.elitemobs.EliteMobManager;
import net.tonicarter.elitemobs.affixes.AffixState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** The pinned native ability map, without opting into replacement spawning or bonus loot. */
@Mixin(value = EliteMobManager.class, remap = false)
public interface EliteManagerAccess {
    @Accessor("AFFIX_STATES")
    static Map<UUID, AffixState> qm$affixStates() { throw new AssertionError("Mixin not applied"); }
}
