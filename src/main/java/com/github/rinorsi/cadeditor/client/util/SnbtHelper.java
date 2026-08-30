package com.github.rinorsi.cadeditor.client.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;

public final class SnbtHelper {
    private SnbtHelper() {}

    public static CompoundTag parse(String input) throws CommandSyntaxException {
        return TagParser.parseCompoundFully(input);
    }
}
