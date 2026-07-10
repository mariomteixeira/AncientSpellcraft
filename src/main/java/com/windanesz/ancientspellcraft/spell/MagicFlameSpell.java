package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.windanesz.ancientspellcraft.block.MagicFlameBlockEntity;
import com.windanesz.ancientspellcraft.block.TemporaryBlockEntity;
import com.windanesz.ancientspellcraft.registry.ASBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Ray que planta uma chama mágica (1.12.2 ArcaneFlame/WildfireFlame): lifetime = 30s * potência.
 * arcane usa o bloco temporário existente; wildfire tem BE próprio que persegue inimigos
 * (spreadRadius = 2 * blast).
 */
public class MagicFlameSpell extends ASRaySpell {

    private final boolean wildfire;

    public MagicFlameSpell(boolean wildfire) {
        this.wildfire = wildfire;
    }

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        if (ctx.world().isClientSide) return true;
        BlockPos pos = blockHit.getBlockPos().relative(blockHit.getDirection());
        if (!ctx.world().getBlockState(pos).canBeReplaced()) return false;
        int lifetime = (int) (30 * 20 * ctx.modifiers().get(SpellModifiers.POTENCY));

        if (!wildfire) {
            TemporaryBlockEntity.place(ctx.caster(), ctx.world(), ASBlocks.ARCANE_FLAME.get(), pos, lifetime);
            return true;
        }
        ctx.world().setBlockAndUpdate(pos, ASBlocks.WILDFIRE_FLAME.get().defaultBlockState());
        if (ctx.world().getBlockEntity(pos) instanceof MagicFlameBlockEntity flame) {
            flame.setLifetime(lifetime);
            if (ctx.caster() != null) flame.setCaster(ctx.caster());
            flame.setSpreadRadius(2 * ctx.modifiers().get(SpellModifiers.BLAST));
        }
        return true;
    }

    @Override
    protected boolean onMiss(CastContext ctx, Vec3 origin, Vec3 direction) {
        return false;
    }
}
