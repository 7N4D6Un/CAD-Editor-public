package com.github.franckyi.guapi.base.theme.vanilla.delegate;

import com.github.franckyi.guapi.api.node.Button;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;

public class VanillaButtonSkinDelegate<N extends Button> extends net.minecraft.client.gui.components.Button implements VanillaWidgetSkinDelegate {
    protected final N node;

    public VanillaButtonSkinDelegate(N node) {
        super(node.getX(), node.getY(), node.getWidth(), node.getHeight(), node.getLabel(), button -> {
        }, value -> value.get());
        this.node = node;
        initLabeledWidget(node);
    }

    protected void extractContents(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        extractDefaultSprite(guiGraphicsExtractor);
        int color = this.active ? 16777215 : 10526880;
        int alphaColor = Mth.ceil(this.alpha * 255.0f) << 24;
        guiGraphicsExtractor.centeredText(Minecraft.getInstance().font, getMessage(), getX() + (this.width / 2), getY() + ((this.height - 8) / 2), color | alphaColor);
    }
}
