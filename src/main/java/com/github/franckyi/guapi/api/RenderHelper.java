package com.github.franckyi.guapi.api;

import com.github.rinorsi.cadeditor.client.util.AttributeTooltipFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public final class RenderHelper {
    private static Minecraft mc() {
        return Minecraft.getInstance();
    }

    private static Font font() {
        return mc().font;
    }

    public static int getFontHeight() {
        Objects.requireNonNull(font());
        return 9;
    }

    public static int getFontWidth(Component text) {
        return font().width(text);
    }

    public static void drawString(GuiGraphicsExtractor guiGraphicsExtractor, Component text, float x, float y, int color, boolean shadow) {
        guiGraphicsExtractor.text(font(), text, (int) x, (int) y, ensureOpaqueColor(color), shadow);
    }

    public static int ensureOpaqueColor(int color) {
        return (color & (-16777216)) == 0 ? color | (-16777216) : color;
    }

    public static void fillRectangle(GuiGraphicsExtractor guiGraphicsExtractor, int x0, int y0, int x1, int y1, int color) {
        guiGraphicsExtractor.fill(x0, y0, x1, y1, color);
    }

    public static void drawVLine(GuiGraphicsExtractor guiGraphicsExtractor, int x, int y0, int y1, int color) {
        fillRectangle(guiGraphicsExtractor, x, y0, x + 1, y1, color);
    }

    public static void drawHLine(GuiGraphicsExtractor guiGraphicsExtractor, int y, int x0, int x1, int color) {
        fillRectangle(guiGraphicsExtractor, x0, y, x1, y + 1, color);
    }

    public static void drawRectangle(GuiGraphicsExtractor guiGraphicsExtractor, int x0, int y0, int x1, int y1, int color) {
        drawHLine(guiGraphicsExtractor, y0, x0, x1 - 1, color);
        drawVLine(guiGraphicsExtractor, x1 - 1, y0, y1 - 1, color);
        drawHLine(guiGraphicsExtractor, y1 - 1, x1, x0 + 1, color);
        drawVLine(guiGraphicsExtractor, x0, y1, y0 + 1, color);
    }

    public static void drawTexture(GuiGraphicsExtractor guiGraphicsExtractor, Identifier id, int x, int y, int width, int height, int imageX, int imageY, int imageWidth, int imageHeight) {
        guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, id, x, y, imageX, imageY, width, height, imageWidth, imageHeight);
    }

    public static void drawSprite(GuiGraphicsExtractor guiGraphicsExtractor, TextureAtlasSprite sprite, int x, int y, int imageWidth, int imageHeight) {
        guiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, imageWidth, imageHeight);
    }

    public static void drawTooltip(GuiGraphicsExtractor guiGraphicsExtractor, List<Component> text, int x, int y) {
        renderTooltip(guiGraphicsExtractor, font(), text, Optional.empty(), x, y);
    }

    public static void drawTooltip(GuiGraphicsExtractor guiGraphicsExtractor, ItemStack itemStack, int x, int y) {
        Item.TooltipContext context;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            context = Item.TooltipContext.of(minecraft.level);
        } else if (minecraft.getConnection() != null) {
            context = Item.TooltipContext.of(minecraft.getConnection().registryAccess());
        } else {
            context = Item.TooltipContext.EMPTY;
        }
        List<Component> lines = AttributeTooltipFormatter.buildTooltipLines(itemStack, context, minecraft.player, TooltipFlag.Default.NORMAL);
        renderTooltip(guiGraphicsExtractor, font(), lines, itemStack.getTooltipImage(), x, y);
    }

    public static void drawItem(GuiGraphicsExtractor guiGraphicsExtractor, ItemStack itemStack, int x, int y) {
        guiGraphicsExtractor.fakeItem(itemStack, x, y);
    }

    public static void drawItemDecorations(GuiGraphicsExtractor guiGraphicsExtractor, ItemStack itemStack, int x, int y) {
        guiGraphicsExtractor.itemDecorations(font(), itemStack, x, y);
    }

    private static void renderTooltip(GuiGraphicsExtractor guiGraphicsExtractor, Font font, List<Component> lines, Optional<TooltipComponent> image, int x, int y) {
        List<ClientTooltipComponent> components = new ArrayList<>(lines.size() + (image.isPresent() ? 1 : 0));
        for (Component line : lines) {
            components.add(ClientTooltipComponent.create(line.getVisualOrderText()));
        }
        image.ifPresent(extra -> {
            components.add(components.isEmpty() ? 0 : 1, ClientTooltipComponent.create(extra));
        });
        guiGraphicsExtractor.tooltip(font, components, x, y, DefaultTooltipPositioner.INSTANCE, (Identifier) null);
    }
}
