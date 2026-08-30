package com.github.rinorsi.cadeditor.client.screen.controller.selection.element;

import com.github.franckyi.guapi.api.GuapiHelper;
import com.github.franckyi.guapi.api.mvc.AbstractController;
import com.github.franckyi.guapi.api.node.CheckBox;
import com.github.rinorsi.cadeditor.client.debug.DebugLog;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.ListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.SelectableListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.view.selection.element.ListSelectionElementView;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;


public class ListSelectionElementController<M extends ListSelectionElementModel, V extends ListSelectionElementView> extends AbstractController<M, V> {
    private Component fullDisplayName;

    public ListSelectionElementController(M model, V view) {
        super(model, view);
    }

    @Override 
    public void bind() {
        this.fullDisplayName = this.model.getDisplayName();
        ((ListSelectionElementView) this.view).getNameLabel().getTooltip().setAll(this.fullDisplayName.copy());
        ((ListSelectionElementView) this.view).getRoot().widthProperty().addListener(newValue -> {
            updateDisplayName();
        });
        updateDisplayName();
        ((ListSelectionElementView) this.view).getIdLabel().setLabel(GuapiHelper.text(this.model.getId().toString()).withStyle(ChatFormatting.ITALIC));
        M m = this.model;
        if (m.isMultiSelect() && m instanceof SelectableListSelectionElementModel) {
            SelectableListSelectionElementModel selectable = (SelectableListSelectionElementModel) m;
            ((ListSelectionElementView) this.view).enableSelection();
            CheckBox checkBox = ((ListSelectionElementView) this.view).getSelectionCheckBox();
            checkBox.checkedProperty().bindBidirectional(selectable.selectedProperty());
            checkBox.onAction(() -> {
                DebugLog.ui("list_selection_toggle", () -> {
                    return "entry=" + String.valueOf(this.model.getId()) + " state=" + selectable.isSelected();
                });
            });
            updateDisplayName();
        }
    }

    private void updateDisplayName() {
        ((ListSelectionElementView) this.view).getNameLabel().setLabel(truncateDisplayName(this.fullDisplayName));
    }

    private Component truncateDisplayName(Component component) {
        String trimmed;
        if (component == null) {
            return Component.empty();
        }
        int maxWidth = computeAvailableWidth();
        if (maxWidth <= 0) {
            return Component.empty();
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.font == null) {
            return component.copy();
        }
        Font font = minecraft.font;
        if (font.width(component) <= maxWidth) {
            return component.copy();
        }
        int ellipsisWidth = font.width("...");
        int availableWidth = Math.max(0, maxWidth - ellipsisWidth);
        String strPlainSubstrByWidth = font.plainSubstrByWidth(component.getString(), availableWidth);
        while (true) {
            trimmed = strPlainSubstrByWidth;
            if (trimmed.isEmpty() || font.width(trimmed) <= availableWidth) {
                break;
            }
            strPlainSubstrByWidth = trimmed.substring(0, trimmed.length() - 1);
        }
        if (trimmed.isEmpty()) {
            return Component.literal("...").withStyle(component.getStyle());
        }
        return Component.literal(trimmed).withStyle(component.getStyle()).append(Component.literal("...").withStyle(component.getStyle()));
    }

    private int computeAvailableWidth() {
        int width = ((ListSelectionElementView) this.view).getRoot().getWidth() - ((ListSelectionElementView) this.view).getRoot().getPadding().getHorizontal();
        if (((ListSelectionElementView) this.view).getSelectionCheckBox() != null) {
            width -= ((ListSelectionElementView) this.view).getSelectionCheckBox().getWidth() + ((ListSelectionElementView) this.view).getRoot().getSpacing();
        }
        return Math.max(width, 0);
    }
}
