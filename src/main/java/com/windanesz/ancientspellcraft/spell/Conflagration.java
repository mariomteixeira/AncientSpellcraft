package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.koomplo.wizardry.api.content.util.BlockUtil;
import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.koomplo.wizardry.api.content.util.MagicDamageSource;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.core.AllyDesignation;
import com.koomplo.wizardry.setup.registries.EBDamageSources;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

/** Explosao de fogo em area: incendeia o terreno e fere pelo raio (1.12.2 Conflagration). */
public class Conflagration extends Spell {

    public static final SpellProperty<Integer> BURN_DURATION = SpellProperty.intProperty("burn_duration");
    public static final SpellProperty<Float> MAX_DAMAGE = SpellProperty.floatProperty("max_damage");

    @Override
    public boolean cast(PlayerCastContext ctx) {
        float radius = property(DefaultProperties.EFFECT_RADIUS) * ctx.modifiers().get(SpellModifiers.BLAST);
        if (!ctx.world().isClientSide) {
            if (EntityUtil.canDamageBlocks(ctx.caster(), ctx.world())) {
                for (BlockPos pos : BlockUtil.getBlockSphere(ctx.caster().blockPosition(), radius)) {
                    if (pos.distSqr(ctx.caster().blockPosition()) < 2) continue;
                    if (ctx.world().random.nextInt(4) <= 2 && ctx.world().isEmptyBlock(pos.above())
                            && !ctx.world().isEmptyBlock(pos)) {
                        ctx.world().setBlockAndUpdate(pos.above(), Blocks.FIRE.defaultBlockState());
                    }
                }
            }
            for (LivingEntity target : EntityUtil.getLivingWithinRadius(radius,
                    ctx.caster().getX(), ctx.caster().getY(), ctx.caster().getZ(), ctx.world())) {
                if (target == ctx.caster() || !AllyDesignation.isValidTarget(ctx.caster(), target)) continue;
                float damage = Math.max(property(MAX_DAMAGE)
                        - target.distanceTo(ctx.caster()) * 4, 0) * ctx.modifiers().get(SpellModifiers.POTENCY);
                target.hurt(MagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.BLAST), damage);
                target.igniteForSeconds(property(BURN_DURATION));
            }
        } else {
            ctx.world().addParticle(net.minecraft.core.particles.ParticleTypes.EXPLOSION_EMITTER,
                    ctx.caster().getX(), ctx.caster().getY() + 0.5, ctx.caster().getZ(), 0, 0, 0);
        }
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }
}
