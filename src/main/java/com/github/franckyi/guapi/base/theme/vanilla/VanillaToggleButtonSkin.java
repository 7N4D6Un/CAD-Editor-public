package com.github.franckyi.guapi.base.theme.vanilla;

import com.github.franckyi.guapi.api.node.ToggleButton;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class VanillaToggleButtonSkin<N extends ToggleButton> extends VanillaButtonSkin<N> implements VanillaToggleSkin<N> {
    public VanillaToggleButtonSkin(N node) {
        super(node);
    }

    @Override
    public void render(N node, GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        super.render(node, guiGraphicsExtractor, mouseX, mouseY, delta);
        renderToggle(node, guiGraphicsExtractor);
    }
}
