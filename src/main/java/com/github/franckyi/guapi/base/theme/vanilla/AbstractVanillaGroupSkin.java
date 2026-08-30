package com.github.franckyi.guapi.base.theme.vanilla;

import com.github.franckyi.guapi.api.node.Group;
import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.base.theme.AbstractSkin;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class AbstractVanillaGroupSkin<N extends Group> extends AbstractSkin<N> {
    @Override
    public boolean preRender(N node, GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        boolean res = false;
        for (Node child : node.getChildren()) {
            res |= child.preRender(guiGraphicsExtractor, mouseX, mouseY, delta);
        }
        return res;
    }

    @Override
    public void render(N node, GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        super.render(node, guiGraphicsExtractor, mouseX, mouseY, delta);
        for (Node child : node.getChildren()) {
            child.render(guiGraphicsExtractor, mouseX, mouseY, delta);
        }
    }

    @Override
    public void postRender(N node, GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        super.postRender(node, guiGraphicsExtractor, mouseX, mouseY, delta);
        for (Node child : node.getChildren()) {
            child.postRender(guiGraphicsExtractor, mouseX, mouseY, delta);
        }
    }
}
