package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.entity.ItemFrameItemEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EnumEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.SpacerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.FloatEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.IntegerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.TextEntryModel;
import com.github.rinorsi.cadeditor.client.util.ComponentJsonHelper;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.TypedEntityData;

import java.util.Optional;

public class ItemFrameDataCategoryModel extends ItemEditorCategoryModel {
    private ItemFrameItemEntryModel itemEntry;
    private final EntityType<?> entityType;

    public ItemFrameDataCategoryModel(ItemEditorModel editor) {
        super(ModTexts.ITEM_FRAME, editor);
        ItemStack stack = getParent().getContext().getItemStack();
        TypedEntityData<EntityType<?>> data = stack.get(DataComponents.ENTITY_DATA);
        this.entityType = data != null ? data.type() : (stack.is(Items.GLOW_ITEM_FRAME) ? EntityTypes.GLOW_ITEM_FRAME : EntityTypes.ITEM_FRAME);
    }

    @Override
    protected void setupEntries() {
        CompoundTag data = entityTag();

        getEntries().add(new TextEntryModel(this, ModTexts.CUSTOM_NAME, getCustomName(), value -> setCustomName(value)));
        getEntries().add(new BooleanEntryModel(this, ModTexts.ALWAYS_SHOW_NAME, data.getBooleanOr("CustomNameVisible", false),
                value -> setBoolean("CustomNameVisible", value)));
        getEntries().add(new BooleanEntryModel(this, ModTexts.ITEM_FRAME_FIXED, data.getBooleanOr("Fixed", false),
                value -> setBoolean("Fixed", value)));
        getEntries().add(new BooleanEntryModel(this, ModTexts.ITEM_FRAME_INVISIBLE, data.getBooleanOr("Invisible", false),
                value -> setBoolean("Invisible", value)));
        getEntries().add(new BooleanEntryModel(this, ModTexts.NO_GRAVITY, data.getBooleanOr("NoGravity", false),
                value -> setBoolean("NoGravity", value)));
        getEntries().add(new BooleanEntryModel(this, ModTexts.INVULNERABLE, data.getBooleanOr("Invulnerable", false),
                value -> setBoolean("Invulnerable", value)));
        getEntries().add(new BooleanEntryModel(this, ModTexts.GLOWING, data.getBooleanOr("Glowing", false),
                value -> setBoolean("Glowing", value)));
        getEntries().add(new IntegerEntryModel(this, ModTexts.ITEM_FRAME_ROTATION, Byte.toUnsignedInt(data.getByteOr("ItemRotation", (byte) 0)),
                value -> setItemRotation(value), value -> value != null && value >= 0 && value <= 7));
        getEntries().add(new FloatEntryModel(this, ModTexts.ITEM_FRAME_DROP_CHANCE, data.getFloatOr("ItemDropChance", 1f),
                value -> setItemDropChance(value), value -> value != null && value >= 0f && value <= 1f));

        Direction facing = Direction.from3DDataValue(Byte.toUnsignedInt(data.getByteOr("Facing", (byte) 0)));
        EnumEntryModel<Direction> facingEntry = new EnumEntryModel<>(this, ModTexts.ITEM_FRAME_FACING, Direction.values(), facing,
                direction -> setFacing(direction.get3DDataValue()));
        facingEntry.withTextFactory(ModTexts::direction);
        getEntries().add(facingEntry);

        getEntries().add(new SpacerEntryModel(this));

        ItemStack currentItem = readDisplayedItem();
        itemEntry = new ItemFrameItemEntryModel(this, currentItem, ModTexts.ITEM_FRAME_ITEM);
        itemEntry.itemStackProperty().addListener(stack -> updateDisplayedItem(itemEntry.getItemStack()));
        getEntries().add(itemEntry);
    }

    private CompoundTag entityTag() {
        ItemStack stack = getParent().getContext().getItemStack();
        TypedEntityData<EntityType<?>> data = stack.get(DataComponents.ENTITY_DATA);
        return data == null ? new CompoundTag() : data.copyTagWithoutId();
    }

    private void writeEntityTag(CompoundTag tag) {
        ItemStack stack = getParent().getContext().getItemStack();
        if (tag.isEmpty()) {
            stack.remove(DataComponents.ENTITY_DATA);
        } else {
            stack.set(DataComponents.ENTITY_DATA, TypedEntityData.of(entityType, tag));
        }
    }

    private ItemStack readDisplayedItem() {
        CompoundTag itemTag = entityTag().getCompound("Item").orElse(null);
        if (itemTag == null || itemTag.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return ClientUtil.parseItemStack(ClientUtil.registryAccess(), itemTag);
    }

    private void updateDisplayedItem(ItemStack displayed) {
        CompoundTag tag = entityTag();
        if (displayed.isEmpty()) {
            tag.remove("Item");
        } else {
            tag.put("Item", ClientUtil.saveItemStack(ClientUtil.registryAccess(), displayed));
        }
        writeEntityTag(tag);
    }

    private MutableComponent getCustomName() {
        Tag encoded = entityTag().get("CustomName");
        return ComponentJsonHelper.decode(encoded, ClientUtil.registryAccess());
    }

    private void setCustomName(MutableComponent value) {
        CompoundTag tag = entityTag();
        if (value != null && !value.getString().isEmpty()) {
            Tag encoded = ComponentJsonHelper.encodeToTag(value, ClientUtil.registryAccess());
            if (encoded != null) {
                tag.put("CustomName", encoded);
            }
        } else {
            tag.remove("CustomName");
        }
        writeEntityTag(tag);
    }

    private void setBoolean(String key, boolean value) {
        CompoundTag tag = entityTag();
        if (value) {
            tag.putBoolean(key, true);
        } else {
            tag.remove(key);
        }
        writeEntityTag(tag);
    }

    private void setItemRotation(Integer value) {
        int rotation = value == null ? 0 : Math.floorMod(value, 8);
        CompoundTag tag = entityTag();
        if (rotation != 0) {
            tag.putByte("ItemRotation", (byte) rotation);
        } else {
            tag.remove("ItemRotation");
        }
        writeEntityTag(tag);
    }

    private void setItemDropChance(Float value) {
        float chance = value == null ? 1f : Math.max(0f, Math.min(1f, value));
        CompoundTag tag = entityTag();
        if (Math.abs(chance - 1f) > 1.0e-6f) {
            tag.putFloat("ItemDropChance", chance);
        } else {
            tag.remove("ItemDropChance");
        }
        writeEntityTag(tag);
    }

    private void setFacing(int dataValue) {
        CompoundTag tag = entityTag();
        tag.putByte("Facing", (byte) dataValue);
        writeEntityTag(tag);
    }
}
