package com.github.rinorsi.cadeditor.client.screen.controller.entry;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.screen.model.entry.SelectionEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.ListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.view.entry.SelectionEntryView;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.List;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;


public class SelectionEntryController<M extends SelectionEntryModel, V extends SelectionEntryView> extends StringEntryController<M, V> {
    public SelectionEntryController(M model, V view) {
        super(model, view);
    }

    @Override 
    public void bind() {
        super.bind();
        ((SelectionEntryView) this.view).getTextField().setPlaceholder(this.model.getSelectionScreenTitle());
        ((SelectionEntryView) this.view).getTextField().getSuggestions().setAll(this.model.getSuggestions());
        ((SelectionEntryView) this.view).getSelectionScreenButton().getTooltip().add(ModTexts.choose(this.model.getSelectionScreenTitle()));
        ((SelectionEntryView) this.view).getSelectionScreenButton().onAction(this::openSelectionScreen);
        if (this.model.getLabel() == null || this.model.getLabel().getString().isEmpty()) {
            ((SelectionEntryView) this.view).getRoot().getChildren().remove(((SelectionEntryView) this.view).getLabel());
        }
    }

    protected void openSelectionScreen() {
        MutableComponent selectionScreenTitle = this.model.getSelectionScreenTitle();
        String str = ((String) this.model.getValue()).contains(":") ? (String) this.model.getValue() : "minecraft:" + ((String) this.model.getValue());
        List<? extends ListSelectionElementModel> selectionItems = this.model.getSelectionItems();
        ModScreenHandler.openListSelectionScreen(selectionScreenTitle, str, selectionItems, this.model::setValue);
    }

    protected Identifier parseResourceLocation(String value) {
        return ClientUtil.parseResourceLocation(value);
    }
}
