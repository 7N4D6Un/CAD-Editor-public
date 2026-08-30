package com.github.rinorsi.cadeditor.client.screen.model.entry.item;

import com.github.rinorsi.cadeditor.client.ClientCache;
import com.github.rinorsi.cadeditor.client.screen.model.category.CategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.SelectionEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.ListSelectionElementModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;
import java.util.function.Consumer;

public class BannerPatternSelectionEntryModel extends SelectionEntryModel {
    public BannerPatternSelectionEntryModel(CategoryModel category, String value, Consumer<String> action) {
        super(category, ModTexts.gui("banner_pattern"), value, action);
    }

    @Override
    public List<String> getSuggestions() {
        return ClientCache.getBannerPatternSuggestions();
    }

    @Override
    public MutableComponent getSelectionScreenTitle() {
        return ModTexts.gui("banner_pattern");
    }

    @Override
    public List<? extends ListSelectionElementModel> getSelectionItems() {
        return ClientCache.getBannerPatternSelectionItems();
    }
}