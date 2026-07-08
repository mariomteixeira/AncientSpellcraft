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

/** Ancora os proprios minions: estende o lifetime deles (1.12.2 SummonAnchor). */
public class SummonAnchor extends Spell {

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }

    public static final SpellProperty<Integer> ANCHOR_DURATION = SpellProperty.intProperty("duration");
    public static final SpellProperty<Integer> MAX_AFFECTED_ENTITIES = SpellProperty.intProperty("max_affected_entities");

    @Override
    public boolean cast(PlayerCastContext ctx) {
        double radius = property(DefaultProperties.BLAST_RADIUS) * ctx.modifiers().get(SpellModifiers.BLAST);
        int duration = Math.round(property(ANCHOR_DURATION) * ctx.modifiers().get(SpellModifiers.DURATION));
        boolean foundSummon = false;
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        int entityCount = property(MAX_AFFECTED_ENTITIES);
        for (LivingEntity target : EntityUtil.getLivingWithinRadius(radius,
                ctx.caster().getX(), ctx.caster().getY(), ctx.caster().getZ(), ctx.world())) {
            if (!(target instanceof net.minecraft.world.entity.Mob mob)) continue;
            var data = mob.getData(EBAttachments.MINION_DATA);
            if (!data.isSummoned() || data.getOwner() != ctx.caster()) continue;
            if (--entityCount < 0) break;
            foundSummon = true;
            if (!ctx.world().isClientSide) {
                data.setLifetime(data.getLifetime() + duration);
            } else {
                ParticleBuilder.create(EBParticles.SPARKLE, mob).color(0x7bd6f0).spawn(ctx.world());
            }
        }
        return foundSummon;
    }
}
