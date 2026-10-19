package qm.performance.mixin.dungeons;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import qm.performance.dungeons.DungeonsDataOptimizations;

@Mixin(RegistryDataLoader.class)
public abstract class EnchantmentDataMixin {
    @ModifyExpressionValue(method = "loadElementFromResource", at = @At(value = "INVOKE",
        target = "Lcom/google/gson/JsonParser;parseReader(Ljava/io/Reader;)Lcom/google/gson/JsonElement;"),
        require = 1, allow = 1, remap = false)
    private static JsonElement soqm$directBossTag(JsonElement data, @Local(argsOnly = true) ResourceKey<?> key) {
        return key.isFor(Registries.ENCHANTMENT) && key.location().equals(DungeonsDataOptimizations.BOSS_BEHAVIOUR)
            ? DungeonsDataOptimizations.enchantment(data) : data;
    }
}
