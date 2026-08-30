package com.github.rinorsi.cadeditor.mixin;

import net.minecraft.client.gui.components.AbstractTextAreaWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;


@Mixin({AbstractTextAreaWidget.class})
public interface AbstractTextAreaWidgetMixin {
    @Invoker
    int invokeGetInnerLeft();

    @Invoker
    int invokeGetInnerTop();

    @Invoker
    int invokeTotalInnerPadding();

    @Invoker
    int invokeGetInnerHeight();

    @Invoker
    int invokeInnerPadding();

    @Invoker
    boolean invokeWithinContentAreaTopBottom(int top, int bottom);
}
