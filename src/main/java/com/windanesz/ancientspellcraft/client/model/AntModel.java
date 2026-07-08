package com.windanesz.ancientspellcraft.client.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

/** ModelAnt do 1.12.2 traduzido (cabeca+antenas, pescoco, corpo, 8 pernas com animacao de aranha). */
public class AntModel<T extends Entity> extends EntityModel<T> {

    private final ModelPart head, neck, body;
    private final ModelPart leg1, leg2, leg3, leg4, leg5, leg6, leg7, leg8;
    private final ModelPart root;

    public AntModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.neck = root.getChild("neck");
        this.body = root.getChild("body");
        this.leg1 = root.getChild("leg1");
        this.leg2 = root.getChild("leg2");
        this.leg3 = root.getChild("leg3");
        this.leg4 = root.getChild("leg4");
        this.leg5 = root.getChild("leg5");
        this.leg6 = root.getChild("leg6");
        this.leg7 = root.getChild("leg7");
        this.leg8 = root.getChild("leg8");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(25, 31).addBox(-2, -2, -6, 4, 4, 5), PartPose.offset(0, 13, -3));
        head.addOrReplaceChild("antenna_l", CubeListBuilder.create()
                .texOffs(0, 4).addBox(-0.7758F, -2, 0.1543F, 1, 1, 3), PartPose.offset(-1, 2, -8));
        head.addOrReplaceChild("antenna_r", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-0.1481F, -2, -0.2284F, 1, 1, 3), PartPose.offset(1, 2, -8));
        root.addOrReplaceChild("neck", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2, -2, -4, 4, 4, 13), PartPose.offset(0, 15, 0));
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0, 15, 9));
        body.addOrReplaceChild("abdomen", CubeListBuilder.create()
                .texOffs(0, 17).addBox(-3, -11.1566F, 2.4627F, 6, 4, 8)
                .texOffs(10, 9).addBox(-0.5F, -8.4924F, 8.0546F, 1, 1, 6), PartPose.offset(0, 9, -2));
        root.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(0, 31).addBox(-11, -1, 2, 13, 2, 2), PartPose.offset(-4, 15, 2));
        root.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(26, 27).addBox(-2, -1, 2, 13, 2, 2), PartPose.offset(4, 15, 2));
        root.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(21, 12).addBox(-11, -1, 1, 13, 2, 2), PartPose.offset(-4, 15, 1));
        root.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(21, 16).addBox(-2, -1, 1, 13, 2, 2), PartPose.offset(4, 15, 1));
        root.addOrReplaceChild("leg5", CubeListBuilder.create().texOffs(21, 8).addBox(-11, -1, 1, 13, 2, 2), PartPose.offset(-4, 15, 0));
        root.addOrReplaceChild("leg6", CubeListBuilder.create().texOffs(21, 4).addBox(-2, -1, 1, 13, 2, 2), PartPose.offset(4, 15, 0));
        root.addOrReplaceChild("leg7", CubeListBuilder.create().texOffs(21, 0).addBox(-11, -1, -2, 13, 2, 2), PartPose.offset(-4, 15, -1));
        root.addOrReplaceChild("leg8", CubeListBuilder.create().texOffs(20, 20).addBox(-2, -1, -2, 13, 2, 2), PartPose.offset(4, 15, -1));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        head.xRot = headPitch * ((float) Math.PI / 180F);
        float quarterPi = (float) Math.PI / 4F;
        leg1.zRot = -quarterPi;
        leg2.zRot = quarterPi;
        leg3.zRot = -0.58119464F;
        leg4.zRot = 0.58119464F;
        leg5.zRot = -0.58119464F;
        leg6.zRot = 0.58119464F;
        leg7.zRot = -quarterPi;
        leg8.zRot = quarterPi;
        // Animacao de pernas estilo aranha vanilla
        float swing1 = -(Mth.cos(limbSwing * 0.6662F * 2.0F) * 0.4F) * limbSwingAmount;
        float swing2 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + (float) Math.PI) * 0.4F) * limbSwingAmount;
        float swing3 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + ((float) Math.PI / 2F)) * 0.4F) * limbSwingAmount;
        float swing4 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + ((float) Math.PI * 1.5F)) * 0.4F) * limbSwingAmount;
        leg1.yRot = quarterPi + swing1;
        leg2.yRot = -quarterPi - swing1;
        leg3.yRot = quarterPi * 0.5F + swing2;
        leg4.yRot = -quarterPi * 0.5F - swing2;
        leg5.yRot = -quarterPi * 0.5F + swing3;
        leg6.yRot = quarterPi * 0.5F - swing3;
        leg7.yRot = -quarterPi + swing4;
        leg8.yRot = quarterPi - swing4;
    }

    @Override
    public void renderToBuffer(com.mojang.blaze3d.vertex.PoseStack poseStack, com.mojang.blaze3d.vertex.VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
