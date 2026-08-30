package com.github.franckyi.guapi.base.theme.vanilla.delegate;

import com.github.franckyi.guapi.api.EventTarget;
import com.github.franckyi.guapi.api.Renderable;
import com.github.franckyi.guapi.api.event.MouseDragEvent;
import com.github.franckyi.guapi.api.event.MouseEvent;
import com.github.franckyi.guapi.api.event.MouseScrollEvent;
import com.github.franckyi.guapi.api.event.TypeEvent;
import com.github.franckyi.guapi.api.node.Labeled;
import com.github.franckyi.guapi.api.node.Node;
import com.github.rinorsi.cadeditor.mixin.AbstractWidgetMixin;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;

public interface VanillaWidgetSkinDelegate extends Renderable, EventTarget, GuiEventListener {
    private static MouseButtonEvent toNativeMouseButtonEvent(com.github.franckyi.guapi.api.event.MouseButtonEvent event) {
        return new MouseButtonEvent(event.getMouseX(), event.getMouseY(), new MouseButtonInfo(event.getButton(), 0));
    }

    private static KeyEvent toNativeKeyEvent(com.github.franckyi.guapi.api.event.KeyEvent event) {
        return new KeyEvent(event.getKeyCode(), event.getScanCode(), event.getModifiers());
    }

    default void mouseClicked(com.github.franckyi.guapi.api.event.MouseButtonEvent event) {
        mouseClicked(toNativeMouseButtonEvent(event), false);
    }

    default void mouseReleased(com.github.franckyi.guapi.api.event.MouseButtonEvent event) {
        mouseReleased(toNativeMouseButtonEvent(event));
    }

    default void mouseDragged(MouseDragEvent event) {
        mouseDragged(toNativeMouseButtonEvent(event), event.getDeltaX(), event.getDeltaY());
    }

    default void mouseScrolled(MouseScrollEvent event) {
        mouseScrolled(event.getMouseX(), event.getMouseY(), event.getDeltaX(), event.getDeltaY());
    }

    @Override
    default void keyPressed(com.github.franckyi.guapi.api.event.KeyEvent event) {
        keyPressed(toNativeKeyEvent(event));
    }

    @Override
    default void keyReleased(com.github.franckyi.guapi.api.event.KeyEvent event) {
        keyReleased(toNativeKeyEvent(event));
    }

    @Override
    default void charTyped(TypeEvent event) {
        charTyped(new CharacterEvent(event.getCharacter()));
    }

    default void mouseMoved(MouseEvent event) {
        mouseMoved(event.getMouseX(), event.getMouseY());
    }

    default void initNodeWidget(Node node) {
        AbstractWidget widget = (AbstractWidget) this;
        widget.active = !node.isDisabled();
        widget.visible = node.isVisible();
        node.xProperty().addListener(widget::setX);
        node.yProperty().addListener(widget::setY);
        node.widthProperty().addListener(widget::setWidth);
        node.heightProperty().addListener(((AbstractWidgetMixin) widget)::setHeight);
        node.disabledProperty().addListener(newVal -> widget.active = !newVal);
        node.visibleProperty().addListener(newVal -> widget.visible = newVal);
    }

    default void initLabeledWidget(Labeled node) {
        initNodeWidget(node);
        node.labelProperty().addListener(((AbstractWidget) this)::setMessage);
    }

    @Override
    default void render(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        ((AbstractWidget) this).extractRenderState(guiGraphicsExtractor, mouseX, mouseY, delta);
    }
}
