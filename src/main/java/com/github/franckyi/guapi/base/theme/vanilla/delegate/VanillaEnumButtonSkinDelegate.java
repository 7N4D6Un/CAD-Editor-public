package com.github.franckyi.guapi.base.theme.vanilla.delegate;

import com.github.franckyi.guapi.api.node.EnumButton;
import net.minecraft.client.input.MouseButtonInfo;

public class VanillaEnumButtonSkinDelegate extends VanillaButtonSkinDelegate<EnumButton<?>> {
    public VanillaEnumButtonSkinDelegate(EnumButton<?> node) {
        super(node);
    }

    protected boolean isValidClickButton(MouseButtonInfo buttonInfo) {
        return super.isValidClickButton(buttonInfo) || buttonInfo.button() == 1;
    }
}
