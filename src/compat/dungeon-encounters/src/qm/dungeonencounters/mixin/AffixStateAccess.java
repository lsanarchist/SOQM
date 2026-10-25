package qm.dungeonencounters.mixin;

import java.util.Map;
import java.util.Set;
import net.tonicarter.elitemobs.affixes.AffixState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AffixState.class, remap = false)
public interface AffixStateAccess {
    @Accessor("cooldowns") Map<String, Integer> qm$cooldowns();
    @Accessor("firedOnce") Set<String> qm$firedOnce();
}
