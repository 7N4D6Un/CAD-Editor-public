package com.github.rinorsi.cadeditor.client.screen.model.entry.item;

import com.github.rinorsi.cadeditor.client.screen.model.category.CategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.SelectionEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.ListSelectionElementModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.SelectableItemListSelectionElementModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DyeColorSelectionEntryModel extends SelectionEntryModel {
    private static final List<String> VALUES = List.of(java.util.Arrays.stream(DyeColor.values()).map(DyeColor::getName).toArray(String[]::new));

    public DyeColorSelectionEntryModel(CategoryModel category, MutableComponent label, String value, Consumer<String> action) {
        super(category, label, normalize(value), action);
    }

    private static String normalize(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        String s = (value.contains(":") ? value.substring(value.indexOf(58) + 1) : value).toLowerCase();
        return VALUES.contains(s) ? s : s.trim();
    }

    @Override
    public void setValue(String value) {
        super.setValue(normalize(value));
    }

    @Override
    public List<String> getSuggestions() {
        return VALUES;
    }

    @Override
    public MutableComponent getSelectionScreenTitle() {
        return ModTexts.gui("select_base_color");
    }

    @Override
    public List<? extends ListSelectionElementModel> getSelectionItems() {
        return getDyeColorSelectionItems();
    }

    public static List<ListSelectionElementModel> getDyeColorSelectionItems() {
        List<ListSelectionElementModel> list = new ArrayList<>();
        for (String color : VALUES) {
            Item bannerItem = BuiltInRegistries.ITEM.getValue(Identifier.withDefaultNamespace(color + "_banner"));
            list.add(new SelectableItemListSelectionElementModel("block.minecraft." + color + "_banner",
                    Identifier.withDefaultNamespace(color),
                    () -> bannerItem == null ? ItemStack.EMPTY : new ItemStack(bannerItem)));
        }
        return list;
    }
}
