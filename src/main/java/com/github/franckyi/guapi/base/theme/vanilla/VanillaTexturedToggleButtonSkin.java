package com.github.franckyi.guapi.base.theme.vanilla;

import com.github.franckyi.guapi.api.node.TexturedToggleButton;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class VanillaTexturedToggleButtonSkin<N extends TexturedToggleButton> extends VanillaTexturedButtonSkin<N> implements VanillaToggleSkin<N> {
    public VanillaTexturedToggleButtonSkin(N node) {
        super(node);
    }

    @Override
    public void render(N node, GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        super.render(node, guiGraphicsExtractor, mouseX, mouseY, delta);
        renderToggle(node, guiGraphicsExtractor);
    }
}
