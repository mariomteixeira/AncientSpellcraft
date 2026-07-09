package com.windanesz.ancientspellcraft.registry;

import com.koomplo.wizardry.api.content.spell.Element;
import com.koomplo.wizardry.core.registry.EBRegistries;
import com.windanesz.ancientspellcraft.AncientSpellcraft;
import net.minecraft.ChatFormatting;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ASElements {

    public static final DeferredRegister<Element> ELEMENTS = DeferredRegister.create(EBRegistries.ELEMENT, AncientSpellcraft.MODID);

    // Elemento novo deste port (não existia no 1.12.2). Só classificação:
    // npcSelectable=false — sem armor/wand/cristal, wizards NPC nunca sorteiam chaos.
    public static final DeferredHolder<Element, Element> CHAOS = ELEMENTS.register("chaos",
            () -> new Element(ChatFormatting.LIGHT_PURPLE, false, 0xe93cf0, 0xffd1fa, 0x76126e));

    private ASElements() {
    }
}
