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

/** Drena a vida dos proprios minions para se curar (1.12.2 WithdrawLife). */
public class WithdrawLife extends Spell {

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }

    public static final SpellProperty<Float> PERCENT_PER_MINION = SpellProperty.floatProperty("percent_per_minion");

    @Override
    public boolean cast(PlayerCastContext ctx) {
        float percentPerMinion = property(PERCENT_PER_MINION);
        int minions = 0;
        for (LivingEntity entity : EntityUtil.getLivingWithinRadius(property(DefaultProperties.EFFECT_RADIUS),
                ctx.caster().getX(), ctx.caster().getY(), ctx.caster().getZ(), ctx.world())) {
            if (entity == ctx.caster() || !(entity instanceof net.minecraft.world.entity.Mob mob)) continue;
            var data = mob.getData(EBAttachments.MINION_DATA);
            if (!data.isSummoned() || data.getOwner() != ctx.caster()) continue;
            minions++;
            if (!ctx.world().isClientSide) {
                float newHP = mob.getHealth() - mob.getMaxHealth() * percentPerMinion * 1.5f;
                if (newHP <= 0) mob.discard();
                else mob.setHealth(newHP);
            } else {
                ParticleBuilder.create(EBParticles.DARK_MAGIC, mob).color(0x8b0000).spawn(ctx.world());
            }
        }
        if (minions == 0) return false;
        if (!ctx.world().isClientSide) {
            ctx.caster().heal(ctx.caster().getMaxHealth() * percentPerMinion * minions);
        }
        this.playSound(ctx.world(), ctx.caster(), ctx.castingTicks(), -1);
        return true;
    }
}
