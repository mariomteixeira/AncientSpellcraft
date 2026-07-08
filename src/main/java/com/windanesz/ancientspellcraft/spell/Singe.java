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

/** Dano de fogo, multiplicado se o alvo ja esta queimando (1.12.2 Singe). */
public class Singe extends ASRaySpell {

    public static final SpellProperty<Float> MULTIPLIER_ON_FIRE = SpellProperty.floatProperty("damage_multiplier_on_fire");

    @Override
    protected boolean onEntityHit(CastContext ctx, EntityHitResult entityHit, Vec3 origin) {
        if (!(entityHit.getEntity() instanceof LivingEntity target)) return false;
        float damage = property(DefaultProperties.DAMAGE) * ctx.modifiers().get(SpellModifiers.POTENCY);
        if (target.isOnFire()) damage *= property(MULTIPLIER_ON_FIRE);

        if (MagicDamageSource.isEntityImmune(EBDamageSources.FIRE, target)) {
            if (!ctx.world().isClientSide && ctx.caster() instanceof Player player) {
                player.displayClientMessage(net.minecraft.network.chat.Component.translatable("spell.resist",
                        target.getName(), getDescriptionFormatted()), true);
            }
            return true;
        }
        if (!ctx.world().isClientSide) {
            target.hurt(MagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.FIRE), damage);
        }
        return true;
    }

    @Override
    protected boolean onBlockHit(CastContext ctx, BlockHitResult blockHit, Vec3 origin) {
        return false;
    }

    @Override
    protected void spawnParticle(CastContext ctx, double x, double y, double z, double vx, double vy, double vz) {
        ParticleBuilder.create(EBParticles.MAGIC_FIRE).pos(x, y, z).spawn(ctx.world());
    }
}
