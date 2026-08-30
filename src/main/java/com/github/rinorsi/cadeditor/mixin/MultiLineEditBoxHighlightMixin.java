package com.github.rinorsi.cadeditor.mixin;

import com.github.rinorsi.cadeditor.client.util.texteditor.MultiLineEditBoxHighlightAccess;
import com.github.rinorsi.cadeditor.client.util.texteditor.SNBTSyntaxHighlighter;
import com.github.rinorsi.cadeditor.client.util.texteditor.SyntaxHighlightingPalette;
import com.github.rinorsi.cadeditor.client.util.texteditor.SyntaxHighlightingPreset;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.IMEPreeditOverlay;
import net.minecraft.client.gui.components.MultilineTextField;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.TextCursorUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiLineEditBox.class)
public abstract class MultiLineEditBoxHighlightMixin implements MultiLineEditBoxHighlightAccess {
    @Shadow
    @Final
    private MultilineTextField textField;

    @Shadow
    @Final
    private Font font;

    @Shadow
    @Final
    private int textColor;

    @Shadow
    @Final
    private boolean textShadow;

    @Shadow
    @Final
    private int cursorColor;

    @Shadow
    @Final
    private Component placeholder;

    @Shadow
    private long focusedTime;

    @Shadow
    private IMEPreeditOverlay preeditOverlay;

    @Unique
    private SNBTSyntaxHighlighter cadeditor$highlighter;

    @Unique
    private static final int cadeditor$PLACEHOLDER_TEXT_COLOR = ARGB.color(204, -2039584);

    @Override
    public void cadeditor$setHighlighter(SNBTSyntaxHighlighter highlighter) {
        this.cadeditor$highlighter = highlighter;
    }

    @Inject(method = "extractContents", at = @At("HEAD"), cancellable = true)
    private void cadeditor$extractHighlightedContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        SNBTSyntaxHighlighter highlighter = this.cadeditor$highlighter;
        if (highlighter == null) {
            return;
        }
        ci.cancel();
        AbstractWidget self = (AbstractWidget) (Object) this;
        AbstractTextAreaWidgetMixin area = (AbstractTextAreaWidgetMixin) (Object) this;
        String value = this.textField.value();
        if (value.isEmpty() && !self.isFocused()) {
            graphics.textWithWordWrap(this.font, this.placeholder, area.invokeGetInnerLeft(), area.invokeGetInnerTop(), self.getWidth() - area.invokeTotalInnerPadding(), cadeditor$PLACEHOLDER_TEXT_COLOR);
            return;
        }
        highlighter.setSource(value);
        List<SNBTSyntaxHighlighter.Token> tokens = highlighter.getTokens();
        SyntaxHighlightingPalette palette = SyntaxHighlightingPreset.resolveCurrent().palette();
        int cursor = this.textField.cursor();
        boolean showCursor = self.isFocused() && TextCursorUtils.isCursorVisible(Util.getMillis() - this.focusedTime);
        boolean needsValidCursorPos = this.preeditOverlay != null;
        boolean insertCursor = cursor < value.length();
        int cursorX = 0;
        int cursorY = 0;
        int drawTop = area.invokeGetInnerTop();
        int innerLeft = area.invokeGetInnerLeft();
        boolean hasDrawnCursor = false;
        for (Object lineObj : this.textField.iterateLines()) {
            MultilineTextFieldStringViewAccessor lineView = (MultilineTextFieldStringViewAccessor) lineObj;
            int lineBegin = lineView.cadeditor$beginIndex();
            int lineEnd = lineView.cadeditor$endIndex();
            boolean lineWithinVisibleBounds = area.invokeWithinContentAreaTopBottom(drawTop, drawTop + 9);
            if (!hasDrawnCursor && (needsValidCursorPos || showCursor) && insertCursor && cursor >= lineBegin && cursor <= lineEnd) {
                if (lineWithinVisibleBounds) {
                    cadeditor$drawLine(graphics, value, tokens, palette, lineBegin, lineEnd, innerLeft, drawTop);
                    cursorX = innerLeft + this.font.width(value.substring(lineBegin, cursor));
                    cursorY = drawTop;
                    if (showCursor) {
                        TextCursorUtils.extractInsertCursor(graphics, cursorX, cursorY, this.cursorColor, 9 + 1);
                    }
                    hasDrawnCursor = true;
                }
            } else if (lineWithinVisibleBounds) {
                cadeditor$drawLine(graphics, value, tokens, palette, lineBegin, lineEnd, innerLeft, drawTop);
                if ((needsValidCursorPos || showCursor) && !insertCursor) {
                    cursorX = innerLeft + this.font.width(value.substring(lineBegin, lineEnd));
                    cursorY = drawTop;
                }
            }
            drawTop += 9;
        }
        if (showCursor && !insertCursor && area.invokeWithinContentAreaTopBottom(cursorY, cursorY + 9)) {
            TextCursorUtils.extractAppendCursor(graphics, this.font, cursorX, cursorY, this.cursorColor, this.textShadow);
        }
        if (this.textField.hasSelection()) {
            MultilineTextFieldStringViewAccessor selection = (MultilineTextFieldStringViewAccessor) (Object) this.textField.getSelected();
            int selectionBegin = selection.cadeditor$beginIndex();
            int selectionEnd = selection.cadeditor$endIndex();
            int drawX = innerLeft;
            int selectionTop = area.invokeGetInnerTop();
            for (Object lineObj : this.textField.iterateLines()) {
                MultilineTextFieldStringViewAccessor lineView = (MultilineTextFieldStringViewAccessor) lineObj;
                int lineBegin = lineView.cadeditor$beginIndex();
                int lineEnd = lineView.cadeditor$endIndex();
                if (selectionBegin > lineEnd) {
                    selectionTop += 9;
                } else {
                    if (lineBegin > selectionEnd) {
                        break;
                    }
                    if (area.invokeWithinContentAreaTopBottom(selectionTop, selectionTop + 9)) {
                        int drawBegin = this.font.width(value.substring(lineBegin, Math.max(selectionBegin, lineBegin)));
                        int drawEnd = selectionEnd > lineEnd
                                ? self.getWidth() - area.invokeInnerPadding()
                                : this.font.width(value.substring(lineBegin, selectionEnd));
                        graphics.textHighlight(drawX + drawBegin, selectionTop, drawX + drawEnd, selectionTop + 9, true);
                    }
                    selectionTop += 9;
                }
            }
        }
        if (self.isHovered()) {
            graphics.requestCursor(CursorTypes.IBEAM);
        }
        if (this.preeditOverlay != null) {
            this.preeditOverlay.updateInputPosition(cursorX, cursorY);
            graphics.setPreeditOverlay(this.preeditOverlay);
        }
    }

    @Unique
    private void cadeditor$drawLine(GuiGraphicsExtractor graphics, String value, List<SNBTSyntaxHighlighter.Token> tokens,
                                    SyntaxHighlightingPalette palette, int begin, int end, int x, int y) {
        if (begin >= end) {
            return;
        }
        int index = begin;
        for (SNBTSyntaxHighlighter.Token token : tokens) {
            if (token.end() <= index) {
                continue;
            }
            if (token.start() >= end) {
                break;
            }
            int colouredStart = Math.max(token.start(), index);
            x = cadeditor$drawSegment(graphics, value, index, colouredStart, x, y, this.textColor);
            int colouredEnd = Math.min(token.end(), end);
            x = cadeditor$drawSegment(graphics, value, colouredStart, colouredEnd, x, y,
                    cadeditor$colour(palette, token.type()));
            index = colouredEnd;
            if (index >= end) {
                return;
            }
        }
        cadeditor$drawSegment(graphics, value, index, end, x, y, this.textColor);
    }

    @Unique
    private int cadeditor$drawSegment(GuiGraphicsExtractor graphics, String value, int start, int end, int x, int y, int color) {
        if (end <= start) {
            return x;
        }
        String segment = value.substring(start, end);
        graphics.text(this.font, segment, x, y, color, this.textShadow);
        return x + this.font.width(segment);
    }

    @Unique
    private static int cadeditor$colour(SyntaxHighlightingPalette palette, SNBTSyntaxHighlighter.TokenType type) {
        ChatFormatting formatting = palette.colour(type);
        TextColor textColor = formatting == null ? null : TextColor.fromLegacyFormat(formatting);
        return textColor == null ? -2039584 : textColor.getValue() | 0xFF000000;
    }
}
