package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.util.BlockUtil;
import com.koomplo.wizardry.setup.registries.EBAttachments;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Teleporta o BLOCO mirado para o circulo de transporte lembrado (1.12.2 TeleportObject).
 * TODO charm_hoarders_orb (espalha em posicoes aleatorias ao redor do circulo).
 */
public class TeleportObject extends SageRaySpell {

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        if (!(ctx.caster() instanceof Player player) || ctx.world().isClientSide) return true;
        var data = player.getData(EBAttachments.WIZARD_DATA);
        BlockPos destination = data.getTransportationPos();
        if (destination == null
                || !player.level().dimension().location().toString().equals(data.getTransportationDimension())) {
            player.displayClientMessage(Component.translatable("spell.ebwizardry:transportation.undefined"), true);
            return false;
        }
        BlockPos from = blockHit.getBlockPos();
        var state = ctx.world().getBlockState(from);
        if (state.getDestroySpeed(ctx.world(), from) < 0 || ctx.world().getBlockEntity(from) != null
                || !BlockUtil.canBreak(player, ctx.world(), from, false)) {
            return false;
        }
        BlockPos target = destination;
        if (!BlockUtil.canBlockBeReplaced(ctx.world(), target)) {
            target = BlockUtil.findNearbyFloorSpace(ctx.world(), destination, 4, 2, false, player);
            if (target == null) return false;
        }
        ctx.world().removeBlock(from, false);
        ctx.world().setBlockAndUpdate(target, state);
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.DUST).pos(x, y, z).color(0x9d2cf3).spawn(ctx.world());
    }
}
