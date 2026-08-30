package com.github.rinorsi.cadeditor.client.screen.model.entry.entity;

import com.github.rinorsi.cadeditor.client.screen.model.category.CategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.SelectionEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.ListSelectionElementModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.List;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

public class VariantEntryModel extends SelectionEntryModel {
    private final List<String> variantIds;
    private final Consumer<String> action;

    public VariantEntryModel(CategoryModel category, List<String> variantIds, String value, Consumer<String> action) {
        super(category, ModTexts.VARIANT, value, action);
        this.variantIds = variantIds;
        this.action = action;
    }

    @Override
    public List<String> getSuggestions() {
        return variantIds;
    }

    @Override
    public MutableComponent getSelectionScreenTitle() {
        return ModTexts.VARIANT;
    }

    @Override
    public List<? extends ListSelectionElementModel> getSelectionItems() {
        return variantIds.stream()
                .map(id -> new ListSelectionElementModel(id, Identifier.tryParse(id)))
                .filter(element -> element.getId() != null)
                .sorted()
                .toList();
    }

    @Override
    public Type getType() {
        return Type.SELECTION;
    }
}
