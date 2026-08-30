package com.github.franckyi.guapi.base.theme.vanilla.delegate;

import com.github.franckyi.guapi.api.node.TexturedButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class VanillaTexturedButtonSkinDelegate<N extends TexturedButton> extends Button implements VanillaWidgetSkinDelegate {
    protected final N node;

    public VanillaTexturedButtonSkinDelegate(N node) {
        super(node.getX(), node.getY(), node.getWidth(), node.getHeight(), node.getTooltip().isEmpty() ? Component.empty() : (Component) node.getTooltip().get(0), button -> {
        }, value -> value.get());
        this.node = node;
        initNodeWidget(node);
    }

    @Override
    public void render(@NotNull GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        if (this.node.isDrawButton()) {
            super.extractRenderState(guiGraphicsExtractor, mouseX, mouseY, delta);
        } else {
            this.isHovered = this.active && this.visible && isMouseOver((double) mouseX, (double) mouseY);
        }
    }

    protected void extractContents(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        if (this.node.isDrawButton()) {
            extractDefaultSprite(guiGraphicsExtractor);
        }
        if (getMessage().getString().isEmpty()) {
            return;
        }
        int color = this.active ? 16777215 : 10526880;
        int alphaColor = Mth.ceil(this.alpha * 255.0f) << 24;
        guiGraphicsExtractor.centeredText(Minecraft.getInstance().font, getMessage(), getX() + (this.width / 2), getY() + ((this.height - 8) / 2), color | alphaColor);
    }
}
