package com.github.rinorsi.cadeditor.client.screen.model.entry.item;

import com.github.franckyi.databindings.api.ObjectProperty;
import com.github.rinorsi.cadeditor.client.screen.model.category.CategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.UseRemainderState;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.ItemSelectionEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.Optional;
import net.minecraft.world.item.ItemStack;


public class UseRemainderEntryModel extends ItemSelectionEntryModel {
    private final ObjectProperty<ItemStack> previewStackProperty;
    private final UseRemainderState state;

    public static UseRemainderEntryModel create(CategoryModel category, UseRemainderState state) {
        UseRemainderEntryModel model = new UseRemainderEntryModel(category, state);
        model.valueProperty().addListener(id -> {
            state.setUsingConvertsToId(id);
            model.updatePreviewFromState();
        });
        model.updatePreviewFromState();
        return model;
    }

    private UseRemainderEntryModel(CategoryModel category, UseRemainderState state) {
        super(category, ModTexts.gui("use_remainder_item"), state.getUsingConvertsToId(), state::setUsingConvertsToId);
        this.state = state;
        this.previewStackProperty = ObjectProperty.create(ItemStack.EMPTY);
    }

    @Override
    public EntryModel.Type getType() {
        return EntryModel.Type.USE_REMAINDER;
    }

    public ObjectProperty<ItemStack> previewStackProperty() {
        return this.previewStackProperty;
    }

    public ItemStack getPreviewStack() {
        return previewStackProperty().getValue();
    }

    public void updatePreviewFromState() {
        ItemStack stack = (ItemStack) this.state.getUsingConvertsToPreview().map(value -> value.copy()).orElse(ItemStack.EMPTY);
        this.previewStackProperty.setValue(stack);
    }

    public Optional<ItemStack> getEditableStack() {
        return this.state.getUsingConvertsToEditorStack();
    }

    public void useStack(ItemStack stack) {
        this.state.useCustomUsingConvertsTo(stack);
        if (!java.util.Objects.equals(getValue(), this.state.getUsingConvertsToId())) {
            setValue(this.state.getUsingConvertsToId());
        } else {
            updatePreviewFromState();
        }
    }
}
