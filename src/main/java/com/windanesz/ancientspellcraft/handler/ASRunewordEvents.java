package com.windanesz.ancientspellcraft.handler;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.core.platform.Services;
import com.windanesz.ancientspellcraft.item.BattlemageSwordItem;
import com.windanesz.ancientspellcraft.spell.RunewordSpell;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/**
 * Aplicação das runewords no golpe (1.12.2: onAboutToHitEntity/affectDamage no pipeline de hit):
 * golpe melee de player com espada de battlemage e runeword ativa -> modifica o dano e/ou aplica
 * o efeito, gastando uma carga.
 */
public final class ASRunewordEvents {

    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getSource().getDirectEntity() instanceof Player player)) return;
        ItemStack sword = player.getMainHandItem();
        if (!(sword.getItem() instanceof BattlemageSwordItem)) return;

        String active = RunewordSpell.getActive(sword);
        if (active.isEmpty() || RunewordSpell.getCharges(sword) <= 0) return;
        Spell spell = Services.REGISTRY_UTIL.getSpell(ResourceLocation.tryParse(active));
        if (!(spell instanceof RunewordSpell runeword)) return;

        if (runeword.damageModifier() != null) {
            event.setAmount(runeword.damageModifier().modify(runeword, event.getAmount(), player, event.getEntity(), sword));
        }
        if (runeword.hitEffect() != null) {
            runeword.hitEffect().apply(runeword, player, event.getEntity(), sword);
        }
        if (runeword.spendsOnHit()) {
            RunewordSpell.spendCharge(sword);
        }
    }

    private ASRunewordEvents() {
    }
}
