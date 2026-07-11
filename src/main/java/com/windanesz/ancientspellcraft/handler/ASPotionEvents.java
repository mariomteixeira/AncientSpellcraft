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
        loopPlayer(player);
    }

    /** amulet_pendant_of_eternity: quando um buff da spell encaixada expira, re-casta a spell (cd 1s). */
    public static void onPendantBuffExpired(MobEffectEvent.Expired event) {
        MobEffectInstance instance = event.getEffectInstance();
        if (instance == null || !(event.getEntity() instanceof net.minecraft.world.entity.player.Player player)
                || player.level().isClientSide) return;
        for (var artifact : com.koomplo.wizardry.core.integrations.ArtifactChannel.getEquippedArtifacts(player)) {
            if (!artifact.is(com.windanesz.ancientspellcraft.registry.ASItems.AMULET_PENDANT_OF_ETERNITY.get())) continue;
            if (player.getCooldowns().isOnCooldown(artifact.getItem())) return;
            var book = com.windanesz.ancientspellcraft.item.SocketedArtifactItem.getSocketed(
                    artifact, player.level().registryAccess());
            if (book.isEmpty()) return;
            var spell = com.koomplo.wizardry.api.content.util.RegistryUtils.getSpell(book);
            if (!(spell instanceof com.koomplo.wizardry.content.spell.abstr.BuffSpell buff)) return;
            if (!buff.getMobEffects().contains(instance.getEffect())) return;
            player.getCooldowns().addCooldown(artifact.getItem(), 20);
            spell.cast(new com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext(
                    player.level(), player, net.minecraft.world.InteractionHand.MAIN_HAND, 0,
                    new com.koomplo.wizardry.api.content.spell.internal.SpellModifiers()));
            return;
        }
    }

    /** Restaura o jogador ao ponto gravado pelo time_knot (também usado pelo amulet_time_knot). */
    public static boolean loopPlayer(net.minecraft.server.level.ServerPlayer player) {
        net.minecraft.nbt.CompoundTag tag = player.getData(com.windanesz.ancientspellcraft.registry.ASAttachments.TIME_KNOT);
        if (tag.isEmpty()) return false;
        net.minecraft.resources.ResourceLocation dim = net.minecraft.resources.ResourceLocation.tryParse(tag.getString("Dim"));
        net.minecraft.server.level.ServerLevel level = dim == null ? player.serverLevel()
                : player.getServer().getLevel(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, dim));
        if (level == null) level = player.serverLevel();
        player.teleportTo(level, tag.getDouble("X"), tag.getDouble("Y"), tag.getDouble("Z"), player.getYRot(), player.getXRot());
        player.setHealth(tag.getFloat("Health"));
        player.setData(com.windanesz.ancientspellcraft.registry.ASAttachments.TIME_KNOT, new net.minecraft.nbt.CompoundTag());
        return true;
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
        boolean isCurse = event.getEffectInstance().getEffect().value() instanceof CurseMobEffect;

        if (isCurse) {
            MobEffectInstance curseWard = event.getEntity().getEffect(ASEffects.CURSE_WARD);
            if (curseWard != null) {
                int newDuration = curseWard.getDuration() / 2;
                event.getEntity().removeEffect(ASEffects.CURSE_WARD);
                event.getEntity().addEffect(new MobEffectInstance(ASEffects.CURSE_WARD, newDuration));
                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
                return;
            }
        }

        // onda 2c: amuletos que filtram efeitos (1.12.2 onPotionApplicableEvent)
        if (!(event.getEntity() instanceof net.minecraft.world.entity.player.Player player)) return;
        var effect = event.getEffectInstance().getEffect();
        var random = player.level().random;

        // belt_temporal_anchor: imune a slowness
        if (effect.value() == net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN.value()
                && com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(player,
                com.windanesz.ancientspellcraft.registry.ASItems.BELT_TEMPORAL_ANCHOR.get())) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            return;
        }
        // amulet_poison_resistance: 50% de negar veneno
        if (effect.value() == net.minecraft.world.effect.MobEffects.POISON.value()
                && com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(player,
                com.windanesz.ancientspellcraft.registry.ASItems.AMULET_POISON_RESISTANCE.get())
                && random.nextFloat() < 0.5f) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            return;
        }
        // amulet_persistence: imune a shrinkage/growth
        if ((effect.value() == ASEffects.SHRINKAGE.get() || effect.value() == ASEffects.GROWTH.get())
                && com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(player,
                com.windanesz.ancientspellcraft.registry.ASItems.AMULET_PERSISTENCE.get())) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            return;
        }
        if (isCurse) {
            // amulet_curse_ward (artefato): nega maldições
            if (com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(player,
                    com.windanesz.ancientspellcraft.registry.ASItems.AMULET_CURSE_WARD.get())) {
                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
                return;
            }
            // amulet_cursed_mirror: 50% de espelhar a maldição em inimigos conjuradores num raio de 12
            if (com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(player,
                    com.windanesz.ancientspellcraft.registry.ASItems.AMULET_CURSED_MIRROR.get())
                    && random.nextBoolean()) {
                for (var target : com.koomplo.wizardry.api.content.util.EntityUtil.getLivingWithinRadius(
                        12, player.getX(), player.getY(), player.getZ(), player.level())) {
                    if (target == player || com.koomplo.wizardry.core.AllyDesignation.isAllied(player, target)) continue;
                    if (target instanceof net.minecraft.world.entity.player.Player
                            || target instanceof com.koomplo.wizardry.api.content.entity.living.ISpellCaster) {
                        target.addEffect(new MobEffectInstance(event.getEffectInstance()));
                    }
                }
            }
        }
        // amulet_absorb_potion: nega o efeito ruim absorvido pelo absorb_potion
        if (!effect.value().isBeneficial()
                && com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(player,
                com.windanesz.ancientspellcraft.registry.ASItems.AMULET_ABSORB_POTION.get())) {
            String absorbed = player.getData(com.windanesz.ancientspellcraft.registry.ASAttachments.WARLOCK_DATA).getString("Effect");
            var id = net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.getKey(effect.value());
            if (!absorbed.isEmpty() && id != null && absorbed.equals(id.toString())) {
                event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            }
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
