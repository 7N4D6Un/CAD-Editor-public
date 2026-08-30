package com.github.rinorsi.cadeditor.client.theme;

import com.github.franckyi.guapi.api.node.Button;
import com.github.franckyi.guapi.base.theme.vanilla.delegate.VanillaButtonSkinDelegate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;


public class MonochromeButtonSkinDelegate<N extends Button> extends VanillaButtonSkinDelegate<N> {
    private static final int NORMAL_BG = -14013906;
    private static final int HOVER_BG = -12961216;
    private static final int DISABLED_BG = -14935009;
    private static final int BORDER_COLOR = -11184806;
    private static final int FOCUS_BORDER_COLOR = -8750464;
    private static final int NORMAL_TEXT = -1118482;
    private static final int DISABLED_TEXT = -8947844;

    public MonochromeButtonSkinDelegate(N node) {
        super(node);
    }

    @Override 
    protected void extractContents(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        int x = getX();
        int y = getY();
        int width = getWidth();
        int height = getHeight();
        int backgroundColor = resolveBackgroundColor();
        guiGraphicsExtractor.fill(x, y, x + width, y + height, backgroundColor);
        drawBorders(guiGraphicsExtractor, x, y, width, height);
        drawLabel(guiGraphicsExtractor, x, y, width, height);
    }

    private int resolveBackgroundColor() {
        if (this.active) {
            return isHoveredOrFocused() ? HOVER_BG : NORMAL_BG;
        }
        return DISABLED_BG;
    }

    private void drawBorders(GuiGraphicsExtractor guiGraphicsExtractor, int x, int y, int width, int height) {
        int topColor = isFocused() ? FOCUS_BORDER_COLOR : BORDER_COLOR;
        int bottomColor = isFocused() ? FOCUS_BORDER_COLOR : BORDER_COLOR;
        guiGraphicsExtractor.fill(x, y, x + width, y + 1, topColor);
        guiGraphicsExtractor.fill(x, (y + height) - 1, x + width, y + height, bottomColor);
        guiGraphicsExtractor.fill(x, y, x + 1, y + height, topColor);
        guiGraphicsExtractor.fill((x + width) - 1, y, x + width, y + height, bottomColor);
    }

    private void drawLabel(GuiGraphicsExtractor guiGraphicsExtractor, int x, int y, int width, int height) {
        Font font = Minecraft.getInstance().font;
        int textColor = this.active ? NORMAL_TEXT : DISABLED_TEXT;
        int textY = y + ((height - 8) / 2);
        guiGraphicsExtractor.centeredText(font, getMessage(), x + (width / 2), textY, textColor);
    }
}
