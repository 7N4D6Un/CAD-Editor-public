package com.github.rinorsi.cadeditor.client.util.texteditor;

import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.PlainTextContents;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TextEditorInputParser {
    private final List<Formatting> formattings = new ArrayList<>();
    private final StringBuilder flattened = new StringBuilder();
    private int index;

    public String flatten(MutableComponent text) {
        appendComponent(text);
        return flattened.toString();
    }

    private void appendComponent(MutableComponent text) {
        String piece = contentToText(text);
        int start = index;
        flattened.append(piece);
        int length = piece.length();
        if (length > 0) {
            Style style = text.getStyle();
            addStyleFormattingIf(style.isBold(), start, length, StyleType.BOLD);
            addStyleFormattingIf(style.isItalic(), start, length, StyleType.ITALIC);
            addStyleFormattingIf(style.isUnderlined(), start, length, StyleType.UNDERLINED);
            addStyleFormattingIf(style.isStrikethrough(), start, length, StyleType.STRIKETHROUGH);
            addStyleFormattingIf(style.isObfuscated(), start, length, StyleType.OBFUSCATED);
            if (style.getColor() != null) {
                addColorFormatting(new ColorFormatting(start, start + length, style.getColor().toString()));
            }
            if (style.getFont() != null && !FontDescription.DEFAULT.equals(style.getFont())
                    && style.getFont() instanceof FontDescription.Resource resource) {
                addFormatting(new FontFormatting(start, start + length, resource.id().toString()));
            }
            if (style.getShadowColor() != null) {
                addFormatting(new ShadowColorFormatting(start, start + length, style.getShadowColor()));
            }
        }
        index += length;
        if (text.getSiblings() != null) {
            text.getSiblings().stream()
                    .filter(MutableComponent.class::isInstance)
                    .map(MutableComponent.class::cast)
                    .forEach(this::appendComponent);
        }
    }

    private String contentToText(MutableComponent text) {
        String token = TextTokens.contentsToToken(text);
        if (token != null) {
            return token;
        }
        if (text.getContents() instanceof PlainTextContents lc) {
            return lc.text();
        }
        return text.getString();
    }

    private void addStyleFormattingIf(boolean condition, int start, int length, StyleType type) {
        if (condition) {
            addStyleFormatting(new StyleFormatting(start, start + length, type));
        }
    }

    private void addStyleFormatting(StyleFormatting formatting) {
        Optional<StyleFormatting> merge = formattings.stream()
                .filter(StyleFormatting.class::isInstance)
                .map(StyleFormatting.class::cast)
                .filter(other -> other.getType() == formatting.getType() && other.getEnd() == formatting.getStart())
                .findAny();
        if (merge.isPresent()) {
            merge.get().setEnd(formatting.getEnd());
        } else {
            formattings.add(formatting);
        }
    }

    private void addColorFormatting(ColorFormatting formatting) {
        Optional<ColorFormatting> merge = formattings.stream()
                .filter(ColorFormatting.class::isInstance)
                .map(ColorFormatting.class::cast)
                .filter(other -> Objects.equals(other.getColor(), formatting.getColor()) && other.getEnd() == formatting.getStart())
                .findAny();
        if (merge.isPresent()) {
            merge.get().setEnd(formatting.getEnd());
        } else {
            formattings.add(formatting);
        }
    }

    private void addFormatting(Formatting formatting) {
        Optional<Formatting> merge = formattings.stream()
                .filter(other -> other.getClass() == formatting.getClass() && other.getEnd() == formatting.getStart())
                .filter(other -> new FormattingValue(other).matches(formatting))
                .findAny();
        if (merge.isPresent()) {
            merge.get().setEnd(formatting.getEnd());
        } else {
            formattings.add(formatting);
        }
    }

    public List<Formatting> getFormattings() {
        return formattings;
    }

    private record FormattingValue(Formatting formatting) {
        boolean matches(Formatting other) {
            if (formatting instanceof FontFormatting font && other instanceof FontFormatting otherFont) {
                return Objects.equals(font.getFontId(), otherFont.getFontId());
            }
            if (formatting instanceof ShadowColorFormatting shadow && other instanceof ShadowColorFormatting otherShadow) {
                return shadow.getArgb() == otherShadow.getArgb();
            }
            return false;
        }
    }
}
