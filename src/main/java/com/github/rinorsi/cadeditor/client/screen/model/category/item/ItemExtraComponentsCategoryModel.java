package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.InfoEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.ChatFormatting;

public class ItemExtraComponentsCategoryModel extends ItemEditorCategoryModel {
    private final Map<ItemExtraToggle, BooleanEntryModel> toggleEntries = new LinkedHashMap<>();

    public ItemExtraComponentsCategoryModel(ItemEditorModel editor) {
        super(ModTexts.gui("extra_components").copy().withStyle(ChatFormatting.GREEN), editor);
    }

    @Override
    protected void setupEntries() {
        getEntries().add(new InfoEntryModel(this, ModTexts.gui("extra_components_tooltip_info").copy().withStyle(ChatFormatting.GRAY)));
        toggleEntries.clear();
        for (ItemExtraToggle toggle : ItemExtraToggle.values()) {
            boolean enabled = getParent().isExtraCategoryVisible(toggle);
            BooleanEntryModel entry = new BooleanEntryModel(this, toggle.coloredLabel(), enabled,
                    value -> getParent().setExtraCategoryVisible(toggle, value != null && value));
            toggleEntries.put(toggle, entry);
            getEntries().add(entry);
        }
    }

    public void setToggleValue(ItemExtraToggle key, boolean enabled) {
        BooleanEntryModel entry = toggleEntries.get(key);
        if (entry != null && entry.getValue() != enabled) {
            entry.setValue(enabled);
        }
    }

    public BooleanEntryModel getEntry(ItemExtraToggle key) {
        return toggleEntries.get(key);
    }

    @Override
    public void apply() {
    }
}
