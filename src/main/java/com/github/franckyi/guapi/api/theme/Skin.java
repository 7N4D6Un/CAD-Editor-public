package com.github.franckyi.guapi.api.theme;

import com.github.franckyi.guapi.api.EventTarget;
import com.github.franckyi.guapi.api.event.ScreenEvent;
import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.api.util.ScreenEventType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface Skin<N extends Node> extends EventTarget {
    boolean preRender(N n, GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float f);

    void render(N n, GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float f);

    void postRender(N n, GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float f);

    int computeWidth(N n);

    int computeHeight(N n);

    <E extends ScreenEvent> void onEvent(ScreenEventType<E> screenEventType, E e);

    default Minecraft mc() {
        return Minecraft.getInstance();
    }

    default Font font() {
        return mc().font;
    }
}
