package com.github.rinorsi.cadeditor.client.screen.controller.entry.item;

import com.github.franckyi.guapi.api.node.TextArea;
import com.github.rinorsi.cadeditor.client.screen.controller.entry.EntryController;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.WritableBookPagesEntryModel;
import com.github.rinorsi.cadeditor.client.screen.view.entry.item.WritableBookPagesEntryView;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;


public class WritableBookPagesEntryController extends EntryController<WritableBookPagesEntryModel, WritableBookPagesEntryView> {
    private boolean updating;

    public WritableBookPagesEntryController(WritableBookPagesEntryModel model, WritableBookPagesEntryView view) {
        super(model, view);
    }

    
    @Override 
    public void bind() {
        super.bind();
        ((WritableBookPagesEntryView) getView()).setListButtonsVisible(false);
        ((WritableBookPagesEntryView) getView()).getResetButton().setVisible(false);
        TextArea area = ((WritableBookPagesEntryView) getView()).getEditorArea();
        area.setValidator(this::isLengthValid);
        area.textProperty().addListener(value -> {
            if (!this.updating) {
                ((WritableBookPagesEntryModel) getModel()).setSelectedPage(value);
                updateStatusLabel(value);
            }
        });
        ((WritableBookPagesEntryView) getView()).getPrevButton().onAction(() -> {
            ((WritableBookPagesEntryModel) getModel()).selectPrevious();
            refresh();
        });
        ((WritableBookPagesEntryView) getView()).getNextButton().onAction(() -> {
            ((WritableBookPagesEntryModel) getModel()).selectNext();
            refresh();
        });
        ((WritableBookPagesEntryView) getView()).getAddButton().onAction(() -> {
            ((WritableBookPagesEntryModel) getModel()).insertAfterCurrent();
            refresh();
        });
        ((WritableBookPagesEntryView) getView()).getRemoveButton().onAction(() -> {
            ((WritableBookPagesEntryModel) getModel()).removeCurrent();
            refresh();
        });
        ((WritableBookPagesEntryModel) getModel()).pages().addListener(this::refresh);
        ((WritableBookPagesEntryModel) getModel()).selectedIndexProperty().addListener(i -> {
            refresh();
        });
        refresh();
    }

    
    private void refresh() {
        this.updating = true;
        try {
            String text = ((WritableBookPagesEntryModel) getModel()).getSelectedPage();
            TextArea area = ((WritableBookPagesEntryView) getView()).getEditorArea();
            area.setText(text);
            int caret = text == null ? 0 : text.length();
            area.setCursorPosition(caret);
            area.setHighlightPosition(caret);
            updateStatusLabel(text);
            updateButtonStates();
        } finally {
            this.updating = false;
        }
    }

    
    private void updateStatusLabel(String value) {
        int length = value == null ? 0 : value.length();
        int remaining = 1024 - length;
        int index = ((WritableBookPagesEntryModel) getModel()).getSelectedIndex();
        int total = Math.max(1, ((WritableBookPagesEntryModel) getModel()).getPageCount());
        Component status = ModTexts.gui("book_page_indicator").copy().append(Component.literal(" " + (index + 1) + "/" + total).withStyle(ChatFormatting.AQUA)).append(Component.literal(" · ").withStyle(ChatFormatting.DARK_GRAY)).append(ModTexts.gui("book_char_count").copy()).append(Component.literal(" " + length + "/1024").withStyle(remaining >= 0 ? ChatFormatting.GRAY : ChatFormatting.RED));
        ((WritableBookPagesEntryView) getView()).getStatusLabel().setLabel(status);
    }

    
    private void updateButtonStates() {
        int index = ((WritableBookPagesEntryModel) getModel()).getSelectedIndex();
        int total = ((WritableBookPagesEntryModel) getModel()).getPageCount();
        ((WritableBookPagesEntryView) getView()).getPrevButton().setDisable(index <= 0);
        ((WritableBookPagesEntryView) getView()).getNextButton().setDisable(index >= total - 1);
        ((WritableBookPagesEntryView) getView()).getRemoveButton().setDisable(total <= 1);
        ((WritableBookPagesEntryView) getView()).getAddButton().setDisable(total >= 100);
    }

    private boolean isLengthValid(String value) {
        return value == null || value.length() <= 1024;
    }
}
