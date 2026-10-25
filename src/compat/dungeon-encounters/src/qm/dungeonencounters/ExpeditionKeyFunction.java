package qm.dungeonencounters;

import com.catastrophe573.dimdungeons.item.ItemPortalKey;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

/** Issues a fresh native address only when a treasure table selects an expedition key. */
public final class ExpeditionKeyFunction extends LootItemConditionalFunction {
    public static final MapCodec<ExpeditionKeyFunction> CODEC = RecordCodecBuilder.mapCodec(instance ->
        commonFields(instance).and(Codec.intRange(4, 5).fieldOf("theme")
            .forGetter(function -> function.theme)).apply(instance, ExpeditionKeyFunction::new));
    private final int theme;

    public ExpeditionKeyFunction(List<LootItemCondition> conditions, int theme) {
        super(conditions);
        this.theme = theme;
    }

    @Override public LootItemFunctionType<ExpeditionKeyFunction> getType() {
        return QmDungeonEncounters.EXPEDITION_KEY.get();
    }

    @Override protected ItemStack run(ItemStack stack, LootContext context) {
        if (!(stack.getItem() instanceof ItemPortalKey key) || key.isActivated(stack)) return stack;
        // Native activation allocates/registers the address and all key components.
        // Reapplying a loot function to an already activated key never changes its destination.
        ItemStack result = stack.copyWithCount(1);
        key.activateKeyLevel1(context.getLevel().getServer(), result, theme);
        String name = theme == QmExpeditions.CITADEL ? "citadel" : "abyss";
        result.set(DataComponents.CUSTOM_NAME, Component.translatable("item.qm_dungeon_encounters." + name + "_key"));
        result.set(DataComponents.LORE, new ItemLore(List.of(
            Component.translatable("item.qm_dungeon_encounters." + name + "_key.description"),
            Component.translatable("item.qm_dungeon_encounters.expedition_key.portal"))));
        return result;
    }
}
