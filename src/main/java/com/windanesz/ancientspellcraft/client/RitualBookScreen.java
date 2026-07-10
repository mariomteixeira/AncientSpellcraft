package com.windanesz.ancientspellcraft.client;

import com.windanesz.ancientspellcraft.AncientSpellcraft;
import com.windanesz.ancientspellcraft.ritual.ASRituals;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Livro de ritual (1.12.2 GuiRitualBook): nome, descrição e runas necessárias com ícones.
 * Desvios: uma página só (sem double page), sem elder futhark/discovery (o port não tem
 * RitualDiscoveryData) e runas como lista com contagem (o pattern espacial virou contagem).
 */
public class RitualBookScreen extends Screen {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(AncientSpellcraft.MODID, "textures/gui/ritual_book.png");
    private static final int X_SIZE = 288, Y_SIZE = 180, TEX_W = 512, TEX_H = 256;

    private final String ritual;

    public RitualBookScreen(String ritual) {
        super(Component.translatable("ritual.ancientspellcraft." + ritual));
        this.ritual = ritual;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        int left = (width - X_SIZE) / 2;
        int top = (height - Y_SIZE) / 2;

        graphics.blit(TEXTURE, left, top, 0, 0, X_SIZE, Y_SIZE, TEX_W, TEX_H);

        // página esquerda: nome + descrição
        graphics.drawString(font, title, left + 20, top + 18, 0, false);
        graphics.drawWordWrap(font, Component.translatable("ritual.ancientspellcraft." + ritual + ".desc"),
                left + 17, top + 34, 118, 0);

        // página direita: runas necessárias
        graphics.drawString(font, Component.translatable("item.ancientspellcraft.ritual_book.runes"),
                left + 150, top + 18, 0, false);
        List<ItemStack> stacks = new ArrayList<>();
        ASRituals.RUNES_REQUIRED.getOrDefault(ritual, java.util.Map.of()).forEach((rune, count) -> {
            var item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(AncientSpellcraft.MODID, rune));
            stacks.add(new ItemStack(item, count));
        });
        ItemStack hovered = ItemStack.EMPTY;
        for (int i = 0; i < stacks.size(); i++) {
            int x = left + 150 + (i % 5) * 22;
            int y = top + 34 + (i / 5) * 22;
            graphics.renderItem(stacks.get(i), x, y);
            graphics.renderItemDecorations(font, stacks.get(i), x, y);
            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                hovered = stacks.get(i);
            }
        }
        if (!hovered.isEmpty()) {
            graphics.renderTooltip(font, hovered, mouseX, mouseY);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
