package com.windanesz.ancientspellcraft.item;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.content.item.SpellBookItem;
import net.minecraft.resources.ResourceLocation;

/** Mystic spell book (1.12.2 ItemSageSpellBook): o livro das class spells. */
public class MysticSpellBookItem extends SpellBookItem {

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("ancientspellcraft", "textures/gui/spell_book_sage.png");

    public MysticSpellBookItem() {
        super(new Properties().stacksTo(16));
    }

    @Override
    public ResourceLocation getGuiTexture(Spell spell) {
        return GUI_TEXTURE;
    }
}
