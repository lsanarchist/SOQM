package qm.chancelifecycle;

import chanceCubes.rewards.biodomeGen.BioDomeGen;
import chanceCubes.rewards.defaultRewards.BossBaseReward;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

// Java's protected nested-type access is available to a subclass. This
// abstract bridge is never instantiated and contains no copied upstream class.
public abstract class BossAccess extends BossBaseReward {
    protected BossAccess() { super("qm_unused_bridge"); }
    public static BioDomeGen dome(Object state) { return ((BattleWrapper)state).domeGen; }
    public static void abort(BossBaseReward reward, ServerLevel level, BlockPos pos,
                             Player player, Object state) {
        BattleWrapper battle = (BattleWrapper)state;
        for (Entity entity : new ArrayList<>(battle.trackedEntities)) {
            if (!(entity instanceof Player) && entity.level() == level && entity.isAlive()) entity.discard();
        }
        for (Entity entity : new ArrayList<>(battle.trackedSubEntities)) {
            if (!(entity instanceof Player) && entity.level() == level && entity.isAlive()) entity.discard();
        }
        reward.endBossfight(false, level, pos, player, battle);
        battle.trackedEntities.clear();
        battle.trackedSubEntities.clear();
        battle.trackedPlayers.clear();
    }
}

