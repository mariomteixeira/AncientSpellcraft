package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.client.ParticleBuilder;
import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperty;
import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.koomplo.wizardry.api.content.util.MagicDamageSource;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.koomplo.wizardry.core.AllyDesignation;
import com.koomplo.wizardry.setup.registries.EBAttachments;
import com.koomplo.wizardry.setup.registries.EBDamageSources;
import com.koomplo.wizardry.setup.registries.client.EBParticles;
import com.windanesz.ancientspellcraft.registry.ASEffects;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

/** Incendeia todos os inimigos ao redor enquanto canaliza (1.12.2 MassPyrokinesis). */
public class MassPyrokinesis extends Spell {

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }

    public static final SpellProperty<Integer> BURN_DURATION = SpellProperty.intProperty("burn_duration");
    public static final SpellProperty<Integer> SLOW_DURATION = SpellProperty.intProperty("slow_duration");

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    public boolean canCastByEntity() {
        return true;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        for (LivingEntity entity : EntityUtil.getLivingWithinRadius(property(DefaultProperties.EFFECT_RADIUS),
                ctx.caster().getX(), ctx.caster().getY(), ctx.caster().getZ(), ctx.world())) {
            if (entity == ctx.caster() || AllyDesignation.isAllied(ctx.caster(), entity)) continue;
            if (MagicDamageSource.isEntityImmune(EBDamageSources.FIRE, entity)) continue;
            if (!ctx.world().isClientSide && entity.tickCount % 20 == 1) {
                entity.igniteForSeconds(property(BURN_DURATION));
                EntityUtil.attackEntityWithoutKnockback(entity,
                        MagicDamageSource.causeDirectMagicDamage(ctx.caster(), EBDamageSources.FIRE),
                        property(DefaultProperties.DAMAGE) * ctx.modifiers().get(SpellModifiers.POTENCY));
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, property(SLOW_DURATION), 1));
            } else if (ctx.world().isClientSide) {
                ParticleBuilder.create(EBParticles.MAGIC_FIRE, entity).spawn(ctx.world());
            }
        }
        this.playSoundLoop(ctx.world(), ctx.caster(), ctx.castingTicks());
        return true;
    }
}
