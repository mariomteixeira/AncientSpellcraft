package com.windanesz.ancientspellcraft.client.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

/** ModelVolcano do 1.12.2 (piramide de 4 camadas). */
public class VolcanoModel<T extends Entity> extends EntityModel<T> {

    private final ModelPart root;

    public VolcanoModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        mesh.getRoot().addOrReplaceChild("volcano", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-8, -6, -8, 16, 6, 16)
                        .texOffs(0, 22).addBox(-7, -11, -7, 14, 5, 14)
                        .texOffs(0, 41).addBox(-6, -15, -6, 12, 4, 12)
                        .texOffs(42, 22).addBox(-5, -19, -5, 10, 4, 10),
                PartPose.offset(0, 24, 0));
        return LayerDefinition.create(mesh, 96, 96);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(com.mojang.blaze3d.vertex.PoseStack poseStack, com.mojang.blaze3d.vertex.VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
