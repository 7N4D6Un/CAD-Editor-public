package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.StringEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public class ItemRecipesCategoryModel extends ItemEditorCategoryModel {
    private StringEntryModel recipesEntry;
    private String initialRecipes;

    public ItemRecipesCategoryModel(ItemEditorModel editor) {
        super(ModTexts.gui("knowledge_book"), editor);
    }

    @Override
    protected void setupEntries() {
        this.initialRecipes = readRecipes();
        this.recipesEntry = new StringEntryModel(this, ModTexts.gui("recipes"), this.initialRecipes, this::setRecipes);
        this.recipesEntry.setPlaceholder("minecraft:example_recipe");
        getEntries().add(this.recipesEntry);
    }

    @Override
    public void apply() {
        super.apply();
        ItemStack stack = getParent().getContext().getItemStack();
        if (stack == null) {
            return;
        }
        List<ResourceKey<Recipe<?>>> keys = parseRecipeIds(this.initialRecipes);
        if (keys.isEmpty()) {
            stack.remove(DataComponents.RECIPES);
        } else {
            stack.set(DataComponents.RECIPES, keys);
        }
    }

    private String readRecipes() {
        ItemStack stack = getParent().getContext().getItemStack();
        if (stack == null) {
            return "";
        }
        List<ResourceKey<Recipe<?>>> keys = stack.getOrDefault(DataComponents.RECIPES, List.of());
        List<String> ids = new ArrayList<>(keys.size());
        for (ResourceKey<Recipe<?>> key : keys) {
            ids.add(key.identifier().toString());
        }
        return String.join(", ", ids);
    }

    private void setRecipes(String value) {
        this.initialRecipes = value == null ? "" : value.trim();
    }

    private static List<ResourceKey<Recipe<?>>> parseRecipeIds(String raw) {
        List<ResourceKey<Recipe<?>>> list = new ArrayList<>();
        if (raw == null || raw.isBlank()) {
            return list;
        }
        for (String part : raw.split(",")) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            Identifier id = Identifier.tryParse(trimmed);
            if (id != null) {
                list.add(ResourceKey.create(Registries.RECIPE, id));
            }
        }
        return list;
    }
}