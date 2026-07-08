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

    public static void onAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ORDINARY_SPIDER_MINION.get(), Spider.createAttributes().build());
        event.put(SKELETON_HORSE_MINION.get(), AbstractHorse.createBaseHorseAttributes().build());
    }

    private ASEntities() {
    }
}
