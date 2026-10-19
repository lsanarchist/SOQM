package qm.performance;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import qm.performance.dungeons.DungeonsEvents;
import qm.performance.dungeons.EntityTagCondition;
import qm.performance.guardian.GuardianLifecycle;

@Mod(QmRuntimeOptimizations.ID)
public final class QmRuntimeOptimizations {
    public static final String ID = "qm_runtime_optimizations";
    private static final DeferredRegister<LootItemConditionType> CONDITIONS =
        DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, ID);
    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> ENTITY_TAG =
        CONDITIONS.register("entity_tag", () -> new LootItemConditionType(EntityTagCondition.CODEC));

    public QmRuntimeOptimizations(IEventBus modBus) {
        CONDITIONS.register(modBus);
        GuardianLifecycle.register();
        DungeonsEvents.register();
    }
}
