package com.github.rinorsi.cadeditor.client.screen.view.entry.item;

import com.github.franckyi.guapi.api.GuapiHelper;
import com.github.franckyi.guapi.api.node.EnumButton;
import com.github.franckyi.guapi.api.node.Label;
import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.api.node.TextField;
import com.github.franckyi.guapi.api.node.builder.EnumButtonBuilder;
import com.github.franckyi.guapi.api.node.builder.HBoxBuilder;
import com.github.franckyi.guapi.api.node.builder.LabelBuilder;
import com.github.franckyi.guapi.api.node.builder.TextFieldBuilder;
import com.github.franckyi.guapi.api.node.builder.VBoxBuilder;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.MapDecorationEntryModel;
import com.github.rinorsi.cadeditor.client.screen.view.entry.EntryView;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;


public class MapDecorationEntryView extends EntryView {
    private static final int LABEL_WIDTH = 110;
    private static final int FIELD_PREF_WIDTH = 180;
    private TextField nameField;
    private TextField xField;
    private TextField zField;
    private TextField rotationField;
    private EnumButton<MapDecorationEntryModel.DecorationTypeOption> typeSelector;

    
    @Override 
    protected Node createContent() {
        return GuapiHelper.vBox((Consumer<VBoxBuilder>) content -> {
            MutableComponent nameLabelText = ModTexts.MAP_DECORATION_NAME;
            TextField nameTextField = sizedTextField(ModTexts.MAP_DECORATION_NAME);
            this.nameField = nameTextField;
            content.add(buildRow(nameLabelText, nameTextField));
            MutableComponent xLabelText = ModTexts.MAP_DECORATION_X;
            TextField xTextField = sizedTextField(ModTexts.MAP_DECORATION_X);
            this.xField = xTextField;
            content.add(buildRow(xLabelText, xTextField));
            MutableComponent zLabelText = ModTexts.MAP_DECORATION_Z;
            TextField zTextField = sizedTextField(ModTexts.MAP_DECORATION_Z);
            this.zField = zTextField;
            content.add(buildRow(zLabelText, zTextField));
            MutableComponent rotationLabelText = ModTexts.MAP_DECORATION_ROTATION;
            TextField rotationTextField = sizedTextField(ModTexts.MAP_DECORATION_ROTATION);
            this.rotationField = rotationTextField;
            content.add(buildRow(rotationLabelText, rotationTextField));
            MutableComponent typeLabelText = ModTexts.MAP_DECORATION_TYPE;
            EnumButton<MapDecorationEntryModel.DecorationTypeOption> enumButton = GuapiHelper.<MapDecorationEntryModel.DecorationTypeOption>enumButton(MapDecorationEntryModel.DecorationTypeOption.defaults()).prefHeight(16).prefWidth(FIELD_PREF_WIDTH).maxWidth(Node.INFINITE_SIZE).textFactory(MapDecorationEntryModel.DecorationTypeOption::getDisplayName).tooltip(ModTexts.MAP_DECORATION_TYPE);
            this.typeSelector = enumButton;
            content.add(buildRow(typeLabelText, enumButton));
            content.spacing(4);
        }).maxWidth(Node.INFINITE_SIZE);
    }

    private TextField sizedTextField(Component placeholderText) {
        return ((TextFieldBuilder) ((TextFieldBuilder) ((TextFieldBuilder) GuapiHelper.textField().prefHeight(16)).prefWidth(FIELD_PREF_WIDTH)).maxWidth(Node.INFINITE_SIZE)).placeholder(placeholderText);
    }

    
    private Node buildRow(Component labelText, Node field) {
        return GuapiHelper.hBox((Consumer<HBoxBuilder>) row -> {
            row.add(createLabel(labelText));
            row.add(field, 1);
            ((HBoxBuilder) row.spacing(6)).align(GuapiHelper.CENTER_LEFT);
        }).maxWidth(Node.INFINITE_SIZE);
    }

    private Label createLabel(Component labelText) {
        return ((LabelBuilder) ((LabelBuilder) GuapiHelper.label().label(labelText)).prefWidth(LABEL_WIDTH)).textAlign(GuapiHelper.CENTER_RIGHT);
    }

    public void setTypeOptions(List<MapDecorationEntryModel.DecorationTypeOption> options) {
        MapDecorationEntryModel.DecorationTypeOption matched;
        MapDecorationEntryModel.DecorationTypeOption current = this.typeSelector.getValue();
        this.typeSelector.getValues().setAll(options);
        if (options.isEmpty()) {
            this.typeSelector.setValue(null);
        } else if (current != null && (matched = MapDecorationEntryModel.DecorationTypeOption.find(options, current.getResourceId())) != null) {
            this.typeSelector.setValue(matched);
        } else {
            this.typeSelector.setValue(options.get(0));
        }
    }

    public TextField getNameField() {
        return this.nameField;
    }

    public TextField getXField() {
        return this.xField;
    }

    public TextField getZField() {
        return this.zField;
    }

    public TextField getRotationField() {
        return this.rotationField;
    }

    public EnumButton<MapDecorationEntryModel.DecorationTypeOption> getTypeSelector() {
        return this.typeSelector;
    }
}
