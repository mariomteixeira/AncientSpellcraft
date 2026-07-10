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

/** Conjura um lirio d'agua no ponto mirado (1.12.2 LilyPad); com ring_lily_flower planta uma fileira de 10 x range. */
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

        // ring_lily_flower: fileira na direção olhada (1.12.2: 10 x range, a partir do ponto atingido)
        if (ctx.caster() instanceof Player player
                && com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(player, com.windanesz.ancientspellcraft.registry.ASItems.RING_LILY_FLOWER.get())) {
            Direction direction = player.getDirection();
            int start = direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? -1 : 0;
            boolean any = false;
            int count = (int) (10 * ctx.modifiers().get(SpellModifiers.RANGE));
            for (int i = 0; i < count; i++) {
                any = placeLilyAt(ctx, pos.above().relative(direction, start + i)) || any;
            }
            return any;
        }
        return placeLilyAt(ctx, pos.above());
    }

    private boolean placeLilyAt(CastContext ctx, BlockPos lilyPos) {
        if (!ctx.world().getBlockState(lilyPos.below()).is(Blocks.WATER)
                || !BlockUtil.canBlockBeReplaced(ctx.world(), lilyPos)
                || !BlockUtil.canPlaceBlock(ctx.caster(), ctx.world(), lilyPos)) {
            return false;
        }
        if (!ctx.world().isClientSide) {
            ctx.world().setBlockAndUpdate(lilyPos, Blocks.LILY_PAD.defaultBlockState());
        } else {
            ParticleBuilder.create(EBParticles.FLASH).pos(lilyPos.getX() + 0.5, lilyPos.getY() + 0.5, lilyPos.getZ() + 0.5)
                    .scale(3.5f).color(111 / 255f, 173 / 255f, 24 / 255f).time(20).spawn(ctx.world());
        }
        return true;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.LEAF).pos(x, y, z).spawn(ctx.world());
    }
}
