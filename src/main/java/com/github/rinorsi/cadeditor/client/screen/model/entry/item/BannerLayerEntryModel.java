package com.github.rinorsi.cadeditor.client.screen.model.entry.item;

import com.github.rinorsi.cadeditor.client.screen.model.category.CategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemBannerPatternCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.StringWithActionsEntryModel;
import java.util.function.Predicate;
import net.minecraft.network.chat.MutableComponent;

public class BannerLayerEntryModel extends StringWithActionsEntryModel {
    public BannerLayerEntryModel(CategoryModel category, MutableComponent label, String spec) {
        super(category, label, spec, value -> {
        });
    }

    @Override
    public Type getType() {
        return Type.BANNER_LAYER;
    }

    public Predicate<String> getValidator() {
        return spec -> {
            String s = spec == null ? "" : spec.trim();
            return s.isEmpty() || ItemBannerPatternCategoryModel.isValidLayerSpec(s);
        };
    }
}
