package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.InfoEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.StringWithActionsEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.SoundEventSelectionEntryModel;
import com.github.rinorsi.cadeditor.client.util.NbtHelper;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;

public class ItemProfileCategoryModel extends ItemEditorCategoryModel {
    private static final String PROFILE_COMPONENT_KEY = "minecraft:profile";
    private static final String TEXTURES_PROPERTY_NAME = "textures";
    private static final int MAX_PROPERTY_VALUE_LENGTH = 32767;
    private static final int MAX_PROPERTY_SIGNATURE_LENGTH = 1024;

    private StringWithActionsEntryModel nameEntry;
    private StringWithActionsEntryModel uuidEntry;
    private StringWithActionsEntryModel textureValueEntry;
    private StringWithActionsEntryModel textureSignatureEntry;
    private SoundEventSelectionEntryModel noteBlockSoundEntry;
    private ListTag otherProperties = new ListTag();

    public ItemProfileCategoryModel(ItemEditorModel editor) {
        super(ModTexts.gui("profile"), editor);
    }

    @Override
    protected void setupEntries() {
        getEntries().add(new InfoEntryModel(this, ModTexts.gui("profile_info").copy().withStyle(ChatFormatting.GRAY)));
        CompoundTag components;
        CompoundTag profile;
        String name = "";
        String uuid = "";
        String textureValue = "";
        String textureSignature = "";
        this.otherProperties = new ListTag();
        CompoundTag data = getData();
        if (data != null && (components = data.getCompound("components").orElse(null)) != null && (profile = components.getCompound(PROFILE_COMPONENT_KEY).orElse(null)) != null) {
            name = NbtHelper.getString(profile, "name", name);
            int[] idArray = profile.getIntArray("id").orElse(null);
            if (idArray != null && idArray.length == 4) {
                uuid = UUIDUtil.uuidFromIntArray(idArray).toString();
            }
            ListTag properties = profile.getList("properties").orElse(null);
            if (properties != null) {
                for (int i = 0; i < properties.size(); i++) {
                    CompoundTag property = properties.getCompound(i).orElse(null);
                    if (property == null) {
                        continue;
                    }
                    String propertyName = property.getString("name").orElse("");
                    if (TEXTURES_PROPERTY_NAME.equals(propertyName)) {
                        if (textureValue.isEmpty()) {
                            textureValue = property.getString("value").orElse("");
                            textureSignature = property.getString("signature").orElse("");
                        }
                    } else {
                        this.otherProperties.add(property.copy());
                    }
                }
            }
        }
        this.nameEntry = new StringWithActionsEntryModel(this, ModTexts.gui("player_name"), name, v -> {
        });
        this.nameEntry.addButton(new StringWithActionsEntryModel.ActionButton(ModTexts.gui("fill_own_data"), null, this::fillMyName));
        this.uuidEntry = new StringWithActionsEntryModel(this, ModTexts.gui("uuid"), uuid, v -> {
        });
        this.uuidEntry.addButton(new StringWithActionsEntryModel.ActionButton(ModTexts.gui("fill_own_data"), null, this::fillMyUuid));
        this.textureValueEntry = new StringWithActionsEntryModel(this, ModTexts.gui("profile_texture_value"), textureValue, v -> {
        });
        this.textureSignatureEntry = new StringWithActionsEntryModel(this, ModTexts.gui("profile_texture_signature"), textureSignature, v -> {
        });
        Identifier noteBlockSound = getStack().get(DataComponents.NOTE_BLOCK_SOUND);
        this.noteBlockSoundEntry = new SoundEventSelectionEntryModel(this, ModTexts.gui("note_block_sound_value"),
                noteBlockSound == null ? "" : noteBlockSound.toString(), this::setNoteBlockSound, null);
        getEntries().add(this.nameEntry);
        getEntries().add(this.uuidEntry);
        getEntries().add(this.textureValueEntry);
        getEntries().add(this.textureSignatureEntry);
        getEntries().add(this.noteBlockSoundEntry);
    }

    private void setNoteBlockSound(String id) {
        String text = id == null ? "" : id.trim();
        ItemStack stack = getStack();
        if (text.isEmpty()) {
            stack.remove(DataComponents.NOTE_BLOCK_SOUND);
            this.noteBlockSoundEntry.setValid(true);
            return;
        }
        Identifier location = ClientUtil.parseResourceLocation(text);
        if (location == null) {
            this.noteBlockSoundEntry.setValid(false);
            return;
        }
        this.noteBlockSoundEntry.setValid(true);
        stack.set(DataComponents.NOTE_BLOCK_SOUND, location);
    }

    private void fillMyName() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            this.nameEntry.setValue(player.getName().getString());
        }
    }

    private void fillMyUuid() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            this.uuidEntry.setValue(player.getUUID().toString());
        }
    }

    @Override
    public int getEntryListStart() {
        return -1;
    }

    @Override
    public void apply() {
        super.apply();
        this.noteBlockSoundEntry.apply();
        String name = this.nameEntry.getValue() == null ? "" : this.nameEntry.getValue().trim();
        String uuid = this.uuidEntry.getValue() == null ? "" : this.uuidEntry.getValue().trim();
        if (!uuid.isEmpty()) {
            try {
                UUID.fromString(uuid);
            } catch (IllegalArgumentException e) {
                this.uuidEntry.setValid(false);
                return;
            }
        }
        String textureValue = this.textureValueEntry.getValue() == null ? "" : this.textureValueEntry.getValue().trim();
        String textureSignature = this.textureSignatureEntry.getValue() == null ? "" : this.textureSignatureEntry.getValue().trim();
        if (textureValue.length() > MAX_PROPERTY_VALUE_LENGTH) {
            this.textureValueEntry.setValid(false);
            return;
        }
        if (textureSignature.length() > MAX_PROPERTY_SIGNATURE_LENGTH) {
            this.textureSignatureEntry.setValid(false);
            return;
        }
        this.textureValueEntry.setValid(true);
        this.textureSignatureEntry.setValid(true);
        CompoundTag profile = new CompoundTag();
        if (!name.isEmpty()) {
            profile.putString("name", name);
        }
        if (!uuid.isEmpty()) {
            profile.putIntArray("id", UUIDUtil.uuidToIntArray(UUID.fromString(uuid)));
        }
        ListTag properties = buildProperties(textureValue, textureSignature);
        if (!properties.isEmpty()) {
            profile.put("properties", properties);
        }
        ItemStack stack = getStack();
        if (profile.isEmpty()) {
            stack.remove(DataComponents.PROFILE);
            return;
        }
        ResolvableProfile parsed = ResolvableProfile.CODEC.parse(
                RegistryOps.create(NbtOps.INSTANCE, ClientUtil.registryAccess()), profile).result().orElse(null);
        if (parsed != null) {
            stack.set(DataComponents.PROFILE, parsed);
        } else {
            stack.remove(DataComponents.PROFILE);
        }
    }

    private ListTag buildProperties(String textureValue, String textureSignature) {
        ListTag properties = new ListTag();
        if (!textureValue.isEmpty()) {
            CompoundTag property = new CompoundTag();
            property.putString("name", TEXTURES_PROPERTY_NAME);
            property.putString("value", textureValue);
            if (!textureSignature.isEmpty()) {
                property.putString("signature", textureSignature);
            }
            properties.add(property);
        }
        for (Tag tag : this.otherProperties) {
            properties.add(tag.copy());
        }
        return properties;
    }

    private ItemStack getStack() {
        return getParent().getContext().getItemStack();
    }
}
