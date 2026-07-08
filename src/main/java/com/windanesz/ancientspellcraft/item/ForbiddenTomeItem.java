package com.windanesz.ancientspellcraft.item;

import com.koomplo.wizardry.api.content.spell.Spell;
import com.koomplo.wizardry.content.item.SpellBookItem;
import net.minecraft.resources.ResourceLocation;

/** Tomo proibido (1.12.2 ItemWarlockSpellBook): o livro das spells de warlock. TODO textura por tier. */
public class ForbiddenTomeItem extends SpellBookItem {

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("ancientspellcraft", "textures/gui/spell_book_warlock.png");

    public ForbiddenTomeItem() {
        super(new Properties().stacksTo(16));
    }

    @Override
    public ResourceLocation getGuiTexture(Spell spell) {
        return GUI_TEXTURE;
    }
}
