package com.windanesz.ancientspellcraft.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.windanesz.ancientspellcraft.block.SphereCognizanceBlockEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

/**
 * Esfera girando sobre o pedestal (1.12.2 RenderTileSphereCognizance + ModelSphereCognizance).
 * Desvio: rotação pelo gameTime (o 1.12.2 acelerava suavemente na direção do jogador próximo).
 */
public class SphereCognizanceRenderer implements BlockEntityRenderer<SphereCognizanceBlockEntity> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("ancientspellcraft", "textures/entity/sphere_cognizance.png");

    private final ModelPart model;

    public SphereCognizanceRenderer(BlockEntityRendererProvider.Context context) {
        this.model = context.bakeLayer(ASRenderers.SPHERE_COGNIZANCE_LAYER);
    }

    @Override
    public void render(SphereCognizanceBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        long time = be.getLevel() != null ? be.getLevel().getGameTime() : 0;
        float rotation = (time + partialTick) * 0.02f;
        poseStack.pushPose();
        poseStack.translate(0.5, 0.75 + Math.sin(rotation * 2) * 0.04, 0.5);
        poseStack.mulPose(Axis.YP.rotation(rotation));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180)); // modelos de entity renderizam com y invertido
        model.render(poseStack, buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), packedLight, packedOverlay);
        poseStack.popPose();
    }
}
