package com.github.rinorsi.cadeditor.client.screen.model.entry;

import com.github.rinorsi.cadeditor.client.screen.model.category.CategoryModel;
import net.minecraft.network.chat.MutableComponent;

public class ReadOnlyStringEntryModel extends StringEntryModel {
    public ReadOnlyStringEntryModel(CategoryModel category, MutableComponent label, String value) {
        super(category, label, value, v -> {});
    }

    @Override
    public boolean isResetable() {
        return false;
    }
}
