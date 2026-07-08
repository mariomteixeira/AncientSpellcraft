package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.koomplo.wizardry.api.content.util.BlockUtil;
import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.koomplo.wizardry.api.content.util.MagicDamageSource;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.content.spell.abstr.BuffSpell;
import com.koomplo.wizardry.setup.registries.EBDamageSources;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/** Conjura um lirio d'agua no ponto mirado (1.12.2 LilyPad). TODO ring_lily_flower: fileira de lirios (porta com os artefatos). */
public class LilyPad extends ASRaySpell {

    public LilyPad() {
        this.hitLiquids(true);
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        if (ctx.caster() == null) return false;
        BlockPos pos = blockHit.getBlockPos();
        BlockPos above = pos.above();
        if (ctx.world().getBlockState(pos).is(Blocks.WATER) && BlockUtil.canBlockBeReplaced(ctx.world(), above)
                && BlockUtil.canPlaceBlock(ctx.caster(), ctx.world(), above)) {
            if (!ctx.world().isClientSide) {
                ctx.world().setBlockAndUpdate(above, Blocks.LILY_PAD.defaultBlockState());
            } else {
                ParticleBuilder.create(EBParticles.FLASH).pos(above.getX() + 0.5, above.getY() + 0.5, above.getZ() + 0.5)
                        .scale(3.5f).color(111 / 255f, 173 / 255f, 24 / 255f).time(20).spawn(ctx.world());
            }
            return true;
        }
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.LEAF).pos(x, y, z).spawn(ctx.world());
    }
}
