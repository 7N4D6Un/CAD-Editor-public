package com.github.rinorsi.cadeditor.client.screen.model.entry.item;

import com.github.franckyi.databindings.api.IntegerProperty;
import com.github.franckyi.databindings.api.ObservableList;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemEditorCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntryModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;


public class WritableBookPagesEntryModel extends EntryModel {
    private final ObservableList<String> pages;
    private final List<String> defaultPages;
    private final IntegerProperty selectedIndexProperty;
    private final Consumer<List<String>> onApply;

    public WritableBookPagesEntryModel(ItemEditorCategoryModel category, List<String> initialPages, Consumer<List<String>> onApply) {
        super(category);
        this.pages = ObservableList.create();
        this.defaultPages = new ArrayList<>();
        this.selectedIndexProperty = IntegerProperty.create(0);
        this.onApply = Objects.requireNonNull(onApply, "onApply");
        if (initialPages == null || initialPages.isEmpty()) {
            this.pages.add("");
        } else {
            initialPages.stream().limit(100L).forEach(this.pages::add);
        }
        this.defaultPages.addAll(this.pages);
        ensureValidSelection();
    }

    public ObservableList<String> pages() {
        return this.pages;
    }

    public int getSelectedIndex() {
        return selectedIndexProperty().getValue();
    }

    public void setSelectedIndex(int index) {
        int clamped = Math.max(0, Math.min(index, Math.max(this.pages.size() - 1, 0)));
        selectedIndexProperty().setValue(clamped);
    }

    public IntegerProperty selectedIndexProperty() {
        return this.selectedIndexProperty;
    }

    public String getSelectedPage() {
        int index = getSelectedIndex();
        return (index < 0 || index >= this.pages.size()) ? "" : this.pages.get(index);
    }

    public void setSelectedPage(String text) {
        int index = getSelectedIndex();
        if (index < 0 || index >= this.pages.size()) {
            return;
        }
        String trimmed = sanitizePage(text);
        this.pages.set(index, trimmed);
    }

    public void selectPrevious() {
        setSelectedIndex(getSelectedIndex() - 1);
    }

    public void selectNext() {
        setSelectedIndex(getSelectedIndex() + 1);
    }

    public void insertAfterCurrent() {
        if (this.pages.size() >= 100) {
            return;
        }
        int insertAt = Math.min(getSelectedIndex() + 1, this.pages.size());
        this.pages.add(insertAt, "");
        setSelectedIndex(insertAt);
    }

    public void removeCurrent() {
        if (this.pages.size() <= 1) {
            this.pages.set(0, "");
            setSelectedIndex(0);
            return;
        }
        int index = getSelectedIndex();
        if (index < 0 || index >= this.pages.size()) {
            return;
        }
        this.pages.remove(index);
        setSelectedIndex(Math.min(index, this.pages.size() - 1));
    }

    public int getPageCount() {
        return this.pages.size();
    }

    @Override 
    public void apply() {
        this.onApply.accept(List.copyOf(this.pages));
    }

    @Override 
    public void reset() {
        this.pages.clear();
        this.pages.addAll(this.defaultPages);
        ensureValidSelection();
    }

    @Override 
    public EntryModel.Type getType() {
        return EntryModel.Type.WRITABLE_BOOK_PAGES;
    }

    private void ensureValidSelection() {
        if (this.pages.isEmpty()) {
            this.pages.add("");
        }
        int clamped = Math.max(0, Math.min(this.selectedIndexProperty.getValue(), this.pages.size() - 1));
        this.selectedIndexProperty.setValue(clamped);
    }

    private static String sanitizePage(String text) {
        String value = text == null ? "" : text;
        if (value.length() > 1024) {
            return value.substring(0, 1024);
        }
        return value;
    }
}
