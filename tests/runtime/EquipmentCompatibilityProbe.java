package soqm.validation;

import com.aetherteam.aether.inventory.AetherAccessorySlots;
import dev.ftb.mods.ftbultimine.FTBUltimine;
import dev.ftb.mods.ftbultimine.config.FTBUltimineServerConfig;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.slot.SlotReference;
import io.wispforest.accessories.api.slot.SlotTypeReference;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

/** Native integration fixtures; requires the fully loaded, isolated probe server. */
final class EquipmentCompatibilityProbe {
    private EquipmentCompatibilityProbe() {}

    static void run(ServerPlayer player, Map<String, Object> results) {
        check(FTBUltimineServerConfig.REQUIRE_TOOL.get(), "Ultimine retains its tool requirement");
        check(FTBUltimineServerConfig.REQUIRE_VALID_TOOL_FOR_BLOCK.get(), "Ultimine retains its harvest check");
        ItemStack previousHand = player.getMainHandItem();
        try {
            player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STICK));
            check(!validTool(player), "An ordinary stick must not become an Ultimine tool");
            player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_PICKAXE));
            check(validTool(player), "Native tiered tools remain allowed");
            for (String id : new String[]{"mekanism:atomic_disassembler", "mekanism:meka_tool"}) {
                ItemStack stack = item(id);
                check(!stack.isDamageableItem(), "The fixture exercises an energy tool without durability");
                check(stack.is(FTBUltimine.ALLOW_TAG), "The pinned inclusion tag contains " + id);
                player.setItemSlot(EquipmentSlot.MAINHAND, stack);
                check(validTool(player), "Native Ultimine accepts " + id);
            }
        } finally { player.setItemSlot(EquipmentSlot.MAINHAND, previousHand); }
        results.put("equipment_ultimine_energy_tools_and_stick_negative", true);

        AccessoriesCapability capability = AccessoriesCapability.get(player);
        check(capability != null, "The native Accessories capability is attached");
        capability.updateContainers();
        Map<String, SlotTypeReference> families = new LinkedHashMap<>();
        families.put("gloves", AetherAccessorySlots.getGlovesSlotType());
        families.put("rings", AetherAccessorySlots.getRingSlotType());
        families.put("pendants", AetherAccessorySlots.getPendantSlotType());
        families.put("capes", AetherAccessorySlots.getCapeSlotType());
        families.put("shields", AetherAccessorySlots.getShieldSlotType());
        families.put("miscellaneous", AetherAccessorySlots.getAccessorySlotType());
        int aetherItems = 0;
        for (var family : families.entrySet()) {
            SlotTypeReference type = family.getValue();
            check(type != null, "Aether keeps its native inventory: " + family.getKey());
            SlotReference reference = SlotReference.of(player, type.slotName(), 0);
            check(reference.isValid(), "Native Aether slot exists for " + family.getKey());
            var tag = TagKey.create(Registries.ITEM,
                    ResourceLocation.parse("aether:accessories_" + family.getKey()));
            var members = BuiltInRegistries.ITEM.getTag(tag).orElseThrow();
            check(members.size() > 0, "The native Aether equipment family is not empty");
            ItemStack previous = reference.getStack();
            try {
                reference.setStack(ItemStack.EMPTY);
                for (var holder : members) {
                    ItemStack stack = new ItemStack(holder);
                    check(AccessoriesAPI.getAccessory(stack) != null, "Aether registers its native item behavior");
                    check(AccessoriesAPI.canInsertIntoSlot(stack, reference),
                            "Aether accepts " + BuiltInRegistries.ITEM.getKey(stack.getItem()));
                    check(reference.setStack(stack.copy()) && reference.getStack().is(stack.getItem()),
                            "Aether can equip and read the actual stack");
                    check(AccessoriesAPI.canUnequip(reference.getStack(), reference), "Native accessory can be removed");
                    reference.setStack(ItemStack.EMPTY);
                    aetherItems++;
                }
            } finally { reference.setStack(previous); }
        }
        check(aetherItems == 32, "All 32 items in the six pinned Aether accessory families were exercised");
        check(!CuriosApi.isStackValid(context(player, "hands"), item("aether:iron_gloves")),
                "Aether gloves do not acquire an inert Curios equipment route");
        results.put("equipment_aether_native_equip_unequip_items", aetherItems);

        String[][] routes = {
                {"industrialforegoing:infinity_backpack", "back"},
                {"industrialforegoing:meat_feeder", "head"},
                {"immersiveengineering:powerpack", "back"},
                {"immersiveengineering:earmuffs", "head"},
                {"relics:midnight_mantle", "back"},
                {"botania:holy_cloak", "body"},
                {"botania:flight_tiara", "head"},
                {"botania:mana_ring", "ring"},
                {"evilcraft:invigorating_pendant", "necklace"},
                {"artifacts:power_glove", "hands"},
                {"artifacts:lucky_scarf", "face"},
                {"artifacts:onion_ring", "bracelet"}
        };
        Map<String, String> routeValidation = new LinkedHashMap<>();
        for (var route : routes) {
            routeValidation.put(route[0], checkCurios(player, route[0], route[1]));
        }
        for (String tier : new String[]{"", "copper_", "iron_", "gold_", "diamond_", "netherite_"}) {
            String id = "sophisticatedbackpacks:" + tier + "backpack";
            routeValidation.put(id, checkCurios(player, id, "backpack"));
        }
        check(!CuriosApi.isStackValid(context(player, "backpack"), item("industrialforegoing:infinity_backpack")),
                "Infinity Backpack is not rerouted to the incompatible backpack slot");
        results.put("equipment_curios_native_routes", routes.length + 6);
        results.put("equipment_curios_native_insert_lookup_extract", routeValidation);
    }

    private static boolean validTool(ServerPlayer player) {
        // Dirt isolates the native require_tool check from battery/module state.
        // Actual stone mining, energy consumption and UI remain gameplay checks.
        return FTBUltimine.isValidTool(player, player.blockPosition(), Blocks.DIRT.defaultBlockState());
    }

    private static SlotContext context(ServerPlayer player, String slot) {
        return new SlotContext(slot, player, 0, false, true);
    }

    private static String checkCurios(ServerPlayer player, String id, String slot) {
        var type = CuriosApi.getPlayerSlots(player).get(slot);
        check(type != null && type.getSize() > 0 && !type.isLocked(), "An active player slot exists: " + slot);
        ItemStack stack = item(id);
        check(CuriosApi.isStackValid(context(player, slot), stack), "Native Curios accepts " + id + " in " + slot);
        var inventory = CuriosApi.getCuriosInventory(player)
                .orElseThrow(() -> new AssertionError("The native Curios inventory is attached for " + id));
        var slots = inventory.getStacksHandler(slot)
                .orElseThrow(() -> new AssertionError("The native Curios inventory allocates slot " + slot));
        var stacks = slots.getStacks();
        check(stacks.getSlots() > 0 && inventory.isSlotActive(slot, 0), "The actual Curios slot is active: " + slot);
        ItemStack previous = stacks.getStackInSlot(0).copy();
        try {
            stacks.setStackInSlot(0, ItemStack.EMPTY);
            // Pinned Curios 9.5.1 DynamicStackHandler validates slot predicates,
            // then optional ICurio.canEquip (orElse(true)), then CurioCanEquipEvent.
            // Tag-based routes need no item capability. Use that native route
            // instead of imposing the fixture's former Optional.orElseThrow().
            check(stacks.isItemValid(0, stack), "Native predicates, item hook and equip event permit " + id);
            ItemStack remaining = stacks.insertItem(0, stack.copy(), false);
            check(remaining.isEmpty() && stacks.getStackInSlot(0).getCount() == 1
                            && ItemStack.isSameItemSameComponents(stacks.getStackInSlot(0), stack),
                    "Native Curios insertion equips the complete stack: " + id);
            var equipped = inventory.findCurio(slot, 0)
                    .orElseThrow(() -> new AssertionError("Native inventory lookup sees equipped " + id));
            check(ItemStack.isSameItemSameComponents(equipped.stack(), stack),
                    "Native equipped-item lookup preserves " + id);
            ItemStack removed = stacks.extractItem(0, 1, false);
            check(removed.getCount() == 1 && ItemStack.isSameItemSameComponents(removed, stack)
                            && stacks.getStackInSlot(0).isEmpty(),
                    "Native canUnequip and unequip-event route removes " + id);
        } finally {
            stacks.setStackInSlot(0, previous);
        }
        return CuriosApi.getCurio(stack).isPresent() ? "slot_predicates_and_item_capability" : "slot_predicates";
    }

    private static ItemStack item(String id) {
        ResourceLocation key = ResourceLocation.parse(id);
        check(BuiltInRegistries.ITEM.containsKey(key), "The pinned item is registered: " + id);
        Item value = BuiltInRegistries.ITEM.get(key);
        return new ItemStack(value);
    }

    private static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
