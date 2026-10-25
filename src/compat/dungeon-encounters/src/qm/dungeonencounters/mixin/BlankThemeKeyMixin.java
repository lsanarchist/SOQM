package qm.dungeonencounters.mixin;

import com.catastrophe573.dimdungeons.item.ItemBlankThemeKey;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import qm.dungeonencounters.QmExpeditions;

@Mixin(value = ItemBlankThemeKey.class, remap = false)
public abstract class BlankThemeKeyMixin {
    @Redirect(method = "performActivationRitual", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"), remap = false, require = 1)
    private int qm$nativeRandomThemesOnly(List<?> themes) {
        return QmExpeditions.randomThemeCount(themes.size());
    }
}
