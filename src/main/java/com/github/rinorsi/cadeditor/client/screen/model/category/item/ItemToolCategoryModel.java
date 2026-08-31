package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.FloatEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.IntegerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.ToolRuleEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;

public class ItemToolCategoryModel extends ItemEditorCategoryModel {
    private float defaultMiningSpeed;
    private int damagePerBlock;
    private boolean creativeCanBreak;
    private BooleanEntryModel behaviorToggle;
    private boolean enableTool;

    public ItemToolCategoryModel(ItemEditorModel editor) {
        super(ModTexts.TOOL, editor);
    }

    @Override
    protected void setupEntries() {
        ItemStack stack = getParent().getContext().getItemStack();
        Tool tool = stack.get(DataComponents.TOOL);
        Tool baseTool = stack.getItem().components().get(DataComponents.TOOL);
        Tool effective = tool != null ? tool : baseTool;
        if (effective != null) {
            defaultMiningSpeed = effective.defaultMiningSpeed();
            damagePerBlock = effective.damagePerBlock();
            creativeCanBreak = effective.canDestroyBlocksInCreative();
        } else {
            defaultMiningSpeed = 1f;
            damagePerBlock = 0;
            creativeCanBreak = false;
        }
        this.enableTool = getParent().isToolBehaviorEnabled();
        this.behaviorToggle = new BooleanEntryModel(this, ModTexts.gui("tool_behavior_enabled"), this.enableTool, value -> {
            this.enableTool = value != null && value;
            getParent().setToolBehaviorEnabled(this.enableTool);
            syncEntriesEnabled();
        });
        getEntries().add(this.behaviorToggle);
        getEntries().add(new BooleanEntryModel(this, ModTexts.gui("tool_can_destroy_in_creative"), this.creativeCanBreak,
                value -> this.creativeCanBreak = value != null && value));
        getEntries().add(new FloatEntryModel(this, ModTexts.TOOL_MINING_SPEED, defaultMiningSpeed,
                value -> defaultMiningSpeed = value == null ? 1f : value));
        getEntries().add(new IntegerEntryModel(this, ModTexts.TOOL_DAMAGE_PER_BLOCK, damagePerBlock,
                value -> damagePerBlock = value == null ? 0 : Math.max(0, value)));
        if (tool != null) {
            tool.rules().forEach(rule -> getEntries().add(new ToolRuleEntryModel(this, rule)));
        }
        syncEntriesEnabled();
    }

    @Override
    public void initalize() {
        super.initalize();
        syncEntriesEnabled();
    }

    @Override
    public void addEntryInList() {
        super.addEntryInList();
        syncEntriesEnabled();
    }

    private void syncEntriesEnabled() {
        for (EntryModel entry : getEntries()) {
            if (entry != this.behaviorToggle) {
                entry.setEnabled(this.enableTool);
            }
        }
    }

    @Override
    public int getEntryListStart() {
        return 4;
    }

    @Override
    protected MutableComponent getAddListEntryButtonTooltip() {
        return ModTexts.gui("tool_rule");
    }

    @Override
    public EntryModel createNewListEntry() {
        return new ToolRuleEntryModel(this);
    }

    @Override
    public void apply() {
        ItemStack stack = getParent().getContext().getItemStack();
        if (!this.enableTool) {
            stack.remove(DataComponents.TOOL);
            getParent().removeComponentFromDataTag("minecraft:tool");
            return;
        }
        super.apply();
        HolderLookup.RegistryLookup<Block> blockLookup = ClientUtil.registryAccess()
                .lookup(Registries.BLOCK)
                .orElse(null);
        List<Tool.Rule> parsedRules = new ArrayList<>();
        boolean hasInvalid = false;
        for (EntryModel entry : getEntries()) {
            if (entry instanceof ToolRuleEntryModel rule) {
                if (!rule.hasSelection() && rule.getSpeedText().isBlank()) {
                    rule.setValid(true);
                    continue;
                }
                if (blockLookup == null) {
                    rule.setValid(false);
                    hasInvalid = true;
                    continue;
                }
                var parsed = rule.toRules(blockLookup);
                if (parsed.isPresent()) {
                    parsedRules.addAll(parsed.get());
                    rule.setValid(true);
                } else {
                    rule.setValid(false);
                    hasInvalid = true;
                }
            }
        }
        if (hasInvalid) {
            return;
        }
        if (parsedRules.isEmpty() && Math.abs(defaultMiningSpeed - 1f) < 1e-6 && damagePerBlock <= 0 && !creativeCanBreak) {
            stack.remove(DataComponents.TOOL);
        } else {
            stack.set(DataComponents.TOOL, new Tool(parsedRules, defaultMiningSpeed, damagePerBlock, creativeCanBreak));
        }
        CompoundTag tag = getData();
        if (tag != null) {
            CompoundTag components = tag.getCompound("components").orElse(null);
            if (components != null) {
                components.remove("minecraft:tool");
                if (components.isEmpty()) {
                    tag.remove("components");
                }
            }
        }
    }
}
