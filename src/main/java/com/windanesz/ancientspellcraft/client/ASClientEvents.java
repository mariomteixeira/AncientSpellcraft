package com.windanesz.ancientspellcraft.client;

import com.koomplo.wizardry.api.content.util.RayTracer;
import com.koomplo.wizardry.core.platform.Services;
import com.windanesz.ancientspellcraft.item.BattlemageSwordItem;
import com.windanesz.ancientspellcraft.network.ExtendedReachC2S;
import com.windanesz.ancientspellcraft.spell.RunewordSpell;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/** Client: clique no vazio com runeword_reach/pull/push ativa -> manda o alvo estendido (até 9). */
public final class ASClientEvents {

    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        var player = Minecraft.getInstance().player;
        if (player == null || !(player.getMainHandItem().getItem() instanceof BattlemageSwordItem)) return;
        String active = RunewordSpell.getActive(player.getMainHandItem());
        if (active.isEmpty()) return;
        var spell = Services.REGISTRY_UTIL.getSpell(ResourceLocation.tryParse(active));
        if (spell == null) return;
        String path = spell.getLocation().getPath();
        if (!path.equals("runeword_reach") && !path.equals("runeword_pull") && !path.equals("runeword_push")) return;

        Vec3 origin = player.getEyePosition();
        Vec3 end = origin.add(player.getLookAngle().scale(9.0));
        var hit = RayTracer.rayTrace(player.level(), player, origin, end, 0.3f, false,
                LivingEntity.class, RayTracer.ignoreEntityFilter(player));
        if (hit instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof LivingEntity target
                && player.distanceTo(target) > 3.0F) {
            PacketDistributor.sendToServer(new ExtendedReachC2S(target.getId()));
        }
    }

    private ASClientEvents() {
    }
}
