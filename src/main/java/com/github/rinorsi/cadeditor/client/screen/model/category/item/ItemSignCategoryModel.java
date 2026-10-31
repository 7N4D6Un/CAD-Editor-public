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
import net.minecraft.util.Unit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignText;

public class ItemSignCategoryModel extends ItemEditorCategoryModel {
    private static final String BLOCK_ENTITY_DATA_KEY = "minecraft:block_entity_data";
    private static final String COMPONENTS_KEY = "components";
    private static final String ID_KEY = "id";
    private final CompoundTag frontFace;
    private final CompoundTag backFace;
    private final boolean initiallyWaxed;
    private BooleanEntryModel waxedEntry;

    public ItemSignCategoryModel(ItemEditorModel editor) {
        super(ModTexts.gui("sign"), editor);
        ItemStack stack = editor.getContext().getItemStack();
        CompoundTag legacy = legacyPayload(stack);
        SignText frontText = stack == null ? null : stack.get(DataComponents.SIGN_TEXT_FRONT);
        SignText backText = stack == null ? null : stack.get(DataComponents.SIGN_TEXT_BACK);
        this.frontFace = frontText == null
                ? SignNbtHelper.readFace(legacy, SignNbtHelper.KEY_FRONT_TEXT)
                : SignNbtHelper.fromSignText(frontText);
        this.backFace = backText == null
                ? SignNbtHelper.readFace(legacy, SignNbtHelper.KEY_BACK_TEXT)
                : SignNbtHelper.fromSignText(backText);
        this.initiallyWaxed = (stack != null && stack.has(DataComponents.WAXED)) || SignNbtHelper.readWaxed(legacy);
    }

    private static CompoundTag legacyPayload(ItemStack stack) {
        TypedEntityData<BlockEntityType<?>> data = stack == null ? null : stack.get(DataComponents.BLOCK_ENTITY_DATA);
        return data == null ? null : data.getUnsafe().copy();
    }

    @Override
    protected void setupEntries() {
        this.waxedEntry = new BooleanEntryModel(this, ModTexts.gui("sign_waxed"), this.initiallyWaxed, value -> {
        });
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
        stack.set(DataComponents.SIGN_TEXT_FRONT, SignNbtHelper.toSignText(this.frontFace));
        stack.set(DataComponents.SIGN_TEXT_BACK, SignNbtHelper.toSignText(this.backFace));
        if (this.waxedEntry != null && Boolean.TRUE.equals(this.waxedEntry.getValue())) {
            stack.set(DataComponents.WAXED, Unit.INSTANCE);
        } else {
            stack.remove(DataComponents.WAXED);
        }
        stripLegacyData(stack);
        stripLegacyTag();
    }

    private static void stripLegacyData(ItemStack stack) {
        TypedEntityData<BlockEntityType<?>> data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data == null) {
            return;
        }
        CompoundTag payload = data.getUnsafe().copy();
        stripLegacyKeys(payload);
        if (payload.isEmpty()) {
            stack.remove(DataComponents.BLOCK_ENTITY_DATA);
        } else {
            stack.set(DataComponents.BLOCK_ENTITY_DATA, TypedEntityData.of(data.type(), payload));
        }
    }

    private void stripLegacyTag() {
        CompoundTag root = getData();
        if (root == null) {
            return;
        }
        CompoundTag components = root.getCompound(COMPONENTS_KEY).orElse(null);
        if (components == null) {
            return;
        }
        CompoundTag payload = components.getCompound(BLOCK_ENTITY_DATA_KEY).orElse(null);
        if (payload == null) {
            return;
        }
        stripLegacyKeys(payload);
        if (payload.isEmpty() || (payload.size() == 1 && payload.contains(ID_KEY))) {
            components.remove(BLOCK_ENTITY_DATA_KEY);
        }
    }

    private static void stripLegacyKeys(CompoundTag payload) {
        payload.remove(SignNbtHelper.KEY_FRONT_TEXT);
        payload.remove(SignNbtHelper.KEY_BACK_TEXT);
        payload.remove(SignNbtHelper.KEY_IS_WAXED);
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
