package com.windanesz.ancientspellcraft.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.windanesz.ancientspellcraft.block.RitualCoreBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;

/** Item do ritual (cristal do condensing / sapling do forest) flutuando sobre o ritual_core. */
public class RitualCoreRenderer implements BlockEntityRenderer<RitualCoreBlockEntity> {

    public RitualCoreRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(RitualCoreBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        if (be.getStoredItem().isEmpty()) return;
        float rotation = Mth.lerp(partialTick, be.rotationPrev, be.rotation);
        poseStack.pushPose();
        poseStack.translate(0.5, 0.6 + Math.sin(rotation * 2) * 0.05, 0.5);
        poseStack.mulPose(Axis.YP.rotation(rotation));
        Minecraft.getInstance().getItemRenderer().renderStatic(be.getStoredItem(), ItemDisplayContext.GROUND,
                packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, be.getLevel(), 0);
        poseStack.popPose();
    }
}
