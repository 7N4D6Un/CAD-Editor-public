package com.github.franckyi.guapi.base.theme.vanilla;

import com.github.franckyi.guapi.api.Color;
import com.github.franckyi.guapi.api.RenderHelper;
import com.github.franckyi.guapi.api.node.TextField;
import com.github.franckyi.guapi.base.theme.vanilla.delegate.VanillaTextFieldSkinDelegate;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class VanillaTextFieldSkin<N extends TextField> extends AbstractVanillaWidgetSkin<N, VanillaTextFieldSkinDelegate<N>> {
    public VanillaTextFieldSkin(N node) {
        this(node, new VanillaTextFieldSkinDelegate(node));
    }

    protected VanillaTextFieldSkin(N node, VanillaTextFieldSkinDelegate<N> widget) {
        super(node, widget);
    }

    @Override
    public void render(N node, GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        super.render(node, guiGraphicsExtractor, mouseX, mouseY, delta);
        if (!node.isValidationForced() && !node.getValidator().test(node.getText())) {
            drawBorder(node, guiGraphicsExtractor, Color.fromRGBA(1.0d, 0.0d, 0.0d, 0.8d));
        } else if (node.isSuggested()) {
            drawBorder(node, guiGraphicsExtractor, Color.fromRGBA(0.0d, 1.0d, 0.0d, 0.8d));
        }
    }

    @Override
    public int computeWidth(N node) {
        return 150;
    }

    @Override
    public int computeHeight(N node) {
        return 20;
    }

    private void drawBorder(N node, GuiGraphicsExtractor guiGraphicsExtractor, int color) {
        RenderHelper.drawRectangle(guiGraphicsExtractor, node.getX(), node.getY(), node.getX() + node.getWidth(), node.getY() + node.getHeight(), color);
    }
}
