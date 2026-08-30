package com.github.franckyi.guapi.api.util;

import net.minecraft.network.chat.Component;

public enum DebugMode {
    INFO("cadeditor.gui.debug_mode.info"),
    FEATURE("cadeditor.gui.debug_mode.feature"),
    UI("cadeditor.gui.debug_mode.ui"),
    NONE("cadeditor.gui.debug_mode.none");

    private final String translationKey;

    DebugMode(String translationKey) {
        this.translationKey = translationKey;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public Component toComponent() {
        return Component.translatable(this.translationKey);
    }
}
