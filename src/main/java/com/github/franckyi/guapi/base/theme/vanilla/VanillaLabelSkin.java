package com.github.franckyi.guapi.base.theme.vanilla;

import com.github.franckyi.guapi.api.RenderHelper;
import com.github.franckyi.guapi.api.node.Label;
import com.github.franckyi.guapi.api.theme.Skin;
import com.github.franckyi.guapi.api.util.Align;
import com.github.franckyi.guapi.base.theme.AbstractSkin;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

public class VanillaLabelSkin extends AbstractSkin<Label> {
    public static final Skin<Label> INSTANCE = new VanillaLabelSkin();

    private VanillaLabelSkin() {
    }

    @Override
    public void render(Label node, GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        super.render(node, guiGraphicsExtractor, mouseX, mouseY, delta);
        renderText(node, guiGraphicsExtractor, mouseX, mouseY, delta);
    }

    protected void renderText(Label node, GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        Component text = node.getLabel();
        int x = getTextX(node, text);
        int y = getTextY(node);
        RenderHelper.drawString(guiGraphicsExtractor, text, x, y, resolveBaseColor(text), node.hasShadow());
    }

    @Override
    public int getTooltipX(Label node, int mouseX, int mouseY) {
        return getTextX(node, node.getLabel()) + 2;
    }

    @Override
    public int getTooltipY(Label node, int mouseX, int mouseY) {
        return getTextY(node) + RenderHelper.getFontHeight() + 2;
    }

    private static int getTextX(Label node, Component text) {
        return Align.getAlignedX(node.getTextAlign().getHorizontalAlign(), node, RenderHelper.getFontWidth(text));
    }

    private static int getTextY(Label node) {
        return Align.getAlignedY(node.getTextAlign().getVerticalAlign(), node, RenderHelper.getFontHeight());
    }

    private static int resolveBaseColor(Component component) {
        TextColor color = findFirstColor(component);
        if (color != null) {
            return color.getValue();
        }
        return 16777215;
    }

    private static TextColor findFirstColor(Component component) {
        if (component.getStyle().getColor() != null) {
            return component.getStyle().getColor();
        }
        for (Component sibling : component.getSiblings()) {
            TextColor color = findFirstColor(sibling);
            if (color != null) {
                return color;
            }
        }
        return null;
    }

    @Override
    public int computeWidth(Label node) {
        return RenderHelper.getFontWidth(node.getLabel());
    }

    @Override
    public int computeHeight(Label node) {
        return RenderHelper.getFontHeight() - 1;
    }
}
