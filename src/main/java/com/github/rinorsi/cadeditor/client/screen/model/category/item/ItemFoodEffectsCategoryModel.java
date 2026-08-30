package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.FloatEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.IntegerEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.MutableComponent;

public class ItemFoodEffectsCategoryModel extends ItemEditorCategoryModel {
    private boolean enableFoodBehavior;
    private BooleanEntryModel foodBehaviorToggle;

    public ItemFoodEffectsCategoryModel(ItemEditorModel editor) {
        super(ModTexts.gui("food_effects"), editor);
    }

    @Override
    protected void setupEntries() {
        FoodComponentState food = getParent().getFoodState();
        this.enableFoodBehavior = getParent().getContext().getItemStack().has(DataComponents.FOOD);
        this.foodBehaviorToggle = new BooleanEntryModel(this, ModTexts.gui("food_behaviour_enabled"), this.enableFoodBehavior, value -> {
            this.enableFoodBehavior = value != null && value;
            syncOtherEntriesEnabled();
        });
        MutableComponent[] hint = ModTexts.wikiTooltip("food_behaviour_enabled", 1);
        for (int i = 0; i < hint.length; i++) {
            hint[i] = hint[i].copy().withStyle(ChatFormatting.AQUA);
        }
        this.foodBehaviorToggle.setLabelTooltip(hint);
        getEntries().add(this.foodBehaviorToggle);
        getEntries().add(new BooleanEntryModel(this, ModTexts.gui("always_eat"), food.isAlwaysEat(), food::setAlwaysEat));
        getEntries().add(new IntegerEntryModel(this, ModTexts.gui("nutrition"), food.getNutrition(), food::setNutrition));
        getEntries().add(new FloatEntryModel(this, ModTexts.gui("saturation"), food.getSaturation(), food::setSaturation));
        syncOtherEntriesEnabled();
    }

    private void syncOtherEntriesEnabled() {
        for (EntryModel entry : getEntries()) {
            if (entry != this.foodBehaviorToggle) {
                entry.setEnabled(this.enableFoodBehavior);
            }
        }
    }

    @Override
    public void apply() {
        super.apply();
        getParent().setFoodBehaviorEnabled(this.enableFoodBehavior);
    }
}