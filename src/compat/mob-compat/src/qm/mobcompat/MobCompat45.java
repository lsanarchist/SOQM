package qm.mobcompat;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Random;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.loot.LootController;
import dev.shadowsoffire.apotheosis.loot.LootRarity;
import dev.shadowsoffire.apotheosis.loot.RarityRegistry;
import dev.shadowsoffire.apotheosis.tiers.GenContext;
import dev.shadowsoffire.apotheosis.tiers.WorldTier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

@Mod("qm_mob_compat")
public final class MobCompat45 {
    public static final String ELITE_MARKER = "qm_elite_mobs_encounter";
    public static final String SCALE_MARKER = "naturalsizes_scaled";
    private static final ResourceLocation MYTHIC = ResourceLocation.parse("apotheosis:mythic");

    public MobCompat45() { NeoForge.EVENT_BUS.register(this); }

    /** Called only from the pinned EliteSpawner.applyStats tail, before its
     * addFreshEntity call. The explicit marker avoids guessing from names. */
    public static void markElite(PathfinderMob mob) {
        mob.getPersistentData().putBoolean(ELITE_MARKER, true);
        mob.addTag("collective.checked");
        mob.setData(Apoth.Attachments.TIER_AUGMENTS_APPLIED, true);
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void beforeJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity living)) return;
        boolean persistent = living instanceof Mob mob && mob.isPersistenceRequired();
        boolean encounter = living.getPersistentData().getBoolean(ELITE_MARKER)
            || living.getPersistentData().contains("apoth.boss")
            || living.getPersistentData().contains("apoth.miniboss");
        // Collective replaces on every join, including disk loads, custom
        // encounters and renamed pets. Limit those cases to their original mob.
        if (event.loadedFromDisk() || living.hasCustomName() || persistent || encounter) {
            living.addTag("collective.checked");
        }
        var scale = living.getAttribute(Attributes.SCALE);
        String namespace = BuiltInRegistries.ENTITY_TYPE.getKey(living.getType()).getNamespace();
        boolean customScale = scale != null && Math.abs(scale.getBaseValue() - 1.0) > 0.000001;
        // Preserve authored hitboxes, NPCs, existing injured saved mobs, and
        // explicit encounters. Ordinary newly spawned wildlife still varies.
        if (event.loadedFromDisk() || customScale || encounter || living instanceof AbstractVillager
                || namespace.equals("ymondf") || namespace.equals("minecolonies")
                || namespace.equals("millenaire")) {
            living.getPersistentData().putBoolean(SCALE_MARKER, true);
        }
    }

    /** The old Elite Mobs API asked for an explicitly mythic item with only a
     * RandomSource. Use a neutral HAVEN generation context, matching that
     * world-tier-independent request, while retaining the actual server level. */
    public static ItemStack mythicGear(ServerLevel level, ItemStack stack, Random random) {
        LootRarity rarity = RarityRegistry.INSTANCE.getValue(MYTHIC);
        if (rarity == null) throw new IllegalStateException("Missing Apotheosis mythic rarity");
        GenContext context = GenContext.standalone(level.getRandom(), WorldTier.HAVEN,
            0.0F, level, level.getSharedSpawnPos());
        return LootController.createLootItem(stack, rarity, context);
    }

    @SubscribeEvent
    public void stopped(ServerStoppedEvent event) {
        // These exact installed mods retain world/entity references in static
        // collections. Empty only their transient tracking on a complete stop;
        // no saved progression or world data is removed.
        clear("com.serilum.giantspawn_common_neoforge.events.GiantEvent",
            "giants_per_world", "tickdelay_per_world");
        clear("com.serilum.skeletonhorsespawn_common_neoforge.events.SkeletonHorseEvent",
            "skeletonhorses_per_world", "tickdelay_per_world");
        clear("net.tonicarter.elitemobs.EliteMobManager", "ACTIVE_BOSS_BARS", "ENCOUNTERS", "AFFIX_STATES");
        clear("net.tonicarter.elitemobs.NightEliteSpawner", "PLAYER_TRACKERS");
    }

    private static void clear(String name, String... fields) {
        try {
            Class<?> type = Class.forName(name);
            for (String nameOfField : fields) {
                Field field = type.getDeclaredField(nameOfField);
                field.setAccessible(true);
                ((Map<?, ?>)field.get(null)).clear();
            }
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Pinned mob lifecycle API changed: " + name, exception);
        }
    }
}
