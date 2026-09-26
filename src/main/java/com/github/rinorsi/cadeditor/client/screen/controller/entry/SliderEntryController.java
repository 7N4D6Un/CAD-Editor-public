package com.github.rinorsi.cadeditor.client.screen.controller.entry;

import com.github.rinorsi.cadeditor.client.screen.model.entry.SliderEntryModel;
import com.github.rinorsi.cadeditor.client.screen.view.entry.SliderEntryView;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public class SliderEntryController extends ValueEntryController<SliderEntryModel, SliderEntryView> {
    public SliderEntryController(SliderEntryModel model, SliderEntryView view) {
        super(model, view);
    }

    @Override
    public void bind() {
        super.bind();
        double min = getModel().getMinValue();
        double max = getModel().getMaxValue();
        double step = getModel().getStep();
        view.getSlider().setMinValue(0);
        view.getSlider().setMaxValue((int) Math.round((max - min) / step));
        view.getSlider().setStep(1);
        view.getSlider().setLabelFactory(value -> Component.literal(String.format(Locale.ROOT, "%.1f°", decode(value))));
        view.getSlider().valueProperty().addListener(v -> getModel().setValue((float) decode(view.getSlider().getValue())));
        getModel().valueProperty().addListener(v -> view.getSlider().setValue(encode(getModel().getValue())));
        view.getSlider().setValue(encode(getModel().getValue()));
    }

    private double decode(double encoded) {
        return getModel().getMinValue() + encoded * getModel().getStep();
    }

    private int encode(float value) {
        return (int) Math.round((value - getModel().getMinValue()) / getModel().getStep());
    }
}
