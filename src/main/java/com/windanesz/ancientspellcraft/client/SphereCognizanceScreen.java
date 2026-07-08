package com.windanesz.ancientspellcraft.client;

import com.windanesz.ancientspellcraft.block.SphereCognizanceBlockEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class SphereCognizanceScreen extends AbstractContainerScreen<SphereCognizanceMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("ancientspellcraft", "textures/gui/container/gui_sphere_cognizance.png");

    public SphereCognizanceScreen(SphereCognizanceMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 202;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        int duration = menu.data.get(1);
        if (duration > 0) {
            int scale = menu.data.get(0) * 74 / duration;
            graphics.blit(TEXTURE, leftPos + 51, topPos + 106, 176, 0, scale, 5);
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        int hintType = menu.data.get(2);
        int hintId = menu.data.get(3);
        if (hintType > 0 && hintId > 0) {
            String type = SphereCognizanceBlockEntity.HINT_TYPES.get(
                    Math.min(hintType, SphereCognizanceBlockEntity.HINT_TYPES.size() - 1));
            Component hint = Component.translatable("gui.ancientspellcraft.sphere_cognizance.hint." + type + "." + hintId);
            var lines = font.split(hint, 150);
            int y = 40;
            for (var line : lines) {
                graphics.drawString(font, line, 13, y, 0x3f3f5a, false);
                y += 10;
            }
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
}
