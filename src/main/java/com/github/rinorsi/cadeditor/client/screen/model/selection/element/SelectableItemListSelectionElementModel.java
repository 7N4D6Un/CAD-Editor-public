package com.github.rinorsi.cadeditor.client.screen.model.selection.element;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class SelectableItemListSelectionElementModel extends ItemListSelectionElementModel {

    public SelectableItemListSelectionElementModel(String name, Identifier id, ItemStack itemStack) {
        super(name, id, itemStack);
    }

    public SelectableItemListSelectionElementModel(String name, Identifier id, Supplier<ItemStack> itemSupplier) {
        super(name, id, itemSupplier);
    }
}
