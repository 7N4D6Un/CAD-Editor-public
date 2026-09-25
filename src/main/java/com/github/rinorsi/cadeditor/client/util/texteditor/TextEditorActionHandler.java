package com.github.rinorsi.cadeditor.client.util.texteditor;

import java.util.List;

public interface TextEditorActionHandler {
    void removeColorFormatting();

    void addColorFormatting(String color);

    void addStyleFormatting(StyleType type);

    void addFontFormatting(String fontId);

    void addShadowColorFormatting(int argb);

    void applyGradient(List<Integer> colors, boolean shadow);

    void insertToken(String token);

    default boolean supportsColorFormatting() {
        return true;
    }

    default boolean supportsColorReset() {
        return true;
    }

    default boolean supportsStyleFormatting() {
        return true;
    }

    default boolean supportsCustomColorPicker() {
        return true;
    }

    default boolean supportsFontFormatting() {
        return true;
    }

    default boolean supportsGradientFormatting() {
        return true;
    }

    default boolean supportsTokenFormatting() {
        return true;
    }
}
