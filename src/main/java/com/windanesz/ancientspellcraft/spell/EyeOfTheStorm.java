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

/** Olho da tempestade: a cada 3s dispara 3 flechas de raio em cada inimigo em 15 blocos (1.12.2 EyeOfTheStorm). */
public class EyeOfTheStorm extends Spell {

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }

    @Override
    public boolean isInstantCast() {
        return false;
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        if (ctx.castingTicks() == 0 || ctx.castingTicks() % 30 == 0) {
            this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        }
        if (ctx.castingTicks() > 1 && ctx.castingTicks() % 60 == 0) {
            if (!ctx.world().isClientSide) {
                for (LivingEntity entity : EntityUtil.getLivingWithinRadius(15,
                        ctx.caster().getX(), ctx.caster().getY(), ctx.caster().getZ(), ctx.world())) {
                    if (entity == ctx.caster() || AllyDesignation.isAllied(ctx.caster(), entity)) continue;
                    for (int i = 0; i < 3; i++) {
                        var arrow = new com.koomplo.wizardry.content.entity.projectile.LightningArrow(
                                com.koomplo.wizardry.setup.registries.EBEntities.LIGHTNING_ARROW.get(), ctx.world());
                        arrow.setOwner(ctx.caster());
                        arrow.setPos(ctx.caster().getX(), ctx.caster().getEyeY(), ctx.caster().getZ());
                        arrow.aim(ctx.caster(), entity, 0.6F, 0F);
                        arrow.damageMultiplier = ctx.modifiers().get(SpellModifiers.POTENCY);
                        ctx.world().addFreshEntity(arrow);
                    }
                }
            }
        } else if (ctx.world().isClientSide) {
            ParticleBuilder.create(EBParticles.SPARK)
                    .pos(ctx.caster().getX() + (ctx.world().random.nextDouble() * 6 - 3),
                            ctx.caster().getY() + ctx.world().random.nextDouble() * 3,
                            ctx.caster().getZ() + (ctx.world().random.nextDouble() * 6 - 3))
                    .spawn(ctx.world());
        }
        return true;
    }
}
