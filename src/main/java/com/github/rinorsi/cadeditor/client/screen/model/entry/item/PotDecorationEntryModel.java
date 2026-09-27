package com.github.rinorsi.cadeditor.client.screen.model.entry.item;

import com.github.rinorsi.cadeditor.client.screen.model.category.EditorCategoryModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;

public class PotDecorationEntryModel extends ItemContainerSlotEntryModel {
    private final MutableComponent label;

    public PotDecorationEntryModel(EditorCategoryModel category, MutableComponent label, ItemStack stack) {
        super(category, stack);
        this.label = label;
    }

    @Override
    public Component getSlotLabel() {
        return label;
    }
}
