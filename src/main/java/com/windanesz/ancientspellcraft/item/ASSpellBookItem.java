package com.windanesz.ancientspellcraft.item;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.content.item.SpellBookItem;
import com.koomplo.wizardry.setup.registries.Elements;
import com.windanesz.ancientspellcraft.AncientSpellcraft;
import net.minecraft.resources.ResourceLocation;

/** Ancient Spellcraft spell book: GUI dourada para spells arcanas (elemento MAGIC), tema proprio para o resto. */
public class ASSpellBookItem extends SpellBookItem {

    private static final ResourceLocation GUI_ANCIENT_ELEMENT =
            ResourceLocation.fromNamespaceAndPath(AncientSpellcraft.MODID, "textures/gui/spell_book_ancient_element.png");
    private static final ResourceLocation GUI_ANCIENT_SPELLCRAFT =
            ResourceLocation.fromNamespaceAndPath(AncientSpellcraft.MODID, "textures/gui/spell_book_ancient_spellcraft.png");

    public ASSpellBookItem() {
        super(new Properties().stacksTo(16));
    }

    @Override
    public ResourceLocation getGuiTexture(Spell spell) {
        return spell.getElement() == Elements.MAGIC ? GUI_ANCIENT_ELEMENT : GUI_ANCIENT_SPELLCRAFT;
    }
}
