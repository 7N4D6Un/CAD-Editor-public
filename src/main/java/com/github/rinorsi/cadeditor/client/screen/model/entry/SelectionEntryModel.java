package com.github.rinorsi.cadeditor.client.screen.model.entry;

import com.github.rinorsi.cadeditor.client.screen.model.category.CategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.ListSelectionElementModel;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class SelectionEntryModel extends StringEntryModel {
    private Predicate<String> validator;

    public SelectionEntryModel(CategoryModel category, MutableComponent label, String value, Consumer<String> action) {
        super(category, label, value, action);
    }

    @Override
    public Type getType() {
        return Type.SELECTION;
    }

    public Predicate<String> getValidator() {
        return validator;
    }

    public void setValidator(Predicate<String> validator) {
        this.validator = validator;
    }

    public abstract List<String> getSuggestions();

    public abstract MutableComponent getSelectionScreenTitle();

    public abstract List<? extends ListSelectionElementModel> getSelectionItems();
}
