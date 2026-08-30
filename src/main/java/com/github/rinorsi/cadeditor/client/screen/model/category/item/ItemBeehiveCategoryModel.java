package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.BeehiveNbtHelper;
import com.github.rinorsi.cadeditor.client.screen.model.entry.IntegerEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Bees;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.BeehiveBlock;

public class ItemBeehiveCategoryModel extends ItemEditorCategoryModel {
    private static final String BEES_COMPONENT = "minecraft:bees";

    private IntegerEntryModel beeCountEntry;
    private IntegerEntryModel honeyLevelEntry;

    public ItemBeehiveCategoryModel(ItemEditorModel editor) {
        super(ModTexts.gui("beehive"), editor);
    }

    @Override
    protected void setupEntries() {
        int beeCount = readBeesRaw().size();
        ItemStack stack = getStack();
        BlockItemStateProperties props = stack == null ? null : stack.get(DataComponents.BLOCK_STATE);
        int honey = 0;
        if (props != null) {
            Integer value = props.get(BeehiveBlock.HONEY_LEVEL);
            honey = value == null ? 0 : value;
        }
        this.beeCountEntry = new IntegerEntryModel(this, ModTexts.gui("bee_count"), Math.max(0, beeCount), value -> {
        }, BeehiveNbtHelper.INT_COUNT);
        this.honeyLevelEntry = new IntegerEntryModel(this, ModTexts.gui("honey_level"), honey, value -> {
        }, BeehiveNbtHelper.INT_HONEY);
        getEntries().add(this.beeCountEntry);
        getEntries().add(this.honeyLevelEntry);
    }

    @Override
    public void apply() {
        super.apply();
        ItemStack stack = getStack();
        if (stack == null) {
            return;
        }
        int targetBees = Math.max(0, this.beeCountEntry.getValue());
        setBeesOnItem(stack, BeehiveNbtHelper.resizeBees(readBeesRaw(), targetBees));
        int honey = Math.max(0, Math.min(5, this.honeyLevelEntry.getValue()));
        BlockItemStateProperties props = stack.get(DataComponents.BLOCK_STATE);
        BlockItemStateProperties base = props == null ? BlockItemStateProperties.EMPTY : props;
        stack.set(DataComponents.BLOCK_STATE, base.with(BeehiveBlock.HONEY_LEVEL, honey));
    }

    private ItemStack getStack() {
        return getParent().getContext().getItemStack();
    }

    private ListTag readBeesRaw() {
        CompoundTag data = getData();
        if (data != null) {
            CompoundTag components = data.getCompound("components").orElse(null);
            if (components != null) {
                ListTag bees = components.getListOrEmpty(BEES_COMPONENT);
                if (!bees.isEmpty() || components.contains(BEES_COMPONENT)) {
                    return bees;
                }
            }
        }
        return new ListTag();
    }

    private void setBeesOnItem(ItemStack stack, ListTag bees) {
        CompoundTag data = getData();
        if (data != null) {
            CompoundTag components = data.getCompound("components").map(CompoundTag::copy).orElseGet(CompoundTag::new);
            if (bees.isEmpty()) {
                components.remove(BEES_COMPONENT);
                components.remove("!" + BEES_COMPONENT);
            } else {
                components.put(BEES_COMPONENT, bees.copy());
            }
            data.put("components", components);
        }
        if (bees.isEmpty()) {
            stack.remove(DataComponents.BEES);
            return;
        }
        try {
            RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, ClientUtil.registryAccess());
            Bees parsed = Bees.CODEC.parse(ops, bees).result().orElse(null);
            if (parsed != null) {
                stack.set(DataComponents.BEES, parsed);
            }
        } catch (Exception ignored) {
        }
    }
}