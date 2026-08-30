package com.github.franckyi.guapi.base.theme.vanilla.delegate;

import com.github.franckyi.guapi.api.node.Slider;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class VanillaSliderSkinDelegate extends AbstractSliderButton implements VanillaWidgetSkinDelegate {
    private final Slider node;

    public VanillaSliderSkinDelegate(Slider node) {
        super(node.getX(), node.getY(), node.getWidth(), node.getHeight(), Component.empty(), node.getValue());
        this.node = node;
        initNodeWidget(node);
        node.valueProperty().addListener(this::updateValue);
        node.labelFactoryProperty().addListener(this::updateMessage);
        updateValue();
        updateMessage();
    }

    private void updateValue() {
        double max = this.node.getMaxValue() - this.node.getMinValue();
        double value = this.node.getValue() - this.node.getMinValue();
        this.value = value / max;
        updateMessage();
    }

    public void onClick(MouseButtonEvent event, boolean isDoubleClick) {
        updateNodeFromMouse(event.x());
    }

    protected void onDrag(MouseButtonEvent event, double deltaX, double deltaY) {
        updateNodeFromMouse(event.x());
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (deltaX + deltaY > 0.0d) {
            this.node.increment();
            return false;
        }
        if (deltaX + deltaY < 0.0d) {
            this.node.decrement();
            return false;
        }
        return false;
    }

    private void updateNodeFromMouse(double mouseX) {
        updateNode((mouseX - ((double) (getX() + 4))) / ((double) (this.width - 8)));
    }

    private void updateNode(double newRawValue) {
        double range = this.node.getMaxValue() - this.node.getMinValue();
        double value = (Mth.clamp(newRawValue, 0.0d, 1.0d) * range) + this.node.getMinValue();
        double fixedValue = value - (value % this.node.getStep());
        this.node.setValue(fixedValue);
    }

    public boolean keyPressed(KeyEvent event) {
        if (event.key() == 262) {
            this.node.increment();
            return true;
        }
        if (event.key() == 263) {
            this.node.decrement();
            return true;
        }
        return super.keyPressed(event);
    }

    protected void updateMessage() {
        setMessage(this.node.getLabelFactory().apply(this.node.getValue()));
    }

    protected void applyValue() {
    }
}
