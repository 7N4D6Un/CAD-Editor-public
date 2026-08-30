package com.github.franckyi.guapi.api;

import com.github.franckyi.guapi.api.event.ScreenEvent;
import com.github.franckyi.guapi.api.node.Scene;
import com.github.franckyi.guapi.api.util.ScreenEventType;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface GuapiExceptionHandler {
    public static final GuapiExceptionHandler NONE = new GuapiExceptionHandler() {
        @Override
        public <E extends ScreenEvent> void handleEventException(Exception e, ScreenEventType<E> type, E event, Scene currentScene) {
        }

        @Override
        public void handleTickException(Exception e, Scene currentScene) {
        }

        @Override
        public void handleRenderException(Exception e, GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta, Scene currentScene) {
        }
    };

    <E extends ScreenEvent> void handleEventException(Exception exc, ScreenEventType<E> screenEventType, E e, Scene scene);

    void handleTickException(Exception exc, Scene scene);

    void handleRenderException(Exception exc, GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float f, Scene scene);
}
