package com.windanesz.ancientspellcraft.client;

import com.windanesz.ancientspellcraft.entity.living.SkeletonMageEntity;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/** Stray + robe overlay tintada pelo elemento (1.12.2 RenderSkeletonMage). */
public class SkeletonMageRenderer extends HumanoidMobRenderer<SkeletonMageEntity, SkeletonModel<SkeletonMageEntity>> {

    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/skeleton/stray.png");
    private static final ResourceLocation OVERLAY = ResourceLocation.fromNamespaceAndPath("ancientspellcraft", "textures/entity/skeleton_mage_overlay.png");

    public SkeletonMageRenderer(EntityRendererProvider.Context context) {
        super(context, new SkeletonModel<>(context.bakeLayer(ModelLayers.SKELETON)), 0.5F);
        this.addLayer(new RenderLayer<>(this) {
            @Override
            public void render(@NotNull com.mojang.blaze3d.vertex.PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight,
                               @NotNull SkeletonMageEntity entity, float limbSwing, float limbSwingAmount, float partialTick,
                               float ageInTicks, float netHeadYaw, float headPitch) {
                var consumer = buffer.getBuffer(RenderType.entityTranslucent(OVERLAY));
                Integer rgb = entity.getMageElement().getColor().getColor();
                int color = rgb == null ? 0xFFFFFFFF : 0xFF000000 | rgb;
                getParentModel().renderToBuffer(poseStack, consumer, packedLight,
                        net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, color);
            }
        });
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SkeletonMageEntity entity) {
        return TEXTURE;
    }
}
