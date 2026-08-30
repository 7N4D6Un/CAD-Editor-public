package com.github.franckyi.guapi.base.theme.vanilla.delegate;

import com.github.franckyi.guapi.api.node.CheckBox;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class VanillaCheckBoxSkinDelegate extends AbstractButton implements VanillaWidgetSkinDelegate {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath("cadeditor", "textures/gui/checkbox.png");
    protected final CheckBox node;
    private boolean selected;

    public VanillaCheckBoxSkinDelegate(CheckBox node) {
        super(node.getX(), node.getY(), 16, 16, node.getLabel());
        this.node = node;
        this.selected = node.isChecked();
        initLabeledWidget(node);
        node.checkedProperty().addListener(this::onModelChange);
    }

    private void onModelChange() {
        if (this.node.isChecked() != this.selected) {
            setSelected(this.node.isChecked());
        }
    }

    private void setSelected(boolean selected) {
        this.selected = selected;
    }

    private boolean isSelected() {
        return this.selected;
    }

    public void onPress(InputWithModifiers input) {
        setSelected(!isSelected());
        this.node.setChecked(isSelected());
    }

    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }

    protected void extractContents(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float delta) {
        Minecraft mc = Minecraft.getInstance();
        guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, getX(), getY(), isHoveredOrFocused() ? 16.0f : 0.0f, isSelected() ? 16.0f : 0.0f, 16, 16, 32, 32);
        Font font = mc.font;
        Component message = getMessage();
        int x = getX() + 20;
        int y = getY();
        int i = this.height;
        Objects.requireNonNull(mc.font);
        guiGraphicsExtractor.text(font, message, x, y + (((i - 9) - 1) / 2), 14737632 | (Mth.ceil(this.alpha * 255.0f) << 24));
    }
}
