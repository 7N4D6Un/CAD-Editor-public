package com.github.rinorsi.cadeditor.client.context;

import com.github.rinorsi.cadeditor.client.ClientCache;
import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class BlockEditorContext extends EditorContext<BlockEditorContext> {
    private BlockState blockState;
    private final BlockState originalBlockState;
    private final BlockEntity blockEntity;
    private final BlockPos pos;

    public BlockEditorContext(BlockState blockState, CompoundTag tag, Component errorTooltip, Consumer<BlockEditorContext> action, BlockPos pos) {
        super(tag, errorTooltip, false, action);
        this.blockState = blockState;
        this.originalBlockState = blockState;
        this.blockEntity = tag == null ? null : BlockEntity.loadStatic(BlockPos.ZERO, blockState, tag, ClientUtil.registryAccess());
        this.pos = pos;
    }

    public BlockState getBlockState() {
        return blockState;
    }

    @Override
    protected boolean isUnchanged() {
        return super.isUnchanged() && (originalBlockState == null || originalBlockState.equals(this.blockState));
    }

    public <T extends Comparable<T>> void updateBlockState(Property<T> property, T value) {
        blockState = blockState.setValue(property, value);
    }

    public BlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public MutableComponent getTargetName() {
        return ModTexts.BLOCK;
    }

    @Override
    public String getCommandName() {
        return "/setblock";
    }

    @Override
    protected String getCommand() {
        String blockStateStr = getBlockState().toString();
        String tagStr = getTag() == null ? "" : getTag().toString();
        return String.format("/setblock ~ ~ ~ %s%s%s replace", BuiltInRegistries.BLOCK.getKey(getBlockState().getBlock()),
                getBlockState().getProperties().isEmpty() ? "" : blockStateStr.substring(blockStateStr.indexOf("[")), tagStr);
    }

    @Override
    protected void applyVanillaCommand() {
        if (pos == null) {
            return;
        }
        String posStr = String.format("%d %d %d", pos.getX(), pos.getY(), pos.getZ());
        boolean stateChanged = originalBlockState != null && !originalBlockState.equals(this.blockState);
        if (stateChanged) {
            sendVanillaCommand(buildSetBlockCommand(posStr));
            return;
        }
        for (String key : findRemovedKeys(Set.of())) {
            sendVanillaCommand(String.format("/data remove block %s %s", posStr, quoteKey(key)));
        }
        CompoundTag changedTag = buildChangedTag(Set.of());
        if (!changedTag.isEmpty()) {
            sendVanillaCommand(String.format("/data merge block %s %s", posStr, changedTag));
        }
    }

    private String buildSetBlockCommand(String posStr) {
        String blockStateStr = getBlockState().toString();
        String statePart = getBlockState().getProperties().isEmpty() ? "" : blockStateStr.substring(blockStateStr.indexOf("["));
        String tagStr = getTag() == null ? "" : getTag().toString();
        return String.format("/setblock %s %s%s%s replace", posStr, BuiltInRegistries.BLOCK.getKey(getBlockState().getBlock()), statePart, tagStr);
    }

    @Override
    public List<String> getStringSuggestions(List<String> path) {
        List<String> suggestions = super.getStringSuggestions(path);
        if (!suggestions.isEmpty()) {
            return suggestions;
        }
        if (path == null || path.isEmpty()) {
            return List.of();
        }
        String key = lastKey(path);
        if (key == null) {
            return List.of();
        }
        String parent = previousNamedKey(path, 1);
        String grandParent = previousNamedKey(path, 2);

        if ("id".equals(key)) {
            if (path.size() == 1) {
                return ClientCache.getBlockEntityTypeSuggestions();
            }
            if (isItemContainer(parent, grandParent)) {
                return ClientCache.getItemSuggestions();
            }
        }

        if (equalsAnyIgnoreCase(key, "item", "Item")) {
            return ClientCache.getItemSuggestions();
        }
        if (equalsAnyIgnoreCase(key, "potion")) {
            return ClientCache.getPotionSuggestions();
        }
        if (equalsAnyIgnoreCase(key, "effect")) {
            return ClientCache.getEffectSuggestions();
        }

        return List.of();
    }

    private static boolean isItemContainer(String parent, String grandParent) {
        return equalsAnyIgnoreCase(parent, "Items", "item", "Item", "RecordItem", "stack")
                || (equalsAnyIgnoreCase(parent, "stack") && equalsAnyIgnoreCase(grandParent, "Items"));
    }
}
