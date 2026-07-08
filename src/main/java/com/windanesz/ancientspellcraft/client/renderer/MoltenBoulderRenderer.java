package com.windanesz.ancientspellcraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.windanesz.ancientspellcraft.entity.construct.MoltenBoulderConstruct;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

/** Renderiza o pedregulho como um bloco de magma girando. */
public class MoltenBoulderRenderer extends EntityRenderer<MoltenBoulderConstruct> {

    private final net.minecraft.client.renderer.block.BlockRenderDispatcher dispatcher;

    public MoltenBoulderRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.dispatcher = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(MoltenBoulderConstruct entity, float yaw, float partialTicks, PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int light) {
        poseStack.pushPose();
        poseStack.translate(-0.6, 0, -0.6);
        poseStack.scale(1.2f, 1.2f, 1.2f);
        float roll = (entity.tickCount + partialTicks) * 12f;
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(com.mojang.math.Axis.of(new org.joml.Vector3f(
                (float) entity.getDeltaMovement().z, 0, (float) -entity.getDeltaMovement().x).normalize())
                .rotationDegrees(roll));
        poseStack.translate(-0.5, -0.5, -0.5);
        dispatcher.renderSingleBlock(Blocks.MAGMA_BLOCK.defaultBlockState(), poseStack, buffer, light,
                net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, buffer, light);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MoltenBoulderConstruct entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
