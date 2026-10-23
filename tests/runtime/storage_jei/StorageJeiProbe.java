package qm.storagejeiprobe;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SequencedMap;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.library.focus.Focus;
import mezz.jei.library.focus.FocusGroup;
import mezz.jei.library.gui.helpers.CraftingGridHelper;
import mezz.jei.library.gui.recipes.supplier.builder.IngredientSlotBuilder;
import mezz.jei.library.gui.recipes.supplier.builder.IngredientSupplierBuilder;
import mezz.jei.library.ingredients.IngredientInfo;
import mezz.jei.library.ingredients.IngredientManager;
import mezz.jei.library.ingredients.RecipeIngredientSupplier;
import mezz.jei.library.ingredients.RegisteredIngredients;
import net.minecraft.SharedConstants;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.p3pp3rf1y.sophisticatedcore.compat.recipeviewers.common.CraftingDisplaySpec;
import net.p3pp3rf1y.sophisticatedcore.compat.recipeviewers.common.CraftingDisplayVariant;
import net.p3pp3rf1y.sophisticatedcore.compat.recipeviewers.common.IRecipeViewerCraftingSpecRecipe;
import net.p3pp3rf1y.sophisticatedcore.compat.recipeviewers.common.SourceResultFocusBehavior;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.launch.platform.container.ContainerHandleVirtual;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.service.*;

/**
 * Executes the pinned Core display extension and JEI layout, ingredient and focus
 * methods, before and after the shipped Mixin. ItemStacks, components and recipe
 * dimensions use native Minecraft code. The only ingredient helper is a fixture
 * for vanilla ItemStack identity; rendering is unused. No game, storage registry,
 * world, server, UI or EULA is started. This tests the display-spec boundary, not
 * Sophisticated Storage's full mod-registration/factory lifecycle.
 */
public final class StorageJeiProbe {
    private static final String TARGET = "net.p3pp3rf1y.sophisticatedcore.compat.recipeviewers.jei.CraftingSpecCategoryExtension";
    private static final String CONFIG = "qm_storage_jei_compat.mixins.json";
    private static final String FOCUS_ERROR = "All slots must have the same number of ingredients in order to create a focus link.";
    private static int assertions, cases, linkedCases, unlinkedCases, baselineThrows, baselineWrong, baselineCorrect;
    private static IngredientManager manager;

    public static final class Service extends MixinServiceAbstract implements IClassProvider, IClassBytecodeProvider {
        public String getName() { return "SOQM-storage-JEI-probe"; }
        public boolean isValid() { return true; }
        public MixinEnvironment.Phase getInitialPhase() { return MixinEnvironment.Phase.DEFAULT; }
        public IClassProvider getClassProvider() { return this; }
        public IClassBytecodeProvider getBytecodeProvider() { return this; }
        public ITransformerProvider getTransformerProvider() { return null; }
        public IClassTracker getClassTracker() { return null; }
        public IMixinAuditTrail getAuditTrail() { return null; }
        public Collection<String> getPlatformAgents() { return List.of(); }
        public IContainerHandle getPrimaryContainer() { return new ContainerHandleVirtual("SOQM-storage-JEI-probe"); }
        public InputStream getResourceAsStream(String name) { return getClass().getClassLoader().getResourceAsStream(name); }
        public URL[] getClassPath() { return new URL[0]; }
        public Class<?> findClass(String name) throws ClassNotFoundException { return findClass(name, false); }
        public Class<?> findClass(String name, boolean init) throws ClassNotFoundException { return Class.forName(name, init, getClass().getClassLoader()); }
        public Class<?> findAgentClass(String name, boolean init) throws ClassNotFoundException { return findClass(name, init); }
        public ClassNode getClassNode(String name) throws ClassNotFoundException, IOException { return getClassNode(name, false, 0); }
        public ClassNode getClassNode(String name, boolean transform) throws ClassNotFoundException, IOException { return getClassNode(name, transform, 0); }
        public ClassNode getClassNode(String name, boolean transform, int flags) throws ClassNotFoundException, IOException {
            ClassNode node = new ClassNode();
            new ClassReader(resource(name)).accept(node, flags);
            return node;
        }
    }

    public static final class Blackboard implements IGlobalPropertyService {
        private record Key(String name) implements IPropertyKey {}
        private final Map<IPropertyKey, Object> values = new HashMap<>();
        public IPropertyKey resolveKey(String name) { return new Key(name); }
        @SuppressWarnings("unchecked") public <T> T getProperty(IPropertyKey key) { return (T)values.get(key); }
        public void setProperty(IPropertyKey key, Object value) { values.put(key, value); }
        public <T> T getProperty(IPropertyKey key, T fallback) { T value = getProperty(key); return value == null ? fallback : value; }
        public String getPropertyString(IPropertyKey key, String fallback) { return getProperty(key, fallback).toString(); }
    }

    private static byte[] resource(String name) throws IOException {
        try (InputStream stream = StorageJeiProbe.class.getClassLoader().getResourceAsStream(name.replace('.', '/') + ".class")) {
            if (stream == null) throw new IOException("Missing native class: " + name);
            return stream.readAllBytes();
        }
    }

    /** Only the exact Core extension is child-loaded; every dependency stays native. */
    private static final class Loader extends ClassLoader {
        private final byte[] bytes;
        Loader(byte[] bytes) { super(StorageJeiProbe.class.getClassLoader()); this.bytes = bytes; }
        @Override protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            synchronized (getClassLoadingLock(name)) {
                Class<?> type = findLoadedClass(name);
                if (type == null) type = name.equals(TARGET) ? defineClass(name, bytes, 0, bytes.length) : super.loadClass(name, false);
                if (resolve) resolveClass(type);
                return type;
            }
        }
    }

    /** Observe native builders without replacing their ingredient or link logic. */
    private static final class RecordingBuilder extends IngredientSupplierBuilder {
        final List<IngredientSlotBuilder> inputSlots = new ArrayList<>();
        RecordingBuilder() { super(manager); }
        @Override public IRecipeSlotBuilder addSlot(RecipeIngredientRole role) {
            IRecipeSlotBuilder slot = super.addSlot(role);
            if (role == RecipeIngredientRole.INPUT) inputSlots.add((IngredientSlotBuilder)slot);
            return slot;
        }
    }

    @SuppressWarnings("removal")
    private static IngredientManager ingredientManager() {
        IIngredientHelper<ItemStack> helper = new IIngredientHelper<>() {
            public IIngredientType<ItemStack> getIngredientType() { return VanillaTypes.ITEM_STACK; }
            public String getDisplayName(ItemStack stack) { return stack.toString(); }
            public String getUniqueId(ItemStack stack, UidContext context) { return getResourceLocation(stack) + "|" + stack.getComponentsPatch(); }
            public ResourceLocation getResourceLocation(ItemStack stack) { return BuiltInRegistries.ITEM.getKey(stack.getItem()); }
            public ItemStack copyIngredient(ItemStack stack) { return stack.copy(); }
            public String getErrorInfo(ItemStack stack) { return String.valueOf(stack); }
            public boolean isValidIngredient(ItemStack stack) { return !stack.isEmpty(); }
        };
        // A renderer is unnecessary: this uses JEI's ingredient supplier, not GUI rendering.
        IngredientInfo<ItemStack> info = new IngredientInfo<>(VanillaTypes.ITEM_STACK, List.of(), helper, null, ItemStack.CODEC, null);
        SequencedMap<IIngredientType<?>, IngredientInfo<?>> entries = new LinkedHashMap<>();
        entries.put(VanillaTypes.ITEM_STACK, info);
        return new IngredientManager(new RegisteredIngredients(entries));
    }

    private static ItemStack named(Item item, String name, int count) {
        ItemStack stack = new ItemStack(item, count);
        stack.set(DataComponents.CUSTOM_NAME, Component.literal(name));
        return stack;
    }

    private record Fixture(CraftingDisplaySpec spec, RecipeHolder<CraftingRecipe> original, int source) {}

    private static Fixture fixture(int width, int height, int total, int source, int variantCount, boolean sparse, boolean extraOutput) {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        List<CraftingDisplayVariant> variants = new ArrayList<>();
        for (int variant = 0; variant < variantCount; variant++) {
            List<ItemStack> inputs = new ArrayList<>();
            for (int i = 0; i < total; i++) {
                ItemStack input = i == source ? named(Items.CHEST, "source-" + variant, variant + 1)
                    : sparse && i % 2 == 0 ? ItemStack.EMPTY : new ItemStack(Items.IRON_INGOT);
                inputs.add(input);
                if (variant == 0) ingredients.add(input.isEmpty() ? Ingredient.EMPTY : Ingredient.of(input));
            }
            List<ItemStack> outputs = new ArrayList<>();
            outputs.add(named(Items.BARREL, "result-" + variant, variant + 2));
            if (extraOutput) outputs.add(named(Items.BARREL, "additional-result-" + variant, 1));
            variants.add(new CraftingDisplayVariant(inputs, outputs));
        }
        SourceResultFocusBehavior behavior = new SourceResultFocusBehavior(source,
            (variant, focus) -> ItemStack.isSameItemSameComponents(variant.inputs().get(source), focus) ? Optional.of(variant) : Optional.empty(),
            (variant, focus) -> ItemStack.isSameItemSameComponents(variant.firstOutput(), focus) ? Optional.of(variant) : Optional.empty());
        boolean shapeless = width <= 0 || height <= 0;
        ResourceLocation id = ResourceLocation.parse("soqm_probe:display_" + cases + "_" + total + "_" + source);
        CraftingRecipe recipe = shapeless ? new ShapelessRecipe("", CraftingBookCategory.MISC, variants.getFirst().firstOutput(), ingredients)
            : new ShapedRecipe("", CraftingBookCategory.MISC, new ShapedRecipePattern(width, height, ingredients, Optional.empty()), variants.getFirst().firstOutput());
        // Dimensions originate in the native recipe, as in Storage's display factory.
        int nativeWidth = recipe instanceof ShapedRecipe shaped ? shaped.getWidth() : width;
        int nativeHeight = recipe instanceof ShapedRecipe shaped ? shaped.getHeight() : height;
        CraftingDisplaySpec spec = new CraftingDisplaySpec(id, shapeless, nativeWidth, nativeHeight, ingredients, variants, behavior);
        return new Fixture(spec, new RecipeHolder<>(id, recipe), source);
    }

    @SuppressWarnings("unchecked")
    private static ICraftingCategoryExtension<CraftingRecipe> extension(Class<?> type, Fixture fixture, boolean acceptFocus) throws Exception {
        Function<RecipeHolder<CraftingRecipe>, CraftingDisplaySpec> factory = holder -> {
            if (holder.value() instanceof IRecipeViewerCraftingSpecRecipe recipe) return recipe.spec();
            if (holder.value() instanceof ShapedRecipe recipe) {
                check(recipe.getWidth() == fixture.spec.width() && recipe.getHeight() == fixture.spec.height(), "Factory lost native recipe dimensions");
            }
            return fixture.spec;
        };
        Predicate<ItemStack> predicate = stack -> acceptFocus;
        return (ICraftingCategoryExtension<CraftingRecipe>)type.getConstructor(Function.class, Predicate.class).newInstance(factory, predicate);
    }

    private static IFocusGroup focus(RecipeIngredientRole role, ItemStack stack) {
        return Focus.createFromApi(manager, role, VanillaTypes.ITEM_STACK, stack);
    }

    private static List<ItemStack> stacks(List<ITypedIngredient<?>> typed) {
        return typed.stream().map(value -> value.getItemStack().orElseThrow()).toList();
    }

    private static boolean same(List<ItemStack> left, List<ItemStack> right) {
        if (left.size() != right.size()) return false;
        for (int i = 0; i < left.size(); i++) if (!ItemStack.matches(left.get(i), right.get(i))) return false;
        return true;
    }

    private static List<ItemStack> linkIngredients(RecipeIngredientSupplier supplier, RecipeIngredientRole role) {
        return supplier.getFocusLinks().getFirst().slots().stream().filter(slot -> slot.role() == role)
            .findFirst().map(slot -> stacks(slot.ingredients())).orElseThrow();
    }

    private static RecordingBuilder display(Class<?> type, Fixture fixture, RecipeHolder<CraftingRecipe> holder, IFocusGroup focuses, boolean acceptFocus) throws Exception {
        ICraftingCategoryExtension<CraftingRecipe> extension = extension(type, fixture, acceptFocus);
        check(extension.getWidth(holder) == fixture.spec.width() && extension.getHeight(holder) == fixture.spec.height(), "Core extension dimensions differ from recipe/spec");
        RecordingBuilder builder = new RecordingBuilder();
        extension.setRecipe(holder, builder, CraftingGridHelper.INSTANCE, focuses);
        return builder;
    }

    private static void verify(Class<?> type, Fixture fixture, RecipeHolder<CraftingRecipe> holder, IFocusGroup focuses,
                               boolean acceptFocus, List<CraftingDisplayVariant> expectedVariants, String label) throws Exception {
        RecordingBuilder builder = display(type, fixture, holder, focuses, acceptFocus);
        RecipeIngredientSupplier supplier = builder.buildIngredientSupplier();
        List<List<ItemStack>> inputs = fixture.spec.getInputSlots(expectedVariants);
        List<ItemStack> outputs = fixture.spec.getOutputStacks(expectedVariants);
        List<ItemStack> source = inputs.get(fixture.source);
        check(builder.inputSlots.size() == 9, label + ": JEI must create the native nine physical slots");
        int width = fixture.spec.width(), height = fixture.spec.height();
        if (width <= 0 || height <= 0) width = height = inputs.size() > 4 ? 3 : inputs.size() > 1 ? 2 : 1;
        List<List<ItemStack>> expectedSlots = new ArrayList<>();
        for (int i = 0; i < 9; i++) expectedSlots.add(List.of());
        for (int i = 0; i < inputs.size(); i++) expectedSlots.set(CraftingGridHelper.INSTANCE.getCraftingIndex(i, width, height), inputs.get(i));
        for (int slot = 0; slot < 9; slot++) {
            check(same(stacks(builder.inputSlots.get(slot).getAllIngredients()), expectedSlots.get(slot)), label + ": ingredient layout changed at physical slot " + slot);
        }
        check(same(stacks(supplier.getIngredients(RecipeIngredientRole.OUTPUT)), outputs), label + ": output variants/count/components changed");
        boolean shouldLink = source.size() == 1 && outputs.size() == 1;
        check(supplier.getFocusLinks().size() == (shouldLink ? 1 : 0), label + ": original singleton focus-link guard changed");
        if (shouldLink) {
            check(supplier.getFocusLinks().getFirst().slots().size() == 2, label + ": expected one input/output pair");
            check(same(linkIngredients(supplier, RecipeIngredientRole.INPUT), source), label + ": link points to a material/empty slot instead of the source, or lost components");
            check(same(linkIngredients(supplier, RecipeIngredientRole.OUTPUT), outputs), label + ": link output changed");
            var link = supplier.getFocusLinks().getFirst();
            for (var role : List.of(RecipeIngredientRole.INPUT, RecipeIngredientRole.OUTPUT)) {
                ItemStack focused = (role == RecipeIngredientRole.INPUT ? source : outputs).getFirst();
                check(Set.of(0).equals(link.getVisibleIngredientIndexes(focus(role, focused), manager, ingredient -> true)), label + ": native JEI focus does not preserve the linked pair");
            }
            check(link.getVisibleIngredientIndexes(FocusGroup.EMPTY, manager, ingredient -> true).isEmpty(), label + ": unrestricted native link should remain visible");
            check(link.getVisibleIngredientIndexes(FocusGroup.EMPTY, manager, ingredient -> false) == null, label + ": native hidden-pair handling changed");
            linkedCases++;
        } else unlinkedCases++;
        cases++;
    }

    private static void baseline(Class<?> type, Fixture fixture) throws Exception {
        try {
            RecipeIngredientSupplier supplier = display(type, fixture, fixture.spec.recipeHolder(fixture.spec.variants().getFirst()), FocusGroup.EMPTY, true).buildIngredientSupplier();
            check(supplier.getFocusLinks().size() == 1, "Baseline unexpectedly changed its singleton link guard");
            if (same(linkIngredients(supplier, RecipeIngredientRole.INPUT), List.of(fixture.spec.variants().getFirst().inputs().get(fixture.source)))) baselineCorrect++;
            else baselineWrong++;
        } catch (IllegalArgumentException error) {
            check(FOCUS_ERROR.equals(error.getMessage()), "Baseline failed for an unrelated reason: " + error);
            check(Arrays.stream(error.getStackTrace()).anyMatch(frame -> frame.getClassName().equals(TARGET) && frame.getMethodName().equals("setRecipe")), "Baseline failure did not pass through the actual Core extension");
            baselineThrows++;
        }
    }

    private static void matrix(Class<?> patched, int width, int height, int total, int source) throws Exception {
        Fixture single = fixture(width, height, total, source, 1, false, false);
        verify(patched, single, single.spec.recipeHolder(single.spec.variants().getFirst()), FocusGroup.EMPTY, true, single.spec.variants(), "grouped-single");
        verify(patched, single, single.original, FocusGroup.EMPTY, true, single.spec.variants(), "ungrouped-single");
        Fixture multi = fixture(width, height, total, source, 2, false, false);
        CraftingDisplayVariant first = multi.spec.variants().getFirst(), second = multi.spec.variants().get(1);
        verify(patched, multi, multi.original, focus(RecipeIngredientRole.OUTPUT, first.firstOutput()), true, List.of(first), "focused-output");
        verify(patched, multi, multi.original, focus(RecipeIngredientRole.INPUT, second.inputs().get(source)), true, List.of(second), "focused-input");
        verify(patched, multi, multi.original, FocusGroup.EMPTY, true, multi.spec.variants(), "global-two-variants");
        verify(patched, multi, multi.spec.recipeHolder(multi.spec.variants()), focus(RecipeIngredientRole.INPUT, second.inputs().get(source)), true, multi.spec.variants(), "grouped-preserves-variants");
        verify(patched, multi, multi.original, focus(RecipeIngredientRole.OUTPUT, named(Items.BARREL, "absent", 1)), true, multi.spec.variants(), "unmatched-focus-fallback");
        verify(patched, multi, multi.original, focus(RecipeIngredientRole.INPUT, second.inputs().get(source)), false, multi.spec.variants(), "predicate-rejects-focus");
        Fixture sparse = fixture(width, height, total, source, 1, true, false);
        verify(patched, sparse, sparse.spec.recipeHolder(sparse.spec.variants()), FocusGroup.EMPTY, true, sparse.spec.variants(), "sparse-ingredients");
        Fixture extra = fixture(width, height, total, source, 1, false, true);
        verify(patched, extra, extra.original, FocusGroup.EMPTY, true, extra.spec.variants(), "multiple-outputs-no-link");
    }

    private static void check(boolean condition, String message) {
        assertions++;
        if (!condition) throw new AssertionError(message);
    }

    private static void verifyTransform(byte[] bytes) {
        ClassNode node = new ClassNode(); new ClassReader(bytes).accept(node, 0);
        var method = node.methods.stream().filter(value -> value.name.equals("setRecipe")).findFirst().orElseThrow();
        int listGets = 0, handlers = 0;
        for (var instruction : method.instructions) if (instruction instanceof MethodInsnNode call) {
            if (call.owner.equals("java/util/List") && call.name.equals("get") && call.desc.equals("(I)Ljava/lang/Object;")) listGets++;
            if (call.owner.equals(TARGET.replace('.', '/')) && (call.name.contains("qm$") || call.name.contains("soqm$"))) handlers++;
        }
        check(listGets == 2, "Native compact-input and physical-slot lookups must both remain intact");
        check(handlers == 1, "Production Mixin must change exactly one physical-slot argument");
    }

    public static void main(String[] args) throws Exception {
        boolean serverCheck = args.length > 0 && args[0].equals("--server-side-check");
        MixinBootstrap.init();
        MixinEnvironment.getDefaultEnvironment().setSide(serverCheck ? MixinEnvironment.Side.SERVER : MixinEnvironment.Side.CLIENT);
        MixinEnvironment.getDefaultEnvironment().setOption(MixinEnvironment.Option.DEBUG_INJECTORS, true);
        Mixins.addConfiguration(CONFIG);
        var constructor = Class.forName("org.spongepowered.asm.mixin.transformer.MixinTransformer").getDeclaredConstructor();
        constructor.setAccessible(true);
        var transformer = (IMixinTransformer)constructor.newInstance();
        com.llamalad7.mixinextras.MixinExtrasBootstrap.init();
        byte[] original = resource(TARGET);
        byte[] fixed = transformer.transformClassBytes(TARGET, TARGET, original);
        if (serverCheck) {
            check(Arrays.equals(original, fixed), "Client-only Mixin unexpectedly transforms Core on the server side");
            System.out.println("STORAGE_JEI_SERVER_SIDE_UNCHANGED");
            return;
        }
        check(!Arrays.equals(original, fixed), "Shipped client Mixin did not transform the pinned Core extension");
        verifyTransform(fixed);
        if (args.length > 0) {
            Path output = Path.of(args[0], TARGET.replace('.', '/') + ".class");
            Files.createDirectories(output.getParent()); Files.write(output, fixed);
        }
        System.out.println("STORAGE_JEI_MIXIN_TRANSFORM_OK native_list_gets=2 modified_arguments=1");
        SharedConstants.tryDetectVersion(); Bootstrap.bootStrap();
        manager = ingredientManager();
        Class<?> before = new Loader(original).loadClass(TARGET), after = new Loader(fixed).loadClass(TARGET);
        int shapedPositions = 0, shapelessPositions = 0;
        for (int width = 1; width <= 3; width++) for (int height = 1; height <= 3; height++) {
            for (int source = 0; source < width * height; source++) {
                int exceptionsBefore = baselineThrows;
                baseline(before, fixture(width, height, width * height, source, 1, false, false));
                if (height == 1 && (width == 2 || width == 3) && source == 1) {
                    check(baselineThrows == exceptionsBefore + 1, "Reported US/USU source index 1 must reproduce the exact native exception");
                }
                matrix(after, width, height, width * height, source);
                shapedPositions++;
            }
        }
        for (int total = 1; total <= 9; total++) for (int source = 0; source < total; source++) {
            matrix(after, 0, 0, total, source);
            shapelessPositions++;
        }
        // JEI also normalizes either non-positive dimension, not only the pair 0,0.
        matrix(after, -1, 3, 3, 1);
        matrix(after, 3, 0, 4, 3);
        check(baselineThrows > 0 && baselineWrong > 0 && baselineCorrect > 0, "Negative control must detect exceptions, silent material links and preserved native layouts");
        System.out.println("STORAGE_JEI_NATIVE_BASELINE exceptions=" + baselineThrows + " wrong_source_links=" + baselineWrong + " correct_links=" + baselineCorrect);
        System.out.println("STORAGE_JEI_NATIVE_OK cases=" + cases + " linked=" + linkedCases + " unlinked=" + unlinkedCases + " shaped_positions=" + shapedPositions + " shapeless_positions=" + shapelessPositions + " assertions=" + assertions);
    }
}
