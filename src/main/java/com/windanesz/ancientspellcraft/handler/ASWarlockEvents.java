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

    /** Cristal absorvido (+5% potency no elemento casado) + elemental attunement (+/-25% blast/range). */
    public static void onSpellCastPre(SpellCastEvent.Pre event) {
        if (!(event.getCaster() instanceof Player player)) return;
        var tag = player.getData(ASAttachments.WARLOCK_DATA);
        SpellModifiers modifiers = event.getModifiers();
        if (tag.contains("Element")
                && event.getSpell().getElement().getName().equals(tag.getString("Element"))) {
            modifiers.set(SpellModifiers.POTENCY, modifiers.get(SpellModifiers.POTENCY) + 0.05f);
        }
        if (tag.contains("ElementalAttunement")) {
            float mod = event.getSpell().getElement().getName().equals(tag.getString("ElementalAttunement")) ? 0.25f : -0.25f;
            modifiers.set(SpellModifiers.BLAST, modifiers.get(SpellModifiers.BLAST) + mod);
            modifiers.set(SpellModifiers.RANGE, modifiers.get(SpellModifiers.RANGE) + mod);
        }
    }

    public static boolean isWarlockAttuned(Player player) {
        return player.getData(ASAttachments.WARLOCK_DATA).getBoolean("WarlockAttuned");
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
