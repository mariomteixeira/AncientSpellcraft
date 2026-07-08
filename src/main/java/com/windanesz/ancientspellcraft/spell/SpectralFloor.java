package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.util.BlockUtil;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.setup.registries.EBBlocks;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import net.minecraft.core.BlockPos;

/** Piso espectral (1.12.2 SpectralFloor): disco de blocos espectrais sob o conjurador. */
public class SpectralFloor extends SageSpell {

    public static final com.koomplo.wizardry.api.content.spell.properties.SpellProperty<Integer> BLOCK_LIFETIME =
            com.koomplo.wizardry.api.content.spell.properties.SpellProperty.intProperty("block_lifetime");

    @Override
    public boolean cast(PlayerCastContext ctx) {
        BlockPos pos = ctx.caster().blockPosition().below();
        if (ctx.caster().getDeltaMovement().y < 0) pos = pos.below();

        if (ctx.world().isClientSide) {
            ParticleBuilder.create(EBParticles.FLASH).pos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)
                    .scale(3).color(0.75f, 1, 0.85f).spawn(ctx.world());
            return true;
        }
        int radius = property(DefaultProperties.EFFECT_RADIUS)
                + (int) ((ctx.modifiers().get(SpellModifiers.BLAST) - 1) / 0.25f + 0.5f) - 1;
        int lifetime = (int) (property(BLOCK_LIFETIME) * ctx.modifiers().get(SpellModifiers.DURATION));
        boolean placed = false;
        for (BlockPos current : BlockUtil.getBlockSphere(pos, radius)) {
            if (current.getY() != pos.getY()) continue;
            if (BlockUtil.canBlockBeReplaced(ctx.world(), current)
                    && BlockUtil.canPlaceBlock(ctx.caster(), ctx.world(), current)) {
                ctx.world().setBlockAndUpdate(current, EBBlocks.SPECTRAL_BLOCK.get().defaultBlockState());
                ctx.world().scheduleTick(current.immutable(), EBBlocks.SPECTRAL_BLOCK.get(), lifetime);
                placed = true;
            }
        }
        if (placed) this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return placed;
    }
}
