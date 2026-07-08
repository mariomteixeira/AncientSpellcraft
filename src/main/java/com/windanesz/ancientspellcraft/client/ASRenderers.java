package com.windanesz.ancientspellcraft.client;

import com.windanesz.ancientspellcraft.registry.ASEntities;
import net.minecraft.client.renderer.entity.UndeadHorseRenderer;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import com.windanesz.ancientspellcraft.client.model.AntModel;
import com.windanesz.ancientspellcraft.client.model.VolcanoModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class ASRenderers {

    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ASEntities.ORDINARY_SPIDER_MINION.get(), SpiderRenderer::new);
        event.registerEntityRenderer(ASEntities.SKELETON_HORSE_MINION.get(), context -> new UndeadHorseRenderer(context, net.minecraft.client.model.geom.ModelLayers.SKELETON_HORSE));
        event.registerEntityRenderer(ASEntities.FIRE_ANT_MINION.get(), context -> new MobRenderer<>(context,
                new AntModel<>(context.bakeLayer(ANT_LAYER)), 0.35F * 0.5F) {
            @Override
            public net.minecraft.resources.ResourceLocation getTextureLocation(com.windanesz.ancientspellcraft.entity.living.FireAntMinion entity) {
                return ResourceLocation.fromNamespaceAndPath("ancientspellcraft", "textures/entity/fire_ant.png");
            }

            @Override
            protected void scale(com.windanesz.ancientspellcraft.entity.living.FireAntMinion entity, com.mojang.blaze3d.vertex.PoseStack poseStack, float partialTick) {
                poseStack.scale(0.35F, 0.35F, 0.35F);
            }
        });
        event.registerEntityRenderer(ASEntities.VOLCANO.get(), context -> new MobRenderer<>(context,
                new VolcanoModel<>(context.bakeLayer(VOLCANO_LAYER)), 0.5F) {
            @Override
            public net.minecraft.resources.ResourceLocation getTextureLocation(com.windanesz.ancientspellcraft.entity.living.VolcanoEntity entity) {
                return ResourceLocation.fromNamespaceAndPath("ancientspellcraft", "textures/entity/volcano.png");
            }
        });
    }

    public static void registerMageRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ASEntities.REMNANT_MINION.get(), com.koomplo.wizardry.client.renderer.entity.RemnantRenderer::new);
        event.registerEntityRenderer(ASEntities.SKELETON_MAGE.get(), com.windanesz.ancientspellcraft.client.SkeletonMageRenderer::new);
        event.registerEntityRenderer(ASEntities.SKELETON_MAGE_MINION.get(), com.windanesz.ancientspellcraft.client.SkeletonMageRenderer::new);
        event.registerEntityRenderer(ASEntities.ANIMATED_ITEM.get(), com.windanesz.ancientspellcraft.client.AnimatedItemRenderer::new);
        event.registerEntityRenderer(ASEntities.FLINT_SHARD.get(), com.koomplo.wizardry.client.renderer.entity.MagicArrowRenderer::new);
        event.registerEntityRenderer(ASEntities.SAFE_ICE_SHARD.get(), com.koomplo.wizardry.client.renderer.entity.MagicArrowRenderer::new);
        event.registerEntityRenderer(ASEntities.WOLF_MINION.get(), net.minecraft.client.renderer.entity.WolfRenderer::new);
        event.registerEntityRenderer(ASEntities.SPIRIT_BEAR.get(), net.minecraft.client.renderer.entity.PolarBearRenderer::new);
    }

    public static final ModelLayerLocation ANT_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath("ancientspellcraft", "ant"), "main");
    public static final ModelLayerLocation VOLCANO_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath("ancientspellcraft", "volcano"), "main");

    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ANT_LAYER, AntModel::createBodyLayer);
        event.registerLayerDefinition(VOLCANO_LAYER, VolcanoModel::createBodyLayer);
    }

    private ASRenderers() {
    }
}
