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
        event.registerEntityRenderer(ASEntities.SPELL_CASTER.get(), net.minecraft.client.renderer.entity.NoopRenderer::new);
        event.registerEntityRenderer(ASEntities.METAMAGIC_PROJECTILE.get(), context ->
                new com.koomplo.wizardry.client.renderer.entity.MagicProjectileRenderer<>(context,
                        ResourceLocation.fromNamespaceAndPath("ancientspellcraft", "textures/entity/dispel_magic.png")));
        event.registerBlockEntityRenderer(com.windanesz.ancientspellcraft.registry.ASBlocks.SENTINEL_BLOCK_ENTITY.get(),
                SentinelRenderer::new);
        event.registerEntityRenderer(ASEntities.VOID_CREEPER.get(), context ->
                new net.minecraft.client.renderer.entity.CreeperRenderer(context) {
                    private static final ResourceLocation TEXTURE =
                            ResourceLocation.fromNamespaceAndPath("ancientspellcraft", "textures/entity/void_creeper.png");

                    @Override
                    public ResourceLocation getTextureLocation(net.minecraft.world.entity.monster.Creeper entity) {
                        return TEXTURE;
                    }
                });
        event.registerEntityRenderer(ASEntities.SKELETON_HORSE_MINION.get(), context -> new UndeadHorseRenderer(context, net.minecraft.client.model.geom.ModelLayers.SKELETON_HORSE) {
            // Renderer vanilla resolve textura por Map keyed em EntityType vanilla; tipo custom -> null -> NPE no render
            @Override
            public ResourceLocation getTextureLocation(net.minecraft.world.entity.animal.horse.AbstractHorse entity) {
                return ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_skeleton.png");
            }
        });
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
        event.registerEntityRenderer(ASEntities.CREEPER_MINION.get(), net.minecraft.client.renderer.entity.CreeperRenderer::new);
        event.registerEntityRenderer(ASEntities.MOLTEN_BOULDER.get(), com.windanesz.ancientspellcraft.client.renderer.MoltenBoulderRenderer::new);
        event.registerEntityRenderer(ASEntities.CLASS_WIZARD.get(), com.koomplo.wizardry.client.renderer.entity.WizardRenderer::new);
        event.registerEntityRenderer(ASEntities.EVIL_CLASS_WIZARD.get(), com.koomplo.wizardry.client.renderer.entity.EvilWizardRenderer::new);
        event.registerEntityRenderer(ASEntities.LEVITATING_BLOCK.get(), net.minecraft.client.renderer.entity.FallingBlockRenderer::new);
        event.registerEntityRenderer(ASEntities.CHAOS_ORB.get(), net.minecraft.client.renderer.entity.ThrownItemRenderer::new);
        event.registerEntityRenderer(ASEntities.CHAOS_FIELD.get(), com.koomplo.wizardry.client.renderer.entity.BlankRenderer::new);
        event.registerEntityRenderer(ASEntities.PIG_ZOMBIE_MINION.get(), context ->
                new net.minecraft.client.renderer.entity.PiglinRenderer(context,
                        net.minecraft.client.model.geom.ModelLayers.ZOMBIFIED_PIGLIN,
                        net.minecraft.client.model.geom.ModelLayers.ZOMBIFIED_PIGLIN_INNER_ARMOR,
                        net.minecraft.client.model.geom.ModelLayers.ZOMBIFIED_PIGLIN_OUTER_ARMOR, true));
    }

    public static void onRegisterScreens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
        event.register(com.windanesz.ancientspellcraft.registry.ASMenus.ARCANE_ANVIL.get(),
                com.windanesz.ancientspellcraft.client.ArcaneAnvilScreen::new);
        event.register(com.windanesz.ancientspellcraft.registry.ASMenus.SCRIBING_DESK.get(),
                com.windanesz.ancientspellcraft.client.ScribingDeskScreen::new);
        event.register(com.windanesz.ancientspellcraft.registry.ASMenus.SPHERE_COGNIZANCE.get(),
                com.windanesz.ancientspellcraft.client.SphereCognizanceScreen::new);
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
