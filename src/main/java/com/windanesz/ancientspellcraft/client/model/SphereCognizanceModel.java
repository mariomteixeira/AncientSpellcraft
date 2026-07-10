package com.windanesz.ancientspellcraft.client.model;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;

/** Esfera da cognição (1.12.2 ModelSphereCognizance): cubo 4x4x4 flutuante. */
public final class SphereCognizanceModel {

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        mesh.getRoot().addOrReplaceChild("ball", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.0F, -4.0F, -2.0F, 4, 4, 4), PartPose.ZERO);
        return LayerDefinition.create(mesh, 16, 16);
    }

    private SphereCognizanceModel() {}
}
