package com.github.rinorsi.cadeditor.client.screen.model.entry;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.screen.model.category.CategoryModel;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Consumer;

import static com.github.franckyi.guapi.api.GuapiHelper.*;

public class TextEntryModel extends ValueEntryModel<MutableComponent> {
    private Runnable onApply;
    private boolean deletable = true;

    public TextEntryModel(CategoryModel category, MutableComponent label, MutableComponent value, Consumer<MutableComponent> action) {
        super(category, label, value == null ? text() : value, action);
    }

    public Runnable getOnApply() {
        return onApply;
    }

    public void setOnApply(Runnable onApply) {
        this.onApply = onApply;
    }

    @Override
    public void apply() {
        if (onApply != null) {
            onApply.run();
        }
        super.apply();
    }

    @Override
    protected boolean valuesEqual(MutableComponent a, MutableComponent b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (!a.getString().equals(b.getString())) {
            return false;
        }
        return java.util.Objects.equals(
                com.github.rinorsi.cadeditor.client.util.ComponentJsonHelper.encodeToTag(a, ClientUtil.registryAccess()),
                com.github.rinorsi.cadeditor.client.util.ComponentJsonHelper.encodeToTag(b, ClientUtil.registryAccess()));
    }

    @Override
    public Type getType() {
        return Type.TEXT;
    }

    public void resetDefaultValue() {
        defaultValue = getValue();
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    @Override
    public boolean isDeletable() {
        return deletable && super.isDeletable();
    }
}
