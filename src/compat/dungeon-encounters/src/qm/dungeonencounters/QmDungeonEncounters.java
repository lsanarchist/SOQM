package qm.dungeonencounters;

import com.mojang.logging.LogUtils;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.tiers.WorldTier;
import dev.shadowsoffire.apotheosis.tiers.augments.TierAugment;
import dev.shadowsoffire.apotheosis.tiers.augments.TierAugmentRegistry;
import divinerpg.entities.base.EntityDivineBoss;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.tonicarter.elitemobs.DifficultyLevel;
import net.tonicarter.elitemobs.EliteMobManager;
import net.tonicarter.elitemobs.EliteTier;
import net.tonicarter.elitemobs.PlayerProgressionData;
import net.tonicarter.elitemobs.affixes.Affix;
import net.tonicarter.elitemobs.affixes.AffixRegistry;
import net.tonicarter.elitemobs.affixes.AffixState;
import org.slf4j.Logger;
import qm.dungeonencounters.mixin.AffixStateAccess;
import qm.dungeonencounters.mixin.EliteManagerAccess;
import qm.mobcompat.MobCompat45;

/** In-place encounters: native Elite abilities and one Apotheosis tier, with no replacement or bonus gear. */
@Mod(QmDungeonEncounters.MOD_ID)
public final class QmDungeonEncounters {
    public static final String MOD_ID = "qm_dungeon_encounters";
    public static final String DATA_KEY = MOD_ID;
    public static final int PENDING = 1;
    public static final int ACTIVE = 2;
    public static final int DECLINED = 3;
    public static final int APPLYING = 4;
    public static final int FAILED = 5;
    public static final ResourceLocation HEALTH_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "elite_health");
    public static final ResourceLocation DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "elite_damage");
    private static final ResourceLocation DUNGEON = ResourceLocation.parse("dimdungeons:dungeon_dimension");
    private static final List<TagKey<EntityType<?>>> BOSS_TAGS = List.of(
        TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse("c:bosses")),
        TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse("forge:bosses")),
        TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.parse("minecraft:bosses")));
    private static final Logger LOGGER = LogUtils.getLogger();

    public QmDungeonEncounters(ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, EncounterConfig.SPEC, MOD_ID + "-common.toml");
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void joined(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        Entity entity = event.getEntity();
        if (isDungeon(entity) && entity instanceof PathfinderMob) {
            // EntityType.spawn adds the mob before DD sets persistence/name.
            // Protect that first join from unrelated species/size replacement;
            // the RETURN hook still owns enrollment and later stat activation.
            entity.addTag("collective.checked");
            entity.getPersistentData().putBoolean(MobCompat45.SCALE_MARKER, true);
        }
        if (state(entity) == ACTIVE) {
            restore(entity);
        } else if (!event.loadedFromDisk() && entity instanceof PathfinderMob mob && !isDungeon(entity)) {
            considerDivine(mob);
        }
        // Pending state needs no queue restoration. The next nearby player's
        // local query finds it even after a complete server restart.
    }

    @SubscribeEvent
    public void left(EntityLeaveLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        // Visibility can become HIDDEN without removal and later TRACKED
        // without another join event. Keep that live registration; a real
        // removal is detached here or reaped by Elite's native alive check.
        if (event.getEntity().isRemoved()) detach(event.getEntity());
        else checkpoint(event.getEntity());
    }

    @SubscribeEvent
    public void playerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player instanceof FakePlayer
                || !player.isAlive() || player.isSpectator()) return;
        int interval = EncounterConfig.ACTIVATION_INTERVAL.get();
        int phase = Math.floorMod(player.getUUID().hashCode(), interval);
        if (Math.floorMod(player.tickCount, interval) == phase) activateNearby(player);
    }

    /** Called only after DD has created its chosen entity; no mutation of DD's spawn or pool. */
    public static boolean enrollDungeon(Entity entity, boolean advanced, int theme) {
        if (!(entity instanceof PathfinderMob mob) || !isDungeon(entity)
                || !EncounterConfig.ENABLE_DUNGEONS.get() || !eligible(mob, true, false)) return false;
        return enroll(mob, "dungeon", advanced, theme, EncounterConfig.DUNGEON_CHANCE.get());
    }

    public static boolean considerDivine(PathfinderMob mob) {
        if (isDungeon(mob)
                || !BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()).getNamespace().equals("divinerpg")) return false;
        boolean boss = isDivineBoss(mob);
        if (boss ? !EncounterConfig.ENABLE_DIVINE_BOSSES.get() : !EncounterConfig.ENABLE_DIVINE.get()) return false;
        if (!eligible(mob, false, boss)) return false;
        return enroll(mob, boss ? "divine_boss" : "divine", false, 0,
            boss ? EncounterConfig.DIVINE_BOSS_CHANCE.get() : EncounterConfig.DIVINE_CHANCE.get());
    }

    /** All 23 pinned Divine boss classes, including its flying and WaterAnimal branches. */
    public static boolean isDivineBoss(Entity entity) {
        if (entity == null || !BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getNamespace().equals("divinerpg")) return false;
        if (entity instanceof EntityDivineBoss) return true;
        for (Class<?> type = entity.getClass(); type != null; type = type.getSuperclass()) {
            if (type.getName().startsWith("divinerpg.entities.boss.")) return true;
        }
        return false;
    }

    private static boolean enroll(PathfinderMob mob, String source, boolean advanced, int theme, double chance) {
        if (state(mob) != 0) return false;
        CompoundTag saved = new CompoundTag();
        saved.putInt("version", 1);
        saved.putString("source", source);
        saved.putBoolean("boss", source.equals("divine_boss"));
        saved.putBoolean("advanced", advanced);
        saved.putInt("theme", theme);
        boolean chosen = chance > 0.0 && mob.getRandom().nextDouble() < chance;
        saved.putInt("state", chosen ? PENDING : DECLINED);
        mob.getPersistentData().put(DATA_KEY, saved);
        return chosen;
    }

    private static boolean eligible(PathfinderMob mob, boolean authoredDungeonName, boolean allowDivineBoss) {
        boolean nativeBoss = isDivineBoss(mob);
        // Ordinary world selection follows Monster, not the broader Enemy
        // marker that Divine also gives neutral whales. DD's authored pool
        // includes native Enemy animals such as hoglins; Kitra has its own
        // explicitly authorized Divine-boss path.
        boolean hostile = mob instanceof Monster || authoredDungeonName && mob instanceof Enemy
            || allowDivineBoss && nativeBoss;
        if (mob.level().isClientSide() || !mob.isAlive() || !hostile
                || mob instanceof AbstractVillager
                || mob instanceof TamableAnimal tame && tame.isTame()
                || mob instanceof OwnableEntity owned && owned.getOwnerUUID() != null) return false;
        if (nativeBoss && !allowDivineBoss) return false;
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
        if (EncounterConfig.EXCLUDED_ENTITY_IDS.get().contains(id.toString())
                || !(allowDivineBoss && nativeBoss) && BOSS_TAGS.stream().anyMatch(mob.getType()::is)) return false;
        CompoundTag data = mob.getPersistentData();
        if (data.getBoolean(MobCompat45.ELITE_MARKER) || data.contains("apoth.boss")
                || data.contains("apoth.miniboss") || EliteMobManager.isTrackedElite(mob.getUUID())
                || EliteMobManager.getAffix(mob.getUUID()).isPresent()) return false;
        // A name tag or existing authored encounter in the world is not an
        // invitation to reroll it. DD intentionally names its ordinary mobs;
        // the separately authorized native Divine bosses keep authored names.
        return authoredDungeonName || allowDivineBoss && nativeBoss
            || !mob.hasCustomName() && !mob.isPersistenceRequired();
    }

    public static boolean isPending(Entity entity) { return state(entity) == PENDING; }

    private static int state(Entity entity) {
        return entity == null ? 0 : entity.getPersistentData().getCompound(DATA_KEY).getInt("state");
    }

    private static boolean isDungeon(Entity entity) {
        return entity.level().dimension().location().equals(DUNGEON);
    }

    /** Bounded, player-local spatial query; it neither loads chunks nor scans all worlds. */
    public static int activateNearby(ServerPlayer player) {
        if (player instanceof FakePlayer || !player.isAlive() || player.isSpectator()) return 0;
        double radius = EncounterConfig.ACTIVATION_RADIUS.get();
        int budget = EncounterConfig.ACTIVATION_BUDGET.get();
        int applied = 0;
        for (PathfinderMob mob : player.serverLevel().getEntitiesOfClass(PathfinderMob.class,
                player.getBoundingBox().inflate(radius), QmDungeonEncounters::isPending)) {
            if (mob.distanceToSqr(player) > radius * radius) continue;
            if (activate(mob, player) && ++applied >= budget) break;
        }
        return applied;
    }

    /** The first nearby real player supplies a tier exactly once, including pre-generated DD rooms. */
    public static boolean activate(PathfinderMob mob, ServerPlayer player) {
        if (!isPending(mob) || !mob.isAlive() || player instanceof FakePlayer || !player.isAlive()
                || player.isSpectator() || mob.level() != player.level()) return false;
        double radius = EncounterConfig.ACTIVATION_RADIUS.get();
        if (mob.distanceToSqr(player) > radius * radius) return false;
        CompoundTag saved = mob.getPersistentData().getCompound(DATA_KEY);
        boolean dungeon = saved.getString("source").equals("dungeon");
        boolean boss = saved.getBoolean("boss") && isDivineBoss(mob);
        if (dungeon ? !EncounterConfig.ENABLE_DUNGEONS.get()
                : boss ? !EncounterConfig.ENABLE_DIVINE_BOSSES.get() : !EncounterConfig.ENABLE_DIVINE.get()) return false;
        // A different mod may have promoted the mob while it waited for a player.
        if (!eligible(mob, dungeon, boss && !dungeon)) {
            saved.putInt("state", DECLINED);
            return false;
        }
        List<Affix> pool = new ArrayList<>();
        for (String id : EncounterConfig.AFFIXES.get()) {
            AffixRegistry.byId(id).filter(affix -> supportsAffix(mob, affix)).ifPresent(pool::add);
        }
        if (pool.isEmpty()) {
            saved.putInt("state", DECLINED);
            LOGGER.warn("No configured Elite Mobs abilities are compatible with {}; declined encounter {}",
                BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()), mob.getUUID());
            return false;
        }
        Affix affix = pool.get(mob.getRandom().nextInt(pool.size()));
        WorldTier tier = WorldTier.getTier(player);
        boolean advanced = saved.getBoolean("advanced");
        EliteTier abilityTier = advanced || boss ? EliteTier.CHAMPION : EliteTier.ELITE;
        DifficultyLevel circle = PlayerProgressionData.get(player).getActiveLevel();
        float healthRatio = mob.getMaxHealth() <= 0 ? 1.0F : mob.getHealth() / mob.getMaxHealth();
        saved.putInt("state", APPLYING);
        saved.putString("affix", affix.id());
        saved.putString("worldTier", tier.getSerializedName());
        saved.putString("eliteTier", abilityTier.name());
        saved.putString("circle", circle.name());
        try {
            ServerLevel level = player.serverLevel();
            // Vanilla FinalizeSpawn may already have used a distant player.
            // Remove only registered Apotheosis-owned monster augments, then
            // select one tier. Different tier IDs must never coexist here.
            for (WorldTier old : WorldTier.values()) {
                for (TierAugment augment : TierAugmentRegistry.getAugments(old, TierAugment.Target.MONSTERS)) {
                    augment.remove(level, mob);
                }
            }
            for (TierAugment augment : TierAugmentRegistry.getAugments(tier, TierAugment.Target.MONSTERS)) {
                augment.apply(level, mob);
            }
            mob.setData(Apoth.Attachments.TIER_AUGMENTS_APPLIED, true);
            addModifier(mob, Attributes.MAX_HEALTH, HEALTH_ID, boss ? EncounterConfig.BOSS_HEALTH_MULTIPLIER.get() : advanced
                ? EncounterConfig.ADVANCED_HEALTH_MULTIPLIER.get() : EncounterConfig.HEALTH_MULTIPLIER.get());
            addModifier(mob, Attributes.ATTACK_DAMAGE, DAMAGE_ID, boss ? EncounterConfig.BOSS_DAMAGE_MULTIPLIER.get() : advanced
                ? EncounterConfig.ADVANCED_DAMAGE_MULTIPLIER.get() : EncounterConfig.DAMAGE_MULTIPLIER.get());
            mob.setHealth(Math.min(mob.getMaxHealth(), Math.max(0.0F, healthRatio * mob.getMaxHealth())));
            MobCompat45.markElite(mob);
            mob.getPersistentData().putBoolean(MobCompat45.SCALE_MARKER, true);
            // Preserve native persistence. DD has already set its own flag;
            // ordinary world mobs should retain their normal despawn rules.
            Component baseName = mob.getName().copy();
            mob.setCustomName(Component.literal("[")
                .append(Component.translatable("elitemobs.affix." + affix.id()))
                .append("] ").append(baseName).withStyle(affix.color()));
            mob.setCustomNameVisible(true);
            // Uses the upstream lifecycle, not an imitation of the effects.
            float originalAbsorption = mob.getAbsorptionAmount();
            affix.onSpawn(level, mob, abilityTier, circle);
            if (mob.getAbsorptionAmount() < originalAbsorption) mob.setAbsorptionAmount(originalAbsorption);
            EliteManagerAccess.qm$affixStates().put(mob.getUUID(), new AffixState(mob, affix));
            saved.putInt("state", ACTIVE);
            checkpoint(mob);
            return true;
        } catch (RuntimeException failure) {
            saved.putInt("state", FAILED);
            LOGGER.error("Encounter activation failed once for {} ({}); it will not be rerolled",
                mob.getUUID(), BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType()), failure);
            return false;
        }
    }

    private static boolean supportsAffix(PathfinderMob mob, Affix affix) {
        // Use the original entity's immunity rules. Kitra and other undead
        // cannot receive Regenerating's regeneration, so choose another real
        // configured ability instead of overriding native boss behavior.
        return switch (affix.id()) {
            case "regenerating" -> mob.canBeAffected(new MobEffectInstance(MobEffects.REGENERATION, 100, 2))
                && mob.canBeAffected(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 1));
            case "shielded" -> mob.canBeAffected(new MobEffectInstance(MobEffects.ABSORPTION, Integer.MAX_VALUE, 1));
            default -> true;
        };
    }

    private static void addModifier(PathfinderMob mob, Holder<Attribute> attribute,
            ResourceLocation id, double multiplier) {
        AttributeInstance instance = mob.getAttribute(attribute);
        if (instance != null) instance.addOrReplacePermanentModifier(new AttributeModifier(
            id, multiplier - 1.0, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
    }

    /** Called before Entity serializes NeoForge persistent data, including autosaves. */
    public static void checkpoint(Entity entity) {
        if (!(entity instanceof PathfinderMob mob) || entity.level().isClientSide() || state(entity) != ACTIVE) return;
        AffixState nativeState = EliteManagerAccess.qm$affixStates().get(entity.getUUID());
        if (nativeState == null || nativeState.mob != mob) return;
        AffixStateAccess access = (AffixStateAccess) (Object) nativeState;
        CompoundTag saved = entity.getPersistentData().getCompound(DATA_KEY);
        CompoundTag cooldowns = new CompoundTag();
        for (Map.Entry<String, Integer> entry : access.qm$cooldowns().entrySet()) {
            cooldowns.putInt(entry.getKey(), Math.max(0, entry.getValue()));
        }
        saved.put("cooldowns", cooldowns);
        ListTag once = new ListTag();
        access.qm$firedOnce().stream().sorted().forEach(key -> once.add(StringTag.valueOf(key)));
        saved.put("firedOnce", once);
    }

    /** Re-registers only native ability state. Stats, current HP and one-shot effects stay saved. */
    public static void restore(Entity entity) {
        if (!(entity instanceof PathfinderMob mob) || entity.level().isClientSide() || state(entity) != ACTIVE) return;
        AffixState existing = EliteManagerAccess.qm$affixStates().get(entity.getUUID());
        if (existing != null && existing.mob == mob) return;
        CompoundTag saved = entity.getPersistentData().getCompound(DATA_KEY);
        Affix affix = AffixRegistry.byId(saved.getString("affix")).orElse(null);
        if (affix == null) {
            saved.putInt("state", FAILED);
            LOGGER.warn("Saved Elite ability {} no longer exists for {}", saved.getString("affix"), entity.getUUID());
            return;
        }
        AffixState nativeState = new AffixState(mob, affix);
        AffixStateAccess access = (AffixStateAccess) (Object) nativeState;
        CompoundTag cooldowns = saved.getCompound("cooldowns");
        for (String key : cooldowns.getAllKeys()) access.qm$cooldowns().put(key, Math.max(0, cooldowns.getInt(key)));
        ListTag once = saved.getList("firedOnce", Tag.TAG_STRING);
        for (int i = 0; i < once.size(); i++) access.qm$firedOnce().add(once.getString(i));
        MobCompat45.markElite(mob);
        mob.getPersistentData().putBoolean(MobCompat45.SCALE_MARKER, true);
        EliteManagerAccess.qm$affixStates().put(entity.getUUID(), nativeState);
    }

    /** Unloading is not death and must not grant loot or retain world/entity references. */
    public static void detach(Entity entity) {
        if (state(entity) != ACTIVE) return;
        checkpoint(entity);
        AffixState current = EliteManagerAccess.qm$affixStates().get(entity.getUUID());
        if (current != null && current.mob == entity) EliteManagerAccess.qm$affixStates().remove(entity.getUUID());
    }
}
