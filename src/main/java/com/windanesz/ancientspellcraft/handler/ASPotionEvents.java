package com.windanesz.ancientspellcraft.handler;

import com.koomplo.wizardry.api.content.effect.CurseMobEffect;
import com.koomplo.wizardry.api.content.event.SpellCastEvent;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.windanesz.ancientspellcraft.registry.ASEffects;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

/**
 * Lógica de evento das potions do 1.12.2 que a carregavam via @SubscribeEvent.
 * TODO astral_projection e eagle_eye: dependem do sistema de câmera client (astral travel) — portam juntos.
 */
public final class ASPotionEvents {

    /** curse_of_death mata quando o efeito expira (1.12.2 PotionCurseDeath). */
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        MobEffectInstance instance = event.getEffectInstance();
        if (instance != null && instance.getEffect().value() == ASEffects.CURSE_OF_DEATH.get()
                && event.getEntity() instanceof Player player && !player.level().isClientSide) {
            player.hurt(player.damageSources().source(DamageTypes.MAGIC), Float.MAX_VALUE);
        }
    }

    /** time_knot: no expiry, volta o caster para a posicao/vida do cast (1.12.2 TimeKnot). */
    public static void onTimeKnotExpired(MobEffectEvent.Expired event) {
        MobEffectInstance instance = event.getEffectInstance();
        if (instance == null || instance.getEffect().value() != com.windanesz.ancientspellcraft.registry.ASEffects.TIME_KNOT.get()) return;
        if (!(event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player)) return;

        net.minecraft.nbt.CompoundTag tag = player.getData(com.windanesz.ancientspellcraft.registry.ASAttachments.TIME_KNOT);
        if (tag.isEmpty()) return;
        net.minecraft.resources.ResourceLocation dim = net.minecraft.resources.ResourceLocation.tryParse(tag.getString("Dim"));
        net.minecraft.server.level.ServerLevel level = dim == null ? player.serverLevel()
                : player.getServer().getLevel(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, dim));
        if (level == null) level = player.serverLevel();
        player.teleportTo(level, tag.getDouble("X"), tag.getDouble("Y"), tag.getDouble("Z"), player.getYRot(), player.getXRot());
        player.setHealth(tag.getFloat("Health"));
        player.setData(com.windanesz.ancientspellcraft.registry.ASAttachments.TIME_KNOT, new net.minecraft.nbt.CompoundTag());
    }

    /** soul_scorch reduz toda cura a 40% (1.12.2 PotionSoulScorch). */
    public static void onLivingHeal(LivingHealEvent event) {
        if (event.getEntity().hasEffect(ASEffects.SOUL_SCORCH)) {
            event.setAmount(event.getAmount() * 0.4f);
        }
    }

    /** bubble_head estoura ao tomar dano (1.12.2 PotionBubbleHead). */
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        if (event.getEntity().hasEffect(ASEffects.BUBBLE_HEAD)) {
            event.getEntity().removeEffect(ASEffects.BUBBLE_HEAD);
        }
    }

    /** curse_ward bloqueia curses novas ao custo de metade da própria duração (1.12.2 PotionCurseWard). */
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getEffectInstance().getEffect().value() instanceof CurseMobEffect)) return;

        MobEffectInstance curseWard = event.getEntity().getEffect(ASEffects.CURSE_WARD);
        if (curseWard != null) {
            int newDuration = curseWard.getDuration() / 2;
            event.getEntity().removeEffect(ASEffects.CURSE_WARD);
            event.getEntity().addEffect(new MobEffectInstance(ASEffects.CURSE_WARD, newDuration));
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
    }

    /** unlimited_power multiplica os modifiers do cast (1.12.2 PotionUnlimitedPower; IF_POTENCY_PER_LEVEL=0.5, IF_MODIFIERS_PER_LEVEL=1). */
    public static void onSpellCastPre(SpellCastEvent.Pre event) {
        if (event.getCaster() == null) return;
        MobEffectInstance effect = event.getCaster().getEffect(ASEffects.UNLIMITED_POWER);
        if (effect == null) return;

        int amplifier = effect.getAmplifier() + 1;
        float potency = 1 + 0.5f * amplifier * 0.5f;
        float upgradeModifier = 1 + 1f * amplifier * 0.5f;

        SpellModifiers modifiers = event.getModifiers();
        modifiers.set(SpellModifiers.POTENCY, modifiers.get(SpellModifiers.POTENCY) * potency);
        modifiers.set(SpellModifiers.BLAST, modifiers.get(SpellModifiers.BLAST) * upgradeModifier);
        modifiers.set(SpellModifiers.RANGE, modifiers.get(SpellModifiers.RANGE) * upgradeModifier);
        modifiers.set(SpellModifiers.DURATION, modifiers.get(SpellModifiers.DURATION) * upgradeModifier);
        modifiers.set(SpellModifiers.COOLDOWN, modifiers.get(SpellModifiers.COOLDOWN) * upgradeModifier);
    }

    private ASPotionEvents() {
    }
}
