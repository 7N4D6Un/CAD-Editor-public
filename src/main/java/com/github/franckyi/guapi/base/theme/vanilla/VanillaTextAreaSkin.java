package com.github.franckyi.guapi.base.theme.vanilla;

import com.github.franckyi.guapi.api.Color;
import com.github.franckyi.guapi.api.RenderHelper;
import com.github.franckyi.guapi.api.node.TextArea;
import com.github.franckyi.guapi.base.theme.vanilla.delegate.VanillaTextAreaSkinDelegate;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class VanillaTextAreaSkin extends AbstractVanillaWidgetSkin<TextArea, VanillaTextAreaSkinDelegate<TextArea>> {
    public VanillaTextAreaSkin(TextArea node) {
        this(node, new VanillaTextAreaSkinDelegate(node));
    }

    protected VanillaTextAreaSkin(TextArea node, VanillaTextAreaSkinDelegate<TextArea> widget) {
        super(node, widget);
    }

    @Override
    public void render(TextArea node, GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        super.render(node, guiGraphicsExtractor, mouseX, mouseY, delta);
        if (!node.isValidationForced() && !node.getValidator().test(node.getText())) {
            drawBorder(node, guiGraphicsExtractor, Color.fromRGBA(1.0d, 0.0d, 0.0d, 0.8d));
        } else if (node.isSuggested()) {
            drawBorder(node, guiGraphicsExtractor, Color.fromRGBA(0.0d, 1.0d, 0.0d, 0.8d));
        }
    }

    @Override
    public int computeWidth(TextArea node) {
        return 150;
    }

    @Override
    public int computeHeight(TextArea node) {
        return 20;
    }

    private void drawBorder(TextArea node, GuiGraphicsExtractor guiGraphicsExtractor, int color) {
        RenderHelper.drawRectangle(guiGraphicsExtractor, node.getX(), node.getY(), node.getX() + node.getWidth(), node.getY() + node.getHeight(), color);
    }
}
