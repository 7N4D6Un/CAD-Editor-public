package com.github.rinorsi.cadeditor.client.screen.widget;

import com.github.franckyi.guapi.api.node.Node;
import com.github.franckyi.guapi.api.theme.Skin;
import com.github.franckyi.guapi.base.node.AbstractNode;
import com.github.franckyi.guapi.base.node.TextAreaImpl;
import com.github.franckyi.guapi.base.theme.vanilla.AbstractVanillaWidgetSkin;
import com.github.rinorsi.cadeditor.client.screen.skin.SyntaxHighlightingTextAreaSkin;
import com.github.rinorsi.cadeditor.client.screen.skin.SyntaxHighlightingTextAreaSkinDelegate;
import com.github.rinorsi.cadeditor.client.util.texteditor.SNBTSyntaxHighlighter;

import java.lang.reflect.Field;

public class SyntaxHighlightingTextArea extends TextAreaImpl {
    private final SNBTSyntaxHighlighter highlighter = new SNBTSyntaxHighlighter();

    public SyntaxHighlightingTextArea() {
        this("");
    }

    @SuppressWarnings({"unchecked", "this-escape"})
    public SyntaxHighlightingTextArea(String value) {
        super(value);
        highlighter.setSource(value);
        textProperty().addListener(highlighter::setSource);
        skin = (Skin<? super Node>) (Skin<?>) new SyntaxHighlightingTextAreaSkin(this);
    }

    @Override
    protected Class<?> getType() {
        return SyntaxHighlightingTextArea.class;
    }

    public SNBTSyntaxHighlighter getHighlighter() {
        return highlighter;
    }

    public String getTextFromTextBox() {
        try {
            Field skinField = AbstractNode.class.getDeclaredField("skin");
            skinField.setAccessible(true);
            Object skinObj = skinField.get(this);
            SyntaxHighlightingTextAreaSkin skin = (SyntaxHighlightingTextAreaSkin) skinObj;
            Field widgetField = AbstractVanillaWidgetSkin.class.getDeclaredField("widget");
            widgetField.setAccessible(true);
            SyntaxHighlightingTextAreaSkinDelegate delegate = (SyntaxHighlightingTextAreaSkinDelegate) widgetField.get(skin);
            return delegate.getTextBox().getValue();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
