package qm.hudprobe;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.nio.file.Files;
import java.nio.file.Path;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.launch.platform.container.ContainerHandleVirtual;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.service.*;

/** Real pinned Sponge transformer, with a bytecode-only host instead of an OpenGL game. */
public final class HudMixinProbe {
    public static final class Service extends MixinServiceAbstract implements IClassProvider, IClassBytecodeProvider {
        public String getName() { return "SOQM-HUD-bytecode-probe"; }
        public boolean isValid() { return true; }
        public MixinEnvironment.Phase getInitialPhase() { return MixinEnvironment.Phase.DEFAULT; }
        public IClassProvider getClassProvider() { return this; }
        public IClassBytecodeProvider getBytecodeProvider() { return this; }
        public ITransformerProvider getTransformerProvider() { return null; }
        public IClassTracker getClassTracker() { return null; }
        public IMixinAuditTrail getAuditTrail() { return null; }
        public Collection<String> getPlatformAgents() { return List.of(); }
        public IContainerHandle getPrimaryContainer() { return new ContainerHandleVirtual("SOQM-HUD-probe"); }
        public InputStream getResourceAsStream(String name) { return getClass().getClassLoader().getResourceAsStream(name); }
        public URL[] getClassPath() { return new URL[0]; }
        public Class<?> findClass(String name) throws ClassNotFoundException { return findClass(name, false); }
        public Class<?> findClass(String name, boolean init) throws ClassNotFoundException { return Class.forName(name, init, getClass().getClassLoader()); }
        public Class<?> findAgentClass(String name, boolean init) throws ClassNotFoundException { return findClass(name, init); }
        public ClassNode getClassNode(String name) throws ClassNotFoundException, IOException { return getClassNode(name, false, 0); }
        public ClassNode getClassNode(String name, boolean transform) throws ClassNotFoundException, IOException { return getClassNode(name, transform, 0); }
        public ClassNode getClassNode(String name, boolean transform, int flags) throws ClassNotFoundException, IOException {
            try (var stream = getResourceAsStream(name.replace('.', '/') + ".class")) {
                if (stream == null) throw new ClassNotFoundException(name);
                var node = new ClassNode(); new ClassReader(stream).accept(node, flags); return node;
            }
        }
    }
    public static final class Blackboard implements IGlobalPropertyService {
        private record Key(String name) implements IPropertyKey {}
        private final Map<IPropertyKey,Object> values = new HashMap<>();
        public IPropertyKey resolveKey(String name) { return new Key(name); }
        @SuppressWarnings("unchecked") public <T> T getProperty(IPropertyKey key) { return (T)values.get(key); }
        public void setProperty(IPropertyKey key, Object value) { values.put(key, value); }
        public <T> T getProperty(IPropertyKey key, T fallback) { T value = getProperty(key); return value == null ? fallback : value; }
        public String getPropertyString(IPropertyKey key, String fallback) { return getProperty(key, fallback).toString(); }
    }

    public static void main(String[] args) throws Exception {
        MixinBootstrap.init();
        MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);
        MixinEnvironment.getDefaultEnvironment().setOption(MixinEnvironment.Option.DEBUG_INJECTORS, true);
        Mixins.addConfiguration("qm_quest_ui.mixins.json");
        var type = Class.forName("org.spongepowered.asm.mixin.transformer.MixinTransformer");
        var constructor = type.getDeclaredConstructor(); constructor.setAccessible(true);
        var transformer = (IMixinTransformer)constructor.newInstance();
        String[] targets = {
            "xaero.hud.gui.util.GuiUtils", "xaero.hud.module.ModuleSession",
            "xaero.hud.minimap.module.MinimapRenderer", "xaero.hud.minimap.info.render.InfoDisplayRenderer",
            "dev.ftb.mods.ftbultimine.client.FTBUltimineClient", "earth.terrarium.adastra.client.screens.player.OverlayScreen",
            "mekanism.client.render.HUDRenderer", "snownee.jade.impl.ui.BoxElement",
            "dev.ftb.mods.ftbquests.client.PinnedQuestsTracker$RenderData",
            "dev.ftb.mods.ftbquests.client.PinnedQuestsTracker",
            "net.minecraft.client.gui.components.toasts.ToastComponent",
            "mcjty.rftoolsutility.client.RenderGameOverlayEventHandler",
            "dev.ftb.mods.ftblibrary.ui.BaseScreen", "dev.ftb.mods.ftbquests.client.gui.quests.QuestScreen",
            "dev.ftb.mods.ftbquests.quest.task.ItemTask"
        };
        int transformed = 0;
        for (String target : targets) {
            byte[] original;
            try (var stream = HudMixinProbe.class.getClassLoader().getResourceAsStream(target.replace('.', '/') + ".class")) {
                if (stream == null) throw new AssertionError("Missing native target " + target);
                original = stream.readAllBytes();
            }
            byte[] changed = transformer.transformClassBytes(target, target, original);
            if (java.util.Arrays.equals(original, changed)) throw new AssertionError("Mixin did not transform " + target);
            var node = new ClassNode(); new ClassReader(changed).accept(node, 0);
            if (node.methods.stream().noneMatch(method -> method.name.contains("qm$")))
                throw new AssertionError("No injected/accessor method in " + target);
            if (args.length > 0) {
                Path output = Path.of(args[0], target.replace('.', '/') + ".class");
                Files.createDirectories(output.getParent()); Files.write(output, changed);
            }
            transformed++;
            System.out.println("TRANSFORMED " + target);
        }
        System.out.println("HUD_MIXINS_OK targets=" + transformed);
    }
}
