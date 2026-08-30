package com.github.franckyi.guapi.base.theme.vanilla;

import com.github.franckyi.guapi.api.RenderHelper;
import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.api.node.Toggle;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface VanillaToggleSkin<N extends Node & Toggle> {
    default void renderToggle(N node, GuiGraphicsExtractor guiGraphicsExtractor) {
        if (node.isActive()) {
            RenderHelper.drawRectangle(guiGraphicsExtractor, node.getX(), node.getY(), node.getX() + node.getWidth(), node.getY() + node.getHeight(), node.getBorderColor());
        }
    }
}
