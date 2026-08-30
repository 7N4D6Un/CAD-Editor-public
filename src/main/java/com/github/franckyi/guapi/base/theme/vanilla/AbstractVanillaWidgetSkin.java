package com.github.franckyi.guapi.base.theme.vanilla;

import com.github.franckyi.guapi.api.event.KeyEvent;
import com.github.franckyi.guapi.api.event.MouseButtonEvent;
import com.github.franckyi.guapi.api.event.MouseDragEvent;
import com.github.franckyi.guapi.api.event.MouseEvent;
import com.github.franckyi.guapi.api.event.MouseScrollEvent;
import com.github.franckyi.guapi.api.event.TypeEvent;
import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.base.theme.SuppliedSkin;
import com.github.franckyi.guapi.base.theme.vanilla.delegate.VanillaWidgetSkinDelegate;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class AbstractVanillaWidgetSkin<N extends Node, W extends VanillaWidgetSkinDelegate> extends SuppliedSkin<N> {
    private final W widget;

    protected AbstractVanillaWidgetSkin(N node, W widget) {
        super(node);
        this.widget = widget;
    }

    @Override
    public boolean preRender(N node, GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        boolean res = super.preRender(node, guiGraphicsExtractor, mouseX, mouseY, delta);
        return res | this.widget.preRender(guiGraphicsExtractor, mouseX, mouseY, delta);
    }

    @Override
    public void render(N node, GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        super.render(node, guiGraphicsExtractor, mouseX, mouseY, delta);
        this.widget.render(guiGraphicsExtractor, mouseX, mouseY, delta);
    }

    @Override
    public void postRender(N node, GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        super.postRender(node, guiGraphicsExtractor, mouseX, mouseY, delta);
        this.widget.postRender(guiGraphicsExtractor, mouseX, mouseY, delta);
    }

    @Override
    public void mouseClicked(MouseButtonEvent event) {
        this.widget.mouseClicked(event);
    }

    @Override
    public void mouseReleased(MouseButtonEvent event) {
        this.widget.mouseReleased(event);
    }

    @Override
    public void mouseDragged(MouseDragEvent event) {
        this.widget.mouseDragged(event);
    }

    @Override
    public void mouseScrolled(MouseScrollEvent event) {
        this.widget.mouseScrolled(event);
    }

    @Override
    public void keyPressed(KeyEvent event) {
        this.widget.keyPressed(event);
    }

    @Override
    public void keyReleased(KeyEvent event) {
        this.widget.keyReleased(event);
    }

    @Override
    public void charTyped(TypeEvent event) {
        this.widget.charTyped(event);
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        this.widget.mouseMoved(event);
    }
}
