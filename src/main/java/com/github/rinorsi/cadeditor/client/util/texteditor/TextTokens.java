package com.github.rinorsi.cadeditor.client.util.texteditor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.ObjectContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.chat.contents.objects.AtlasSprite;
import net.minecraft.network.chat.contents.objects.ObjectInfo;
import net.minecraft.network.chat.contents.objects.PlayerSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.Base64;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public final class TextTokens {
    public static final String TOKEN_START = "[cad:";

    private static final String TYPE_HEAD = "head";
    private static final String TYPE_HEAD_UUID = "head_uuid";
    private static final String TYPE_HEAD_TEXTURE = "head_texture";
    private static final String TYPE_SPRITE = "sprite";
    private static final String TYPE_TRANSLATE = "translate";

    private static final Pattern PLAYER_NAME = Pattern.compile("[A-Za-z0-9_]{1,16}");

    private TextTokens() {
    }

    public static int tokenLengthAt(String input, int index) {
        if (input == null || index < 0 || index + TOKEN_START.length() > input.length()
                || !input.startsWith(TOKEN_START, index)) {
            return 0;
        }
        int end = input.indexOf(']', index + TOKEN_START.length());
        return end < 0 ? 0 : end - index + 1;
    }

    public static int nextTokenStart(String input, int from) {
        for (int i = Math.max(0, from); i < input.length(); i++) {
            if (tokenLengthAt(input, i) > 0) {
                return i;
            }
        }
        return -1;
    }

    public static boolean isToken(String text) {
        return text != null && tokenLengthAt(text, 0) == text.length();
    }

    public static MutableComponent buildComponent(String tokenText) {
        if (!isToken(tokenText)) {
            return null;
        }
        String body = tokenText.substring(TOKEN_START.length(), tokenText.length() - 1);
        int sep = body.indexOf(':');
        if (sep <= 0) {
            return null;
        }
        String type = body.substring(0, sep);
        String rest = body.substring(sep + 1);
        return switch (type) {
            case TYPE_HEAD -> buildHead(rest);
            case TYPE_HEAD_UUID -> buildHeadUuid(rest);
            case TYPE_HEAD_TEXTURE -> buildHeadTexture(rest);
            case TYPE_SPRITE -> buildSprite(rest);
            case TYPE_TRANSLATE -> buildTranslation(rest);
            default -> null;
        };
    }

    public static String contentsToToken(Component component) {
        if (component.getContents() instanceof TranslatableContents translatable) {
            String fallback = translatable.getFallback();
            return TOKEN_START + TYPE_TRANSLATE + ":" + translatable.getKey()
                    + (fallback != null ? "|" + fallback : "") + "]";
        }
        if (component.getContents() instanceof ObjectContents object) {
            return objectInfoToToken(object.contents());
        }
        return null;
    }

    private static String objectInfoToToken(ObjectInfo info) {
        if (info instanceof PlayerSprite playerSprite) {
            ResolvableProfile profile = playerSprite.player();
            String hat = playerSprite.hat() ? "true" : "false";
            String texture = extractTextureValue(profile);
            if (texture != null) {
                String signature = extractTextureSignature(profile);
                return TOKEN_START + TYPE_HEAD_TEXTURE + ":" + texture + ":" + (signature == null ? "" : signature) + ":" + hat + "]";
            }
            String name = profile.name().orElse(null);
            if (name != null && PLAYER_NAME.matcher(name).matches()) {
                return TOKEN_START + TYPE_HEAD + ":" + name + ":" + hat + "]";
            }
            UUID id = profile.partialProfile().id();
            if (id != null) {
                return TOKEN_START + TYPE_HEAD_UUID + ":" + id + ":" + hat + "]";
            }
            return null;
        }
        if (info instanceof AtlasSprite atlasSprite) {
            return TOKEN_START + TYPE_SPRITE + ":" + atlasSprite.atlas() + "|" + atlasSprite.sprite() + "]";
        }
        return null;
    }

    public static String buildHeadToken(String playerName, boolean hat) {
        return TOKEN_START + TYPE_HEAD + ":" + playerName + ":" + (hat ? "true" : "false") + "]";
    }

    public static String buildHeadUuidToken(String uuid, boolean hat) {
        return TOKEN_START + TYPE_HEAD_UUID + ":" + uuid + ":" + (hat ? "true" : "false") + "]";
    }

    public static String buildHeadTextureToken(String textureValue, String textureSignature, boolean hat) {
        return TOKEN_START + TYPE_HEAD_TEXTURE + ":" + textureValue + ":"
                + (textureSignature == null ? "" : textureSignature) + ":" + (hat ? "true" : "false") + "]";
    }

    public static String buildSpriteToken(String atlas, String sprite) {
        return TOKEN_START + TYPE_SPRITE + ":" + atlas + "|" + sprite + "]";
    }

    public static String buildTranslationToken(String key, String fallback) {
        return TOKEN_START + TYPE_TRANSLATE + ":" + key + (fallback == null || fallback.isEmpty() ? "" : "|" + fallback) + "]";
    }

    private static MutableComponent buildHead(String rest) {
        int idx = rest.lastIndexOf(':');
        if (idx <= 0) {
            return null;
        }
        Boolean hat = parseHat(rest.substring(idx + 1));
        if (hat == null) {
            return null;
        }
        String name = rest.substring(0, idx);
        if (!PLAYER_NAME.matcher(name).matches()) {
            return null;
        }
        return Component.object(new PlayerSprite(ResolvableProfile.createUnresolved(name), hat));
    }

    private static MutableComponent buildHeadUuid(String rest) {
        int idx = rest.lastIndexOf(':');
        if (idx <= 0) {
            return null;
        }
        Boolean hat = parseHat(rest.substring(idx + 1));
        if (hat == null) {
            return null;
        }
        UUID uuid;
        try {
            uuid = UUID.fromString(rest.substring(0, idx));
        } catch (IllegalArgumentException e) {
            return null;
        }
        return Component.object(new PlayerSprite(ResolvableProfile.createUnresolved(uuid), hat));
    }

    private static MutableComponent buildHeadTexture(String rest) {
        int hatIdx = rest.lastIndexOf(':');
        if (hatIdx <= 0) {
            return null;
        }
        Boolean hat = parseHat(rest.substring(hatIdx + 1));
        if (hat == null) {
            return null;
        }
        String front = rest.substring(0, hatIdx);
        int valueIdx = front.indexOf(':');
        if (valueIdx < 0) {
            return null;
        }
        String value = front.substring(0, valueIdx);
        String signature = front.substring(valueIdx + 1);
        if (!isValidTextureValue(value)) {
            return null;
        }
        ResolvableProfile profile = createTextureProfile(value, signature.isEmpty() ? null : signature);
        if (profile == null) {
            return null;
        }
        return Component.object(new PlayerSprite(profile, hat));
    }

    private static MutableComponent buildSprite(String rest) {
        int sep = rest.indexOf('|');
        if (sep <= 0 || sep == rest.length() - 1) {
            return null;
        }
        Identifier atlas = Identifier.tryParse(rest.substring(0, sep));
        Identifier sprite = Identifier.tryParse(rest.substring(sep + 1));
        if (atlas == null || sprite == null) {
            return null;
        }
        return Component.object(new AtlasSprite(atlas, sprite));
    }

    private static MutableComponent buildTranslation(String rest) {
        if (rest.isEmpty()) {
            return null;
        }
        int sep = rest.indexOf('|');
        String key = sep >= 0 ? rest.substring(0, sep) : rest;
        String fallback = sep >= 0 ? rest.substring(sep + 1) : null;
        if (key.isEmpty() || key.indexOf(']') >= 0 || (fallback != null && fallback.indexOf(']') >= 0)) {
            return null;
        }
        return fallback == null ? Component.translatable(key) : Component.translatableWithFallback(key, fallback);
    }

    private static Boolean parseHat(String value) {
        if (value.equals("true")) {
            return Boolean.TRUE;
        }
        if (value.equals("false")) {
            return Boolean.FALSE;
        }
        return null;
    }

    private static boolean isValidTextureValue(String value) {
        if (value.isEmpty() || value.length() % 4 != 0) {
            return false;
        }
        try {
            Base64.getDecoder().decode(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static ResolvableProfile createTextureProfile(String value, String signature) {
        CompoundTag property = new CompoundTag();
        property.putString("name", "textures");
        property.putString("value", value);
        if (signature != null) {
            property.putString("signature", signature);
        }
        ListTag properties = new ListTag();
        properties.add(property);
        CompoundTag profileTag = new CompoundTag();
        profileTag.put("properties", properties);
        return ResolvableProfile.CODEC.parse(NbtOps.INSTANCE, profileTag).result().orElse(null);
    }

    private static String extractTextureValue(ResolvableProfile profile) {
        return profile.partialProfile().properties().get("textures").stream()
                .map(property -> Objects.toString(property.value(), ""))
                .filter(value -> !value.isBlank())
                .findFirst()
                .orElse(null);
    }

    private static String extractTextureSignature(ResolvableProfile profile) {
        return profile.partialProfile().properties().get("textures").stream()
                .map(property -> Objects.toString(property.signature(), ""))
                .filter(signature -> !signature.isBlank())
                .findFirst()
                .orElse(null);
    }
}
