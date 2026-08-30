package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.SignNbtHelper;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EnumEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.InfoEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.TextEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;

public class ItemSignCategoryModel extends ItemEditorCategoryModel {
    private CompoundTag root;
    private CompoundTag frontFace;
    private CompoundTag backFace;
    private BooleanEntryModel waxedEntry;
    private final BlockEntityType<?> blockEntityType;

    public ItemSignCategoryModel(ItemEditorModel editor) {
        super(ModTexts.gui("sign"), editor);
        ItemStack stack = editor.getContext().getItemStack();
        this.blockEntityType = resolveBlockEntityType(stack);
        TypedEntityData<BlockEntityType<?>> data = stack == null ? null : stack.get(DataComponents.BLOCK_ENTITY_DATA);
        this.root = data == null ? new CompoundTag() : data.getUnsafe().copy();
        this.frontFace = SignNbtHelper.readFace(this.root, SignNbtHelper.KEY_FRONT_TEXT);
        this.backFace = SignNbtHelper.readFace(this.root, SignNbtHelper.KEY_BACK_TEXT);
    }

    private static BlockEntityType<?> resolveBlockEntityType(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof net.minecraft.world.item.BlockItem blockItem
                && blockItem.getBlock() instanceof net.minecraft.world.level.block.HangingSignBlock) {
            return BlockEntityTypes.HANGING_SIGN;
        }
        return BlockEntityTypes.SIGN;
    }

    @Override
    protected void setupEntries() {
        this.waxedEntry = new BooleanEntryModel(this, ModTexts.gui("sign_waxed"), SignNbtHelper.readWaxed(this.root),
                value -> this.root.putBoolean(SignNbtHelper.KEY_IS_WAXED, value));
        getEntries().add(this.waxedEntry);
        addFaceEntries(this.frontFace, "sign_front");
        addFaceEntries(this.backFace, "sign_back");
    }

    @Override
    public void apply() {
        super.apply();
        ItemStack stack = getParent().getContext().getItemStack();
        if (stack == null) {
            return;
        }
        this.root.put(SignNbtHelper.KEY_FRONT_TEXT, this.frontFace);
        this.root.put(SignNbtHelper.KEY_BACK_TEXT, this.backFace);
        CompoundTag payload = this.root.copy();
        payload.remove("id");
        stack.set(DataComponents.BLOCK_ENTITY_DATA, TypedEntityData.of(this.blockEntityType, payload));
    }

    private void addFaceEntries(CompoundTag face, String titleKey) {
        final CompoundTag faceRef = face;
        getEntries().add(new InfoEntryModel(this, ModTexts.gui(titleKey)));
        getEntries().add(new BooleanEntryModel(this, ModTexts.gui("sign_glowing"), SignNbtHelper.readGlowing(faceRef),
                value -> faceRef.putBoolean(SignNbtHelper.KEY_HAS_GLOWING_TEXT, value)));
        getEntries().add(new EnumEntryModel<>(this, ModTexts.gui("sign_color"), DyeColor.values(), SignNbtHelper.readColor(faceRef),
                value -> faceRef.putString(SignNbtHelper.KEY_COLOR, value == null ? "black" : value.getName())));
        for (int i = 0; i < SignNbtHelper.LINES; i++) {
            final int lineIndex = i;
            MutableComponent line = SignNbtHelper.readLine(faceRef, lineIndex);
            getEntries().add(new TextEntryModel(this, ModTexts.gui("sign_line_" + (lineIndex + 1)), line, value -> {
                MutableComponent safe = value == null ? Component.empty() : value;
                SignNbtHelper.writeLine(faceRef, lineIndex, safe);
            }));
        }
    }
}
