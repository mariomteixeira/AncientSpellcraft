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

/** Planta um cogumelo benefico aleatorio (1.12.2 SporelingsAid). */
public class SporelingsAid extends ASRaySpell {

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
                    .scale(2).time(20).color(120 / 255f, 200 / 255f, 90 / 255f).spawn(ctx.world());
            return true;
        }
        return MagicMushroomBlock.tryPlace(ctx.world(), pos, ctx.caster(),
                MagicMushroomBlock.randomMushroom(ctx.world(), false), property(BLOCK_LIFETIME),
                ctx.modifiers().get(SpellModifiers.POTENCY));
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.SPARKLE).pos(x, y, z).color(0x9be07c).spawn(ctx.world());
    }
}
