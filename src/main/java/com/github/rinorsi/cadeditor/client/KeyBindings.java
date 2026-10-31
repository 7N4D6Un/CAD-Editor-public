package com.github.rinorsi.cadeditor.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;


public final class KeyBindings {
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("cadeditor", "cadeditor"));
    private static final KeyMapping editorKey = new KeyMapping("cadeditor.key.editor", 12, CATEGORY);
    private static final KeyMapping nbtEditorKey = new KeyMapping("cadeditor.key.nbt_editor", 17, CATEGORY);
    private static final KeyMapping snbtEditorKey = new KeyMapping("cadeditor.key.snbt_editor", 21, CATEGORY);
    private static final KeyMapping vaultKey = new KeyMapping("cadeditor.key.vault", 13, CATEGORY);

    public static KeyMapping getEditorKey() {
        return editorKey;
    }

    public static KeyMapping getNBTEditorKey() {
        return nbtEditorKey;
    }

    public static KeyMapping getSNBTEditorKey() {
        return snbtEditorKey;
    }

    public static KeyMapping getVaultKey() {
        return vaultKey;
    }
}
