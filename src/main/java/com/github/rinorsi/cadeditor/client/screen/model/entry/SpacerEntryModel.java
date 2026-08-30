package com.github.rinorsi.cadeditor.client.screen.model.entry;

import com.github.rinorsi.cadeditor.client.screen.model.category.CategoryModel;

public class SpacerEntryModel extends EntryModel {
    public SpacerEntryModel(CategoryModel category) {
        super(category);
        setReorderable(false);
    }

    @Override
    public void apply() {
    }

    @Override
    public boolean isDeletable() {
        return false;
    }

    @Override
    public boolean isResetable() {
        return false;
    }

    @Override
    public Type getType() {
        return Type.SPACER;
    }
}