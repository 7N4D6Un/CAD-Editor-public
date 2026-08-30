package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.IntegerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.StringEntryModel;
import com.github.rinorsi.cadeditor.client.util.ComponentJsonHelper;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.block.entity.BlockEntityTypes;

public class ItemCommandBlockCategoryModel extends ItemEditorCategoryModel {
    private static final String KEY_COMMAND = "Command";
    private static final String KEY_CUSTOM_NAME = "CustomName";
    private static final String KEY_TRACK_OUTPUT = "TrackOutput";
    private static final String KEY_SUCCESS_COUNT = "SuccessCount";
    private static final String KEY_AUTO = "auto";
    private static final String KEY_UPDATE_LAST_EXECUTION = "UpdateLastExecution";

    private CompoundTag root;
    private StringEntryModel commandEntry;
    private StringEntryModel customNameEntry;
    private BooleanEntryModel trackOutputEntry;
    private IntegerEntryModel successCountEntry;
    private BooleanEntryModel autoEntry;
    private BooleanEntryModel updateLastExecutionEntry;

    public ItemCommandBlockCategoryModel(ItemEditorModel editor) {
        super(ModTexts.gui("command_block"), editor);
        ItemStack stack = editor.getContext().getItemStack();
        TypedEntityData<net.minecraft.world.level.block.entity.BlockEntityType<?>> data = stack == null ? null : stack.get(DataComponents.BLOCK_ENTITY_DATA);
        this.root = data == null ? new CompoundTag() : data.getUnsafe().copy();
    }

    @Override
    protected void setupEntries() {
        this.commandEntry = new StringEntryModel(this, ModTexts.gui("command"), root.getStringOr(KEY_COMMAND, ""), this::setCommand);
        this.customNameEntry = new StringEntryModel(this, ModTexts.CUSTOM_NAME, readCustomName(), this::setCustomName);
        this.trackOutputEntry = new BooleanEntryModel(this, ModTexts.gui("command_track_output"), root.getBooleanOr(KEY_TRACK_OUTPUT, true), value -> root.putBoolean(KEY_TRACK_OUTPUT, value));
        this.successCountEntry = new IntegerEntryModel(this, ModTexts.gui("command_success_count"), Math.max(0, root.getIntOr(KEY_SUCCESS_COUNT, 0)), value -> root.putInt(KEY_SUCCESS_COUNT, Math.max(0, value)), value -> value >= 0);
        this.autoEntry = new BooleanEntryModel(this, ModTexts.gui("command_auto"), root.getBooleanOr(KEY_AUTO, false), value -> root.putBoolean(KEY_AUTO, value));
        this.updateLastExecutionEntry = new BooleanEntryModel(this, ModTexts.gui("command_update_last_execution"), root.getBooleanOr(KEY_UPDATE_LAST_EXECUTION, true), value -> root.putBoolean(KEY_UPDATE_LAST_EXECUTION, value));
        getEntries().addAll(this.commandEntry, this.customNameEntry, this.trackOutputEntry, this.successCountEntry,
                this.autoEntry, this.updateLastExecutionEntry);
    }

    @Override
    public void apply() {
        super.apply();
        ItemStack stack = getParent().getContext().getItemStack();
        if (stack == null) {
            return;
        }
        CompoundTag payload = root.copy();
        payload.remove("id");
        stack.set(DataComponents.BLOCK_ENTITY_DATA, TypedEntityData.of(BlockEntityTypes.COMMAND_BLOCK, payload));
    }

    private void setCommand(String value) {
        root.putString(KEY_COMMAND, value == null ? "" : value);
    }

    private void setCustomName(String value) {
        String text = value == null ? "" : value.trim();
        if (text.isEmpty()) {
            root.remove(KEY_CUSTOM_NAME);
            return;
        }
        Tag encoded = ComponentJsonHelper.encodeToTag(Component.literal(text), ClientUtil.registryAccess());
        if (encoded != null) {
            root.put(KEY_CUSTOM_NAME, encoded);
        }
    }

    private String readCustomName() {
        Tag encoded = root.get(KEY_CUSTOM_NAME);
        MutableComponent decoded = ComponentJsonHelper.decode(encoded, ClientUtil.registryAccess());
        return decoded == null ? "" : decoded.getString();
    }
}