package com.github.rinorsi.cadeditor.client.context;

import com.github.rinorsi.cadeditor.client.ClientCache;
import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.Vault;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntitySpawnRequest;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class EntityEditorContext extends EditorContext<EntityEditorContext> {
    private static final Logger LOGGER = LogManager.getLogger();
    private Entity entity;
    private final int originalEntityId;

    public EntityEditorContext(CompoundTag tag, Component errorTooltip, boolean canSaveToVault, Consumer<EntityEditorContext> action) {
        super(tag, errorTooltip, canSaveToVault, action);
        this.entity = createEntity(tag);
        this.originalEntityId = tag.getInt("UUID").orElse(-1);
        if (this.entity == null) {
            this.canSaveToVault = false;
        }
    }

    public EntityEditorContext(CompoundTag tag, Component errorTooltip, boolean canSaveToVault, Consumer<EntityEditorContext> action, int entityId) {
        super(tag, errorTooltip, canSaveToVault, action);
        this.entity = createEntity(tag);
        this.originalEntityId = entityId;
        if (this.entity == null) {
            this.canSaveToVault = false;
        }
    }

    @Override 
    public void setTag(CompoundTag tag) {
        super.setTag(tag);
        LOGGER.info("[EntityEditorContext] setTag called, tag: {}", tag);
        if (tag != null) {
            Entity newEntity = createEntity(tag);
            if (newEntity != null) {
                this.entity = newEntity;
                LOGGER.info("[EntityEditorContext] Entity updated successfully: {}", newEntity.getType());
            } else {
                LOGGER.warn("[EntityEditorContext] Failed to create entity from tag");
            }
        }
    }

    public Entity getEntity() {
        return this.entity;
    }

    public int getOriginalEntityId() {
        return this.originalEntityId;
    }

    public void applyEntityChangesToWorld() {
        if (this.originalEntityId == -1) {
            LOGGER.warn("[EntityEditorContext] No original entity ID, cannot apply changes to world");
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            LOGGER.warn("[EntityEditorContext] No client level, cannot apply changes");
            return;
        }
        Entity existingEntity = mc.level.getEntity(this.originalEntityId);
        if (existingEntity == null) {
            LOGGER.warn("[EntityEditorContext] Cannot find entity {} in world", this.originalEntityId);
            return;
        }
        LOGGER.info("[EntityEditorContext] Applying changes to entity: {}", existingEntity.getType());
        try {
            ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, mc.level.registryAccess(), getTag());
            existingEntity.load(input);
            LOGGER.info("[EntityEditorContext] Entity updated successfully in world");
        } catch (Exception e) {
            LOGGER.error("[EntityEditorContext] Failed to update entity in world", e);
        }
    }

    @Override 
    public void saveToVault() {
        Vault.getInstance().saveEntity(getTag());
        ClientUtil.showMessage(ModTexts.Messages.successSavedVault(ModTexts.ENTITY));
    }

    @Override 
    public MutableComponent getTargetName() {
        return ModTexts.ENTITY;
    }

    @Override 
    public String getCommandName() {
        return "/summon";
    }

    @Override 
    protected String getCommand() {
        return String.format("/summon %s ~ ~ ~ %s", getTag().getString("id").orElse(""), getSimpleTag());
    }

    @Override 
    public List<String> getStringSuggestions(List<String> path) {
        List<String> suggestions = super.getStringSuggestions(path);
        if (!suggestions.isEmpty()) {
            return suggestions;
        }
        if (path == null || path.isEmpty()) {
            return List.of();
        }
        String key = lastKey(path);
        if (key == null) {
            return List.of();
        }
        String parent = previousNamedKey(path, 1);
        String grandParent = previousNamedKey(path, 2);
        if ("id".equals(key)) {
            if (path.size() == 1) {
                return ClientCache.getEntitySuggestions();
            }
            if (isItemSlot(parent, grandParent)) {
                return ClientCache.getItemSuggestions();
            }
            if (isEffectContainer(parent, grandParent)) {
                return ClientCache.getEffectSuggestions();
            }
        }
        if (equalsAnyIgnoreCase(key, "item", "Item")) {
            return ClientCache.getItemSuggestions();
        }
        if (equalsAnyIgnoreCase(key, "potion")) {
            return ClientCache.getPotionSuggestions();
        }
        if (equalsAnyIgnoreCase(key, "effect")) {
            return ClientCache.getEffectSuggestions();
        }
        return List.of();
    }

    private static boolean isItemSlot(String parent, String grandParent) {
        return equalsAnyIgnoreCase(parent, "HandItems", "ArmorItems", "Items", "Inventory", "item", "Item", "SaddleItem") || (equalsAnyIgnoreCase(parent, "stack") && equalsAnyIgnoreCase(grandParent, "minecraft:equipment"));
    }

    private static boolean isEffectContainer(String parent, String grandParent) {
        return equalsAnyIgnoreCase(parent, "ActiveEffects", "effects") || equalsAnyIgnoreCase(grandParent, "ActiveEffects");
    }

    private CompoundTag getSimpleTag() {
        CompoundTag tag = getTag().copy();
        tag.remove("UUID");
        tag.remove("Pos");
        tag.remove("Rotation");
        return tag;
    }

    private Entity createEntity(CompoundTag tag) {
        return EntityType.loadEntityRecursive(tag, Minecraft.getInstance().level, new EntitySpawnRequest(EntitySpawnReason.COMMAND, false), entity -> entity);
    }

    public boolean replaceEntity(CompoundTag tag) {
        Entity newEntity = createEntity(tag);
        if (newEntity == null) {
            return false;
        }
        setTag(tag);
        this.entity = newEntity;
        return true;
    }
}
