package com.github.rinorsi.cadeditor.mixin;

import java.util.List;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin({EditBox.class})
public interface TextFieldWidgetMixin {
    @Accessor
    int getDisplayPos();

    @Accessor
    @Mutable
    void setDisplayPos(int i);

    @Accessor
    int getCursorPos();

    @Accessor("cursorPos")
    void setCursorPos(int i);

    @Accessor
    int getHighlightPos();

    @Accessor("highlightPos")
    @Mutable
    void setHighlightPosRaw(int i);

    @Accessor("canLoseFocus")
    boolean getCanLoseFocus();

    @Accessor("value")
    void setRawValue(String str);

    @Accessor
    boolean isBordered();

    @Accessor
    long getFocusedTime();

    @Accessor
    int getMaxLength();

    @Accessor
    List<EditBox.TextFormatter> getFormatters();

    @Accessor
    boolean isCentered();

    @Accessor
    Component getHint();

    @Accessor
    int getTextColor();
}
