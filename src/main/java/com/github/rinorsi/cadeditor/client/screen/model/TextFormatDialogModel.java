package com.github.rinorsi.cadeditor.client.screen.model;

import com.github.franckyi.guapi.api.mvc.Model;
import com.github.franckyi.guapi.api.node.TextField;
import net.minecraft.network.chat.MutableComponent;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class TextFormatDialogModel implements Model {
    private final MutableComponent title;
    private final List<DialogField> fields;
    private final Consumer<Map<String, String>> onApply;

    public TextFormatDialogModel(MutableComponent title, List<DialogField> fields, Consumer<Map<String, String>> onApply) {
        this.title = title;
        this.fields = fields;
        this.onApply = onApply;
    }

    public MutableComponent getTitle() {
        return title;
    }

    public List<DialogField> getFields() {
        return fields;
    }

    public void apply(Map<String, String> values) {
        onApply.accept(values);
    }

    public static Map<String, String> emptyValues() {
        return new LinkedHashMap<>();
    }

    public record DialogField(String id, MutableComponent label, FieldType type, String initialValue, List<DialogAction> actions) {
        public static DialogField text(String id, MutableComponent label, String initialValue) {
            return text(id, label, initialValue, List.of());
        }

        public static DialogField text(String id, MutableComponent label, String initialValue, List<DialogAction> actions) {
            return new DialogField(id, label, FieldType.TEXT, initialValue, actions);
        }

        public static DialogField color(String id, MutableComponent label, String initialValue) {
            return new DialogField(id, label, FieldType.COLOR, initialValue, List.of());
        }

        public static DialogField checkbox(String id, MutableComponent label, boolean initialValue) {
            return new DialogField(id, label, FieldType.CHECKBOX, Boolean.toString(initialValue), List.of());
        }
    }

    public record DialogAction(MutableComponent label, Consumer<TextField> action) {
    }

    public enum FieldType {
        TEXT, COLOR, CHECKBOX
    }
}
