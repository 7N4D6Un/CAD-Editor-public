package com.github.rinorsi.cadeditor.client.screen.controller.entry;

import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.screen.model.entry.FilteredSelectionEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.ListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.view.entry.SelectionEntryView;
import java.util.List;
import net.minecraft.network.chat.MutableComponent;


public class FilteredSelectionEntryController<M extends FilteredSelectionEntryModel> extends SelectionEntryController<M, SelectionEntryView> {
    public FilteredSelectionEntryController(M model, SelectionEntryView view) {
        super(model, view);
    }

    @Override 
    protected void openSelectionScreen() {
        String value = (String) this.model.getValue();
        String namespacedValue = value.contains(":") ? value : "minecraft:" + value;
        MutableComponent selectionScreenTitle = this.model.getSelectionScreenTitle();
        List<? extends ListSelectionElementModel> selectionItems = this.model.getSelectionItems();
        ModScreenHandler.openListSelectionScreen(selectionScreenTitle, namespacedValue, selectionItems, this.model::setValue, this.model.getFilters(), this.model.getInitialFilterId());
    }
}
