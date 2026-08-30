package com.github.rinorsi.cadeditor.client.screen.model.category.block;

import com.github.rinorsi.cadeditor.client.screen.model.BlockEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.SignNbtHelper;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EnumEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.InfoEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.TextEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.DyeColor;

public class BlockSignCategoryModel extends BlockEditorCategoryModel {
    private CompoundTag frontFace;
    private CompoundTag backFace;
    private BooleanEntryModel waxedEntry;

    public BlockSignCategoryModel(BlockEditorModel editor) {
        super(ModTexts.gui("sign"), editor);
    }

    @Override
    protected void setupEntries() {
        CompoundTag raw = getData();
        if (raw == null) {
            raw = new CompoundTag();
            getContext().setTag(raw);
        }
        final CompoundTag root = raw;
        this.frontFace = SignNbtHelper.readFace(root, SignNbtHelper.KEY_FRONT_TEXT);
        this.backFace = SignNbtHelper.readFace(root, SignNbtHelper.KEY_BACK_TEXT);
        this.waxedEntry = new BooleanEntryModel(this, ModTexts.gui("sign_waxed"), SignNbtHelper.readWaxed(root),
                value -> root.putBoolean(SignNbtHelper.KEY_IS_WAXED, value));
        getEntries().add(this.waxedEntry);
        addFaceEntries(this.frontFace, "sign_front");
        addFaceEntries(this.backFace, "sign_back");
    }

    @Override
    public void apply() {
        super.apply();
        CompoundTag root = getData();
        if (root == null) {
            return;
        }
        root.put(SignNbtHelper.KEY_FRONT_TEXT, this.frontFace);
        root.put(SignNbtHelper.KEY_BACK_TEXT, this.backFace);
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
                MutableComponent safe = value == null ? net.minecraft.network.chat.Component.empty() : value;
                SignNbtHelper.writeLine(faceRef, lineIndex, safe);
            }));
        }
    }
}
