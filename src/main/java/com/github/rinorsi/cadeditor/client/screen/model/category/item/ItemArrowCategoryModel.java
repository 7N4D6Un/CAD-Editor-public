package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.context.ItemEditorContext;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;

public class ItemArrowCategoryModel extends ItemEditorCategoryModel {
    private BooleanEntryModel intangibleToggleEntry;

    public ItemArrowCategoryModel(ItemEditorModel editor) {
        super(ModTexts.gui("arrow"), editor);
    }

    @Override
    protected void setupEntries() {
        ItemStack stack = getParent().getContext().getItemStack();
        this.intangibleToggleEntry = new BooleanEntryModel(this, ModTexts.INTANGIBLE_PROJECTILE, stack.has(DataComponents.INTANGIBLE_PROJECTILE), value -> setIntangibleProjectile(value));
        getEntries().add(this.intangibleToggleEntry);
    }

    private void setIntangibleProjectile(boolean value) {
        ItemEditorModel parent = getParent();
        ItemStack stack = parent.getContext().getItemStack();
        if (value) {
            stack.set(DataComponents.INTANGIBLE_PROJECTILE, Unit.INSTANCE);
        } else {
            stack.remove(DataComponents.INTANGIBLE_PROJECTILE);
        }
        syncIntangibleProjectileComponent(parent, value);
    }

    private void syncIntangibleProjectileComponent(ItemEditorModel parent, boolean enabled) {
        ItemEditorContext context = parent.getContext();
        CompoundTag root = context.getTag();
        if (root == null) {
            return;
        }
        CompoundTag compoundTag = root.getCompound("components").orElse(null);
        if (enabled) {
            if (compoundTag == null) {
                compoundTag = new CompoundTag();
                root.put("components", compoundTag);
            }
            compoundTag.put("minecraft:intangible_projectile", new CompoundTag());
            compoundTag.remove("!minecraft:intangible_projectile");
        } else if (compoundTag != null) {
            compoundTag.remove("minecraft:intangible_projectile");
            compoundTag.remove("!minecraft:intangible_projectile");
            if (compoundTag.isEmpty()) {
                root.remove("components");
            }
        }
        context.setTag(root);
    }
}