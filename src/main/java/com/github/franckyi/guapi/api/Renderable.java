package com.github.franckyi.guapi.api;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface Renderable {
    void render(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float f);

    default boolean preRender(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        return false;
    }

    default void postRender(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
    }
}
