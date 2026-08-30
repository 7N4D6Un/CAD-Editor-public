package com.github.franckyi.guapi.base.theme;

import com.github.franckyi.guapi.base.node.AbstractNode;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class ProvidedSkin<N extends AbstractNode> extends AbstractSkin<N> {
    private final N node;

    protected abstract <M> void renderNode(M m, int mouseX, int mouseY, float f);

    protected ProvidedSkin(N node) {
        this.node = node;
    }

    @Override
    public void render(N node, GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        super.render(node, guiGraphicsExtractor, mouseX, mouseY, delta);
        renderNode(guiGraphicsExtractor, mouseX, mouseY, delta);
    }

    public N getNode() {
        return this.node;
    }
}
