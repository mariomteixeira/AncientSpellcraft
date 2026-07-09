package com.windanesz.ancientspellcraft.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.windanesz.ancientspellcraft.block.SentinelBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Cristal flutuante girando sobre a base do sentinel (1.12.2 RenderTileSentinel/ModelSentinel).
 * Desvio documentado: usa o render do magic_crystal do Redux em vez do modelo próprio.
 */
public class SentinelRenderer implements BlockEntityRenderer<SentinelBlockEntity> {

    private final ItemStack crystal = new ItemStack(com.koomplo.wizardry.setup.registries.EBItems.MAGIC_CRYSTAL.get());

    public SentinelRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(SentinelBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        float rotation = Mth.lerp(partialTick, be.crystalRotationPrev, be.crystalRotation);
        poseStack.pushPose();
        poseStack.translate(0.5, 0.85 + Math.sin(rotation * 2) * 0.06, 0.5);
        poseStack.mulPose(Axis.YP.rotation(rotation));
        Minecraft.getInstance().getItemRenderer().renderStatic(crystal, ItemDisplayContext.GROUND,
                packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, be.getLevel(), 0);
        poseStack.popPose();
    }
}
