package com.github.rinorsi.cadeditor.client.screen.model.category.entity;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.screen.model.EntityEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EnumEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.IntegerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.SpacerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.TextEntryModel;
import com.github.rinorsi.cadeditor.client.util.ComponentJsonHelper;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Display;

public class EntityTextDisplayCategoryModel extends EntityDisplayCategoryModel {
    private static final int DEFAULT_BACKGROUND = 1073741824;

    public EntityTextDisplayCategoryModel(EntityEditorModel editor) {
        super(editor);
    }

    @Override
    protected void setupEntries() {
        CompoundTag data = getData();
        getEntries().add(new TextEntryModel(this, ModTexts.DISPLAY_TEXT, readText(), this::setText));
        getEntries().add(new IntegerEntryModel(this, ModTexts.DISPLAY_LINE_WIDTH, data.getIntOr("line_width", 200), this::setLineWidth));
        getEntries().add(new IntegerEntryModel(this, ModTexts.DISPLAY_BACKGROUND, data.getIntOr("background", DEFAULT_BACKGROUND), this::setBackground));
        getEntries().add(new IntegerEntryModel(this, ModTexts.DISPLAY_TEXT_OPACITY, data.getByteOr("text_opacity", (byte) -1),
                this::setTextOpacity, value -> value != null && value >= -128 && value <= 127));
        getEntries().add(new BooleanEntryModel(this, ModTexts.DISPLAY_SHADOW, data.getBooleanOr("shadow", false), value -> writeBoolean("shadow", value)));
        getEntries().add(new BooleanEntryModel(this, ModTexts.DISPLAY_SEE_THROUGH, data.getBooleanOr("see_through", false), value -> writeBoolean("see_through", value)));
        getEntries().add(new BooleanEntryModel(this, ModTexts.DISPLAY_DEFAULT_BACKGROUND, data.getBooleanOr("default_background", false), value -> writeBoolean("default_background", value)));
        Display.TextDisplay.Align alignment = align(data.getStringOr("alignment", "center"));
        EnumEntryModel<Display.TextDisplay.Align> alignmentEntry = new EnumEntryModel<>(this, ModTexts.DISPLAY_ALIGNMENT,
                Display.TextDisplay.Align.values(), alignment, this::setAlignment);
        alignmentEntry.withTextFactory(ModTexts::displayAlignment);
        getEntries().add(alignmentEntry);

        getEntries().add(new SpacerEntryModel(this));
        super.setupEntries();
    }

    private MutableComponent readText() {
        Tag encoded = getData().get("text");
        return encoded == null ? null : ComponentJsonHelper.decode(encoded, ClientUtil.registryAccess());
    }

    private void setText(MutableComponent value) {
        CompoundTag data = getData();
        if (value == null || value.getString().isEmpty()) {
            data.remove("text");
            return;
        }
        Tag encoded = ComponentJsonHelper.encodeToTag(value, ClientUtil.registryAccess());
        if (encoded != null) {
            data.put("text", encoded);
        }
    }

    private void setLineWidth(Integer value) {
        int width = value == null ? 200 : value;
        CompoundTag data = getData();
        if (width == 200) {
            data.remove("line_width");
        } else {
            data.putInt("line_width", width);
        }
    }

    private void setBackground(Integer value) {
        int background = value == null ? DEFAULT_BACKGROUND : value;
        CompoundTag data = getData();
        if (background == DEFAULT_BACKGROUND) {
            data.remove("background");
        } else {
            data.putInt("background", background);
        }
    }

    private void setTextOpacity(Integer value) {
        int opacity = value == null ? -1 : Math.max(-128, Math.min(127, value));
        CompoundTag data = getData();
        if (opacity == -1) {
            data.remove("text_opacity");
        } else {
            data.putByte("text_opacity", (byte) opacity);
        }
    }

    private void writeBoolean(String key, boolean value) {
        CompoundTag data = getData();
        if (value) {
            data.putBoolean(key, true);
        } else {
            data.remove(key);
        }
    }

    private static Display.TextDisplay.Align align(String name) {
        for (Display.TextDisplay.Align align : Display.TextDisplay.Align.values()) {
            if (align.getSerializedName().equals(name)) {
                return align;
            }
        }
        return Display.TextDisplay.Align.CENTER;
    }

    private void setAlignment(Display.TextDisplay.Align value) {
        CompoundTag data = getData();
        if (value == null || value == Display.TextDisplay.Align.CENTER) {
            data.remove("alignment");
        } else {
            data.putString("alignment", value.getSerializedName());
        }
    }
}
