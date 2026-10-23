package qm.foundryprobe;

import java.io.InputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.launch.platform.container.ContainerHandleVirtual;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.service.*;

/**
 * Runs the exact pinned Modern Foundry takeResult bytecode before and after the
 * production Mixin. Native CraftingInput positioning and CraftingContainerWrapper
 * are retained. Fixtures replace inventory storage, items, player, recipes and
 * event delivery; no crafting-consumption algorithm is duplicated in this test.
 * This is a bytecode/behavior compatibility gate, not a launched-game/UI test.
 */
public final class FoundryCraftingProbe {
    private static final String TARGET = "modernmods.modernfoundry.tables.block.entity.table.CraftingStationBlockEntity";
    private static final String BASE = "modernmods.modernfoundry.tables.block.entity.table.RetexturedTableBlockEntity";
    private static final String WRAPPER = "modernmods.modernfoundry.tables.block.entity.inventory.CraftingContainerWrapper";
    private static final String INPUT = "net.minecraft.world.item.crafting.CraftingInput";
    private static final String STACK = "net.minecraft.world.item.ItemStack";
    private static final String PLAYER = "net.minecraft.world.entity.player.Player";
    private static final String HOLDER = "net.minecraft.world.item.crafting.RecipeHolder";
    private static final String RECIPE = "net.minecraft.world.item.crafting.CraftingRecipe";
    private static final String CONTAINER = "net.minecraft.world.Container";
    private static final String TAKE = "(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;I)V";
    private static int assertions;

    public static final class Service extends MixinServiceAbstract implements IClassProvider, IClassBytecodeProvider {
        public String getName() { return "SOQM-Foundry-bytecode-probe"; }
        public boolean isValid() { return true; }
        public MixinEnvironment.Phase getInitialPhase() { return MixinEnvironment.Phase.DEFAULT; }
        public IClassProvider getClassProvider() { return this; }
        public IClassBytecodeProvider getBytecodeProvider() { return this; }
        public ITransformerProvider getTransformerProvider() { return null; }
        public IClassTracker getClassTracker() { return null; }
        public IMixinAuditTrail getAuditTrail() { return null; }
        public Collection<String> getPlatformAgents() { return List.of(); }
        public IContainerHandle getPrimaryContainer() { return new ContainerHandleVirtual("SOQM-Foundry-probe"); }
        public InputStream getResourceAsStream(String name) { return getClass().getClassLoader().getResourceAsStream(name); }
        public URL[] getClassPath() { return new URL[0]; }
        public Class<?> findClass(String name) throws ClassNotFoundException { return findClass(name, false); }
        public Class<?> findClass(String name, boolean init) throws ClassNotFoundException { return Class.forName(name, init, getClass().getClassLoader()); }
        public Class<?> findAgentClass(String name, boolean init) throws ClassNotFoundException { return findClass(name, init); }
        public ClassNode getClassNode(String name) throws ClassNotFoundException, IOException { return getClassNode(name, false, 0); }
        public ClassNode getClassNode(String name, boolean transform) throws ClassNotFoundException, IOException { return getClassNode(name, transform, 0); }
        public ClassNode getClassNode(String name, boolean transform, int flags) throws ClassNotFoundException, IOException {
            try (InputStream stream = getResourceAsStream(name.replace('.', '/') + ".class")) {
                if (stream == null) throw new ClassNotFoundException(name);
                ClassNode node = new ClassNode(); new ClassReader(stream).accept(node, flags); return node;
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

    // External-state fixtures, renamed to the native API names in a child loader.
    public interface Container {
        Stack getItem(int index); void setItem(int index, Stack stack);
        int getContainerSize(); boolean isEmpty();
        Stack removeItem(int index, int amount); Stack removeItemNoUpdate(int index);
        void setChanged(); boolean stillValid(Player player); void clearContent();
    }
    public static class Stack {
        public static final Stack EMPTY = new Stack("air", 0);
        public final String item; public int count;
        public Stack(String item, int count) { this.item = item; this.count = count; }
        public boolean isEmpty() { return count == 0 || item.equals("air"); }
        public int getCount() { return count; }
        public Stack copyWithCount(int count) { return new Stack(item, count); }
        public void grow(int count) { this.count += count; }
        public void shrink(int count) { this.count -= count; }
        public static boolean isSameItemSameComponents(Stack left, Stack right) { return left.item.equals(right.item); }
        public void onCraftedBy(Level level, Player player, int amount) { player.crafted += amount; }
    }
    public static class StackedContents {
        public void accountStack(Stack stack, int count) {}
        public void accountSimpleStack(Stack stack) {}
    }
    public static class Level {}
    public static class ItemEntity {}
    public static class Inventory {
        public boolean full; public final List<Stack> delivered = new ArrayList<>();
        public boolean add(Stack stack) {
            if (full) return false;
            delivered.add(stack.copyWithCount(stack.count)); stack.count = 0; return true;
        }
    }
    public static class Player {
        public int crafted, awards;
        public final Inventory inventory = new Inventory();
        public final List<Stack> dropped = new ArrayList<>();
        public int awardRecipes(Collection<?> recipes) { awards += recipes.size(); return recipes.size(); }
        public Inventory getInventory() { return inventory; }
        public ItemEntity drop(Stack stack, boolean random) { dropped.add(stack.copyWithCount(stack.count)); return null; }
    }
    public static class Base implements Container {
        public final Stack[] stacks = new Stack[9];
        public Level level = new Level();
        public final List<Integer> writes = new ArrayList<>();
        public Base() { Arrays.fill(stacks, Stack.EMPTY); }
        public Stack getItem(int index) { return stacks[index]; }
        public void setItem(int index, Stack stack) { writes.add(index); stacks[index] = stack; }
        public int getContainerSize() { return stacks.length; }
        public boolean isEmpty() { return Arrays.stream(stacks).allMatch(Stack::isEmpty); }
        public Stack removeItem(int index, int count) { throw new AssertionError("Unexpected removeItem path"); }
        public Stack removeItemNoUpdate(int index) { throw new AssertionError("Unexpected removeItemNoUpdate path"); }
        public void setChanged() {}
        public boolean stillValid(Player player) { return true; }
        public void clearContent() { Arrays.fill(stacks, Stack.EMPTY); }
    }
    public interface RecipeInput { Stack getItem(int index); int size(); boolean isEmpty(); }
    public static class Remaining<E> extends AbstractList<E> {
        private final List<E> values = new ArrayList<>();
        public E get(int index) { return values.get(index); }
        public int size() { return values.size(); }
        public E set(int index, E value) { return values.set(index, value); }
        public boolean add(E value) { return values.add(value); }
    }
    public interface Recipe { boolean isSpecial(); Remaining<Stack> getRemainingItems(RecipeInput input); }
    public interface CraftingRecipe extends Recipe {}
    public static class RecipeFixture implements CraftingRecipe {
        public String remainder = "air";
        public boolean isSpecial() { return false; }
        public Remaining<Stack> getRemainingItems(RecipeInput input) {
            Remaining<Stack> result = new Remaining<>();
            for (int i = 0; i < input.size(); i++) {
                Stack source = input.getItem(i);
                result.add(source.isEmpty() || remainder.equals("air") ? Stack.EMPTY
                    : new Stack(remainder.equals("same") ? source.item : remainder, 1));
            }
            return result;
        }
    }
    public static class Holder {
        public final Recipe recipe;
        public Holder(Recipe recipe) { this.recipe = recipe; }
        public Recipe value() { return recipe; }
    }
    public static class Hooks {
        public static Player craftingPlayer;
        public static void setCraftingPlayer(Player player) { craftingPlayer = player; }
    }
    public static class Events {
        public static int count;
        public static void firePlayerCraftingEvent(Player player, Stack result, Container inventory) { count++; }
    }

    private static byte[] resource(String name) throws IOException {
        try (InputStream stream = FoundryCraftingProbe.class.getClassLoader().getResourceAsStream(name.replace('.', '/') + ".class")) {
            if (stream == null) throw new AssertionError("Missing pinned class " + name);
            return stream.readAllBytes();
        }
    }
    private static byte[] boundary(Class<?> fixture, Map<String, String> names) throws Exception {
        ClassWriter writer = new ClassWriter(0);
        new ClassReader(resource(fixture.getName())).accept(new ClassRemapper(writer, new SimpleRemapper(names)) {
            @Override public void visitNestHost(String name) {}
            @Override public void visitInnerClass(String name, String outer, String inner, int access) {}
        }, ClassReader.SKIP_DEBUG);
        return writer.toByteArray();
    }
    /** Preserve takeResult and injected handlers verbatim; omit game bootstrap. */
    private static byte[] isolate(byte[] original) {
        ClassNode node = new ClassNode(); new ClassReader(original).accept(node, 0);
        node.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER;
        node.signature = null; node.interfaces.clear(); node.innerClasses.clear();
        node.nestHostClass = null; node.nestMembers = null;
        node.visibleAnnotations = null; node.invisibleAnnotations = null;
        node.methods.removeIf(m -> !m.name.equals("takeResult") && !m.name.contains("qm$") && !m.name.contains("soqm$"));
        node.fields.removeIf(f -> !f.name.equals("lastRecipe") && !f.name.equals("craftingInventory"));
        node.fields.forEach(f -> f.access = Opcodes.ACC_PUBLIC);
        MethodNode constructor = new MethodNode(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        constructor.visitCode(); constructor.visitVarInsn(Opcodes.ALOAD, 0);
        constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, BASE.replace('.', '/'), "<init>", "()V", false);
        constructor.visitInsn(Opcodes.RETURN); constructor.visitMaxs(1, 1); constructor.visitEnd();
        node.methods.add(constructor);
        ClassWriter writer = new ClassWriter(0); node.accept(writer); return writer.toByteArray();
    }
    private static final class Loader extends ClassLoader {
        private final Map<String, byte[]> classes;
        Loader(Map<String, byte[]> classes) { super(FoundryCraftingProbe.class.getClassLoader()); this.classes = classes; }
        @Override protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            synchronized (getClassLoadingLock(name)) {
                Class<?> type = findLoadedClass(name);
                if (type == null) {
                    byte[] bytes = classes.get(name);
                    type = bytes == null ? super.loadClass(name, false) : defineClass(name, bytes, 0, bytes.length);
                }
                if (resolve) resolveClass(type); return type;
            }
        }
    }
    private static Map<String, byte[]> fixtureClasses() throws Exception {
        Map<Class<?>,String> fixtures = Map.ofEntries(
            Map.entry(Container.class, CONTAINER), Map.entry(Stack.class, STACK),
            Map.entry(StackedContents.class, "net.minecraft.world.entity.player.StackedContents"),
            Map.entry(Level.class, "net.minecraft.world.level.Level"),
            Map.entry(ItemEntity.class, "net.minecraft.world.entity.item.ItemEntity"),
            Map.entry(Inventory.class, "net.minecraft.world.entity.player.Inventory"),
            Map.entry(Player.class, PLAYER), Map.entry(Base.class, BASE),
            Map.entry(RecipeInput.class, "net.minecraft.world.item.crafting.RecipeInput"),
            Map.entry(Remaining.class, "net.minecraft.core.NonNullList"),
            Map.entry(Recipe.class, "net.minecraft.world.item.crafting.Recipe"),
            Map.entry(CraftingRecipe.class, RECIPE), Map.entry(Holder.class, HOLDER),
            Map.entry(RecipeFixture.class, "qm.foundryprobe.fixture.Recipe"),
            Map.entry(Hooks.class, "modernmods.modernfoundry.compat.neoforged.neoforge.common.ForgeHooks"),
            Map.entry(Events.class, "modernmods.modernfoundry.compat.neoforged.neoforge.event.ForgeEventFactory"));
        Map<String,String> names = new HashMap<>();
        fixtures.forEach((fixture, name) -> names.put(fixture.getName().replace('.', '/'), name.replace('.', '/')));
        Map<String,byte[]> classes = new HashMap<>();
        for (var entry : fixtures.entrySet()) classes.put(entry.getValue(), boundary(entry.getKey(), names));
        // Native positioning, dimensions and wrapper accessors remain unmodified.
        for (String name : List.of(INPUT, INPUT + "$Positioned", WRAPPER,
                "net.minecraft.world.inventory.CraftingContainer", "net.minecraft.world.inventory.StackedContentsCompatible")) {
            classes.put(name, resource(name));
        }
        return classes;
    }
    private static final class Fixture {
        final Loader loader; final Object table, player, recipe; final Object[] stacks;
        Fixture(Map<String,byte[]> classes) throws Exception {
            loader = new Loader(classes);
            table = type(TARGET).getConstructor().newInstance();
            player = type(PLAYER).getConstructor().newInstance();
            recipe = type("qm.foundryprobe.fixture.Recipe").getConstructor().newInstance();
            Object holder = type(HOLDER).getConstructor(type("net.minecraft.world.item.crafting.Recipe")).newInstance(recipe);
            Object wrapper = type(WRAPPER).getConstructor(type(CONTAINER), int.class, int.class).newInstance(table, 3, 3);
            set(table, "lastRecipe", holder); set(table, "craftingInventory", wrapper);
            stacks = (Object[])table.getClass().getField("stacks").get(table);
        }
        Class<?> type(String name) throws Exception { return loader.loadClass(name); }
        Object stack(String id, int count) throws Exception { return type(STACK).getConstructor(String.class, int.class).newInstance(id, count); }
        void seed(int left, int top, int width, int height, int count) throws Exception {
            Object empty = type(STACK).getField("EMPTY").get(null); Arrays.fill(stacks, empty);
            for (int y = top; y < top + height; y++) for (int x = left; x < left + width; x++) stacks[x + 3 * y] = stack("sand", count);
        }
        void craft() throws Exception {
            type(TARGET).getMethod("takeResult", type(PLAYER), type(STACK), int.class).invoke(table, player, stack("sandstone", 1), 1);
        }
        int[] counts() throws Exception {
            int[] result = new int[9];
            for (int i = 0; i < 9; i++) result[i] = (Integer)stacks[i].getClass().getMethod("getCount").invoke(stacks[i]);
            return result;
        }
        String item(int index) throws Exception { return (String)stacks[index].getClass().getField("item").get(stacks[index]); }
        @SuppressWarnings("unchecked") List<Integer> writes() throws Exception { return (List<Integer>)table.getClass().getField("writes").get(table); }
        int crafted() throws Exception { return player.getClass().getField("crafted").getInt(player); }
        int events() throws Exception { return type("modernmods.modernfoundry.compat.neoforged.neoforge.event.ForgeEventFactory").getField("count").getInt(null); }
        Object inventory() throws Exception { return player.getClass().getMethod("getInventory").invoke(player); }
        int delivered(boolean dropped) throws Exception {
            Object owner = dropped ? player : inventory();
            return ((List<?>)owner.getClass().getField(dropped ? "dropped" : "delivered").get(owner)).size();
        }
    }
    private static void set(Object receiver, String name, Object value) throws Exception { receiver.getClass().getField(name).set(receiver, value); }
    private static void check(boolean condition, String message) { assertions++; if (!condition) throw new AssertionError(message); }
    private static void verifyWrites(byte[] bytes) {
        ClassNode node = new ClassNode(); new ClassReader(bytes).accept(node, 0);
        var method = node.methods.stream().filter(m -> m.name.equals("takeResult") && m.desc.equals(TAKE)).findFirst().orElseThrow();
        int stores = 0, handlers = 0;
        for (var instruction : method.instructions) if (instruction instanceof MethodInsnNode call) {
            if (call.owner.equals(TARGET.replace('.', '/')) && call.name.equals("setItem") && call.desc.equals("(ILnet/minecraft/world/item/ItemStack;)V")) stores++;
            if (call.owner.equals(TARGET.replace('.', '/')) && (call.name.contains("qm$") || call.name.contains("soqm$"))) handlers++;
        }
        check(stores == 3, "Expected exactly three native remainder write sites");
        check(handlers == 3, "Each native write must receive the injected physical index");
    }
    public static void main(String[] args) throws Exception {
        MixinBootstrap.init();
        MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.SERVER);
        MixinEnvironment.getDefaultEnvironment().setOption(MixinEnvironment.Option.DEBUG_INJECTORS, true);
        Mixins.addConfiguration("qm_foundry_compat.mixins.json");
        var constructor = Class.forName("org.spongepowered.asm.mixin.transformer.MixinTransformer").getDeclaredConstructor();
        constructor.setAccessible(true);
        var transformer = (IMixinTransformer)constructor.newInstance();
        com.llamalad7.mixinextras.MixinExtrasBootstrap.init();
        byte[] nativeBytes = resource(TARGET);
        byte[] fixedBytes = transformer.transformClassBytes(TARGET, TARGET, nativeBytes);
        check(!Arrays.equals(nativeBytes, fixedBytes), "Production Mixin did not transform the exact native class");
        verifyWrites(fixedBytes);
        System.out.println("FOUNDRY_MIXIN_TRANSFORM_OK native_write_sites=3 transformed_write_sites=3");
        if (args.length > 0) {
            Path output = Path.of(args[0], TARGET.replace('.', '/') + ".class");
            Files.createDirectories(output.getParent()); Files.write(output, fixedBytes);
        }
        Map<String,byte[]> baseline = fixtureClasses();
        baseline.put(TARGET, isolate(nativeBytes));
        Map<String,byte[]> patched = new HashMap<>(baseline); patched.put(TARGET, isolate(fixedBytes));

        Fixture old = new Fixture(baseline); old.seed(1, 1, 2, 2, 16); old.craft();
        check(Arrays.equals(old.counts(), new int[]{15,15,15,15,16,16,0,16,16}), "Pinned native bytecode must reproduce the supplied screenshot");
        check(old.crafted() == 1 && old.events() == 1, "Negative control still fires native craft hooks once");
        Fixture oldSingle = new Fixture(baseline); oldSingle.seed(1, 1, 2, 2, 1);
        for (int repeat = 0; repeat < 4; repeat++) oldSingle.craft();
        check(Arrays.stream(oldSingle.counts()).sum() == 4 && oldSingle.crafted() == 4, "Pinned native single-item case must reproduce non-consuming crafts");
        System.out.println("FOUNDRY_OLD_DUPLICATION_REPRODUCED grid=" + Arrays.toString(old.counts()) + " sand=124 sandstone=1 input_sand=64");

        int cases = 0;
        for (int width = 1; width <= 3; width++) for (int height = 1; height <= 3; height++) {
            for (int top = 0; top <= 3 - height; top++) for (int left = 0; left <= 3 - width; left++) {
                for (int count : new int[]{1, 2, 16, 64}) for (String remainder : new String[]{"air", "same", "bucket"}) {
                    for (boolean full : new boolean[]{false, true}) {
                        Fixture fixed = new Fixture(patched); fixed.seed(left, top, width, height, count);
                        set(fixed.recipe, "remainder", remainder); set(fixed.inventory(), "full", full);
                        fixed.craft();
                        int expectedDelivery = remainder.equals("bucket") && count > 1 ? width * height : 0;
                        int[] after = fixed.counts();
                        List<Integer> expectedWrites = new ArrayList<>();
                        for (int y = 0; y < 3; y++) for (int x = 0; x < 3; x++) {
                            int index = x + y * 3; boolean occupied = x >= left && x < left + width && y >= top && y < top + height;
                            int expected = occupied ? count - 1 + (remainder.equals("same") || remainder.equals("bucket") && count == 1 ? 1 : 0) : 0;
                            check(after[index] == expected, "Wrong remaining count at " + index + " shape=" + width + "x" + height + " offset=" + left + "," + top + " remainder=" + remainder + " count=" + count);
                            if (occupied) expectedWrites.add(index);
                            if (occupied && count == 1 && remainder.equals("bucket")) check(fixed.item(index).equals("bucket"), "Bucket remainder replaced the wrong slot");
                        }
                        check(fixed.writes().equals(expectedWrites), "All writes must remain in the occupied physical recipe cells");
                        check(fixed.delivered(false) == (full ? 0 : expectedDelivery), "Incorrect remainder inventory delivery");
                        check(fixed.delivered(true) == (full ? expectedDelivery : 0), "Incorrect remainder drop with full inventory");
                        check(fixed.crafted() == 1 && fixed.events() == 1, "Craft hooks must still fire exactly once");
                        check(fixed.type("modernmods.modernfoundry.compat.neoforged.neoforge.common.ForgeHooks").getField("craftingPlayer").get(null) == null, "Crafting-player context leaked");
                        cases++;
                    }
                }
            }
        }
        for (int top = 0; top <= 1; top++) for (int left = 0; left <= 1; left++) {
            Fixture fixed = new Fixture(patched); fixed.seed(left, top, 2, 2, 16);
            for (int repeat = 0; repeat < 16; repeat++) fixed.craft();
            check(Arrays.stream(fixed.counts()).sum() == 0 && fixed.crafted() == 16 && fixed.events() == 16,
                "Repeated sandstone crafting must consume exactly 64 sand for 16 outputs");
            cases++;
        }
        System.out.println("FOUNDRY_NATIVE_BEHAVIOR_OK cases=" + cases + " assertions=" + assertions
            + " shapes=1x1-through-3x3 offsets=all counts=1,2,16,64 remainders=empty,same,bucket inventory=space,full repeated_sandstone=4-corners");
    }
}
