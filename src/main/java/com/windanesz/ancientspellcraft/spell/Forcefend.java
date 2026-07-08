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

/** Deflete projeteis num raio de 3 blocos enquanto canaliza (1.12.2 Forcefend). */
public class Forcefend extends Spell {

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
        var caster = ctx.caster();
        var box = new net.minecraft.world.phys.AABB(caster.getX() - 3, caster.getY() - 3, caster.getZ() - 3,
                caster.getX() + 3, caster.getY() + 3, caster.getZ() + 3);
        for (var projectile : ctx.world().getEntitiesOfClass(net.minecraft.world.entity.projectile.Projectile.class, box)) {
            if (projectile.getOwner() == caster) continue;
            var away = projectile.position().subtract(caster.position()).normalize().scale(0.3);
            projectile.push(away.x, away.y, away.z);
            if (ctx.world().isClientSide) {
                ParticleBuilder.create(EBParticles.SPARK, projectile).spawn(ctx.world());
            }
        }
        this.playSoundLoop(ctx.world(), ctx.caster(), ctx.castingTicks());
        return true;
    }
}
