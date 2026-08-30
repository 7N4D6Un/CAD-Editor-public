package com.github.franckyi.guapi.base.theme;

import com.github.franckyi.guapi.api.Color;
import com.github.franckyi.guapi.api.Guapi;
import com.github.franckyi.guapi.api.RenderHelper;
import com.github.franckyi.guapi.api.event.ScreenEvent;
import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.api.theme.Skin;
import com.github.franckyi.guapi.api.util.DebugMode;
import com.github.franckyi.guapi.api.util.ScreenEventType;
import com.github.rinorsi.cadeditor.client.debug.DebugLog;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public abstract class AbstractSkin<N extends Node> implements Skin<N> {
    private final int debugColor = new Random().nextInt(16777216) + Color.NONE;
    private static final Map<Node, Integer> NODE_IDS = new ConcurrentHashMap<>();
    private static final AtomicInteger NODE_ID_SEQ = new AtomicInteger();

    protected AbstractSkin() {
    }

    @Override
    public boolean preRender(N node, GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        return false;
    }

    @Override
    public void render(N node, GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        renderBackground(node, guiGraphicsExtractor);
    }

    @Override
    public void postRender(N node, GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        DebugMode mode = Guapi.getDebugMode();
        if (mode == DebugMode.UI) {
            DebugLog.ui(nodeDebugKey(node), () -> {
                return describeNode(node, mouseX, mouseY);
            });
            renderDebug(node, guiGraphicsExtractor);
        }
        if (!node.getTooltip().isEmpty() && node.isHovered()) {
            RenderHelper.drawTooltip(guiGraphicsExtractor, node.getTooltip(), getTooltipX(node, mouseX, mouseY), getTooltipY(node, mouseX, mouseY));
        }
    }

    protected int getTooltipX(N node, int mouseX, int mouseY) {
        return mouseX;
    }

    protected int getTooltipY(N node, int mouseX, int mouseY) {
        return mouseY;
    }

    @Override
    public <E extends ScreenEvent> void onEvent(ScreenEventType<E> type, E event) {
        type.onEvent(this, event);
    }

    protected void renderDebug(N node, GuiGraphicsExtractor guiGraphicsExtractor) {
        RenderHelper.drawRectangle(guiGraphicsExtractor, node.getLeft(), node.getTop(), node.getRight(), node.getBottom(), this.debugColor);
        if (node.getHeight() > 20) {
            int id = nodeId(node);
            String label = "#" + id + " " + node.getClass().getSimpleName();
            RenderHelper.drawString(guiGraphicsExtractor, Component.literal(label), node.getLeft() + 2, node.getTop() + 2, -1, true);
        }
    }

    protected void renderBackground(N node, GuiGraphicsExtractor guiGraphicsExtractor) {
        RenderHelper.fillRectangle(guiGraphicsExtractor, node.getLeft(), node.getTop(), node.getRight(), node.getBottom(), node.getBackgroundColor());
    }

    private String nodeDebugKey(N node) {
        return node.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(node));
    }

    private String describeNode(N node, int mouseX, int mouseY) {
        int id = nodeId(node);
        String parent = node.getParent() != null ? node.getParent().getClass().getSimpleName() : "<root>";
        String scene = node.getScene() != null ? node.getScene().getClass().getSimpleName() : "<none>";
        String type = node.getClass().getName();
        return String.format("id=%d type=%s parent=%s scene=%s bounds=[%d,%d -> %d,%d] size[w=%d,h=%d pref=%dx%d computed=%dx%d] hovered=%s visible=%s mouse=(%d,%d)", id, type, parent, scene, node.getLeft(), node.getTop(), node.getRight(), node.getBottom(), node.getWidth(), node.getHeight(), node.getPrefWidth(), node.getPrefHeight(), node.getComputedWidth(), node.getComputedHeight(), node.isHovered(), node.isVisible(), mouseX, mouseY);
    }

    private int nodeId(Node node) {
        return NODE_IDS.computeIfAbsent(node, n -> NODE_ID_SEQ.incrementAndGet()).intValue();
    }
}
