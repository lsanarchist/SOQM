package qm.zoomdefaults.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Zume 1.2.2's NeoForge key enum creates zume.zoom with GLFW key 90 (Z).
 * F6 (295) is free in this pack, while C, Z, X and Left Alt are occupied.
 * Replace the constructed default, before Options loads saved key mappings.
 * Saved user choices and all unrelated defaults therefore remain authoritative.
 * Pseudo makes this optional target inert when Zume is absent.
 */
@Pseudo
@Mixin(targets = "zume.T", remap = false)
public abstract class ZumeDefaultKeyMixin {
    @ModifyConstant(
        method = "<clinit>",
        constant = @Constant(intValue = 90),
        require = 1
    )
    private static int qmZoomDefault(int upstreamDefault) {
        return upstreamDefault == 90 ? 295 : upstreamDefault;
    }
}
