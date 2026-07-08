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

    public static void onAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ORDINARY_SPIDER_MINION.get(), Spider.createAttributes().build());
        event.put(SKELETON_HORSE_MINION.get(), AbstractHorse.createBaseHorseAttributes().build());
        event.put(FIRE_ANT_MINION.get(), com.windanesz.ancientspellcraft.entity.living.FireAntMinion.createFireAntAttributes().build());
        event.put(VOLCANO.get(), com.windanesz.ancientspellcraft.entity.living.VolcanoEntity.createVolcanoAttributes().build());
    }

    private ASEntities() {
    }
}
