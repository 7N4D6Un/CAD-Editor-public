package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.debug.DebugLog;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;


public class FoodComponentState {
    private boolean enabled;
    private boolean alwaysEat;
    private int nutrition = 0;
    private float saturation = 0.0f;

    public void loadFrom(ItemStack stack) {
        FoodProperties food = (FoodProperties) stack.get(DataComponents.FOOD);
        if (food == null) {
            resetToDefaults();
            setEnabled(false);
            return;
        }
        setEnabled(true);
        this.nutrition = food.nutrition();
        this.saturation = food.saturation();
        this.alwaysEat = food.canAlwaysEat();
        DebugLog.infoKey("cadeditor.debug.food.loaded", describeStack(stack), this.nutrition, this.saturation);
    }

    private void resetToDefaults() {
        this.nutrition = 0;
        this.saturation = 0.0f;
        this.alwaysEat = false;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getNutrition() {
        return this.nutrition;
    }

    public void setNutrition(int nutrition) {
        this.nutrition = Math.max(0, nutrition);
    }

    public float getSaturation() {
        return this.saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = Math.max(0.0f, saturation);
    }

    public boolean isAlwaysEat() {
        return this.alwaysEat;
    }

    public void setAlwaysEat(boolean alwaysEat) {
        this.alwaysEat = alwaysEat;
    }

    public FoodProperties buildFoodProperties() {
        return new FoodProperties(this.nutrition, this.saturation, this.alwaysEat);
    }

    private String describeStack(ItemStack stack) {
        return stack.isEmpty() ? "<empty>" : stack.toString();
    }
}
