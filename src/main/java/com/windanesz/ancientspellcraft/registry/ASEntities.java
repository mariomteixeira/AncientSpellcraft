package com.windanesz.ancientspellcraft.registry;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.entity.living.OrdinarySpiderMinion;
import com.windanesz.ancientspellcraft.entity.living.SkeletonHorseMinion;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Spider;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ASEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, AncientSpellcraft.MODID);

    // Tamanhos vanilla: spider 1.4x0.9, skeleton_horse 1.3965x1.6
    public static final Supplier<EntityType<OrdinarySpiderMinion>> ORDINARY_SPIDER_MINION = ENTITIES.register("ordinary_spider_minion",
            () -> EntityType.Builder.of(OrdinarySpiderMinion::new, MobCategory.MONSTER).sized(1.4F, 0.9F)
                    .build(AncientSpellcraft.MODID + ":ordinary_spider_minion"));

    public static final Supplier<EntityType<SkeletonHorseMinion>> SKELETON_HORSE_MINION = ENTITIES.register("skeleton_horse_minion",
            () -> EntityType.Builder.of(SkeletonHorseMinion::new, MobCategory.CREATURE).sized(1.3965F, 1.6F).eyeHeight(1.52F)
                    .build(AncientSpellcraft.MODID + ":skeleton_horse_minion"));

    // 1.12.2: fire ant 0.35x0.25; volcano ~1.4x1.4
    public static final Supplier<EntityType<com.windanesz.ancientspellcraft.entity.living.FireAntMinion>> FIRE_ANT_MINION = ENTITIES.register("fire_ant_minion",
            () -> EntityType.Builder.of(com.windanesz.ancientspellcraft.entity.living.FireAntMinion::new, MobCategory.MONSTER)
                    .sized(0.35F, 0.25F).fireImmune().build(AncientSpellcraft.MODID + ":fire_ant_minion"));

    public static final Supplier<EntityType<com.windanesz.ancientspellcraft.entity.living.VolcanoEntity>> VOLCANO = ENTITIES.register("volcano",
            () -> EntityType.Builder.of(com.windanesz.ancientspellcraft.entity.living.VolcanoEntity::new, MobCategory.MISC)
                    .sized(1.4F, 1.4F).fireImmune().build(AncientSpellcraft.MODID + ":volcano"));

    public static final Supplier<EntityType<com.windanesz.ancientspellcraft.entity.living.RemnantMinion>> REMNANT_MINION = ENTITIES.register("remnant_minion",
            () -> EntityType.Builder.of(com.windanesz.ancientspellcraft.entity.living.RemnantMinion::new, MobCategory.MONSTER)
                    .sized(0.8F, 0.8F).build(AncientSpellcraft.MODID + ":remnant_minion"));

    public static final Supplier<EntityType<com.windanesz.ancientspellcraft.entity.living.SpellCasterEntity>> SPELL_CASTER = ENTITIES.register("spellcaster_entity",
            () -> EntityType.Builder.of(com.windanesz.ancientspellcraft.entity.living.SpellCasterEntity::new, MobCategory.MISC)
                    .sized(0.4F, 0.7F).build(AncientSpellcraft.MODID + ":spellcaster_entity"));

    public static final Supplier<EntityType<com.windanesz.ancientspellcraft.entity.living.VoidCreeperEntity>> VOID_CREEPER = ENTITIES.register("void_creeper",
            () -> EntityType.Builder.of(com.windanesz.ancientspellcraft.entity.living.VoidCreeperEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.7F).build(AncientSpellcraft.MODID + ":void_creeper"));

    public static final Supplier<EntityType<com.windanesz.ancientspellcraft.entity.living.SkeletonMageEntity>> SKELETON_MAGE = ENTITIES.register("skeleton_mage",
            () -> EntityType.Builder.of(com.windanesz.ancientspellcraft.entity.living.SkeletonMageEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F).eyeHeight(1.74F).build(AncientSpellcraft.MODID + ":skeleton_mage"));

    public static final Supplier<EntityType<com.windanesz.ancientspellcraft.entity.living.SkeletonMageMinion>> SKELETON_MAGE_MINION = ENTITIES.register("skeleton_mage_minion",
            () -> EntityType.Builder.of(com.windanesz.ancientspellcraft.entity.living.SkeletonMageMinion::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F).eyeHeight(1.74F).build(AncientSpellcraft.MODID + ":skeleton_mage_minion"));

    public static final Supplier<EntityType<com.windanesz.ancientspellcraft.entity.living.AnimatedItemEntity>> ANIMATED_ITEM = ENTITIES.register("animated_item",
            () -> EntityType.Builder.of(com.windanesz.ancientspellcraft.entity.living.AnimatedItemEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.2F).build(AncientSpellcraft.MODID + ":animated_item"));

    public static final Supplier<EntityType<com.windanesz.ancientspellcraft.entity.projectile.FlintEntity>> FLINT_SHARD = ENTITIES.register("flint_shard",
            () -> EntityType.Builder.<com.windanesz.ancientspellcraft.entity.projectile.FlintEntity>of(
                    com.windanesz.ancientspellcraft.entity.projectile.FlintEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build(AncientSpellcraft.MODID + ":flint_shard"));

    public static final Supplier<EntityType<com.windanesz.ancientspellcraft.entity.projectile.SafeIceShardEntity>> SAFE_ICE_SHARD = ENTITIES.register("safe_ice_shard",
            () -> EntityType.Builder.<com.windanesz.ancientspellcraft.entity.projectile.SafeIceShardEntity>of(
                    com.windanesz.ancientspellcraft.entity.projectile.SafeIceShardEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build(AncientSpellcraft.MODID + ":safe_ice_shard"));

    public static final Supplier<EntityType<com.windanesz.ancientspellcraft.entity.living.WolfMinion>> WOLF_MINION = ENTITIES.register("wolf_minion",
            () -> EntityType.Builder.of(com.windanesz.ancientspellcraft.entity.living.WolfMinion::new, MobCategory.CREATURE)
                    .sized(0.6F, 0.85F).build(AncientSpellcraft.MODID + ":wolf_minion"));

    public static final Supplier<EntityType<com.windanesz.ancientspellcraft.entity.living.SpiritBearEntity>> SPIRIT_BEAR = ENTITIES.register("spirit_bear",
            () -> EntityType.Builder.of(com.windanesz.ancientspellcraft.entity.living.SpiritBearEntity::new, MobCategory.CREATURE)
                    .sized(1.4F, 1.4F).build(AncientSpellcraft.MODID + ":spirit_bear"));

    public static final Supplier<EntityType<com.windanesz.ancientspellcraft.entity.living.CreeperMinion>> CREEPER_MINION = ENTITIES.register("creeper_minion",
            () -> EntityType.Builder.of(com.windanesz.ancientspellcraft.entity.living.CreeperMinion::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.7F).build(AncientSpellcraft.MODID + ":creeper_minion"));

    public static final Supplier<EntityType<com.windanesz.ancientspellcraft.entity.living.PigZombieMinion>> PIG_ZOMBIE_MINION = ENTITIES.register("pig_zombie_minion",
            () -> EntityType.Builder.of(com.windanesz.ancientspellcraft.entity.living.PigZombieMinion::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).fireImmune().build(AncientSpellcraft.MODID + ":pig_zombie_minion"));

    public static final Supplier<EntityType<com.windanesz.ancientspellcraft.entity.LevitatingBlockEntity>> LEVITATING_BLOCK = ENTITIES.register("levitating_block",
            () -> EntityType.Builder.<com.windanesz.ancientspellcraft.entity.LevitatingBlockEntity>of(
                    com.windanesz.ancientspellcraft.entity.LevitatingBlockEntity::new, MobCategory.MISC)
                    .sized(0.98F, 0.98F).clientTrackingRange(10).updateInterval(20).build(AncientSpellcraft.MODID + ":levitating_block"));

    public static final Supplier<EntityType<com.windanesz.ancientspellcraft.entity.ChaosOrbEntity>> CHAOS_ORB = ENTITIES.register("chaos_orb",
            () -> EntityType.Builder.<com.windanesz.ancientspellcraft.entity.ChaosOrbEntity>of(
                    com.windanesz.ancientspellcraft.entity.ChaosOrbEntity::new, MobCategory.MISC)
                    .sized(0.4F, 0.4F).clientTrackingRange(8).updateInterval(10).build(AncientSpellcraft.MODID + ":chaos_orb"));

    public static final Supplier<EntityType<com.windanesz.ancientspellcraft.entity.construct.ChaosFieldConstruct>> CHAOS_FIELD = ENTITIES.register("chaos_field",
            () -> EntityType.Builder.<com.windanesz.ancientspellcraft.entity.construct.ChaosFieldConstruct>of(
                    com.windanesz.ancientspellcraft.entity.construct.ChaosFieldConstruct::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F).clientTrackingRange(10).build(AncientSpellcraft.MODID + ":chaos_field"));

    public static final Supplier<EntityType<com.windanesz.ancientspellcraft.entity.construct.MoltenBoulderConstruct>> MOLTEN_BOULDER = ENTITIES.register("molten_boulder",
            () -> EntityType.Builder.<com.windanesz.ancientspellcraft.entity.construct.MoltenBoulderConstruct>of(
                    com.windanesz.ancientspellcraft.entity.construct.MoltenBoulderConstruct::new, MobCategory.MISC)
                    .sized(1.2F, 1.2F).clientTrackingRange(10).build(AncientSpellcraft.MODID + ":molten_boulder"));

    public static final Supplier<EntityType<com.windanesz.ancientspellcraft.entity.living.ClassWizard>> CLASS_WIZARD = ENTITIES.register("class_wizard",
            () -> EntityType.Builder.<com.windanesz.ancientspellcraft.entity.living.ClassWizard>of(
                    com.windanesz.ancientspellcraft.entity.living.ClassWizard::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.8F).clientTrackingRange(10).build(AncientSpellcraft.MODID + ":class_wizard"));

    public static final Supplier<EntityType<com.windanesz.ancientspellcraft.entity.living.EvilClassWizard>> EVIL_CLASS_WIZARD = ENTITIES.register("evil_class_wizard",
            () -> EntityType.Builder.<com.windanesz.ancientspellcraft.entity.living.EvilClassWizard>of(
                    com.windanesz.ancientspellcraft.entity.living.EvilClassWizard::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.8F).clientTrackingRange(10).build(AncientSpellcraft.MODID + ":evil_class_wizard"));

    public static void onAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(CLASS_WIZARD.get(), com.koomplo.wizardry.content.entity.living.AbstractWizard.createAttributes().build());
        event.put(EVIL_CLASS_WIZARD.get(), com.koomplo.wizardry.content.entity.living.AbstractWizard.createAttributes().build());
        event.put(ORDINARY_SPIDER_MINION.get(), Spider.createAttributes().build());
        event.put(SKELETON_HORSE_MINION.get(), AbstractHorse.createBaseHorseAttributes().build());
        event.put(FIRE_ANT_MINION.get(), com.windanesz.ancientspellcraft.entity.living.FireAntMinion.createFireAntAttributes().build());
        event.put(VOLCANO.get(), com.windanesz.ancientspellcraft.entity.living.VolcanoEntity.createVolcanoAttributes().build());
        event.put(REMNANT_MINION.get(), com.koomplo.wizardry.content.entity.living.Remnant.createAttributes().build());
        event.put(VOID_CREEPER.get(), net.minecraft.world.entity.monster.Creeper.createAttributes().build());
        event.put(SPELL_CASTER.get(), com.windanesz.ancientspellcraft.entity.living.SpellCasterEntity.createAttributes().build());
        event.put(SKELETON_MAGE.get(), net.minecraft.world.entity.monster.AbstractSkeleton.createAttributes().build());
        event.put(SKELETON_MAGE_MINION.get(), net.minecraft.world.entity.monster.AbstractSkeleton.createAttributes().build());
        event.put(ANIMATED_ITEM.get(), com.windanesz.ancientspellcraft.entity.living.AnimatedItemEntity.createAnimatedItemAttributes().build());
        event.put(WOLF_MINION.get(), net.minecraft.world.entity.animal.Wolf.createAttributes().build());
        event.put(SPIRIT_BEAR.get(), net.minecraft.world.entity.animal.PolarBear.createAttributes().build());
        event.put(CREEPER_MINION.get(), net.minecraft.world.entity.monster.Creeper.createAttributes().build());
        event.put(PIG_ZOMBIE_MINION.get(), net.minecraft.world.entity.monster.ZombifiedPiglin.createAttributes().build());
    }

    private ASEntities() {
    }
}
