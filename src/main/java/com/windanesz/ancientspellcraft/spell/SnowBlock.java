package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.api.content.util.BlockUtil;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.content.spell.abstr.RaySpell;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/** Coloca neve no bloco mirado (1.12.2 SnowBlock). */
public class SnowBlock extends ASRaySpell {

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        BlockPos pos = blockHit.getBlockPos().relative(blockHit.getDirection());
        if (!ctx.world().isClientSide) {
            if (ctx.caster() instanceof Player player && BlockUtil.canPlaceBlock(player, ctx.world(), pos)
                    && ctx.world().getBlockState(pos).canBeReplaced()) {
                ctx.world().setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.SNOW_BLOCK.defaultBlockState());
            }
        } else {
            for (int i = 1; i < 12; i++) {
                ParticleBuilder.create(EBParticles.SNOW, ctx.world().random, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1, false)
                        .spawn(ctx.world());
            }
        }
        return true;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.SNOW).pos(x, y, z).spawn(ctx.world());
    }
}
