package com.github.franckyi.guapi.base.theme.vanilla.delegate;

import com.github.franckyi.databindings.api.IntegerProperty;
import com.github.franckyi.databindings.api.ObservableBooleanValue;
import com.github.franckyi.guapi.api.node.TextArea;
import com.github.rinorsi.cadeditor.mixin.AbstractTextAreaWidgetMixin;
import com.github.rinorsi.cadeditor.mixin.MultiLineEditBoxMixin;
import com.github.rinorsi.cadeditor.mixin.MultilineTextFieldMixin;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.Whence;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.PreeditEvent;
import net.minecraft.network.chat.Component;

public class VanillaTextAreaSkinDelegate<N extends TextArea> implements VanillaWidgetSkinDelegate {
    private final N node;
    private final MultiLineEditBox textBox;
    private final MultiLineEditBoxMixin self;
    private final MultilineTextFieldMixin textFieldMixin;
    private static final int DEFAULT_TEXT_COLOR = -2105377;
    private static final int DEFAULT_CURSOR_COLOR = -3092272;

    public VanillaTextAreaSkinDelegate(N node) {
        this.node = node;
        this.textBox = MultiLineEditBox.builder().setX(node.getX()).setY(node.getY()).setPlaceholder(node.getPlaceholder()).setTextColor(DEFAULT_TEXT_COLOR).setTextShadow(true).setCursorColor(DEFAULT_CURSOR_COLOR).setShowBackground(true).setShowDecorations(true).build(Minecraft.getInstance().font, node.getWidth() - 8, node.getHeight(), node.getLabel());
        this.self = (MultiLineEditBoxMixin) (Object) this.textBox;
        this.textFieldMixin = (MultilineTextFieldMixin) (Object) this.self.getTextField();
        this.textBox.active = !node.isDisabled();
        this.textBox.setCharacterLimit(node.getMaxLength());
        this.textBox.setValue(node.getText());
        this.textBox.setFocused(node.isFocused());
        MultiLineEditBox textBoxForTextListener = this.textBox;
        Objects.requireNonNull(node);
        textBoxForTextListener.setValueListener(node::setText);
        node.xProperty().addListener(value -> setX(value));
        node.yProperty().addListener(value -> setY(value));
        node.widthProperty().addListener(newVal -> {
            setWidth(newVal.intValue() - 8);
            this.textFieldMixin.setWidth(newVal.intValue() - 8);
            this.textFieldMixin.invokeReflowDisplayLines();
        });
        node.heightProperty().addListener(value -> setHeight(value));
        node.disabledProperty().addListener(disabled -> {
            this.textBox.active = !disabled.booleanValue();
        });
        node.labelProperty().addListener(this::setMessage);
        IntegerProperty maxLengthProperty = node.maxLengthProperty();
        MultiLineEditBox textBoxForCharacterLimit = this.textBox;
        Objects.requireNonNull(textBoxForCharacterLimit);
        maxLengthProperty.addListener(textBoxForCharacterLimit::setCharacterLimit);
        node.textProperty().addListener(this::updateText);
        ObservableBooleanValue focusedProperty = node.focusedProperty();
        MultiLineEditBox textBoxForFocusListener = this.textBox;
        Objects.requireNonNull(textBoxForFocusListener);
        focusedProperty.addListener(textBoxForFocusListener::setFocused);
        this.self.getTextField().seekCursor(Whence.ABSOLUTE, 0);
    }

    private void updateText(String text) {
        if (this.node.getValidator().test(text)) {
            if (text.length() > this.node.getMaxLength()) {
                this.textFieldMixin.setRawValue(text.substring(0, this.node.getMaxLength()));
            } else {
                this.textFieldMixin.setRawValue(text);
            }
            this.textFieldMixin.invokeReflowDisplayLines();
        }
    }

    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        if (isWithinContentArea(event.x(), event.y()) && event.button() == 0) {
            this.self.getTextField().setSelecting((event.modifiers() & 1) != 0);
            this.self.invokeSeekCursorScreen(event.x(), event.y());
            return true;
        }
        return this.textBox.mouseClicked(event, isDoubleClick);
    }

    private boolean isWithinContentArea(double mouseX, double mouseY) {
        AbstractTextAreaWidgetMixin areaMixin = (AbstractTextAreaWidgetMixin) (Object) this.textBox;
        int innerLeft = areaMixin.invokeGetInnerLeft();
        int innerTop = areaMixin.invokeGetInnerTop();
        int innerRight = innerLeft + (this.textBox.getWidth() - areaMixin.invokeTotalInnerPadding());
        int innerBottom = innerTop + areaMixin.invokeGetInnerHeight();
        return mouseX >= ((double) innerLeft) && mouseX < ((double) innerRight) && mouseY >= ((double) innerTop) && mouseY < ((double) innerBottom);
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        this.textBox.extractRenderState(guiGraphicsExtractor, mouseX, mouseY, delta);
    }

    public boolean mouseReleased(MouseButtonEvent event) {
        return this.textBox.mouseReleased(event);
    }

    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        return this.textBox.mouseDragged(event, deltaX, deltaY);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        return this.textBox.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }

    public boolean keyPressed(KeyEvent event) {
        return this.textBox.keyPressed(event);
    }

    public boolean keyReleased(KeyEvent event) {
        return this.textBox.keyReleased(event);
    }

    public boolean charTyped(CharacterEvent event) {
        return this.textBox.charTyped(event);
    }

    public boolean preeditUpdated(PreeditEvent event) {
        return this.textBox.preeditUpdated(event);
    }

    public void mouseMoved(double mouseX, double mouseY) {
        this.textBox.mouseMoved(mouseX, mouseY);
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.textBox.isMouseOver(mouseX, mouseY);
    }

    public void setX(int x) {
        this.textBox.setX(x);
    }

    public void setY(int y) {
        this.textBox.setY(y);
    }

    public void setWidth(int width) {
        this.textBox.setWidth(width);
    }

    public void setHeight(int height) {
        this.textBox.setHeight(height);
    }

    public void setMessage(Component message) {
        this.textBox.setMessage(message);
    }

    public boolean isActive() {
        return this.textBox.isActive();
    }

    public void setFocused(boolean focused) {
        this.textBox.setFocused(focused);
    }

    public boolean isFocused() {
        return this.textBox.isFocused();
    }

    public boolean isVisible() {
        return this.textBox.visible;
    }

    public void setVisible(boolean visible) {
        this.textBox.visible = visible;
    }
}
