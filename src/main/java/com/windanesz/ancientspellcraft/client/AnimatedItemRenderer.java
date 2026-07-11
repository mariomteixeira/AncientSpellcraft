package com.windanesz.ancientspellcraft.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.windanesz.ancientspellcraft.entity.living.AnimatedItemEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;

/**
 * Item flutuando/balancando (1.12.2 RenderAnimatedItem); com armadura animada, o corpo humanoide
 * fica invisível e só a armadura vestida aparece (1.12.2 spectral armour).
 */
public class AnimatedItemRenderer extends LivingEntityRenderer<AnimatedItemEntity, HumanoidModel<AnimatedItemEntity>> {

    private final ItemRenderer itemRenderer;

    public AnimatedItemRenderer(EntityRendererProvider.Context context) {
        super(context, invisibleModel(context), 0.4F);
        this.itemRenderer = context.getItemRenderer();
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
                context.getModelManager()));
    }

    private static HumanoidModel<AnimatedItemEntity> invisibleModel(EntityRendererProvider.Context context) {
        HumanoidModel<AnimatedItemEntity> model = new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER));
        model.setAllVisible(false);
        return model;
    }

    @Override
    public void render(AnimatedItemEntity entity, float yaw, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight) {
        super.render(entity, yaw, partialTick, poseStack, buffer, packedLight);

        var stack = entity.getMainHandItem();
        if (!stack.isEmpty()) {
            poseStack.pushPose();
            float bob = Mth.sin((entity.tickCount + partialTick) / 10.0F) * 0.1F + 0.6F;
            poseStack.translate(0, bob, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(-Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot)));
            poseStack.scale(1.4F, 1.4F, 1.4F);
            itemRenderer.renderStatic(stack, ItemDisplayContext.GROUND, packedLight,
                    net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId());
            poseStack.popPose();
        }
    }

    @Override
    protected boolean shouldShowName(@NotNull AnimatedItemEntity entity) {
        return false;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull AnimatedItemEntity entity) {
        return ResourceLocation.withDefaultNamespace("textures/misc/white.png");
    }
}
