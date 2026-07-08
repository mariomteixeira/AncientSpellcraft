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

/** Revela mortos-vivos num raio com glowing (1.12.2 RevealUndead). */
public class RevealUndead extends Spell {

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }

    @Override
    public boolean cast(PlayerCastContext ctx) {
        double radius = property(DefaultProperties.EFFECT_RADIUS) * ctx.modifiers().get(SpellModifiers.BLAST);
        if (!ctx.world().isClientSide) {
            int count = 0;
            for (LivingEntity entity : EntityUtil.getLivingWithinRadius(radius, ctx.caster().getX(), ctx.caster().getY(), ctx.caster().getZ(), ctx.world())) {
                if (entity.getType().is(net.minecraft.tags.EntityTypeTags.UNDEAD)) {
                    entity.addEffect(new MobEffectInstance(MobEffects.GLOWING,
                            (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(SpellModifiers.DURATION)), 0));
                    count++;
                }
            }
            ctx.caster().displayClientMessage(net.minecraft.network.chat.Component.translatable(
                    "spell.ancientspellcraft.reveal_undead.count", count), false);
        } else {
            for (int i = 0; i < 30; i++) {
                ParticleBuilder.create(EBParticles.SPARKLE)
                        .pos(ctx.caster().getX() - 1 + ctx.world().random.nextDouble() * 2,
                                ctx.caster().getEyeY() - 0.25 + ctx.world().random.nextDouble() * 0.5,
                                ctx.caster().getZ() - 1 + ctx.world().random.nextDouble() * 2)
                        .velocity(0, 0.1, 0).spawn(ctx.world());
            }
        }
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }
}
