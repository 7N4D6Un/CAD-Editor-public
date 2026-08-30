package com.github.rinorsi.cadeditor.client.screen.model.entry.entity;

import com.github.rinorsi.cadeditor.client.screen.model.category.CategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.SelectionEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.ListSelectionElementModel;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

public class SoundVariantEntryModel extends SelectionEntryModel {
    private final List<String> variantIds;
    private final MutableComponent selectionTitle;
    private final List<ListSelectionElementModel> selectionItems;

    public SoundVariantEntryModel(CategoryModel category, MutableComponent label, List<String> variantIds,
                                  String value, Consumer<String> action) {
        super(category, label, value, action);
        this.variantIds = variantIds;
        this.selectionTitle = label;
        this.selectionItems = variantIds.stream()
                .map(id -> new ListSelectionElementModel(id, Identifier.tryParse(id)))
                .filter(element -> element.getId() != null)
                .sorted()
                .toList();
    }

    @Override
    public List<String> getSuggestions() {
        return variantIds;
    }

    @Override
    public MutableComponent getSelectionScreenTitle() {
        return selectionTitle;
    }

    @Override
    public List<? extends ListSelectionElementModel> getSelectionItems() {
        return selectionItems;
    }
}