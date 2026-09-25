package com.github.rinorsi.cadeditor.client.logic;

import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.context.EntityEditorContext;
import com.github.rinorsi.cadeditor.client.util.SnbtHelper;
import com.github.rinorsi.cadeditor.common.EditorType;
import com.github.rinorsi.cadeditor.common.ModTexts;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ClientVanillaDataFetcher {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String ENTITY_QUERY_KEY = "commands.data.entity.query";
    private static final int NBT_ARG_INDEX = 1;
    private static final int TIMEOUT_TICKS = 60;

    private static PendingRequest pending;
    private static int timeoutTicks;

    private ClientVanillaDataFetcher() {
    }

    public static boolean hasPendingRequest() {
        return pending != null;
    }

    public static void requestEntityData(EditorType editorType, Entity entity, CompoundTag fallbackTag) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() == null) {
            openEditor(editorType, fallbackTag);
            return;
        }
        pending = new PendingRequest(editorType, fallbackTag, EntityType.getKey(entity.getType()).toString());
        timeoutTicks = TIMEOUT_TICKS;
        minecraft.getConnection().sendCommand("data get entity " + entity.getStringUUID());
    }

    public static boolean onGameMessage(Component message) {
        if (pending == null) {
            return false;
        }
        if (!(message.getContents() instanceof TranslatableContents contents)
                || !ENTITY_QUERY_KEY.equals(contents.getKey())) {
            return false;
        }
        PendingRequest request = pending;
        pending = null;
        CompoundTag tag = parseEntityQueryArgs(contents.getArgs());
        if (tag == null) {
            openEditor(request.editorType, request.fallbackTag);
            return true;
        }
        tag.putString("id", request.entityTypeId);
        openEditor(request.editorType, tag);
        return true;
    }

    public static void tick() {
        if (pending == null || --timeoutTicks > 0) {
            return;
        }
        PendingRequest request = pending;
        pending = null;
        openEditor(request.editorType, request.fallbackTag);
    }

    public static void clear() {
        pending = null;
    }

    private static CompoundTag parseEntityQueryArgs(Object[] args) {
        if (args.length <= NBT_ARG_INDEX || !(args[NBT_ARG_INDEX] instanceof Component nbtComponent)) {
            return null;
        }
        try {
            return SnbtHelper.parse(nbtComponent.getString());
        } catch (CommandSyntaxException e) {
            LOGGER.error("Failed to parse entity data from vanilla command output", e);
            return null;
        }
    }

    private static void openEditor(EditorType editorType, CompoundTag tag) {
        ModScreenHandler.openEditor(editorType, new EntityEditorContext(tag, ModTexts.errorServerModRequiredEntity(), true, null));
    }

    private static final class PendingRequest {
        private final EditorType editorType;
        private final CompoundTag fallbackTag;
        private final String entityTypeId;

        private PendingRequest(EditorType editorType, CompoundTag fallbackTag, String entityTypeId) {
            this.editorType = editorType;
            this.fallbackTag = fallbackTag;
            this.entityTypeId = entityTypeId;
        }
    }
}
