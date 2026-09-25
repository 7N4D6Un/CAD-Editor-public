package com.github.rinorsi.cadeditor.client.screen.model.category.entity;

import com.github.rinorsi.cadeditor.client.screen.model.EntityEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.ActionEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EnumEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.StringEntryModel;
import com.github.rinorsi.cadeditor.client.util.NbtUuidHelper;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.DyeColor;


public class EntityTamingCategoryModel extends EntityCategoryModel {
    private BooleanEntryModel tameEntry;
    private BooleanEntryModel sittingEntry;
    private StringEntryModel ownerUuidEntry;

    public EntityTamingCategoryModel(EntityEditorModel model) {
        super(ModTexts.ENTITY_TAMING, model);
    }

    @Override
    protected void setupEntries() {
        CompoundTag data = getData();
        if (data == null) {
            return;
        }
        boolean supportsSitting = getEntity() instanceof TamableAnimal || getEntity() instanceof Fox;
        boolean tamableAnimal = getEntity() instanceof TamableAnimal;
        boolean tame = tamableAnimal ? data.contains("Owner") : data.getBooleanOr("Tame", false);
        String ownerUuid = readOwnerUuid(data);
        this.tameEntry = new BooleanEntryModel(this, ModTexts.TAME, tame, value -> {
        });
        this.ownerUuidEntry = new StringEntryModel(this, ModTexts.OWNER_UUID, ownerUuid, value -> {
        });
        getEntries().add(this.tameEntry);
        getEntries().add(this.ownerUuidEntry);
        getEntries().add(new ActionEntryModel(this, ModTexts.USE_SELF_UUID, this::useClientUuid));
        if (supportsSitting) {
            this.sittingEntry = new BooleanEntryModel(this, ModTexts.SITTING, data.getBooleanOr("Sitting", false), value -> {
            });
            getEntries().add(this.sittingEntry);
        }
        if ((getEntity() instanceof Wolf || getEntity() instanceof Cat) && tame) {
            getEntries().add(new EnumEntryModel<>(this, ModTexts.COLLAR_COLOR, DyeColor.values(), readCollarColor(data),
                    this::setCollarColor).withTextFactory(color -> Component.literal(capitalize(color.getName()))));
        }
    }

    private static String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    private static DyeColor readCollarColor(CompoundTag data) {
        return DyeColor.byId(data.getByteOr("CollarColor", (byte) DyeColor.RED.getId()));
    }

    private void setCollarColor(DyeColor color) {
        if (color != null) {
            getData().putByte("CollarColor", (byte) color.getId());
        }
    }

    private void useClientUuid() {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) {
            return;
        }
        UUID uuid = localPlayer.getUUID();
        this.ownerUuidEntry.setValue(uuid.toString());
        this.tameEntry.setValue(true);
    }

    private String readOwnerUuid(CompoundTag data) {
        UUID uuid = NbtUuidHelper.getUuid(data, "OwnerUUID");
        if (uuid != null) {
            return uuid.toString();
        }
        UUID ownerUuid = NbtUuidHelper.getUuid(data, "Owner");
        if (ownerUuid != null) {
            return ownerUuid.toString();
        }
        if (data.getLong("OwnerUUIDMost").isPresent() && data.getLong("OwnerUUIDLeast").isPresent()) {
            long most = data.getLongOr("OwnerUUIDMost", 0L);
            long least = data.getLongOr("OwnerUUIDLeast", 0L);
            return new UUID(most, least).toString();
        }
        String ownerUuidLegacy = data.getString("OwnerUUID").orElse("");
        if (!ownerUuidLegacy.isBlank()) {
            return ownerUuidLegacy;
        }
        String owner = data.getString("Owner").orElse("");
        if (isUuidString(owner)) {
            return owner;
        }
        return "";
    }

    private static boolean isUuidString(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public void apply() {
        super.apply();
        CompoundTag data = getData();
        if (data == null) {
            return;
        }
        boolean tamableAnimal = getEntity() instanceof TamableAnimal;
        boolean wantsTame = Boolean.TRUE.equals(this.tameEntry.getValue());
        String ownerUuid = normalize(this.ownerUuidEntry.getValue());

        if (tamableAnimal) {
            data.remove("Tame");
            if (!wantsTame) {
                removeOwnerData(data);
            } else if (!ownerUuid.isEmpty()) {
                applyOwnerUuid(data, ownerUuid);
            } else if (!data.contains("Owner")) {
                removeOwnerData(data);
            }
        } else {
            if (wantsTame) {
                applyTameState(data, true);
                if (!ownerUuid.isEmpty()) {
                    applyOwnerUuid(data, ownerUuid);
                }
            } else {
                applyTameState(data, false);
                removeOwnerData(data);
            }
        }

        if (this.sittingEntry != null) {
            if (Boolean.TRUE.equals(this.sittingEntry.getValue())) {
                data.putBoolean("Sitting", true);
            } else {
                removeBooleanTag(data, "Sitting");
            }
        }
    }

    private void applyTameState(CompoundTag data, boolean tame) {
        if (tame) {
            data.putBoolean("Tame", true);
        } else {
            data.remove("Tame");
        }
    }

    private void applyOwnerUuid(CompoundTag data, String ownerUuid) {
        removeOwnerUuid(data);
        if (isUuidString(ownerUuid)) {
            UUID uuid = UUID.fromString(ownerUuid);
            NbtUuidHelper.putUuid(data, "OwnerUUID", uuid);
            NbtUuidHelper.putUuid(data, "Owner", uuid);
            return;
        }
        data.putString("OwnerUUID", ownerUuid);
    }

    private void removeOwnerData(CompoundTag data) {
        removeOwnerUuid(data);
        data.remove("Owner");
        data.remove("OwnerName");
    }

    private void removeOwnerUuid(CompoundTag data) {
        data.remove("OwnerUUID");
        data.remove("OwnerUUIDMost");
        data.remove("OwnerUUIDLeast");
        String owner = data.getString("Owner").orElse("");
        if (isUuidString(owner) || owner.isBlank()) {
            data.remove("Owner");
        }
    }


    private void removeBooleanTag(CompoundTag data, String key) {
        if (data.contains(key)) {
            data.remove(key);
        }
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
