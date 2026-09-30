package com.github.rinorsi.cadeditor.client.screen.model.category.entity;

import com.github.franckyi.guapi.api.GuapiHelper;
import com.github.rinorsi.cadeditor.client.screen.model.EntityEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BlockSelectionEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EnumEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.IntegerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.SpacerEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.commons.lang3.StringUtils;

public class EntityBlockDisplayCategoryModel extends EntityDisplayCategoryModel {
    private String blockId = "";
    private BlockState state;

    public EntityBlockDisplayCategoryModel(EntityEditorModel editor) {
        super(editor);
    }

    @Override
    protected void setupEntries() {
        readState();
        getEntries().add(new BlockSelectionEntryModel(this, ModTexts.BLOCK_STATE, blockId, this::setBlockId));
        if (state != null) {
            addBlockProperties();
        }
        getEntries().add(new SpacerEntryModel(this));
        super.setupEntries();
    }

    private void readState() {
        CompoundTag tag = getData().getCompoundOrEmpty("block_state");
        state = null;
        blockId = "";
        if (tag.isEmpty()) {
            return;
        }
        state = BlockState.CODEC.parse(ops(), tag).result().orElse(null);
        if (state == null || state.isAir()) {
            state = null;
            return;
        }
        blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
    }

    private void setBlockId(String value) {
        String raw = value == null ? "" : value.trim();
        if (raw.isEmpty()) {
            return;
        }
        Identifier id = Identifier.tryParse(raw.contains(":") ? raw : "minecraft:" + raw);
        Block block = id == null ? null : BuiltInRegistries.BLOCK.getOptional(id).orElse(null);
        if (block == null) {
            return;
        }
        state = block.defaultBlockState();
        blockId = BuiltInRegistries.BLOCK.getKey(block).toString();
        writeState();
    }

    private void addBlockProperties() {
        for (Property<?> property : state.getProperties()) {
            MutableComponent name = GuapiHelper.text(StringUtils.capitalize(property.getName().toLowerCase()));
            if (property instanceof BooleanProperty booleanProperty) {
                getEntries().add(new BooleanEntryModel(this, name, state.getValue(booleanProperty),
                        value -> setProperty(booleanProperty, value)));
            } else if (property instanceof IntegerProperty integerProperty) {
                getEntries().add(new IntegerEntryModel(this, name, state.getValue(integerProperty),
                        value -> setProperty(integerProperty, value), integerProperty.getPossibleValues()::contains));
            } else {
                addEnumProperty(name, property);
            }
        }
    }

    private <T extends Comparable<T>> void addEnumProperty(MutableComponent name, Property<T> property) {
        getEntries().add(new EnumEntryModel<>(this, name, property.getPossibleValues(), state.getValue(property),
                value -> setProperty(property, value)));
    }

    private <T extends Comparable<T>> void setProperty(Property<T> property, T value) {
        if (value == null || state == null) {
            return;
        }
        state = state.setValue(property, value);
        writeState();
    }

    private void writeState() {
        CompoundTag data = getData();
        if (state == null || state.isAir()) {
            data.remove("block_state");
            return;
        }
        CompoundTag blockStateTag = new CompoundTag();
        blockStateTag.putString("Name", BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString());
        CompoundTag properties = new CompoundTag();
        BlockState defaultState = state.getBlock().defaultBlockState();
        for (Property<?> property : state.getProperties()) {
            putProperty(properties, defaultState, property);
        }
        if (!properties.isEmpty()) {
            blockStateTag.put("properties", properties);
        }
        data.put("block_state", blockStateTag);
    }

    private <T extends Comparable<T>> void putProperty(CompoundTag properties, BlockState defaultState, Property<T> property) {
        T value = state.getValue(property);
        if (!value.equals(defaultState.getValue(property))) {
            properties.putString(property.getName(), property.getName(value));
        }
    }
}
