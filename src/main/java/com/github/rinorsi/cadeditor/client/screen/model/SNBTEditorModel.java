package com.github.rinorsi.cadeditor.client.screen.model;

import com.github.franckyi.databindings.api.ObjectProperty;
import com.github.franckyi.databindings.api.ObservableBooleanValue;
import com.github.franckyi.databindings.api.StringProperty;
import com.github.rinorsi.cadeditor.client.context.EditorContext;
import com.github.rinorsi.cadeditor.client.util.SnbtHelper;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.SnbtPrinterTagVisitor;


public class SNBTEditorModel implements EditorModel {
    private final EditorContext<?> context;
    private final StringProperty valueProperty;
    private final ObservableBooleanValue validProperty;
    private final ObjectProperty<SNBTPreviewNode> previewRootProperty;
    private String pendingText;

    public SNBTEditorModel(EditorContext<?> context) {
        String initialValue;
        this.context = context;
        try {
            SnbtPrinterTagVisitor formatter = new SnbtPrinterTagVisitor("  ", 0, new ArrayList<>());
            initialValue = formatter.visit(context.getTag());
        } catch (Exception e) {
            initialValue = context.getTag().toString();
        }
        this.pendingText = initialValue;
        this.valueProperty = StringProperty.create(initialValue);
        this.validProperty = this.valueProperty.mapToBoolean(value -> {
            try {
                return SnbtHelper.parse(value) != null;
            } catch (CommandSyntaxException e) {
                return false;
            }
        });
        this.previewRootProperty = ObjectProperty.create(SNBTPreviewNode.fromTag(context.getTag()));
        this.valueProperty.addListener(newValue -> {
            refreshPreview();
        });
    }

    public String getValue() {
        return this.pendingText != null ? this.pendingText : valueProperty().getValue();
    }

    public StringProperty valueProperty() {
        return this.valueProperty;
    }

    public void setValue(String value) {
        this.pendingText = value;
        valueProperty().setValue(value);
    }

    public void setPendingText(String text) {
        this.pendingText = text;
    }

    public ObjectProperty<SNBTPreviewNode> previewRootProperty() {
        return this.previewRootProperty;
    }

    private void refreshPreview() {
        try {
            previewRootProperty().setValue(SNBTPreviewNode.fromTag(SnbtHelper.parse(getValue())));
        } catch (CommandSyntaxException e) {
            previewRootProperty().setValue(null);
        }
    }

    
    @Override 
    public void apply() {
        try {
            this.context.setTag(SnbtHelper.parse(getValue()));
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override 
    public EditorContext<?> getContext() {
        return this.context;
    }

    @Override 
    public ObservableBooleanValue validProperty() {
        return this.validProperty;
    }
}
