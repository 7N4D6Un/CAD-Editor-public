package com.github.rinorsi.cadeditor.client.screen.model.entry;

import com.github.rinorsi.cadeditor.client.screen.model.category.CategoryModel;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Consumer;

public class SliderEntryModel extends ValueEntryModel<Float> {
    private final double minValue;
    private final double maxValue;
    private final double step;

    public SliderEntryModel(CategoryModel category, MutableComponent label, float value, double minValue, double maxValue, double step, Consumer<Float> action) {
        super(category, label, value, action);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.step = step;
    }

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public double getStep() {
        return step;
    }

    @Override
    public Type getType() {
        return Type.SLIDER;
    }
}
