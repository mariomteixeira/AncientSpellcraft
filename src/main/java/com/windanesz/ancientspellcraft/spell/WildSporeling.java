package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.internal.CastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.block.MagicMushroomBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

/** Planta um cogumelo de combate aleatorio (1.12.2 WildSporeling). */
public class WildSporeling extends ASRaySpell {

    public static final SpellProperty<Integer> BLOCK_LIFETIME = SpellProperty.intProperty("block_lifetime");

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        return false;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        BlockPos pos = blockHit.getBlockPos().relative(blockHit.getDirection());
        if (ctx.world().isClientSide) {
            ParticleBuilder.create(EBParticles.FLASH).pos(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5)
                    .scale(2).time(20).color(78 / 255f, 168 / 255f, 50 / 255f).spawn(ctx.world());
            return true;
        }
        return MagicMushroomBlock.tryPlace(ctx.world(), pos, ctx.caster(),
                MagicMushroomBlock.randomMushroom(ctx.world(), true), property(BLOCK_LIFETIME),
                ctx.modifiers().get(SpellModifiers.POTENCY));
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.LEAF).pos(x, y, z).spawn(ctx.world());
    }
}
