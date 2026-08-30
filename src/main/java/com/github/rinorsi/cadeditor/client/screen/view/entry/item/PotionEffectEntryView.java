package com.github.rinorsi.cadeditor.client.screen.view.entry.item;

import com.github.franckyi.guapi.api.GuapiHelper;
import com.github.franckyi.guapi.api.node.CheckBox;
import com.github.franckyi.guapi.api.node.HBox;
import com.github.franckyi.guapi.api.node.Label;
import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.api.node.SpriteView;
import com.github.franckyi.guapi.api.node.TextField;
import com.github.franckyi.guapi.api.node.ToggleButton;
import com.github.franckyi.guapi.api.node.builder.HBoxBuilder;
import com.github.franckyi.guapi.api.node.builder.SpriteViewBuilder;
import com.github.franckyi.guapi.api.node.builder.TextFieldBuilder;
import com.github.franckyi.guapi.api.node.builder.ToggleButtonBuilder;
import com.github.franckyi.guapi.api.node.builder.VBoxBuilder;
import com.github.franckyi.guapi.api.util.Predicates;
import com.github.rinorsi.cadeditor.client.screen.view.entry.SelectionEntryView;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;


public class PotionEffectEntryView extends SelectionEntryView {
    private TextField amplifierField;
    private TextField durationField;
    private CheckBox ambientBox;
    private CheckBox showParticlesBox;
    private CheckBox showIconBox;
    private ToggleButton durationUnitToggle;
    private SpriteView previewSpriteView;
    private Label previewLabel;
    private HBox previewBox;

    @Override 
    public void build() {
        super.build();
        getTextField().setPlaceholder(ModTexts.EFFECT);
        this.previewBox = GuapiHelper.hBox((Consumer<HBoxBuilder>) preview -> {
            preview.add(this.previewSpriteView = GuapiHelper.spriteView((Consumer<SpriteViewBuilder>) sprite -> {
                sprite.imageWidth(18).imageHeight(18);
            }));
            Label label = (Label) GuapiHelper.label().prefHeight(16);
            this.previewLabel = label;
            preview.add(label);
            ((HBoxBuilder) preview.align(GuapiHelper.CENTER_LEFT)).spacing(4);
        });
        setPreview(this.previewBox);
        setPreviewVisible(false);
    }

    @Override 
    protected Node createLabeledContent() {
        return GuapiHelper.vBox((Consumer<VBoxBuilder>) root -> {
            root.add(super.createLabeledContent());
            root.add(GuapiHelper.hBox((Consumer<HBoxBuilder>) bottom -> {
                addExtraBottomInputsBeforeAmplifier(bottom);
                TextField textField = (TextField) ((TextFieldBuilder) GuapiHelper.textField().prefHeight(16)).validator(Predicates.range(0, Node.INFINITE_SIZE)).tooltip(ModTexts.AMPLIFIER);
                this.amplifierField = textField;
                bottom.add(textField, 1);
                TextField durationTextField = (TextField) ((TextFieldBuilder) GuapiHelper.textField().prefHeight(16)).validator(Predicates.range(1, Node.INFINITE_SIZE)).tooltip(ModTexts.DURATION);
                this.durationField = durationTextField;
                bottom.add(durationTextField, 1);
                addExtraBottomInputsBeforeUnitToggle(bottom);
                ToggleButton toggleButton = (ToggleButton) ((ToggleButtonBuilder) GuapiHelper.toggleButton((Component) ModTexts.TICKS).prefHeight(16)).prefWidth(44);
                this.durationUnitToggle = toggleButton;
                bottom.add(toggleButton);
                addExtraBottomInputs(bottom);
                bottom.add(GuapiHelper.hBox((Consumer<HBoxBuilder>) boxes -> {
                    CheckBox checkBox = (CheckBox) GuapiHelper.checkBox().tooltip(ModTexts.AMBIENT);
                    this.ambientBox = checkBox;
                    boxes.add(checkBox);
                    CheckBox showParticlesCheckBox = (CheckBox) GuapiHelper.checkBox().tooltip(ModTexts.SHOW_PARTICLES);
                    this.showParticlesBox = showParticlesCheckBox;
                    boxes.add(showParticlesCheckBox);
                    CheckBox showIconCheckBox = (CheckBox) GuapiHelper.checkBox().tooltip(ModTexts.SHOW_ICON);
                    this.showIconBox = showIconCheckBox;
                    boxes.add(showIconCheckBox);
                    boxes.spacing(5);
                }));
                bottom.spacing(5);
            }));
            ((VBoxBuilder) root.spacing(5)).fillWidth();
        });
    }

    protected void addExtraBottomInputsBeforeAmplifier(HBoxBuilder bottom) {
    }

    protected void addExtraBottomInputsBeforeUnitToggle(HBoxBuilder bottom) {
    }

    protected void addExtraBottomInputs(HBoxBuilder bottom) {
    }

    public TextField getAmplifierField() {
        return this.amplifierField;
    }

    public TextField getDurationField() {
        return this.durationField;
    }

    public CheckBox getAmbientBox() {
        return this.ambientBox;
    }

    public CheckBox getShowParticlesBox() {
        return this.showParticlesBox;
    }

    public CheckBox getShowIconBox() {
        return this.showIconBox;
    }

    public ToggleButton getDurationUnitToggle() {
        return this.durationUnitToggle;
    }

    public SpriteView getPreviewSpriteView() {
        return this.previewSpriteView;
    }

    public Label getPreviewLabel() {
        return this.previewLabel;
    }

    public HBox getPreviewBox() {
        return this.previewBox;
    }
}
