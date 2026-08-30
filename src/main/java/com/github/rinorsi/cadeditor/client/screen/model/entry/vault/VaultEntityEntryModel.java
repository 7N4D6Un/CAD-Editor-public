package com.github.rinorsi.cadeditor.client.screen.model.entry.vault;

import com.github.franckyi.databindings.api.ObjectProperty;
import com.github.franckyi.databindings.api.ObservableObjectValue;
import com.github.rinorsi.cadeditor.client.Vault;
import com.github.rinorsi.cadeditor.client.screen.model.category.vault.VaultEntityCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntryModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntitySpawnRequest;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;


public class VaultEntityEntryModel extends EntryModel {
    private final ObjectProperty<CompoundTag> tagProperty;
    private final ObservableObjectValue<Entity> entityProperty;

    public VaultEntityEntryModel(VaultEntityCategoryModel parent, CompoundTag tag) {
        super(parent);
        this.tagProperty = ObjectProperty.create(tag);
        this.entityProperty = this.tagProperty.map(entityTag -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (entityTag == null || level == null) {
                return null;
            }
            ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), entityTag);
            return (Entity) EntityType.create(input, level, new EntitySpawnRequest(EntitySpawnReason.LOAD, false)).orElse(null);
        });
    }

    public CompoundTag getData() {
        return tagProperty().getValue();
    }

    public ObjectProperty<CompoundTag> tagProperty() {
        return this.tagProperty;
    }

    public void setData(CompoundTag value) {
        tagProperty().setValue(value);
    }

    public Entity getEntity() {
        return entityProperty().getValue();
    }

    public ObservableObjectValue<Entity> entityProperty() {
        return this.entityProperty;
    }

    @Override 
    public void apply() {
        Vault.getInstance().saveEntity(getData());
    }

    @Override 
    public EntryModel.Type getType() {
        return EntryModel.Type.VAULT_ENTITY;
    }
}
