package com.github.rinorsi.cadeditor.client.screen.model.category.block;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.screen.model.BlockEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.IntegerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.StringEntryModel;
import com.github.rinorsi.cadeditor.client.util.ComponentJsonHelper;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class BlockCommandBlockCategoryModel extends BlockEditorCategoryModel {
    private StringEntryModel commandEntry;
    private StringEntryModel customNameEntry;
    private BooleanEntryModel trackOutputEntry;
    private IntegerEntryModel successCountEntry;
    private BooleanEntryModel autoEntry;
    private BooleanEntryModel updateLastExecutionEntry;
    private BooleanEntryModel conditionalEntry;

    public BlockCommandBlockCategoryModel(BlockEditorModel editor) {
        super(ModTexts.gui("command_block"), editor);
    }

    @Override
    protected void setupEntries() {
        CompoundTag root = getData();
        this.commandEntry = new StringEntryModel(this, ModTexts.gui("command"), root.getStringOr("Command", ""), this::setCommand);
        this.customNameEntry = new StringEntryModel(this, ModTexts.CUSTOM_NAME, readCustomName(), this::setCustomName);
        this.trackOutputEntry = new BooleanEntryModel(this, ModTexts.gui("command_track_output"), root.getBooleanOr("TrackOutput", true), value -> root.putBoolean("TrackOutput", value));
        this.successCountEntry = new IntegerEntryModel(this, ModTexts.gui("command_success_count"), Math.max(0, root.getIntOr("SuccessCount", 0)), value -> root.putInt("SuccessCount", Math.max(0, value)), value -> value >= 0);
        this.autoEntry = new BooleanEntryModel(this, ModTexts.gui("command_auto"), root.getBooleanOr("auto", false), value -> root.putBoolean("auto", value));
        this.updateLastExecutionEntry = new BooleanEntryModel(this, ModTexts.gui("command_update_last_execution"), root.getBooleanOr("UpdateLastExecution", true), value -> root.putBoolean("UpdateLastExecution", value));
        this.conditionalEntry = new BooleanEntryModel(this, ModTexts.gui("command_conditional"), getBlockState().getValue(BlockStateProperties.CONDITIONAL), this::setConditional);
        getEntries().addAll(this.commandEntry, this.customNameEntry, this.trackOutputEntry, this.successCountEntry,
                this.autoEntry, this.updateLastExecutionEntry, this.conditionalEntry);
    }

    private void setCommand(String value) {
        String text = value == null ? "" : value;
        getData().putString("Command", text);
    }

    private void setCustomName(String value) {
        CompoundTag root = getData();
        String text = value == null ? "" : value.trim();
        if (text.isEmpty()) {
            root.remove("CustomName");
            return;
        }
        Tag encoded = ComponentJsonHelper.encodeToTag(Component.literal(text), ClientUtil.registryAccess());
        if (encoded != null) {
            root.put("CustomName", encoded);
        }
    }

    private void setConditional(boolean value) {
        updateState(BlockStateProperties.CONDITIONAL, value);
    }

    private String readCustomName() {
        Tag encoded = getData().get("CustomName");
        MutableComponent decoded = ComponentJsonHelper.decode(encoded, ClientUtil.registryAccess());
        return decoded == null ? "" : decoded.getString();
    }
}