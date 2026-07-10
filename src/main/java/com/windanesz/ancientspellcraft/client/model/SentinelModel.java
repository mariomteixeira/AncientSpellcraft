package com.windanesz.ancientspellcraft.client.model;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/** Cristal duplo do sentinel (1.12.2 ModelSentinel, Blockbench 3.8.4 — conversão 1:1). */
public final class SentinelModel {

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition main = root.addOrReplaceChild("bb_main", CubeListBuilder.create(),
                PartPose.offset(0.0F, 1.0F, 0.0F));
        main.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 5)
                        .addBox(-3.0F, 0.0F, -1.28F, 3.0F, 3.0F, 3.0F),
                PartPose.offsetAndRotation(1.9383F, 7.4621F, 0.9051F, -0.7854F, 0.0F, 0.6109F));
        main.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(9, 9)
                        .addBox(-3.0F, 0.0F, -1.28F, 3.0F, 3.0F, 3.0F),
                PartPose.offsetAndRotation(0.6976F, 5.9963F, -1.2162F, 0.7854F, 0.0F, -0.6109F));
        return LayerDefinition.create(mesh, 32, 32);
    }

    private SentinelModel() {}
}
