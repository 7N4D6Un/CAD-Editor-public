package com.github.rinorsi.cadeditor.client.screen.view;

import com.github.franckyi.guapi.api.GuapiHelper;
import com.github.franckyi.guapi.api.node.Button;
import com.github.franckyi.guapi.api.node.CheckBox;
import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.api.node.TextField;
import com.github.franckyi.guapi.api.node.builder.HBoxBuilder;
import com.github.franckyi.guapi.api.node.builder.VBoxBuilder;
import com.github.franckyi.guapi.api.Color;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.screen.model.TextFormatDialogModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.ColorSelectionScreenModel;
import com.github.rinorsi.cadeditor.common.ModTexts;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.github.franckyi.guapi.api.GuapiHelper.*;

public class TextFormatDialogView extends ScreenView {
    private final List<TextFormatDialogModel.DialogField> fields;
    private final Map<String, TextField> textFields = new LinkedHashMap<>();
    private final Map<String, CheckBox> checkBoxes = new LinkedHashMap<>();

    public TextFormatDialogView(List<TextFormatDialogModel.DialogField> fields) {
        this.fields = fields;
    }

    @Override
    protected Node createEditor() {
        return hBox((Consumer<HBoxBuilder>) editor -> {
            editor.add(vBox(), 1);
            editor.add(vBox((Consumer<VBoxBuilder>) center -> {
                center.add(vBox(), 1);
                center.add(vBox((Consumer<VBoxBuilder>) rows -> {
                    for (TextFormatDialogModel.DialogField field : fields) {
                        switch (field.type()) {
                            case TEXT -> rows.add(createTextRow(field));
                            case COLOR -> rows.add(createColorRow(field));
                            case CHECKBOX -> rows.add(createCheckboxRow(field));
                        }
                    }
                    rows.spacing(6).align(GuapiHelper.CENTER);
                }));
                center.add(vBox(), 1);
                center.spacing(0).align(GuapiHelper.CENTER);
            }), 4);
            editor.add(vBox(), 1);
            editor.spacing(10).fillHeight();
        });
    }

    private Node createTextRow(TextFormatDialogModel.DialogField field) {
        return hBox((Consumer<HBoxBuilder>) row -> {
            row.add(label(field.label()).prefWidth(110).textAlign(GuapiHelper.CENTER_RIGHT));
            TextField textField = textField().prefHeight(16).prefWidth(180);
            if (field.initialValue() != null && !field.initialValue().isEmpty()) {
                textField.setText(field.initialValue());
            }
            textFields.put(field.id(), textField);
            row.add(textField);
            for (TextFormatDialogModel.DialogAction dialogAction : field.actions()) {
                Button actionButton = button(dialogAction.label());
                actionButton.onAction(() -> dialogAction.action().accept(textField));
                row.add(actionButton);
            }
            row.spacing(5).align(GuapiHelper.CENTER);
        });
    }

    private Node createColorRow(TextFormatDialogModel.DialogField field) {
        return hBox((Consumer<HBoxBuilder>) row -> {
            row.add(label(field.label()).prefWidth(110).textAlign(GuapiHelper.CENTER_RIGHT));
            TextField textField = textField().prefHeight(16).prefWidth(180);
            if (field.initialValue() != null && !field.initialValue().isEmpty()) {
                textField.setText(field.initialValue());
            }
            textFields.put(field.id(), textField);
            row.add(textField);
            Button chooseButton = button(ModTexts.choose(ModTexts.CUSTOM_COLOR));
            chooseButton.onAction(() -> {
                int color = parseHexOrDefault(textField.getText(), 0xFFFFFF);
                ModScreenHandler.openColorSelectionScreen(ColorSelectionScreenModel.Target.TEXT, color, hex -> textField.setText(hex));
            });
            row.add(chooseButton);
            row.spacing(5).align(GuapiHelper.CENTER);
        });
    }

    private Node createCheckboxRow(TextFormatDialogModel.DialogField field) {
        CheckBox checkBox = checkBox(field.label());
        checkBox.setChecked(Boolean.parseBoolean(field.initialValue()));
        checkBoxes.put(field.id(), checkBox);
        return checkBox;
    }

    private static int parseHexOrDefault(String hex, int fallback) {
        if (hex == null || hex.length() != 7 || hex.charAt(0) != '#') {
            return fallback;
        }
        try {
            return Color.fromHex(hex);
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return fallback;
        }
    }

    public Map<String, String> collectValues() {
        Map<String, String> values = new LinkedHashMap<>();
        for (TextFormatDialogModel.DialogField field : fields) {
            TextField textField = textFields.get(field.id());
            if (textField != null) {
                values.put(field.id(), textField.getText());
                continue;
            }
            CheckBox checkBox = checkBoxes.get(field.id());
            if (checkBox != null) {
                values.put(field.id(), Boolean.toString(checkBox.isChecked()));
            }
        }
        return values;
    }
}
