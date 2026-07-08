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

/** Cauteriza as feridas: fogo em si mesmo, dano FIRE reduzido por fire protection e regeneracao (1.12.2 Cauterize). */
public class Cauterize extends Spell {

    @Override
    public boolean applicableForItem(Item item) {
        return item == ASItems.ANCIENT_SPELLCRAFT_SPELL_BOOK.get() || item == ASItems.ANCIENT_SPELLCRAFT_SCROLL.get();
    }

    @Override
    protected @NotNull SpellProperties properties() {
        return SpellProperties.empty();
    }

    public static final SpellProperty<Integer> BURN_DURATION = SpellProperty.intProperty("burn_duration");
    public static final SpellProperty<Integer> REGENERATION_DURATION = SpellProperty.intProperty("regeneration_duration");

    @Override
    public boolean cast(PlayerCastContext ctx) {
        var caster = ctx.caster();
        ctx.world().playSound(null, caster.blockPosition(), net.minecraft.sounds.SoundEvents.FLINTANDSTEEL_USE,
                net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, ctx.world().random.nextFloat() * 0.4F + 0.8F);

        boolean fireImmune = MagicDamageSource.isEntityImmune(EBDamageSources.FIRE, caster)
                || caster.hasEffect(MobEffects.FIRE_RESISTANCE);

        float efficiencyRatio = 1.0f;
        var registry = ctx.world().registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT);
        var fireProt = registry.getHolder(net.minecraft.world.item.enchantment.Enchantments.FIRE_PROTECTION).orElse(null);
        if (fireProt != null) {
            for (var slot : caster.getArmorSlots()) {
                int level = net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(fireProt, slot);
                efficiencyRatio = (float) Math.max(0.1, efficiencyRatio - level * 0.1);
            }
        }

        caster.igniteForSeconds(property(BURN_DURATION) / 20f);
        if (!fireImmune && !ctx.world().isClientSide) {
            EntityUtil.attackEntityWithoutKnockback(caster,
                    MagicDamageSource.causeDirectMagicDamage(caster, EBDamageSources.FIRE),
                    property(DefaultProperties.DAMAGE) * ctx.modifiers().get(SpellModifiers.POTENCY));
            caster.addEffect(new MobEffectInstance(MobEffects.REGENERATION,
                    (int) (efficiencyRatio * property(REGENERATION_DURATION)), 0));
        }
        return true;
    }
}
