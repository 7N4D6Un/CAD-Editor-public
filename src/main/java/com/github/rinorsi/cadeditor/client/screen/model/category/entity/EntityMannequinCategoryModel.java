package com.github.rinorsi.cadeditor.client.screen.model.category.entity;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.screen.model.EntityEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EnumEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.SpacerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.StringWithActionsEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.TextEntryModel;
import com.github.rinorsi.cadeditor.client.util.ComponentJsonHelper;
import com.github.rinorsi.cadeditor.client.util.NbtHelper;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.item.component.ResolvableProfile;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class EntityMannequinCategoryModel extends EntityCategoryModel {
    private static final String PROFILE_FIELD = "profile";
    private static final String HIDDEN_LAYERS_FIELD = "hidden_layers";
    private static final String MAIN_HAND_FIELD = "main_hand";
    private static final String POSE_FIELD = "pose";
    private static final String IMMOVABLE_FIELD = "immovable";
    private static final String DESCRIPTION_FIELD = "description";
    private static final String HIDE_DESCRIPTION_FIELD = "hide_description";
    private static final String TEXTURES_PROPERTY_NAME = "textures";
    private static final String TEXTURE_FIELD = "texture";
    private static final String CAPE_FIELD = "cape";
    private static final String ELYTRA_FIELD = "elytra";
    private static final String MODEL_FIELD = "model";
    private static final int MAX_PROPERTY_VALUE_LENGTH = 32767;
    private static final int MAX_PROPERTY_SIGNATURE_LENGTH = 1024;
    private static final Pose[] VALID_POSES = {Pose.STANDING, Pose.CROUCHING, Pose.SWIMMING, Pose.FALL_FLYING, Pose.SLEEPING};

    private StringWithActionsEntryModel nameEntry;
    private StringWithActionsEntryModel uuidEntry;
    private StringWithActionsEntryModel textureValueEntry;
    private StringWithActionsEntryModel textureSignatureEntry;
    private StringWithActionsEntryModel textureEntry;
    private StringWithActionsEntryModel capeEntry;
    private StringWithActionsEntryModel elytraEntry;
    private EnumEntryModel<PlayerModelType> modelEntry;
    private final Map<PlayerModelPart, BooleanEntryModel> layerEntries = new LinkedHashMap<>();
    private EnumEntryModel<HumanoidArm> mainHandEntry;
    private EnumEntryModel<Pose> poseEntry;
    private BooleanEntryModel immovableEntry;
    private TextEntryModel descriptionEntry;
    private BooleanEntryModel hideDescriptionEntry;
    private ListTag otherProperties = new ListTag();

    public EntityMannequinCategoryModel(EntityEditorModel editor) {
        super(ModTexts.gui("mannequin"), editor);
    }

    @Override
    protected void setupEntries() {
        CompoundTag data = getData();

        ProfileFields profile = readProfile();
        this.nameEntry = new StringWithActionsEntryModel(this, ModTexts.gui("player_name"), profile.name(), v -> {
        });
        this.nameEntry.addButton(new StringWithActionsEntryModel.ActionButton(ModTexts.gui("fill_own_data"), null, this::fillMyName));
        this.uuidEntry = new StringWithActionsEntryModel(this, ModTexts.gui("uuid"), profile.uuid(), v -> {
        });
        this.uuidEntry.addButton(new StringWithActionsEntryModel.ActionButton(ModTexts.gui("fill_own_data"), null, this::fillMyUuid));
        this.textureValueEntry = new StringWithActionsEntryModel(this, ModTexts.gui("profile_texture_value"), profile.textureValue(), v -> {
        });
        this.textureSignatureEntry = new StringWithActionsEntryModel(this, ModTexts.gui("profile_texture_signature"), profile.textureSignature(), v -> {
        });
        getEntries().add(this.nameEntry);
        getEntries().add(this.uuidEntry);
        getEntries().add(this.textureValueEntry);
        getEntries().add(this.textureSignatureEntry);
        getEntries().add(new SpacerEntryModel(this));

        this.textureEntry = new StringWithActionsEntryModel(this, ModTexts.gui("profile_texture"), profile.texture(), v -> {
        });
        this.capeEntry = new StringWithActionsEntryModel(this, ModTexts.gui("profile_cape"), profile.cape(), v -> {
        });
        this.elytraEntry = new StringWithActionsEntryModel(this, ModTexts.gui("profile_elytra"), profile.elytra(), v -> {
        });
        this.modelEntry = new EnumEntryModel<>(this, ModTexts.gui("profile_model"), PlayerModelType.values(), profile.model(), v -> {
        });
        this.modelEntry.withTextFactory(type -> ModTexts.gui("profile_model." + type.getSerializedName()));
        getEntries().add(this.textureEntry);
        getEntries().add(this.capeEntry);
        getEntries().add(this.elytraEntry);
        getEntries().add(this.modelEntry);

        getEntries().add(new SpacerEntryModel(this));
        ListTag hiddenLayers = data.getList(HIDDEN_LAYERS_FIELD).orElse(null);
        for (PlayerModelPart part : PlayerModelPart.values()) {
            boolean hidden = isLayerHidden(hiddenLayers, part);
            BooleanEntryModel entry = new BooleanEntryModel(this, ModTexts.gui("mannequin.hide_layer." + part.getId()), hidden, v -> updateHiddenLayers());
            this.layerEntries.put(part, entry);
            getEntries().add(entry);
        }

        getEntries().add(new SpacerEntryModel(this));
        this.mainHandEntry = new EnumEntryModel<>(this, ModTexts.gui("main_hand"), HumanoidArm.values(), readMainHand(), this::setMainHand);
        this.mainHandEntry.withTextFactory(HumanoidArm::caption);
        getEntries().add(this.mainHandEntry);

        this.poseEntry = new EnumEntryModel<>(this, ModTexts.gui("pose"), VALID_POSES, readPose(), this::setPose);
        this.poseEntry.withTextFactory(pose -> ModTexts.gui("pose." + pose.getSerializedName()));
        getEntries().add(this.poseEntry);

        getEntries().add(new SpacerEntryModel(this));
        this.immovableEntry = new BooleanEntryModel(this, ModTexts.gui("immovable"), data.getBooleanOr(IMMOVABLE_FIELD, false), v -> {
        });
        getEntries().add(this.immovableEntry);

        this.descriptionEntry = new TextEntryModel(this, ModTexts.gui("description"), getDescription(), this::setDescription);
        getEntries().add(this.descriptionEntry);

        this.hideDescriptionEntry = new BooleanEntryModel(this, ModTexts.gui("hide_description"), data.getBooleanOr(HIDE_DESCRIPTION_FIELD, false), v -> {
        });
        getEntries().add(this.hideDescriptionEntry);
    }

    private ProfileFields readProfile() {
        this.otherProperties.clear();
        CompoundTag data = getData();
        String name = "";
        String uuid = "";
        String textureValue = "";
        String textureSignature = "";
        String texture = "";
        String cape = "";
        String elytra = "";
        PlayerModelType model = PlayerModelType.WIDE;
        CompoundTag profile = data == null ? null : data.getCompound(PROFILE_FIELD).orElse(null);
        if (profile != null) {
            name = NbtHelper.getString(profile, "name", name);
            texture = NbtHelper.getString(profile, TEXTURE_FIELD, texture);
            cape = NbtHelper.getString(profile, CAPE_FIELD, cape);
            elytra = NbtHelper.getString(profile, ELYTRA_FIELD, elytra);
            model = PlayerModelType.SLIM.getSerializedName().equals(NbtHelper.getString(profile, MODEL_FIELD, "")) ? PlayerModelType.SLIM : PlayerModelType.WIDE;
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
        return new ProfileFields(name, uuid, textureValue, textureSignature, texture, cape, elytra, model);
    }

    private HumanoidArm readMainHand() {
        String s = getData() == null ? "right" : getData().getStringOr(MAIN_HAND_FIELD, "right");
        return "left".equals(s) ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
    }

    private Pose readPose() {
        String s = getData() == null ? "standing" : getData().getStringOr(POSE_FIELD, "standing");
        for (Pose pose : VALID_POSES) {
            if (pose.getSerializedName().equals(s)) {
                return pose;
            }
        }
        return Pose.STANDING;
    }

    private MutableComponent getDescription() {
        CompoundTag data = getData();
        Tag encoded = data == null ? null : data.get(DESCRIPTION_FIELD);
        return ComponentJsonHelper.decode(encoded, ClientUtil.registryAccess());
    }

    private boolean isLayerHidden(ListTag hiddenLayers, PlayerModelPart part) {
        if (hiddenLayers == null) {
            return false;
        }
        for (int i = 0; i < hiddenLayers.size(); i++) {
            if (part.getSerializedName().equals(hiddenLayers.getStringOr(i, ""))) {
                return true;
            }
        }
        return false;
    }

    private void updateHiddenLayers() {
        ListTag list = new ListTag();
        for (Map.Entry<PlayerModelPart, BooleanEntryModel> entry : this.layerEntries.entrySet()) {
            if (Boolean.TRUE.equals(entry.getValue().getValue())) {
                list.add(StringTag.valueOf(entry.getKey().getSerializedName()));
            }
        }
        CompoundTag data = getData();
        if (list.isEmpty()) {
            data.remove(HIDDEN_LAYERS_FIELD);
        } else {
            data.put(HIDDEN_LAYERS_FIELD, list);
        }
    }

    private void setMainHand(HumanoidArm arm) {
        getData().putString(MAIN_HAND_FIELD, arm.getSerializedName());
    }

    private void setPose(Pose pose) {
        getData().putString(POSE_FIELD, pose.getSerializedName());
    }

    private void setDescription(MutableComponent value) {
        CompoundTag data = getData();
        if (value != null && !value.getString().isEmpty()) {
            Tag encoded = ComponentJsonHelper.encodeToTag(value, ClientUtil.registryAccess());
            if (encoded != null) {
                data.put(DESCRIPTION_FIELD, encoded);
            }
        } else {
            data.remove(DESCRIPTION_FIELD);
        }
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
    public void apply() {
        super.apply();
        CompoundTag data = getData();

        String name = this.nameEntry.getValue() == null ? "" : this.nameEntry.getValue().trim();
        String uuid = this.uuidEntry.getValue() == null ? "" : this.uuidEntry.getValue().trim();
        String textureValue = this.textureValueEntry.getValue() == null ? "" : this.textureValueEntry.getValue().trim();
        String textureSignature = this.textureSignatureEntry.getValue() == null ? "" : this.textureSignatureEntry.getValue().trim();
        String texture = this.textureEntry.getValue() == null ? "" : this.textureEntry.getValue().trim();
        String cape = this.capeEntry.getValue() == null ? "" : this.capeEntry.getValue().trim();
        String elytra = this.elytraEntry.getValue() == null ? "" : this.elytraEntry.getValue().trim();
        PlayerModelType model = this.modelEntry.getValue();
        boolean profileValid = true;
        if (!uuid.isEmpty()) {
            try {
                UUID.fromString(uuid);
            } catch (IllegalArgumentException e) {
                this.uuidEntry.setValid(false);
                profileValid = false;
            }
        }
        if (textureValue.length() > MAX_PROPERTY_VALUE_LENGTH) {
            this.textureValueEntry.setValid(false);
            profileValid = false;
        }
        if (textureSignature.length() > MAX_PROPERTY_SIGNATURE_LENGTH) {
            this.textureSignatureEntry.setValid(false);
            profileValid = false;
        }
        if (!texture.isEmpty() && Identifier.tryParse(texture) == null) {
            this.textureEntry.setValid(false);
            profileValid = false;
        }
        if (!cape.isEmpty() && Identifier.tryParse(cape) == null) {
            this.capeEntry.setValid(false);
            profileValid = false;
        }
        if (!elytra.isEmpty() && Identifier.tryParse(elytra) == null) {
            this.elytraEntry.setValid(false);
            profileValid = false;
        }
        if (profileValid) {
            this.textureValueEntry.setValid(true);
            this.textureSignatureEntry.setValid(true);
            this.textureEntry.setValid(true);
            this.capeEntry.setValid(true);
            this.elytraEntry.setValid(true);
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
            if (!texture.isEmpty()) {
                profile.putString(TEXTURE_FIELD, texture);
            }
            if (!cape.isEmpty()) {
                profile.putString(CAPE_FIELD, cape);
            }
            if (!elytra.isEmpty()) {
                profile.putString(ELYTRA_FIELD, elytra);
            }
            if (model == PlayerModelType.SLIM) {
                profile.putString(MODEL_FIELD, PlayerModelType.SLIM.getSerializedName());
            }
            if (profile.isEmpty()) {
                data.remove(PROFILE_FIELD);
            } else {
                ResolvableProfile parsed = ResolvableProfile.CODEC.parse(
                        RegistryOps.create(NbtOps.INSTANCE, ClientUtil.registryAccess()), profile).result().orElse(null);
                if (parsed != null) {
                    data.put(PROFILE_FIELD, profile);
                } else {
                    data.remove(PROFILE_FIELD);
                }
            }
        }

        updateHiddenLayers();

        if (Boolean.TRUE.equals(this.immovableEntry.getValue())) {
            data.putBoolean(IMMOVABLE_FIELD, true);
        } else {
            data.remove(IMMOVABLE_FIELD);
        }

        if (Boolean.TRUE.equals(this.hideDescriptionEntry.getValue())) {
            data.putBoolean(HIDE_DESCRIPTION_FIELD, true);
            data.remove(DESCRIPTION_FIELD);
        } else {
            data.remove(HIDE_DESCRIPTION_FIELD);
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

    private record ProfileFields(String name, String uuid, String textureValue, String textureSignature, String texture, String cape, String elytra, PlayerModelType model) {
    }
}
