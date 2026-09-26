package com.github.rinorsi.cadeditor.client.screen.view.entry;

import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.api.node.Slider;

import static com.github.franckyi.guapi.api.GuapiHelper.*;

public class SliderEntryView extends LabeledEntryView {
    private Slider slider;

    @Override
    protected Node createLabeledContent() {
        return slider = slider().prefHeight(16);
    }

    public Slider getSlider() {
        return slider;
    }
}
