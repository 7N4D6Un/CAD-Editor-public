package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.StringEntryModel;
import com.github.rinorsi.cadeditor.client.util.SnbtHelper;
import com.github.rinorsi.cadeditor.common.ModTexts;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class ItemCustomDataCategoryModel extends ItemEditorCategoryModel {
    private StringEntryModel snbtEntry;

    public ItemCustomDataCategoryModel(ItemEditorModel editor) {
        super(ModTexts.gui("custom_data"), editor);
    }

    @Override
    protected void setupEntries() {
        ItemStack stack = getParent().getContext().getItemStack();
        CustomData existing = (CustomData) stack.get(DataComponents.CUSTOM_DATA);
        String value = existing != null ? existing.copyTag().toString() : "";
        this.snbtEntry = new StringEntryModel(this, ModTexts.gui("custom_data"), value, v -> {
        });
        getEntries().add(this.snbtEntry);
    }

    @Override
    public int getEntryListStart() {
        return -1;
    }

    @Override
    public void apply() {
        super.apply();
        String raw = this.snbtEntry.getValue() == null ? "" : this.snbtEntry.getValue().trim();
        ItemStack stack = getParent().getContext().getItemStack();
        if (raw.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
            this.snbtEntry.setValid(true);
            return;
        }
        try {
            CompoundTag tag = SnbtHelper.parse(raw);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            this.snbtEntry.setValid(true);
        } catch (Exception e) {
            this.snbtEntry.setValid(false);
        }
    }
}
