package com.github.rinorsi.cadeditor.client.util.texteditor;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;

import java.util.Objects;

public class FontFormatting extends Formatting {
    private String fontId;

    public FontFormatting(int start, int end, String fontId) {
        super(start, end);
        this.fontId = fontId;
    }

    public String getFontId() {
        return fontId;
    }

    public void setFontId(String fontId) {
        this.fontId = fontId;
    }

    @Override
    public void apply(MutableComponent text) {
        Identifier id = Identifier.tryParse(fontId);
        if (id != null) {
            text.withStyle(style -> style.withFont(new FontDescription.Resource(id)));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FontFormatting that = (FontFormatting) o;
        return Objects.equals(fontId, that.fontId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fontId);
    }
}
