package com.windanesz.ancientspellcraft.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.windanesz.ancientspellcraft.block.SentinelBlockEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Cristal duplo flutuante girando sobre a base do sentinel
 * (1.12.2 RenderTileSentinel + ModelSentinel 1:1).
 */
public class SentinelRenderer implements BlockEntityRenderer<SentinelBlockEntity> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("ancientspellcraft", "textures/entity/sentinel.png");

    private final ModelPart model;

    public SentinelRenderer(BlockEntityRendererProvider.Context context) {
        this.model = context.bakeLayer(ASRenderers.SENTINEL_LAYER);
    }

    @Override
    public void render(SentinelBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        float rotation = Mth.lerp(partialTick, be.crystalRotationPrev, be.crystalRotation);
        poseStack.pushPose();
        poseStack.translate(0.5, 1.45 + Math.sin(rotation * 2) * 0.06, 0.5);
        poseStack.mulPose(Axis.YP.rotation(rotation));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180)); // modelos de entity renderizam com y invertido
        model.render(poseStack, buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), packedLight, packedOverlay);
        poseStack.popPose();
    }
}
