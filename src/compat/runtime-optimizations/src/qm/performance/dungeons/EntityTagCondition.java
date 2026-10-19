package qm.performance.dungeons;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import qm.performance.QmRuntimeOptimizations;

/** Read a command tag directly, without saving the entity and its attachments. */
public record EntityTagCondition(String tag) implements LootItemCondition {
    public static final MapCodec<EntityTagCondition> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(Codec.STRING.fieldOf("tag").forGetter(EntityTagCondition::tag))
            .apply(instance, EntityTagCondition::new));

    @Override public LootItemConditionType getType() {
        return QmRuntimeOptimizations.ENTITY_TAG.get();
    }

    @Override public Set<LootContextParam<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.THIS_ENTITY);
    }

    @Override public boolean test(LootContext context) {
        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        return entity != null && entity.getTags().contains(tag);
    }
}
