package qm.hudprobe;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Executes the compiled shortcut guard and pinned FTB max-Z method against
 * headless state boundaries. The cumulative draw/tooltip offsets are read from
 * pinned bytecode with checks on its pose contract. No client, network or OpenGL
 * calls execute.
 */
public final class RewardUiRegressionProbe {
    private static final String UI = "dev/ftb/mods/ftblibrary/ui/";
    private static final String QUESTS = "dev/ftb/mods/ftbquests/client/gui/quests/";
    private static final String CLIENT = "qm/questui/client/";
    private static final String POSE = "com/mojang/blaze3d/vertex/PoseStack";

    public static class Widget {}
    public static class TextBox extends Widget {
        public boolean focused;
        public boolean isFocused() { return focused; }
    }
    public static class Modal extends Widget {
        public int extra;
        public int getExtraZlevel() { return extra; }
    }
    public static class Catalogue extends Modal {}
    public static class Quest {
        public long id = 1;
        public long getId() { return id; }
    }
    public static class View extends Modal {
        public Quest quest = new Quest();
        public Quest getViewedQuest() { return quest; }
    }
    public interface Access {
        Widget qm$getFocusedWidget();
        Deque<Modal> qm$getModalPanels();
    }
    public static class Screen implements Access {
        public final Deque<Modal> modalPanels = new ArrayDeque<>();
        public final View viewQuestPanel = new View();
        public Widget focused;
        public int opens;
        public Widget qm$getFocusedWidget() { return focused; }
        public Deque<Modal> qm$getModalPanels() { return modalPanels; }
    }
    public static class Modifiers {
        public boolean control = true, shift = true, alt;
        public boolean control() { return control; }
        public boolean shift() { return shift; }
        public boolean alt() { return alt; }
    }
    public static class Key {
        public int code = 86;
        public final Modifiers modifiers = new Modifiers();
        public boolean is(int value) { return code == value; }
    }
    public static class Data {
        public Map<Long, List<Long>> quests() { return Map.of(1L, List.of(2L)); }
    }
    public static class CatalogueResource {
        public static Data data() { return new Data(); }
    }
    public static void opened(Screen screen, Quest quest) {
        screen.opens++;
        screen.modalPanels.addFirst(new Catalogue());
    }

    private static void check(boolean value, String reason) {
        if (!value) throw new AssertionError(reason);
    }
    private static String type(Class<?> type) { return type.getName().replace('.', '/'); }
    private static ClassNode bytecode(String name) throws Exception {
        try (var stream = RewardUiRegressionProbe.class.getClassLoader().getResourceAsStream(name + ".class")) {
            check(stream != null, "Missing pinned/compiled UI class " + name);
            var node = new ClassNode();
            new ClassReader(stream).accept(node, ClassReader.SKIP_DEBUG);
            return node;
        }
    }
    private static MethodNode method(String owner, String name) throws Exception {
        var matches = bytecode(owner).methods.stream().filter(m -> m.name.equals(name)).toList();
        check(matches.size() == 1, "Review changed native method " + owner + "." + name);
        return matches.getFirst();
    }
    private static AbstractInsnNode previous(AbstractInsnNode instruction) {
        do { instruction = instruction.getPrevious(); } while (instruction != null && instruction.getOpcode() < 0);
        return instruction;
    }
    private static AbstractInsnNode next(AbstractInsnNode instruction) {
        do { instruction = instruction.getNext(); } while (instruction != null && instruction.getOpcode() < 0);
        return instruction;
    }
    private static Double number(AbstractInsnNode instruction) {
        if (instruction instanceof LdcInsnNode load && load.cst instanceof Number n) return n.doubleValue();
        if (instruction instanceof IntInsnNode push) return (double)push.operand;
        int opcode = instruction.getOpcode();
        if (opcode >= Opcodes.ICONST_M1 && opcode <= Opcodes.ICONST_5) return (double)(opcode - Opcodes.ICONST_0);
        if (opcode >= Opcodes.FCONST_0 && opcode <= Opcodes.FCONST_2) return (double)(opcode - Opcodes.FCONST_0);
        return null;
    }
    private static int constructorZ(String owner) throws Exception {
        List<Integer> found = new ArrayList<>();
        for (var constructor : bytecode(owner).methods) if (constructor.name.equals("<init>")) {
            for (var instruction : constructor.instructions) if (instruction instanceof MethodInsnNode call
                    && call.name.equals("setExtraZlevel") && call.desc.equals("(I)V")) {
                Double z = number(previous(call));
                check(z != null, "Review nonconstant modal Z in " + owner);
                found.add(z.intValue());
            }
        }
        check(found.size() == 1, "Review modal constructor Z in " + owner);
        return found.getFirst();
    }
    private static List<Double> floatZTranslations(MethodNode method) {
        List<Double> values = new ArrayList<>();
        for (var instruction : method.instructions) if (instruction instanceof MethodInsnNode call
                && call.owner.equals(POSE) && call.name.equals("translate") && call.desc.equals("(FFF)V")) {
            Double value = number(previous(call));
            if (value != null) values.add(value);
        }
        return values;
    }
    private static long calls(MethodNode method, String name) {
        long count = 0;
        for (var instruction : method.instructions) if (instruction instanceof MethodInsnNode call && call.name.equals(name)) count++;
        return count;
    }

    private static Class<?> harness() throws Exception {
        var node = bytecode(CLIENT + "QuestRewardPreview");
        node.methods.removeIf(m -> !m.name.equals("handle"));
        check(node.methods.size() == 1, "Missing compiled shortcut guard");
        node.fields.clear(); node.innerClasses.clear(); node.nestMembers = null;
        node.nestHostClass = null; node.visibleAnnotations = null; node.invisibleAnnotations = null;
        var open = new MethodNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "open",
                "(L" + QUESTS + "QuestScreen;Ldev/ftb/mods/ftbquests/quest/Quest;)V", null, null);
        open.visitCode(); open.visitVarInsn(Opcodes.ALOAD, 0); open.visitVarInsn(Opcodes.ALOAD, 1);
        open.visitMethodInsn(Opcodes.INVOKESTATIC, type(RewardUiRegressionProbe.class), "opened",
                "(L" + type(Screen.class) + ";L" + type(Quest.class) + ";)V", false);
        open.visitInsn(Opcodes.RETURN); open.visitMaxs(2, 2); open.visitEnd(); node.methods.add(open);
        // Keep the real stream/max instructions, replacing only their state owner.
        var max = method(UI + "BaseScreen", "getMaxZLevel");
        max.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;
        max.desc = "(L" + UI + "BaseScreen;)I";
        node.methods.add(max);
        Map<String, String> names = Map.ofEntries(
                Map.entry(CLIENT + "QuestRewardPreview", "qm/hudprobe/RewardUiBytecode"),
                Map.entry(QUESTS + "QuestScreen", type(Screen.class)),
                Map.entry(QUESTS + "ViewQuestPanel", type(View.class)),
                Map.entry("dev/ftb/mods/ftbquests/quest/Quest", type(Quest.class)),
                Map.entry("qm/questui/mixin/BaseScreenAccess", type(Access.class)),
                Map.entry(UI + "BaseScreen", type(Screen.class)), Map.entry(UI + "Widget", type(Widget.class)),
                Map.entry(UI + "ModalPanel", type(Modal.class)), Map.entry(UI + "TextBox", type(TextBox.class)),
                Map.entry(UI + "input/Key", type(Key.class)), Map.entry(UI + "input/KeyModifiers", type(Modifiers.class)),
                Map.entry(CLIENT + "RewardCataloguePanel", type(Catalogue.class)),
                Map.entry(CLIENT + "RewardCatalogue", type(CatalogueResource.class)),
                Map.entry(CLIENT + "RewardCatalogueData", type(Data.class)));
        var writer = new ClassWriter(0);
        node.accept(new ClassRemapper(writer, new SimpleRemapper(names)));
        byte[] bytes = writer.toByteArray();
        return new ClassLoader(RewardUiRegressionProbe.class.getClassLoader()) {
            Class<?> define() { return defineClass("qm.hudprobe.RewardUiBytecode", bytes, 0, bytes.length); }
        }.define();
    }
    private static Screen detail() {
        Screen screen = new Screen(); screen.modalPanels.addFirst(screen.viewQuestPanel); return screen;
    }
    private static boolean handle(Method handler, Screen screen, Key key) throws Exception {
        return (boolean)handler.invoke(null, screen, key);
    }
    private static void shortcutScopes(Class<?> harness) throws Exception {
        Method handler = harness.getMethod("handle", Screen.class, Key.class);
        Screen open = detail();
        check(handle(handler, open, new Key()) && open.opens == 1, "Quest detail must open its reward catalogue");
        check(handle(handler, open, new Key()) && open.opens == 1, "Repeated shortcut must be consumed without reopening or editor paste");
        for (int invalid = 0; invalid < 4; invalid++) {
            Key key = new Key();
            if (invalid == 0) key.code = 67;
            if (invalid == 1) key.modifiers.control = false;
            if (invalid == 2) key.modifiers.shift = false;
            if (invalid == 3) key.modifiers.alt = true;
            Screen screen = detail();
            check(!handle(handler, screen, key) && screen.opens == 0, "Unrelated key combinations must remain native");
        }
        Screen editor = new Screen();
        check(!handle(handler, editor, new Key()) && editor.opens == 0, "Editor/map paste must remain native outside quest detail");
        Screen other = detail(); other.modalPanels.addFirst(new Modal());
        check(!handle(handler, other, new Key()) && other.opens == 0, "Other modal panels retain their input");
        Screen text = detail(); TextBox field = new TextBox(); field.focused = true; text.focused = field;
        check(!handle(handler, text, new Key()) && text.opens == 0, "Focused text fields retain paste input");
        field.focused = false;
        check(handle(handler, text, new Key()) && text.opens == 1, "An unfocused text field must not block the shortcut");
        Screen missing = detail(); missing.viewQuestPanel.quest = null;
        check(!handle(handler, missing, new Key()), "An empty quest detail must not open a catalogue");
        Screen unknown = detail(); unknown.viewQuestPanel.quest.id = 3;
        check(!handle(handler, unknown, new Key()), "An unmapped quest must retain native input");
    }
    private static void tooltipLayers(Class<?> harness) throws Exception {
        MethodNode draw = method(UI + "BaseScreen", "draw");
        var offsets = floatZTranslations(draw);
        check(offsets.size() == 2 && calls(draw, "pushPose") == 1 && calls(draw, "popPose") == 1
                && calls(draw, "descendingIterator") == 1 && calls(draw, "getExtraZlevel") == 1,
                "Review changed native cumulative modal drawing contract");
        MethodNode wrapper = method(UI + "ScreenWrapper", "render");
        Double padding = null;
        for (var instruction : wrapper.instructions) if (instruction instanceof MethodInsnNode call && call.name.equals("getMaxZLevel")) {
            padding = number(next(call));
            check(next(next(call)).getOpcode() == Opcodes.IADD, "Review native tooltip maximum-Z addition");
        }
        check(padding != null, "Missing native tooltip offset");
        var tooltipOffsets = floatZTranslations(method("net/minecraft/client/gui/GuiGraphics", "renderTooltipInternal"));
        check(tooltipOffsets.size() == 1, "Review changed native tooltip pose");
        int questExtra = constructorZ(QUESTS + "ViewQuestPanel");
        int catalogueExtra = constructorZ(CLIENT + "RewardCataloguePanel");
        Screen screen = detail(); screen.viewQuestPanel.extra = questExtra;
        Catalogue catalogue = new Catalogue(); catalogue.extra = catalogueExtra; screen.modalPanels.addFirst(catalogue);
        Method max = harness.getMethod("getMaxZLevel", Screen.class);
        double questZ = offsets.get(0) + questExtra;
        double catalogueZ = questZ + offsets.get(1) + catalogueExtra;
        double tooltipAnchor = (int)max.invoke(null, screen) + padding;
        double tooltipZ = tooltipAnchor + tooltipOffsets.getFirst();
        check(catalogueZ > questZ && tooltipAnchor > catalogueZ && tooltipZ > catalogueZ,
                "Catalogue must be above quest detail and below native tooltips: catalogue=" + catalogueZ + " tooltip=" + tooltipZ);
        // Negative control reproduces the reviewed absolute-Z defect.
        catalogue.extra = 1500;
        double badCatalogueZ = questZ + offsets.get(1) + catalogue.extra;
        double badTooltipZ = (int)max.invoke(null, screen) + padding + tooltipOffsets.getFirst();
        check(badTooltipZ < badCatalogueZ, "Regression fixture must expose the old hidden-tooltip bug");
        System.out.println("REWARD_UI_LAYERS_OK quest=" + questZ + " catalogue=" + catalogueZ + " tooltip=" + tooltipZ
                + " old_catalogue=" + badCatalogueZ + " old_tooltip=" + badTooltipZ);
    }
    public static void verify() throws Exception {
        Class<?> harness = harness();
        shortcutScopes(harness);
        tooltipLayers(harness);
        System.out.println("REWARD_UI_SHORTCUT_OK open,repeat,editor,text,other_modal,modifiers,missing_quest");
    }
}
