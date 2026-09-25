package com.github.rinorsi.cadeditor.client.util.texteditor;

import net.minecraft.network.chat.MutableComponent;

import java.util.Objects;

public class ShadowColorFormatting extends Formatting {
    private int argb;

    public ShadowColorFormatting(int start, int end, int argb) {
        super(start, end);
        this.argb = argb;
    }

    public int getArgb() {
        return argb;
    }

    public void setArgb(int argb) {
        this.argb = argb;
    }

    @Override
    public void apply(MutableComponent text) {
        text.withStyle(style -> style.withShadowColor(argb));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ShadowColorFormatting that = (ShadowColorFormatting) o;
        return argb == that.argb;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), argb);
    }
}
