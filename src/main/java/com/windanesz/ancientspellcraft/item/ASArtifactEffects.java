package com.windanesz.ancientspellcraft.item;

import com.koomplo.wizardry.api.content.event.SpellCastEvent;
import com.koomplo.wizardry.api.content.spell.Element;
import com.koomplo.wizardry.api.content.spell.internal.SpellModifiers;
import com.koomplo.wizardry.core.IArtifactEffect;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

/**
 * Fábricas dos efeitos de modificador de cast (1.12.2 ASEventHandler, bloco de SpellCastEvent):
 * orbs elementais (+0.30 no elemento / -0.5 fora), reduções/aumentos de custo e as joias de poder.
 */
public final class ASArtifactEffects {

    /** COST vira {@code multiplier * cost} (ex.: mana orb = 0.85). */
    public static IArtifactEffect costMultiplier(float multiplier) {
        return new IArtifactEffect() {
            @Override
            public void onSpellPreCast(SpellCastEvent.Pre event, ItemStack artifact) {
                event.getModifiers().set(SpellModifiers.COST, multiplier * event.getModifiers().get(SpellModifiers.COST));
            }
        };
    }

    /** +25% de custo e +0.25 no modificador dado (ring_blast/range/duration). */
    public static IArtifactEffect tradeoff(String modifierKey) {
        return new IArtifactEffect() {
            @Override
            public void onSpellPreCast(SpellCastEvent.Pre event, ItemStack artifact) {
                event.getModifiers().set(SpellModifiers.COST, 1.25f * event.getModifiers().get(SpellModifiers.COST));
                event.getModifiers().set(modifierKey, event.getModifiers().get(modifierKey) + 0.25f);
            }
        };
    }

    /** Orb elemental (1.12.2, orb_artefact_potency_bonus=30): +0.30 de potência no elemento, -0.5 fora. */
    public static IArtifactEffect elementOrb(Supplier<Element> element) {
        return new IArtifactEffect() {
            @Override
            public void onSpellPreCast(SpellCastEvent.Pre event, ItemStack artifact) {
                float potency = event.getModifiers().get(SpellModifiers.POTENCY);
                event.getModifiers().set(SpellModifiers.POTENCY,
                        event.getSpell().getElement() == element.get() ? potency + 0.30f : potency - 0.5f);
            }
        };
    }

    /** +potência fixa para spells de fire/ice/lightning (charm_elemental_grimoire). */
    public static IArtifactEffect grimoire(Supplier<Element> a, Supplier<Element> b, Supplier<Element> c) {
        return new IArtifactEffect() {
            @Override
            public void onSpellPreCast(SpellCastEvent.Pre event, ItemStack artifact) {
                var element = event.getSpell().getElement();
                if (element == a.get() || element == b.get() || element == c.get()) {
                    event.getModifiers().set(SpellModifiers.POTENCY,
                            event.getModifiers().get(SpellModifiers.POTENCY) + 0.1f);
                }
            }
        };
    }

    /** Joia de poder: +potência e +custo fixos (ring 0.05, amulet 0.10, orb 0.20). */
    public static IArtifactEffect powerJewel(float bonus) {
        return new IArtifactEffect() {
            @Override
            public void onSpellPreCast(SpellCastEvent.Pre event, ItemStack artifact) {
                event.getModifiers().set(SpellModifiers.POTENCY, event.getModifiers().get(SpellModifiers.POTENCY) + bonus);
                event.getModifiers().set(SpellModifiers.COST, event.getModifiers().get(SpellModifiers.COST) + bonus);
            }
        };
    }

    private ASArtifactEffects() {}
}
