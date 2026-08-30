package com.github.rinorsi.cadeditor.client.screen.skin;

import com.github.franckyi.guapi.base.theme.vanilla.delegate.VanillaTextAreaSkinDelegate;
import com.github.rinorsi.cadeditor.client.screen.widget.SyntaxHighlightingTextArea;
import com.github.rinorsi.cadeditor.client.util.texteditor.MultiLineEditBoxHighlightAccess;
import java.lang.reflect.Field;
import net.minecraft.client.gui.components.MultiLineEditBox;


public class SyntaxHighlightingTextAreaSkinDelegate extends VanillaTextAreaSkinDelegate<SyntaxHighlightingTextArea> {
    public SyntaxHighlightingTextAreaSkinDelegate(SyntaxHighlightingTextArea node) {
        super(node);
        ((MultiLineEditBoxHighlightAccess) (Object) getTextBox()).cadeditor$setHighlighter(node.getHighlighter());
    }

    public MultiLineEditBox getTextBox() {
        try {
            Field field = VanillaTextAreaSkinDelegate.class.getDeclaredField("textBox");
            field.setAccessible(true);
            return (MultiLineEditBox) field.get(this);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
