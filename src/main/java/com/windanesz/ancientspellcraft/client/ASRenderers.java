package com.windanesz.ancientspellcraft.client;

import com.windanesz.ancientspellcraft.registry.ASEntities;
import net.minecraft.client.renderer.entity.UndeadHorseRenderer;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class ASRenderers {

    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ASEntities.ORDINARY_SPIDER_MINION.get(), SpiderRenderer::new);
        event.registerEntityRenderer(ASEntities.SKELETON_HORSE_MINION.get(), context -> new UndeadHorseRenderer(context, net.minecraft.client.model.geom.ModelLayers.SKELETON_HORSE));
    }

    private ASRenderers() {
    }
}
