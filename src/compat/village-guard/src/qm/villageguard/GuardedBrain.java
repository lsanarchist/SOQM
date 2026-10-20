package qm.villageguard;

import net.minecraft.world.entity.Mob;

/** Owner binding lives on the Brain; no global Brain/entity registry is retained. */
public interface GuardedBrain {
    void qm$bindOwner(Mob owner);
}
