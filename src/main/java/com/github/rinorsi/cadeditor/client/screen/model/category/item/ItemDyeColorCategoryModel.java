package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EnumEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.Arrays;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class ItemDyeColorCategoryModel extends ItemEditorCategoryModel {
    private EnumEntryModel<DyeColor> dyeColorEntry;

    public ItemDyeColorCategoryModel(ItemEditorModel editor) {
        super(ModTexts.gui("dye_category"), editor);
    }

    @Override
    protected void setupEntries() {
        ItemStack stack = getStack();
        DyeColor dye = stack.get(DataComponents.DYE);
        this.dyeColorEntry = new EnumEntryModel<>(this, ModTexts.gui("dye_color"), Arrays.asList(DyeColor.values()),
                dye == null ? DyeColor.WHITE : dye, this::setDyeColor).withTextFactory(color -> Component.literal(color.getName()));
        getEntries().add(this.dyeColorEntry);
    }

    private void setDyeColor(DyeColor color) {
        getStack().set(DataComponents.DYE, color == null ? DyeColor.WHITE : color);
    }

    private ItemStack getStack() {
        return getParent().getContext().getItemStack();
    }

    @Override
    public void apply() {
        this.dyeColorEntry.apply();
    }
}
