package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.FloatEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.IntegerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.SpacerEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.food.VillagerFood;
import net.minecraft.world.item.ItemStack;

public class ItemFoodEffectsCategoryModel extends ItemEditorCategoryModel {
    private boolean enableFoodBehavior;
    private BooleanEntryModel foodBehaviorToggle;
    private boolean villagerFoodEnabled;
    private BooleanEntryModel villagerFoodToggle;
    private IntegerEntryModel villagerFoodNutritionEntry;

    public ItemFoodEffectsCategoryModel(ItemEditorModel editor) {
        super(ModTexts.gui("food_effects"), editor);
    }

    @Override
    protected void setupEntries() {
        FoodComponentState food = getParent().getFoodState();
        ItemStack stack = getParent().getContext().getItemStack();
        this.enableFoodBehavior = stack.has(DataComponents.FOOD);
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
        getEntries().add(new SpacerEntryModel(this));
        VillagerFood villagerFood = stack.get(DataComponents.VILLAGER_FOOD);
        this.villagerFoodEnabled = villagerFood != null;
        this.villagerFoodToggle = new BooleanEntryModel(this, ModTexts.gui("villager_food_enabled"), this.villagerFoodEnabled, value -> {
            this.villagerFoodEnabled = value != null && value;
            syncVillagerFoodEntryEnabled();
            applyVillagerFood();
        });
        this.villagerFoodNutritionEntry = new IntegerEntryModel(this, ModTexts.gui("villager_food_nutrition"),
                villagerFood == null ? 1 : villagerFood.nutrition(), value -> applyVillagerFood(), value -> value != null && value >= 1);
        getEntries().add(this.villagerFoodToggle);
        getEntries().add(this.villagerFoodNutritionEntry);
        syncOtherEntriesEnabled();
        syncVillagerFoodEntryEnabled();
    }

    private void syncOtherEntriesEnabled() {
        for (EntryModel entry : getEntries()) {
            if (entry == this.foodBehaviorToggle || entry == this.villagerFoodToggle
                    || entry == this.villagerFoodNutritionEntry || entry instanceof SpacerEntryModel) {
                continue;
            }
            entry.setEnabled(this.enableFoodBehavior);
        }
    }

    private void syncVillagerFoodEntryEnabled() {
        if (this.villagerFoodNutritionEntry != null) {
            this.villagerFoodNutritionEntry.setEnabled(this.villagerFoodEnabled);
        }
    }

    private void applyVillagerFood() {
        ItemStack stack = getParent().getContext().getItemStack();
        if (stack == null) {
            return;
        }
        if (!this.villagerFoodEnabled) {
            stack.remove(DataComponents.VILLAGER_FOOD);
            return;
        }
        Integer value = this.villagerFoodNutritionEntry == null ? null : this.villagerFoodNutritionEntry.getValue();
        stack.set(DataComponents.VILLAGER_FOOD, new VillagerFood(value == null ? 1 : Math.max(1, value)));
    }

    @Override
    public void apply() {
        super.apply();
        getParent().setFoodBehaviorEnabled(this.enableFoodBehavior);
    }
}