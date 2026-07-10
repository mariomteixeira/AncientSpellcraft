package com.windanesz.ancientspellcraft.spell;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.spell.properties.SpellProperties;
import com.koomplo.wizardry.content.spell.DefaultProperties;
import com.windanesz.ancientspellcraft.registry.ASEffects;
import com.windanesz.ancientspellcraft.registry.ASItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

/**
 * Eagle Eye (1.12.2 EagleEye): visão aérea — o client trava a câmera 50 blocos acima do ponto de
 * cast enquanto o efeito durar. Recast ou sneak-cast remove; só funciona sob céu aberto.
 */
public class EagleEyeSpell extends Spell {

    @Override
    public boolean cast(PlayerCastContext ctx) {
        Player caster = ctx.caster();

        if (caster.hasEffect(ASEffects.EAGLE_EYE) || caster.isShiftKeyDown()) {
            caster.removeEffect(ASEffects.EAGLE_EYE);
            return true;
        }

        if (!ctx.world().canSeeSky(caster.blockPosition())) {
            if (!ctx.world().isClientSide) {
                caster.displayClientMessage(Component.translatable("spell.ancientspellcraft.eagle_eye.indoor"), true);
            }
            return false;
        }

        if (!ctx.world().isClientSide) {
            int duration = (int) (property(DefaultProperties.EFFECT_DURATION) * ctx.modifiers().get(SpellModifiers.DURATION));
            caster.addEffect(new MobEffectInstance(ASEffects.EAGLE_EYE, duration, 0));
        }
        this.playSound(ctx.world(), caster, ctx.castingTicks(), -1);
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
