package com.windanesz.ancientspellcraft.handler;

import com.koomplo.wizardry.api.content.event.SpellCastEvent;
import com.koomplo.wizardry.api.content.item.IManaItem;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.core.config.EBServerConfig;
import com.windanesz.ancientspellcraft.registry.ASEffects;
import com.windanesz.ancientspellcraft.spell.MetamagicBuffSpell;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.List;

/**
 * Read-hooks do metamagic (1.12.2 ASEventHandler.onSpellCastPreEvent): efeitos metamagic ativos
 * alteram os modifiers do cast. spell_blast/range/cooldown/duration valem pela duração do buff;
 * arcane_augmentation/intensifying_focus/continuity_charm são one-shot (consumidos no cast).
 * spell_siphon: kill recarrega mana do primeiro item com mana da hotbar/offhand.
 * Consumir um one-shot põe a spell metamagic em cooldown de 1200t em todas as wands da hotbar
 * (1.12.2 onMetaMagicFinished/setCooldown).
 */
public final class ASMetamagicEvents {

    /** COST_REDUCTION_PER_ARMOUR do 1.12.2 (ASEventHandler): custo extra por nível do continuity_charm. */
    private static final float CONTINUITY_COST_PER_LEVEL = 0.15f;

    public static List<Holder<MobEffect>> metamagicEffects() {
        return List.of(ASEffects.SPELL_RANGE, ASEffects.SPELL_BLAST, ASEffects.SPELL_COOLDOWN,
                ASEffects.SPELL_DURATION, ASEffects.SPELL_SIPHON, ASEffects.ARCANE_AUGMENTATION,
                ASEffects.INTENSIFYING_FOCUS, ASEffects.CONTINUITY_CHARM);
    }

    public static void onSpellCastPre(SpellCastEvent.Pre event) {
        if (!(event.getCaster() instanceof Player player) || player.level().isClientSide) return;
        if (event.getSpell() instanceof MetamagicBuffSpell) return; // castar o buff não consome o anterior

        SpellModifiers modifiers = event.getModifiers();

        int level = effectLevel(player, ASEffects.SPELL_BLAST);
        if (level > 0) modifiers.set(SpellModifiers.BLAST,
                modifiers.get(SpellModifiers.BLAST) + level * EBServerConfig.BLAST_RADIUS_INCREASE_PER_LEVEL.get());

        level = effectLevel(player, ASEffects.SPELL_RANGE);
        if (level > 0) modifiers.set(SpellModifiers.RANGE,
                modifiers.get(SpellModifiers.RANGE) + level * EBServerConfig.RANGE_INCREASE_PER_LEVEL.get());

        level = effectLevel(player, ASEffects.SPELL_COOLDOWN);
        if (level > 0) modifiers.set(SpellModifiers.COOLDOWN,
                modifiers.get(SpellModifiers.COOLDOWN) - level * EBServerConfig.COOLDOWN_REDUCTION_PER_LEVEL.get());

        level = effectLevel(player, ASEffects.SPELL_DURATION);
        if (level > 0) modifiers.set(SpellModifiers.DURATION,
                modifiers.get(SpellModifiers.DURATION) + level * EBServerConfig.DURATION_INCREASE_PER_LEVEL.get());

        level = effectLevel(player, ASEffects.ARCANE_AUGMENTATION);
        if (level > 0) {
            modifiers.set(SpellModifiers.RANGE,
                    modifiers.get(SpellModifiers.RANGE) + level * EBServerConfig.RANGE_INCREASE_PER_LEVEL.get());
            modifiers.set(SpellModifiers.BLAST,
                    modifiers.get(SpellModifiers.BLAST) + level * EBServerConfig.BLAST_RADIUS_INCREASE_PER_LEVEL.get());
            if (shouldConsumeMetamagic(player)) consume(player, ASEffects.ARCANE_AUGMENTATION);
        }

        level = effectLevel(player, ASEffects.INTENSIFYING_FOCUS);
        if (level > 0) {
            modifiers.set(SpellModifiers.POTENCY,
                    modifiers.get(SpellModifiers.POTENCY) + level * EBServerConfig.POTENCY_INCREASE_PER_TIER.get());
            if (shouldConsumeMetamagic(player)) consume(player, ASEffects.INTENSIFYING_FOCUS);
        }

        level = effectLevel(player, ASEffects.CONTINUITY_CHARM);
        if (level > 0) {
            modifiers.set(SpellModifiers.DURATION,
                    modifiers.get(SpellModifiers.DURATION) + level * EBServerConfig.DURATION_INCREASE_PER_LEVEL.get());
            modifiers.set(SpellModifiers.COST,
                    modifiers.get(SpellModifiers.COST) + level * CONTINUITY_COST_PER_LEVEL);
            if (shouldConsumeMetamagic(player)) consume(player, ASEffects.CONTINUITY_CHARM);
        }

        // contingency (AS-11): listener armado -> captura a spell castada (não casta agora)
        var flags = player.getData(com.windanesz.ancientspellcraft.registry.ASAttachments.PLAYER_DATA);
        if (flags.contains(com.windanesz.ancientspellcraft.spell.ContingencySpell.LISTENER_TAG)) {
            var spell = event.getSpell();
            if (!(spell instanceof com.windanesz.ancientspellcraft.spell.ContingencySpell
                    || spell instanceof MetamagicBuffSpell
                    || spell instanceof com.windanesz.ancientspellcraft.spell.MetamagicProjectileSpell)) {
                String type = flags.getString(com.windanesz.ancientspellcraft.spell.ContingencySpell.LISTENER_TAG);
                flags.putString(com.windanesz.ancientspellcraft.spell.ContingencySpell.STORED_PREFIX + type,
                        spell.getLocation().toString());
                flags.remove(com.windanesz.ancientspellcraft.spell.ContingencySpell.LISTENER_TAG);
                player.setData(com.windanesz.ancientspellcraft.registry.ASAttachments.PLAYER_DATA, flags);
                player.displayClientMessage(net.minecraft.network.chat.Component.translatable(
                        "spell.ancientspellcraft.contingency.stored", spell.getDescriptionFormatted()), true);
                event.setCanceled(true);
                return;
            }
        }

        // metamagic_projectile (AS-9b): flag armada -> cancela o cast e dispara o projétil que
        // carrega a spell. Rays/projéteis/metamagic e a blacklist de config ficam de fora (1.12.2).
        if (flags.getBoolean(com.windanesz.ancientspellcraft.spell.MetamagicProjectileSpell.FLAG)) {
            var spell = event.getSpell();
            if (com.windanesz.ancientspellcraft.ASServerConfig.METAMAGIC_PROJECTILE_INCOMPATIBLE_SPELLS.get()
                    .contains(spell.getLocation())) return;
            if (!(spell instanceof com.koomplo.wizardry.content.spell.abstr.RaySpell
                    || spell instanceof com.koomplo.wizardry.content.spell.abstr.ArrowSpell
                    || spell instanceof com.koomplo.wizardry.content.spell.abstr.ProjectileSpell
                    || spell instanceof MetamagicBuffSpell
                    || spell instanceof com.windanesz.ancientspellcraft.spell.ContingencySpell
                    || spell instanceof com.windanesz.ancientspellcraft.spell.MetamagicProjectileSpell)) {
                var projectile = new com.windanesz.ancientspellcraft.entity.projectile.MetamagicProjectileEntity(
                        com.windanesz.ancientspellcraft.registry.ASEntities.METAMAGIC_PROJECTILE.get(), player.level());
                projectile.setStoredSpell(spell);
                // 1.12.2 calculateVelocity: range 20*RANGE, g=0.03, projeção horizontal
                float launchHeight = player.getEyeHeight()
                        - (float) com.koomplo.wizardry.api.content.entity.projectile.MagicProjectileEntity.LAUNCH_Y_OFFSET;
                float range = 20 * modifiers.get(SpellModifiers.RANGE);
                float velocity = range / net.minecraft.util.Mth.sqrt(2 * launchHeight / 0.03f);
                projectile.aim(player, velocity);
                projectile.damageMultiplier = modifiers.get(SpellModifiers.POTENCY);
                player.level().addFreshEntity(projectile);
                flags.remove(com.windanesz.ancientspellcraft.spell.MetamagicProjectileSpell.FLAG);
                player.setData(com.windanesz.ancientspellcraft.registry.ASAttachments.PLAYER_DATA, flags);
                event.setCanceled(true);
            }
        }
    }

    /** 1.12.2: prioridade LOWEST — sem siphon se o evento foi cancelado (exploit). */
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.isCanceled()) return;
        if (!(event.getSource().getEntity() instanceof Player player)) return;
        int level = effectLevel(player, ASEffects.SPELL_SIPHON);
        if (level <= 0) return;

        for (int slot = 0; slot <= 9; slot++) {
            ItemStack stack = slot == 9 ? player.getOffhandItem() : player.getInventory().getItem(slot);
            if (stack.getItem() instanceof IManaItem manaItem && !manaItem.isManaFull(stack)) {
                manaItem.rechargeMana(stack, 5 * level);
                break; // só um item por kill
            }
        }
    }

    private static int effectLevel(Player player, Holder<MobEffect> effect) {
        return player.hasEffect(effect) ? player.getEffect(effect).getAmplifier() + 1 : 0;
    }

    /** Consome o one-shot: remove o efeito e põe a spell metamagic em cooldown de 1200t nas wands
     * da hotbar (1.12.2 onMetaMagicFinished/setCooldown). */
    private static void consume(Player player, Holder<MobEffect> effect) {
        player.removeEffect(effect);
        var metaSpell = com.koomplo.wizardry.core.platform.Services.REGISTRY_UTIL.getSpells().stream()
                .filter(s -> s instanceof MetamagicBuffSpell mb && mb.getMetamagicEffect().equals(effect))
                .findFirst().orElse(null);
        if (metaSpell == null) return;
        long now = player.level().getGameTime();
        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (!(stack.getItem() instanceof com.koomplo.wizardry.api.content.item.ICastItem)) continue;
            var spells = com.koomplo.wizardry.api.content.util.CastItemDataHelper.getSpells(stack);
            for (int i = 0; i < spells.size(); i++) {
                if (spells.get(i) != metaSpell) continue;
                long[] endTimes = com.koomplo.wizardry.api.content.util.CastItemDataHelper.getCooldownEndTimes(stack);
                if (endTimes.length < spells.size()) endTimes = java.util.Arrays.copyOf(endTimes, spells.size());
                endTimes[i] = now + com.windanesz.ancientspellcraft.ASServerConfig.METAMAGIC_WAND_COOLDOWN.get();
                com.koomplo.wizardry.api.content.util.CastItemDataHelper.setCooldownEndTimes(stack, endTimes);
                int[] maxCooldowns = com.koomplo.wizardry.api.content.util.CastItemDataHelper.getMaxCooldowns(stack);
                if (maxCooldowns.length < spells.size()) maxCooldowns = java.util.Arrays.copyOf(maxCooldowns, spells.size());
                maxCooldowns[i] = com.windanesz.ancientspellcraft.ASServerConfig.METAMAGIC_WAND_COOLDOWN.get();
                com.koomplo.wizardry.api.content.util.CastItemDataHelper.setMaxCooldowns(stack, maxCooldowns);
            }
        }
    }

    /** ring_metamagic_preserve (1.12.2): 33% de chance de NÃO consumir o buff one-shot no cast. */
    private static boolean shouldConsumeMetamagic(Player player) {
        return !(com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(player,
                com.windanesz.ancientspellcraft.registry.ASItems.RING_METAMAGIC_PRESERVE.get())
                && player.level().random.nextFloat() < 0.33f);
    }

    private ASMetamagicEvents() {
    }
}
