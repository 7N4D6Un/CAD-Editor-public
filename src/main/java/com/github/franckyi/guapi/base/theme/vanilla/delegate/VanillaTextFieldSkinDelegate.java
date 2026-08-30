package com.github.franckyi.guapi.base.theme.vanilla.delegate;

import com.github.franckyi.guapi.api.node.TextField;
import com.github.rinorsi.cadeditor.mixin.TextFieldWidgetMixin;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

public class VanillaTextFieldSkinDelegate<N extends TextField> extends EditBox implements VanillaWidgetSkinDelegate {
    private final N node;
    private final TextFieldWidgetMixin self;

    public VanillaTextFieldSkinDelegate(N node) {
        super(Minecraft.getInstance().font, node.getX(), node.getY(), node.getWidth(), node.getHeight(), node.getLabel());
        this.node = node;
        this.self = (TextFieldWidgetMixin) this;
        Objects.requireNonNull(node);
        setResponder(node::setText);
        setMaxLength(node.getMaxLength());
        setValue(node.getText());
        setFocused(node.isFocused());
        node.xProperty().addListener(value -> setX(value));
        node.yProperty().addListener(value -> setY(value));
        node.widthProperty().addListener(value -> setWidth(value));
        node.heightProperty().addListener(value -> this.height = value.intValue());
        node.disabledProperty().addListener(value -> this.active = !value);
        node.labelProperty().addListener(this::setMessage);
        node.maxLengthProperty().addListener(value -> setMaxLength(value));
        node.textProperty().addListener(this::updateText);
        node.focusedProperty().addListener(value -> setFocused(value));
        node.placeholderProperty().addListener(this::updatePlaceholder);
        node.validatorProperty().addListener(value -> updateFormatters());
        node.validationForcedProperty().addListener(value -> updateFormatters());
        node.textRendererProperty().addListener(value -> updateFormatters());
        updateFormatters();
        moveCursorToStart(false);
        String text = node.getText();
        if (text != null && !text.isEmpty()) {
            this.self.setCursorPos(text.length());
            this.self.setHighlightPosRaw(text.length());
            node.setCursorPosition(text.length());
            node.setHighlightPosition(text.length());
        }
    }

    private void updateText(String text) {
        String newValue;
        if (this.node.getValidator().test(text)) {
            if (text.length() > this.node.getMaxLength()) {
                newValue = text.substring(0, this.node.getMaxLength());
                this.self.setRawValue(newValue);
            } else {
                newValue = text;
                this.self.setRawValue(newValue);
            }
            this.self.setCursorPos(newValue.length());
            this.setHighlightPos(newValue.length());
            this.node.setCursorPosition(newValue.length());
        }
    }

    private void updateFormatters() {
        this.self.getFormatters().clear();
        if (this.node.isValidationForced()) {
            if (this.node.getValidator() != null) {
                this.self.getFormatters().add((string, i) -> this.node.getValidator().test(string)
                        ? FormattedCharSequence.forward(string, Style.EMPTY)
                        : FormattedCharSequence.forward(getValue(), Style.EMPTY));
            }
            return;
        }
        var renderer = this.node.getTextRenderer();
        if (renderer != null) {
            this.self.getFormatters().add((string, i) -> renderer.render(string, i).getVisualOrderText());
        }
    }

    private void updateRenderer() {
        moveCursorToStart(false);
    }

    private void updatePlaceholder() {
        setHint(getValue().isEmpty() ? this.node.getPlaceholder() : null);
    }

    private int getInnerHeight() {
        return this.self.isBordered() ? this.height - 8 : this.height;
    }

    public Component renderText(String str, int firstCharacterIndex) {
        return this.node.getTextRenderer() == null ? Component.literal(str) : this.node.getTextRenderer().render(str, firstCharacterIndex);
    }

    public void setCursorPosition(int value) {
        super.setCursorPosition(value);
        this.node.setCursorPosition(getCursorPosition());
        if (getCursorPosition() < this.self.getDisplayPos()) {
            this.self.setDisplayPos(getCursorPosition());
        }
    }

    public void setFocused(boolean focused) {
        if (focused && isFocused() != focused) {
            super.setFocused(focused);
            String text = getValue();
            if (text != null && !text.isEmpty()) {
                int textLength = text.length();
                this.self.setCursorPos(textLength);
                this.self.setDisplayPos(Math.max(0, textLength - 50));
                this.node.setCursorPosition(textLength);
                this.node.setHighlightPosition(textLength);
                return;
            }
            return;
        }
        super.setFocused(focused);
    }

    public void setHighlightPos(int value) {
        super.setHighlightPos(value);
        this.node.setHighlightPosition(this.self.getHighlightPos());
    }

    public void insertText(@NotNull String string) {
        int oldCursorPos = getCursorPosition();
        int oldHighlightPos = this.node.getHighlightPosition();
        String oldText = getValue();
        super.insertText(string);
        this.node.onTextUpdate(oldCursorPos, oldHighlightPos, oldText, getCursorPosition(), getValue());
    }

    public void deleteChars(int characterOffset) {
        if (getHighlighted().isEmpty()) {
            int oldCursorPos = getCursorPosition();
            int oldHighlightPos = this.node.getHighlightPosition();
            String oldText = getValue();
            super.deleteChars(characterOffset);
            this.node.onTextUpdate(oldCursorPos, oldHighlightPos, oldText, getCursorPosition(), getValue());
            return;
        }
        super.deleteChars(characterOffset);
    }

    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        if (!this.visible) {
            return false;
        }
        boolean flag = mouseX >= ((double) getX()) && mouseX < ((double) (getX() + this.width)) && mouseY >= ((double) getY()) && mouseY < ((double) (getY() + this.height));
        if (this.self.getCanLoseFocus() && button == 0) {
            setFocused(flag);
        }
        if (isFocused() && flag && button == 0) {
            onClick(event, isDoubleClick);
            return true;
        }
        return false;
    }

    public void renderCustom(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        String clippedText;
        Font font = Minecraft.getInstance().font;
        int i = getX();
        int j = getY();
        int k = getInnerWidth();
        int l = getInnerHeight();
        int m = (!getValue().isEmpty() || isFocused() || this.self.getHint() == null) ? this.self.getTextColor() : 8421504;
        int n = this.self.getCursorPos();
        int o = this.self.getHighlightPos();
        this.self.getDisplayPos();
        Component renderedText = renderText(getValue().substring(this.self.getDisplayPos()), this.self.getDisplayPos());
        if (renderedText != null) {
            clippedText = font.substrByWidth(renderedText, k).getString();
        } else {
            clippedText = font.plainSubstrByWidth(getValue().substring(this.self.getDisplayPos()), k);
        }
        String displayText = clippedText;
        boolean cursorVisible = n >= 0 && n <= displayText.length();
        boolean drawCursor = isFocused() && ((System.currentTimeMillis() - this.self.getFocusedTime()) / 300) % 2 == 0 && cursorVisible;
        int q = this.self.isBordered() ? i + 4 : i;
        int r = this.self.isBordered() ? j + ((l - 8) / 2) : j;
        int textEndX = q;
        if (o > displayText.length()) {
            o = displayText.length();
        }
        if (!displayText.isEmpty()) {
            String t = cursorVisible ? displayText.substring(0, n) : displayText;
            FormattedCharSequence formattedCharSequence = renderText(t, this.self.getDisplayPos()).getVisualOrderText();
            guiGraphicsExtractor.text(font, formattedCharSequence, q, r, m);
            textEndX = q + font.width(formattedCharSequence);
        }
        boolean useBlockCursor = this.self.getCursorPos() < getValue().length() || getValue().length() >= this.self.getMaxLength();
        int u = textEndX;
        if (!cursorVisible) {
            u = n > 0 ? q + this.width : q;
        } else if (useBlockCursor) {
            u = textEndX - 1;
            textEndX--;
        }
        if (!displayText.isEmpty() && cursorVisible && n < displayText.length()) {
            guiGraphicsExtractor.text(font, renderText(displayText.substring(n), this.self.getCursorPos()).getVisualOrderText(), textEndX, r, m);
        }
        if (!useBlockCursor && this.self.getHint() != null) {
            guiGraphicsExtractor.text(font, this.self.getHint(), u - 1, r, -8355712);
        }
        if (drawCursor) {
            if (useBlockCursor) {
                guiGraphicsExtractor.fill(u, r - 1, u + 1, r + 1 + 9, -3092272);
            } else {
                guiGraphicsExtractor.text(font, "_", u, r, m);
            }
        }
        if (o != n) {
            int v = this.self.getDisplayPos();
            int w = this.self.getCursorPos();
            int x = this.self.getHighlightPos();
            int y = Math.max(Math.min(w, x), v);
            int z = Math.max(w, x);
            Component aa = renderText(getValue().substring(v), v);
            String ab = font.substrByWidth(aa, k).getString();
            if (w == z && z > ab.length() + v) {
                this.self.setDisplayPos(z - ab.length());
            }
            Component ac = renderText(getValue().substring(v, y), v);
            int ad = font.width(ac);
            Component ae = renderText(getValue().substring(y, z), y);
            int af = font.width(ae);
            int ag = i + 4;
            extractSelection(guiGraphicsExtractor, ag + ad, r - 1, ag + ad + af, r + 1 + 9);
        }
    }

    private static void extractSelection(GuiGraphicsExtractor guiGraphicsExtractor, int x1, int y1, int x2, int y2) {
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        if (maxX > minX) {
            guiGraphicsExtractor.fill(minX, y1, maxX, y2, -16776961);
        }
    }
}
