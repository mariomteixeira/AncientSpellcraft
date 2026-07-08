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

/** Atrai itens dropados num raio enquanto canaliza (1.12.2 ArcaneMagnetism). */
public class ArcaneMagnetism extends Spell {

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
        double radius = property(DefaultProperties.EFFECT_RADIUS) * ctx.modifiers().get(SpellModifiers.BLAST);
        var caster = ctx.caster();
        var box = new net.minecraft.world.phys.AABB(caster.getX() - radius, caster.getY() - radius, caster.getZ() - radius,
                caster.getX() + radius, caster.getY() + radius, caster.getZ() + radius);
        for (var item : ctx.world().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class, box)) {
            var pull = caster.position().add(0, 0.5, 0).subtract(item.position()).normalize().scale(0.15);
            item.push(pull.x, pull.y, pull.z);
            if (ctx.world().isClientSide && ctx.world().random.nextInt(4) == 0) {
                ParticleBuilder.create(EBParticles.SPARKLE, item).color(0xc558d6).spawn(ctx.world());
            }
        }
        this.playSoundLoop(ctx.world(), ctx.caster(), ctx.castingTicks());
        return true;
    }
}
