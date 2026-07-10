package com.windanesz.ancientspellcraft.handler;

import com.koomplo.wizardry.api.content.event.SpellCastEvent;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.api.content.util.EntityUtil;
import com.koomplo.wizardry.core.AllyDesignation;
import com.windanesz.ancientspellcraft.registry.ASAttachments;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Consumo das absorcoes do warlock (1.12.2 ASEventHandler + AbsorbPotion.update). */
public final class ASWarlockEvents {

    /** Cristal absorvido (+5% potency no elemento casado; +10% se era bloco) + elemental attunement (+/-25% blast/range). */
    public static void onSpellCastPre(SpellCastEvent.Pre event) {
        if (!(event.getCaster() instanceof Player player)) return;
        var tag = player.getData(ASAttachments.WARLOCK_DATA);
        SpellModifiers modifiers = event.getModifiers();
        if (tag.contains("Element")
                && event.getSpell().getElement().getName().equals(tag.getString("Element"))) {
            float bonus = tag.getBoolean("ElementBlock") ? 0.10f : 0.05f;
            modifiers.set(SpellModifiers.POTENCY, modifiers.get(SpellModifiers.POTENCY) + bonus);
        }
        if (tag.contains("ElementalAttunement")) {
            float mod = event.getSpell().getElement().getName().equals(tag.getString("ElementalAttunement")) ? 0.25f : -0.25f;
            modifiers.set(SpellModifiers.BLAST, modifiers.get(SpellModifiers.BLAST) + mod);
            modifiers.set(SpellModifiers.RANGE, modifiers.get(SpellModifiers.RANGE) + mod);
        }
        // absorb_artefact (1.12.2): +1% de potência por power gem absorvida
        int gems = tag.getInt("PowerGems");
        if (gems > 0) {
            modifiers.set(SpellModifiers.POTENCY, modifiers.get(SpellModifiers.POTENCY) + gems * 0.01f);
        }
    }

    public static boolean isWarlockAttuned(Player player) {
        return player.getData(ASAttachments.WARLOCK_DATA).getBoolean("WarlockAttuned");
    }

    /**
     * Keybind do warlock (1.12.2 PacketCastWarlockSpell.castSpell): casta a spell absorvida pagando
     * FOME — max(1, custo/5), metade com amulet_spellbinding; fome zerada vira dano de inanição.
     * Desvios: cooldown guardado no attachment (o 1.12.2 usava cooldown de um item-marcador) e
     * spell contínua absorvida dá um pulso único (o 1.12.2 castava por até 40t).
     */
    public static void castAbsorbedSpell(net.minecraft.server.level.ServerPlayer player) {
        var tag = player.getData(ASAttachments.WARLOCK_DATA);
        if (!tag.contains("Spell")) return;
        var spell = com.koomplo.wizardry.core.platform.Services.REGISTRY_UTIL.getSpell(
                ResourceLocation.tryParse(tag.getString("Spell")));
        if (spell == null) return;
        if (!isWarlockAttuned(player) && !com.windanesz.ancientspellcraft.handler.ASSpellEvents.isWearingFullSet(
                player, com.koomplo.wizardry.content.item.armor.WizardArmorType.WARLOCK)) return;
        long now = player.level().getGameTime();
        if (tag.getLong("CastCooldownUntil") > now) return;

        SpellModifiers modifiers = new SpellModifiers();
        var ctx = new com.koomplo.wizardry.api.content.spell.internal.PlayerCastContext(
                player.level(), player, net.minecraft.world.InteractionHand.MAIN_HAND, 0, modifiers);
        if (com.koomplo.wizardry.api.content.util.CastItemUtils.fireSpellCastEvent(
                SpellCastEvent.Source.WAND, spell, ctx)) return;
        if (!com.koomplo.wizardry.api.content.util.CastItemUtils.executeSpellCast(
                SpellCastEvent.Source.WAND, spell, ctx)) return;
        com.koomplo.wizardry.api.content.util.CastItemUtils.sendSpellCastPacket(player, spell, ctx);

        tag.putLong("CastCooldownUntil", now + spell.getCooldown());
        player.setData(ASAttachments.WARLOCK_DATA, tag);

        if (!player.isCreative()) {
            int cost = com.koomplo.wizardry.api.content.util.CastItemUtils.calcCastCost(spell, modifiers);
            int hunger = Math.max(1, cost / 5);
            if (com.koomplo.wizardry.core.integrations.ArtifactChannel.isEquipped(player,
                    com.windanesz.ancientspellcraft.registry.ASItems.AMULET_SPELLBINDING.get())) {
                hunger = Math.max(1, hunger / 2);
            }
            var food = player.getFoodData();
            if (food.getFoodLevel() == 0) {
                player.hurt(player.damageSources().starve(), spell.getCost() / 5f);
            } else {
                food.setFoodLevel(Math.max(0, food.getFoodLevel() - hunger));
            }
        }
    }

    /** Pocao absorvida vira AURA: benefica para aliados (e o proprio), ruim para inimigos; expira. */
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;
        var tag = player.getData(ASAttachments.WARLOCK_DATA);
        if (!tag.contains("Effect")) return;

        int duration = tag.getInt("EffectDuration");
        if (duration <= 0) {
            tag.remove("Effect");
            tag.remove("EffectDuration");
            player.setData(ASAttachments.WARLOCK_DATA, tag);
            return;
        }
        tag.putInt("EffectDuration", duration - 1);
        player.setData(ASAttachments.WARLOCK_DATA, tag);

        if (duration % 10 != 0) return;
        ResourceLocation id = ResourceLocation.tryParse(tag.getString("Effect"));
        if (id == null) return;
        var holder = BuiltInRegistries.MOB_EFFECT.getHolder(id).orElse(null);
        if (holder == null) return;
        MobEffect effect = holder.value();
        boolean beneficial = effect.isBeneficial();

        double radius = 6; // EFFECT_RADIUS do absorb_potion (JSON)
        for (var target : EntityUtil.getLivingWithinRadius(radius, player.getX(), player.getY(), player.getZ(), player.level())) {
            boolean ally = target == player || AllyDesignation.isAllied(player, target);
            if (beneficial == ally) {
                target.addEffect(new MobEffectInstance(holder, 60, 0));
            }
        }
    }

    private ASWarlockEvents() {
    }
}
