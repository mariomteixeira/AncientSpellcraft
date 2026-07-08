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

/** Atravessa o chao como um fantasma enquanto canaliza (1.12.2 Burrow; guarda de seguranca reseta noPhysics). */
public class Burrow extends Spell {

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
        if (!caster.onGround() && !caster.noPhysics) return false;

        if (caster.getY() < ctx.world().getMinBuildHeight() + 15) {
            if (!ctx.world().isClientSide && ctx.castingTicks() % 20 == 0) {
                caster.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                        "spell.ancientspellcraft.burrow.y_limit"), false);
            }
            return false;
        }
        var below = ctx.world().getBlockState(caster.blockPosition().below());
        if (below.getDestroySpeed(ctx.world(), caster.blockPosition().below()) < 0
                || below.is(net.minecraft.world.level.block.Blocks.BEDROCK)) {
            if (!ctx.world().isClientSide && ctx.castingTicks() % 20 == 0) {
                caster.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                        "spell.ancientspellcraft.burrow.hard_material"), false);
            }
            return false;
        }

        if (!ctx.world().isClientSide) {
            caster.addEffect(new MobEffectInstance(ASEffects.BURROW, 20));
            if (ctx.castingTicks() % 20 == 0) {
                caster.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 41));
                caster.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 41, 2));
                caster.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 81));
            }
        }
        if (!ctx.world().isEmptyBlock(caster.blockPosition().below())) {
            caster.noPhysics = true; // resetado pelo guarda em ASSpellEvents quando o cast para
        }
        var motion = caster.getDeltaMovement();
        if (motion.y < 0) caster.setDeltaMovement(motion.x, motion.y * 0.5, motion.z);
        this.playSoundLoop(ctx.world(), ctx.caster(), ctx.castingTicks());
        return true;
    }
}
