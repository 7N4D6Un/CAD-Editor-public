package com.github.rinorsi.cadeditor.client.screen.view.selection.color;

import com.github.franckyi.guapi.api.GuapiHelper;
import com.github.franckyi.guapi.api.node.HBox;
import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.api.node.Slider;
import com.github.franckyi.guapi.api.node.TextField;
import com.github.franckyi.guapi.api.node.builder.HBoxBuilder;
import com.github.franckyi.guapi.api.node.builder.VBoxBuilder;
import com.github.rinorsi.cadeditor.client.screen.view.ScreenView;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;


public abstract class ColorSelectionScreenView extends ScreenView {
    private Slider redSlider;
    private Slider greenSlider;
    private Slider blueSlider;
    private TextField hexField;
    private HBox exampleBox;

    protected abstract Node createExample();

    @Override 
    protected MutableComponent getHeaderLabelText() {
        return ModTexts.title(ModTexts.choose(ModTexts.CUSTOM_COLOR));
    }

    @Override 
    protected Node createEditor() {
        return GuapiHelper.hBox((Consumer<HBoxBuilder>) editor -> {
            editor.add(GuapiHelper.vBox(), 1);
            editor.add(GuapiHelper.vBox((Consumer<VBoxBuilder>) center -> {
                center.add(GuapiHelper.vBox((Consumer<VBoxBuilder>) rgb -> {
                    rgb.add(GuapiHelper.hBox((Consumer<HBoxBuilder>) labels -> {
                        labels.add(GuapiHelper.label((Component) ModTexts.RED_COLOR), 1);
                        labels.add(GuapiHelper.label((Component) ModTexts.GREEN_COLOR), 1);
                        labels.add(GuapiHelper.label((Component) ModTexts.BLUE_COLOR), 1);
                        labels.spacing(4);
                    }));
                    rgb.add(GuapiHelper.hBox((Consumer<HBoxBuilder>) fields -> {
                        Slider redColorSlider = colorSlider();
                        this.redSlider = redColorSlider;
                        fields.add(redColorSlider, 1);
                        Slider greenColorSlider = colorSlider();
                        this.greenSlider = greenColorSlider;
                        fields.add(greenColorSlider, 1);
                        Slider blueColorSlider = colorSlider();
                        this.blueSlider = blueColorSlider;
                        fields.add(blueColorSlider, 1);
                        fields.spacing(4);
                    }));
                    ((VBoxBuilder) rgb.fillWidth()).spacing(4);
                }));
                center.add(GuapiHelper.vBox((Consumer<VBoxBuilder>) hex -> {
                    hex.add(GuapiHelper.hBox((Consumer<HBoxBuilder>) labels -> {
                        labels.add(GuapiHelper.label((Component) ModTexts.Literal.HEX), 1);
                        labels.add(GuapiHelper.hBox(), 1);
                        labels.add(GuapiHelper.hBox(), 1);
                        labels.spacing(4);
                    }));
                    hex.add(GuapiHelper.hBox((Consumer<HBoxBuilder>) fields -> {
                        TextField textField = (TextField) GuapiHelper.textField().prefHeight(16);
                        this.hexField = textField;
                        fields.add(textField, 1);
                        fields.add(createExample(), 1);
                        HBox hBox = (HBox) GuapiHelper.hBox().prefHeight(16);
                        this.exampleBox = hBox;
                        fields.add(hBox, 1);
                        ((HBoxBuilder) fields.spacing(4)).align(GuapiHelper.CENTER);
                    }));
                    ((VBoxBuilder) hex.fillWidth()).spacing(4);
                }));
                ((VBoxBuilder) ((VBoxBuilder) center.fillWidth()).spacing(10)).align(GuapiHelper.CENTER);
            }), 4);
            editor.add(GuapiHelper.vBox(), 1);
            editor.fillHeight();
        });
    }

    private Slider colorSlider() {
        return GuapiHelper.slider(0.0d, 0.0d, 255.0d, 1.0d);
    }

    public Slider getRedSlider() {
        return this.redSlider;
    }

    public Slider getGreenSlider() {
        return this.greenSlider;
    }

    public Slider getBlueSlider() {
        return this.blueSlider;
    }

    public TextField getHexField() {
        return this.hexField;
    }

    public HBox getExampleBox() {
        return this.exampleBox;
    }
}
