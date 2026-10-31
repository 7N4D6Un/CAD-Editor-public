package com.github.rinorsi.cadeditor.client.screen.model.category;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.util.ComponentJsonHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.SignText;

public final class SignNbtHelper {
    public static final String KEY_FRONT_TEXT = "front_text";
    public static final String KEY_BACK_TEXT = "back_text";
    public static final String KEY_IS_WAXED = "is_waxed";
    public static final String KEY_MESSAGES = "messages";
    public static final String KEY_COLOR = "color";
    public static final String KEY_HAS_GLOWING_TEXT = "has_glowing_text";
    public static final int LINES = 4;

    private SignNbtHelper() {
    }

    public static CompoundTag readFace(CompoundTag root, String key) {
        if (root != null) {
            CompoundTag face = root.getCompound(key).orElse(null);
            if (face != null) {
                return face;
            }
        }
        return new CompoundTag();
    }

    public static MutableComponent readLine(CompoundTag face, int index) {
        ListTag messages = face.getListOrEmpty(KEY_MESSAGES);
        if (index < messages.size()) {
            MutableComponent decoded = ComponentJsonHelper.decode(messages.get(index), ClientUtil.registryAccess());
            if (decoded != null) {
                return decoded;
            }
        }
        return Component.empty();
    }

    public static void writeLine(CompoundTag face, int index, MutableComponent line) {
        ListTag messages = face.getListOrEmpty(KEY_MESSAGES);
        while (messages.size() < LINES) {
            messages.add(net.minecraft.nbt.StringTag.valueOf("\"\""));
        }
        Tag encoded = ComponentJsonHelper.encodeToTag(line, ClientUtil.registryAccess());
        if (encoded == null) {
            encoded = net.minecraft.nbt.StringTag.valueOf("\"\"");
        }
        while (messages.size() < index + 1) {
            messages.add(net.minecraft.nbt.StringTag.valueOf("\"\""));
        }
        messages.set(index, encoded);
        face.put(KEY_MESSAGES, messages);
    }

    public static DyeColor readColor(CompoundTag face) {
        return DyeColor.byName(face.getStringOr(KEY_COLOR, "black"), DyeColor.BLACK);
    }

    public static boolean readGlowing(CompoundTag face) {
        return face.getBooleanOr(KEY_HAS_GLOWING_TEXT, false);
    }

    public static boolean readWaxed(CompoundTag root) {
        return root != null && root.getBooleanOr(KEY_IS_WAXED, false);
    }

    public static CompoundTag fromSignText(SignText text) {
        CompoundTag face = new CompoundTag();
        ListTag messages = new ListTag();
        List<Component> lines = text.getMessages(false);
        for (int index = 0; index < LINES; index++) {
            Component line = index < lines.size() ? lines.get(index) : Component.empty();
            Tag encoded = ComponentJsonHelper.encodeToTag(line, ClientUtil.registryAccess());
            messages.add(encoded == null ? net.minecraft.nbt.StringTag.valueOf("\"\"") : encoded);
        }
        face.put(KEY_MESSAGES, messages);
        face.putString(KEY_COLOR, text.getColor().getName());
        face.putBoolean(KEY_HAS_GLOWING_TEXT, text.hasGlowingText());
        return face;
    }

    public static SignText toSignText(CompoundTag face) {
        List<Component> messages = new ArrayList<>(LINES);
        for (int index = 0; index < LINES; index++) {
            messages.add(readLine(face, index));
        }
        return new SignText(messages, messages, readColor(face), readGlowing(face));
    }
}
