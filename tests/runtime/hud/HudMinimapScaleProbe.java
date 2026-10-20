package qm.hudprobe;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import qm.questui.client.HudGeometry;
import xaero.hud.minimap.module.MinimapSession;

/**
 * Executes the pinned Xaero sizing call graph after applying the production
 * Mixins. Only window/config/session acquisition is replaced by fixtures; the
 * scale, AUTO-size, effective-size, processor and session-width instructions
 * come from the real dependency JARs. No sizing formula is copied into a test.
 *
 * Unrelated methods/initializers are omitted so this can run without starting
 * Minecraft/OpenGL. This is a behavioral compatibility gate, not a client
 * rendering test. Put the old adapter before new classes on the classpath and
 * pass --expect-overflow to verify the 0.4.3 negative control.
 */
public final class HudMinimapScaleProbe {
    private static final String GUI = "xaero.hud.gui.util.GuiUtils";
    private static final String CONFIG = "xaero.hud.minimap.config.util.MinimapConfigClientUtils";
    private static final String SESSION = "xaero.hud.minimap.module.MinimapSession";
    private static final String PROCESSOR = "xaero.common.minimap.MinimapProcessor";
    private static final String OPTIONS = "xaero.hud.minimap.common.config.option.MinimapProfiledConfigOptions";
    private static final String MANAGER = "xaero.lib.client.config.ClientConfigManager";
    private static final String MOD = "xaero.common.HudMod";
    private static final String MC = "net.minecraft.client.Minecraft";
    private static final String COORDINATOR = "qm.questui.client.HudCoordinator";
    private static final String HUD_CONFIG = "qm.questui.HudConfig";

    // These classes model external state only. Their bytecode is renamed to
    // the dependency API names in an isolated child-first classloader.
    public static class Option { public Object value; }
    public static class RangeOption extends Option {
        public List<Integer> getValidValues() { return List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11); }
    }
    public static class IndexedOption extends Option {}
    public static class Options {
        public static final RangeOption UI_SCALE = new RangeOption();
        public static final IndexedOption SIZE = new IndexedOption();
    }
    public static class Manager {
        public Object getEffective(Option option) { return option.value; }
    }
    public static class Channel {
        public final Manager manager = new Manager();
        public Manager getClientConfigManager() { return manager; }
    }
    public static class Mod {
        public static final Mod INSTANCE = new Mod();
        public final Channel channel = new Channel();
        public Channel getHudConfigs() { return channel; }
    }
    public static class Window {
        public int width, height;
        public double guiScale;
        public int getWidth() { return width; }
        public int getHeight() { return height; }
        public double getGuiScale() { return guiScale; }
        public int getGuiScaledWidth() { return (int)Math.ceil(width / guiScale); }
        public int getGuiScaledHeight() { return (int)Math.ceil(height / guiScale); }
    }
    public static class Minecraft {
        public static final Minecraft INSTANCE = new Minecraft();
        public final Window window = new Window();
        public static Minecraft getInstance() { return INSTANCE; }
        public Window getWindow() { return window; }
    }
    public static class Coordinator {
        public static MinimapSession currentSession;
        public static MinimapSession session() { return currentSession; }
        public static int width() { return Minecraft.getInstance().getWindow().getGuiScaledWidth(); }
        public static int height() { return Minecraft.getInstance().getWindow().getGuiScaledHeight(); }
    }
    public static class HudConfig {
        public static boolean coordinated = true;
        public static boolean enabled() { return coordinated; }
    }

    private static byte[] resource(String name) throws Exception {
        try (InputStream stream = HudMinimapScaleProbe.class.getClassLoader()
                .getResourceAsStream(name.replace('.', '/') + ".class")) {
            if (stream == null) throw new AssertionError("Missing native class " + name);
            return stream.readAllBytes();
        }
    }

    private static byte[] boundary(Class<?> fixture, Map<String, String> names) throws Exception {
        var writer = new ClassWriter(0);
        new ClassReader(resource(fixture.getName())).accept(
                new ClassRemapper(writer, new SimpleRemapper(names)) {
                    // The renamed fixtures are independent public classes.
                    @Override public void visitNestHost(String name) {}
                    @Override public void visitInnerClass(String name, String outer, String inner, int access) {}
                }, ClassReader.SKIP_DEBUG);
        return writer.toByteArray();
    }

    /** Retain the actual instructions, including all injected handler methods. */
    private static byte[] methods(byte[] original, Set<String> keep, Map<String, String> fields) {
        var node = new ClassNode();
        new ClassReader(original).accept(node, ClassReader.SKIP_DEBUG);
        node.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER;
        node.superName = "java/lang/Object";
        node.signature = null;
        node.interfaces.clear();
        node.innerClasses.clear();
        node.nestHostClass = null;
        node.nestMembers = null;
        node.visibleAnnotations = null;
        node.invisibleAnnotations = null;
        node.methods.removeIf(method -> !keep.contains(method.name) && !method.name.contains("qm$"));
        node.fields.clear();
        fields.forEach((name, descriptor) -> node.fields.add(
                new FieldNode(Opcodes.ACC_PUBLIC, name, descriptor, null, null)));
        var constructor = new MethodNode(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        constructor.visitCode();
        constructor.visitVarInsn(Opcodes.ALOAD, 0);
        constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        constructor.visitInsn(Opcodes.RETURN);
        constructor.visitMaxs(1, 1);
        constructor.visitEnd();
        node.methods.add(constructor);
        var writer = new ClassWriter(0);
        node.accept(writer);
        return writer.toByteArray();
    }

    private static final class Loader extends ClassLoader {
        private final Map<String, byte[]> classes;
        Loader(Map<String, byte[]> classes) {
            super(HudMinimapScaleProbe.class.getClassLoader());
            this.classes = classes;
        }
        @Override protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            synchronized (getClassLoadingLock(name)) {
                Class<?> type = findLoadedClass(name);
                if (type == null) {
                    byte[] bytes = classes.get(name);
                    type = bytes == null ? super.loadClass(name, false) : defineClass(name, bytes, 0, bytes.length);
                }
                if (resolve) resolveClass(type);
                return type;
            }
        }
    }

    private static final class Fixture {
        final Loader loader;
        final Object session, processor, window, manager, uiOption, sizeOption;
        Fixture(Map<String, byte[]> classes) throws Exception {
            loader = new Loader(classes);
            Object minecraft = type(MC).getMethod("getInstance").invoke(null);
            window = minecraft.getClass().getMethod("getWindow").invoke(minecraft);
            Object mod = type(MOD).getField("INSTANCE").get(null);
            Object channel = mod.getClass().getMethod("getHudConfigs").invoke(mod);
            manager = channel.getClass().getMethod("getClientConfigManager").invoke(channel);
            session = type(SESSION).getConstructor().newInstance();
            processor = type(PROCESSOR).getConstructor().newInstance();
            set(session, "modMain", mod);
            set(session, "processor", processor);
            uiOption = type(OPTIONS).getField("UI_SCALE").get(null);
            sizeOption = type(OPTIONS).getField("SIZE").get(null);
        }
        Class<?> type(String name) throws Exception { return loader.loadClass(name); }
        void configure(int w, int h, int gui, int ui, int size, int state) throws Exception {
            set(window, "width", w); set(window, "height", h); set(window, "guiScale", (double)gui);
            set(uiOption, "value", ui); set(sizeOption, "value", size);
            type(HUD_CONFIG).getField("coordinated").set(null, state != 1);
            type(COORDINATOR).getField("currentSession").set(null, state == 2 ? null : session);
            set(processor, "enlargedMap", state == 3);
        }
        float scale() throws Exception {
            return (Float)type(GUI).getMethod("getMinimapScale", type(MANAGER)).invoke(null, manager);
        }
        int size() throws Exception {
            return (Integer)type(CONFIG).getMethod("getEffectiveMinimapSize").invoke(null);
        }
        int width(int gui) throws Exception {
            return (Integer)type(SESSION).getMethod("getWidth", double.class).invoke(session, (double)gui);
        }
        void unchanged(int ui, int size) throws Exception {
            check(uiOption.getClass().getField("value").get(uiOption).equals(ui), "UI scale config mutated");
            check(sizeOption.getClass().getField("value").get(sizeOption).equals(size), "Map size config mutated");
        }
    }

    private static void set(Object receiver, String name, Object value) throws Exception {
        receiver.getClass().getField(name).set(receiver, value);
    }
    private static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
    }
    private static Throwable cause(Throwable error) {
        while (error instanceof InvocationTargetException) error = error.getCause();
        return error;
    }

    public static void main(String[] args) throws Exception {
        MixinBootstrap.init();
        MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);
        MixinEnvironment.getDefaultEnvironment().setOption(MixinEnvironment.Option.DEBUG_INJECTORS, true);
        Mixins.addConfiguration("qm_quest_ui.mixins.json");
        var constructor = Class.forName("org.spongepowered.asm.mixin.transformer.MixinTransformer")
                .getDeclaredConstructor();
        constructor.setAccessible(true);
        var transformer = (IMixinTransformer)constructor.newInstance();

        Map<String, String> names = new HashMap<>();
        Map<Class<?>, String> fixtures = Map.ofEntries(
                Map.entry(Option.class, "xaero.lib.common.config.option.ConfigOption"),
                Map.entry(RangeOption.class, "xaero.lib.common.config.option.RangeConfigOption"),
                Map.entry(IndexedOption.class, "xaero.lib.common.config.option.IndexedConfigOption"),
                Map.entry(Options.class, OPTIONS), Map.entry(Manager.class, MANAGER),
                Map.entry(Channel.class, "xaero.lib.common.config.channel.ConfigChannel"),
                Map.entry(Mod.class, MOD), Map.entry(Window.class, "com.mojang.blaze3d.platform.Window"),
                Map.entry(Minecraft.class, MC), Map.entry(Coordinator.class, COORDINATOR),
                Map.entry(HudConfig.class, HUD_CONFIG));
        fixtures.forEach((fixture, name) -> names.put(fixture.getName().replace('.', '/'), name.replace('.', '/')));
        Map<String, byte[]> original = new HashMap<>();
        for (var entry : fixtures.entrySet()) original.put(entry.getValue(), boundary(entry.getKey(), names));
        original.put(SESSION, methods(resource(SESSION),
                Set.of("getWidth", "getHeight", "getConfiguredWidth", "getProcessor"),
                Map.of("modMain", "Lxaero/common/HudMod;", "processor", "Lxaero/common/minimap/MinimapProcessor;")));
        original.put(PROCESSOR, methods(resource(PROCESSOR), Set.of("getMinimapSize", "isEnlargedMap"),
                Map.of("enlargedMap", "Z")));
        Map<String, Set<String>> selected = Map.of(
                GUI, Set.of("getMinimapScale", "getUIScale", "getAutoUIScale"),
                CONFIG, Set.of("getAutoMinimapSize", "getEffectiveMinimapSize"));
        Map<String, byte[]> patched = new HashMap<>(original);
        for (var entry : selected.entrySet()) {
            byte[] bytes = resource(entry.getKey());
            original.put(entry.getKey(), methods(bytes, entry.getValue(), Map.of()));
            byte[] transformed = transformer.transformClassBytes(entry.getKey(), entry.getKey(), bytes);
            patched.put(entry.getKey(), methods(transformed, entry.getValue(), Map.of()));
        }
        Fixture nativeApi = new Fixture(original);
        Fixture coordinated = new Fixture(patched);

        // Exact failure mode from the supplied crash: AUTO size on the first
        // getWidth call, with a new active session and HUD coordination enabled.
        coordinated.configure(1280, 720, 2, 1, 0, 0);
        try {
            coordinated.width(2);
            check(args.length == 0 || !args[0].equals("--expect-overflow"),
                    "Negative control did not reproduce the old StackOverflowError");
        } catch (InvocationTargetException error) {
            if (!(cause(error) instanceof StackOverflowError)) throw error;
            if (args.length > 0 && args[0].equals("--expect-overflow")) {
                System.out.println("HUD_MINIMAP_OLD_CRASH_REPRODUCED StackOverflowError AUTO first width");
                return;
            }
            throw new AssertionError("AUTO minimap sizing recursively calls the scale hook (StackOverflowError)");
        }

        int cases = 0;
        int[][] windows = {{640, 360}, {1280, 720}, {1920, 1080}, {3840, 2160}, {1080, 1920}, {3440, 1440}};
        for (int[] window : windows) for (int gui : new int[]{1, 2, 3, 4, 6}) {
            if (window[0] / gui < 320 || window[1] / gui < 180) continue;
            for (int ui : new int[]{1, 2, 4, 10, 11}) for (int size : new int[]{0, 55, 100, 250}) {
                for (int state = 0; state < 4; state++) {
                    nativeApi.configure(window[0], window[1], gui, ui, size, state);
                    coordinated.configure(window[0], window[1], gui, ui, size, state);
                    float rawScale = nativeApi.scale();
                    int rawSize = nativeApi.size();
                    int rawWidth = nativeApi.width(gui);
                    float scale = coordinated.scale();
                    int width = coordinated.width(gui);
                    String label = "window=" + window[0] + "x" + window[1] + " gui=" + gui
                            + " ui=" + ui + " size=" + size + " state=" + state;
                    check(Float.isFinite(scale) && scale > 0, "Invalid scale: " + label);
                    check(coordinated.size() == rawSize, "AUTO/fixed native size changed: " + label);
                    check(width > 0 && width <= rawWidth, "Map grew or vanished: " + label);
                    if (state == 0) {
                        int cap = HudGeometry.mapSide((int)Math.ceil((double)window[0] / gui),
                                (int)Math.ceil((double)window[1] / gui));
                        check(width <= cap, "Rendered map exceeds its reserved HUD area: " + label);
                        if (rawWidth <= cap) check(scale == rawScale, "Already-fitting map was scaled: " + label);
                    } else {
                        check(scale == rawScale && width == rawWidth, "Bypass changed native behavior: " + label);
                    }
                    // Alternate dimension/scale query order, as native renderer
                    // and other HUD consumers do, after every window resize.
                    for (int repeat = 0; repeat < 5; repeat++) {
                        check(coordinated.size() == rawSize && coordinated.scale() == scale
                                && coordinated.width(gui) == width, "Unstable/reentrant sizing: " + label);
                    }
                    coordinated.unchanged(ui, size);
                    cases++;
                }
            }
        }
        System.out.println("HUD_MINIMAP_SCALE_OK cases=" + cases
                + " native=AUTO,fixed ui=AUTO,fixed,GUI states=active,disabled,no-session,enlarged resize=6-windows");
    }
}
