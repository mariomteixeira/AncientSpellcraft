package com.windanesz.ancientspellcraft.handler;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.core.platform.Services;
import com.windanesz.ancientspellcraft.registry.ASAttachments;
import com.windanesz.ancientspellcraft.spell.ContingencySpell;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * Gatilhos das contingências (1.12.2 ASEventHandler): fogo/afogamento/dano/vida crítica no
 * LivingIncomingDamage, queda no LivingFall, morte no LivingDeath. Disparo remove a contingência.
 */
public final class ASContingencyEvents {

    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) return;
        var source = event.getSource();
        if (source.is(net.minecraft.tags.DamageTypeTags.IS_FIRE)) {
            trigger(player, ContingencySpell.Type.FIRE);
        } else if (source.is(net.minecraft.world.damagesource.DamageTypes.DROWN)) {
            trigger(player, ContingencySpell.Type.DROWNING);
        } else {
            trigger(player, ContingencySpell.Type.DAMAGE);
        }
        // 1.12.2: dispara se a vida está (ou vai ficar) abaixo de 25%
        if (player.getHealth() <= player.getMaxHealth() * 0.25F
                || player.getHealth() - event.getAmount() <= player.getMaxHealth() * 0.25F) {
            trigger(player, ContingencySpell.Type.CRITICAL_HEALTH);
        }
    }

    public static void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide && event.getDistance() > 3) {
            trigger(player, ContingencySpell.Type.FALL);
        }
    }

    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            trigger(player, ContingencySpell.Type.DEATH);
        }
    }


    /** IMMOBILITY (1.12.2 onPotionAddedEvent): efeito da lista aplicado ao player dispara. */
    public static void onEffectAdded(net.neoforged.neoforge.event.entity.living.MobEffectEvent.Added event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) return;
        ResourceLocation id = net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.getKey(
                event.getEffectInstance().getEffect().value());
        if (id != null && com.windanesz.ancientspellcraft.ASServerConfig.IMMOBILITY_CONTINGENCY_EFFECTS.get().contains(id)) {
            trigger(player, ContingencySpell.Type.IMMOBILITY);
        }
    }

    /** HOSTILE_SPELLCAST (1.12.2): spell de ataque/alteração/minion/projétil castada por não-aliado
     * num raio de 20 do player dispara. */
    public static void onSpellCastPost(com.koomplo.wizardry.api.content.event.SpellCastEvent.Post event) {
        if (event.getCaster() == null || event.getCaster().level().isClientSide) return;
        var type = event.getSpell().getType();
        String name = type.name().toLowerCase();
        if (!name.equals("alteration") && !name.equals("attack") && !name.equals("minion") && !name.equals("projectile")) return;
        for (var target : com.koomplo.wizardry.api.content.util.EntityUtil.getLivingWithinRadius(
                20, event.getCaster().getX(), event.getCaster().getY(), event.getCaster().getZ(), event.getCaster().level())) {
            if (target instanceof Player player && player != event.getCaster()
                    && !com.koomplo.wizardry.core.AllyDesignation.isAllied(player, event.getCaster())) {
                trigger(player, ContingencySpell.Type.HOSTILE_SPELLCAST);
            }
        }
    }

    private static void trigger(Player player, ContingencySpell.Type type) {
        CompoundTag tag = player.getData(ASAttachments.PLAYER_DATA);
        String key = ContingencySpell.STORED_PREFIX + type.name();
        if (!tag.contains(key)) return;
        Spell spell = Services.REGISTRY_UTIL.getSpell(ResourceLocation.tryParse(tag.getString(key)));
        // ring_eternal_contingency (1.12.2): a contingência dispara sem ser consumida; o anel entra
        // em cooldown de cooldown_da_spell*10 + (tier+1)*500 ticks
        var ring = com.windanesz.ancientspellcraft.registry.ASItems.RING_ETERNAL_CONTINGENCY.get();
        boolean preserve = spell != null
                && com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(player, ring)
                && !player.getCooldowns().isOnCooldown(ring);
        if (!preserve) {
            tag.remove(key);
            player.setData(ASAttachments.PLAYER_DATA, tag);
        }
        if (spell == null) return;
        if (preserve) {
            player.getCooldowns().addCooldown(ring,
                    spell.getCooldown() * 10 + (spell.getTier().getLevel() + 1) * 500);
        }
        spell.cast(new PlayerCastContext(player.level(), player, InteractionHand.MAIN_HAND, 0, new SpellModifiers()));
        player.level().playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1.0F, 0.7F);
    }

    private ASContingencyEvents() {
    }
}
