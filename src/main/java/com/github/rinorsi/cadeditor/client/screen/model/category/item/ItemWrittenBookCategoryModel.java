package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.IntegerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.StringEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WrittenBookContent;

public class ItemWrittenBookCategoryModel extends ItemEditorCategoryModel {
    private StringEntryModel titleEntry;
    private StringEntryModel authorEntry;
    private IntegerEntryModel generationEntry;

    public ItemWrittenBookCategoryModel(ItemEditorModel editor) {
        super(ModTexts.gui("written_book"), editor);
    }

    @Override
    protected void setupEntries() {
        WrittenBookContent content = readContent();
        String title = content == null ? "" : content.title().raw();
        String author = content == null ? "" : content.author();
        int generation = content == null ? 0 : content.generation();
        this.titleEntry = new StringEntryModel(this, ModTexts.gui("book_title"), title, value -> {
        });
        this.authorEntry = new StringEntryModel(this, ModTexts.gui("book_author"), author, value -> {
        });
        this.generationEntry = new IntegerEntryModel(this, ModTexts.gui("book_generation"), Math.max(0, Math.min(3, generation)),
                value -> {
                }, value -> value >= 0 && value <= 3);
        getEntries().addAll(this.titleEntry, this.authorEntry, this.generationEntry);
    }

    @Override
    public void apply() {
        super.apply();
        ItemStack stack = getParent().getContext().getItemStack();
        if (stack == null) {
            return;
        }
        WrittenBookContent existing = stack.get(DataComponents.WRITTEN_BOOK_CONTENT);
        WrittenBookContent base;
        if (existing == null) {
            String title = safe(this.titleEntry.getValue());
            if (title.isEmpty() && safe(this.authorEntry.getValue()).isEmpty() && this.generationEntry.getValue() == 0) {
                return;
            }
            base = new WrittenBookContent(Filterable.passThrough(""), "", 0, List.of(), true);
        } else {
            base = existing;
        }
        String title = safe(this.titleEntry.getValue());
        String author = safe(this.authorEntry.getValue());
        int generation = Math.max(0, Math.min(3, this.generationEntry.getValue()));
        WrittenBookContent updated = new WrittenBookContent(Filterable.passThrough(title), author, generation,
                base.pages(), base.resolved());
        stack.set(DataComponents.WRITTEN_BOOK_CONTENT, updated);
    }

    private WrittenBookContent readContent() {
        ItemStack stack = getParent().getContext().getItemStack();
        return stack == null ? null : stack.get(DataComponents.WRITTEN_BOOK_CONTENT);
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}