package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.PotDecorationEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.block.entity.PotDecorations;

import java.util.Objects;
import java.util.Optional;

public class ItemPotDecorationsCategoryModel extends ItemEditorCategoryModel {

    private PotDecorationEntryModel backEntry;
    private PotDecorationEntryModel leftEntry;
    private PotDecorationEntryModel rightEntry;
    private PotDecorationEntryModel frontEntry;

    public ItemPotDecorationsCategoryModel(ItemEditorModel editor) {
        super(ModTexts.POT_DECORATIONS, editor);
    }

    @Override
    protected void setupEntries() {
        ItemStack stack = getParent().getContext().getItemStack();
        PotDecorations decorations = stack.get(DataComponents.POT_DECORATIONS);
        if (decorations == null) {
            decorations = PotDecorations.EMPTY;
        }
        backEntry = createEntry(ModTexts.POT_BACK, decorations.back());
        leftEntry = createEntry(ModTexts.POT_LEFT, decorations.left());
        rightEntry = createEntry(ModTexts.POT_RIGHT, decorations.right());
        frontEntry = createEntry(ModTexts.POT_FRONT, decorations.front());

        getEntries().add(backEntry);
        getEntries().add(leftEntry);
        getEntries().add(rightEntry);
        getEntries().add(frontEntry);
    }

    private PotDecorationEntryModel createEntry(MutableComponent label, Optional<ItemStackTemplate> optionalItem) {
        ItemStack stack = optionalItem.map(ItemStackTemplate::create).orElse(ItemStack.EMPTY);
        return new PotDecorationEntryModel(this, label, stack);
    }

    @Override
    public void apply() {
        super.apply();

        Optional<ItemStackTemplate> back = toTemplate(backEntry);
        Optional<ItemStackTemplate> left = toTemplate(leftEntry);
        Optional<ItemStackTemplate> right = toTemplate(rightEntry);
        Optional<ItemStackTemplate> front = toTemplate(frontEntry);

        PotDecorations decorations = new PotDecorations(back, left, right, front);
        ItemStack stack = getParent().getContext().getItemStack();
        if (!Objects.equals(stack.get(DataComponents.POT_DECORATIONS), decorations)) {
            if (decorations.equals(PotDecorations.EMPTY)) {
                stack.remove(DataComponents.POT_DECORATIONS);
            } else {
                stack.set(DataComponents.POT_DECORATIONS, decorations);
            }
        }
        cleanComponentTag();
    }

    private Optional<ItemStackTemplate> toTemplate(PotDecorationEntryModel entry) {
        ItemStack stack = entry.getItemStack();
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(ItemStackTemplate.fromNonEmptyStack(stack));
    }

    private void cleanComponentTag() {
        CompoundTag data = getData();
        if (data == null) return;
        CompoundTag components = data.getCompound("components").orElse(null);
        if (components == null) return;
        components.remove("minecraft:pot_decorations");
        if (components.isEmpty()) {
            data.remove("components");
        }
    }
}
