package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.UseRemainderEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.core.component.DataComponents;

public class ItemUseRemainderCategoryModel extends ItemEditorCategoryModel {
    private boolean enableUseRemainder;
    private BooleanEntryModel useRemainderToggle;

    public ItemUseRemainderCategoryModel(ItemEditorModel editor) {
        super(ModTexts.gui("using_converts_to"), editor);
    }

    @Override
    protected void setupEntries() {
        this.enableUseRemainder = getParent().getContext().getItemStack().has(DataComponents.USE_REMAINDER);
        this.useRemainderToggle = new BooleanEntryModel(this, ModTexts.gui("using_converts_to_enabled"), this.enableUseRemainder, value -> {
            this.enableUseRemainder = value != null && value;
            syncOtherEntriesEnabled();
        });
        getEntries().add(this.useRemainderToggle);
        getEntries().add(UseRemainderEntryModel.create(this, getParent().getUseRemainderState()));
        syncOtherEntriesEnabled();
    }

    private void syncOtherEntriesEnabled() {
        for (EntryModel entry : getEntries()) {
            if (entry != this.useRemainderToggle) {
                entry.setEnabled(this.enableUseRemainder);
            }
        }
    }

    @Override
    public void apply() {
        super.apply();
        getParent().setUseRemainderEnabled(this.enableUseRemainder);
    }
}