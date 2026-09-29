package com.github.rinorsi.cadeditor.client.screen.model.category.entity;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.screen.model.EntityEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EnumEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.FloatEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.IntegerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.SpacerEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import com.mojang.math.Transformation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Display;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class EntityDisplayCategoryModel extends EntityCategoryModel {
    private float translationX;
    private float translationY;
    private float translationZ;
    private float scaleX;
    private float scaleY;
    private float scaleZ;
    private float leftRotationX;
    private float leftRotationY;
    private float leftRotationZ;
    private float leftRotationW = 1f;
    private float rightRotationX;
    private float rightRotationY;
    private float rightRotationZ;
    private float rightRotationW = 1f;
    private boolean brightnessOverride;
    private int brightnessBlock = 15;
    private int brightnessSky = 15;

    public EntityDisplayCategoryModel(EntityEditorModel editor) {
        super(ModTexts.DISPLAY_ENTITY, editor);
    }

    @Override
    protected void setupEntries() {
        CompoundTag data = getData();
        readTransformation();

        getEntries().add(new FloatEntryModel(this, ModTexts.DISPLAY_TRANSLATION_X, translationX, this::setTranslationX));
        getEntries().add(new FloatEntryModel(this, ModTexts.DISPLAY_TRANSLATION_Y, translationY, this::setTranslationY));
        getEntries().add(new FloatEntryModel(this, ModTexts.DISPLAY_TRANSLATION_Z, translationZ, this::setTranslationZ));
        getEntries().add(new FloatEntryModel(this, ModTexts.DISPLAY_SCALE_X, scaleX, this::setScaleX, value -> value != null && value != 0f));
        getEntries().add(new FloatEntryModel(this, ModTexts.DISPLAY_SCALE_Y, scaleY, this::setScaleY, value -> value != null && value != 0f));
        getEntries().add(new FloatEntryModel(this, ModTexts.DISPLAY_SCALE_Z, scaleZ, this::setScaleZ, value -> value != null && value != 0f));
        getEntries().add(new FloatEntryModel(this, ModTexts.DISPLAY_LEFT_ROTATION_X, leftRotationX, this::setLeftRotationX));
        getEntries().add(new FloatEntryModel(this, ModTexts.DISPLAY_LEFT_ROTATION_Y, leftRotationY, this::setLeftRotationY));
        getEntries().add(new FloatEntryModel(this, ModTexts.DISPLAY_LEFT_ROTATION_Z, leftRotationZ, this::setLeftRotationZ));
        getEntries().add(new FloatEntryModel(this, ModTexts.DISPLAY_LEFT_ROTATION_W, leftRotationW, this::setLeftRotationW));
        getEntries().add(new FloatEntryModel(this, ModTexts.DISPLAY_RIGHT_ROTATION_X, rightRotationX, this::setRightRotationX));
        getEntries().add(new FloatEntryModel(this, ModTexts.DISPLAY_RIGHT_ROTATION_Y, rightRotationY, this::setRightRotationY));
        getEntries().add(new FloatEntryModel(this, ModTexts.DISPLAY_RIGHT_ROTATION_Z, rightRotationZ, this::setRightRotationZ));
        getEntries().add(new FloatEntryModel(this, ModTexts.DISPLAY_RIGHT_ROTATION_W, rightRotationW, this::setRightRotationW));
        getEntries().add(new IntegerEntryModel(this, ModTexts.DISPLAY_INTERPOLATION_DURATION, data.getIntOr("interpolation_duration", 0),
                this::setInterpolationDuration, value -> value != null && value >= 0));
        getEntries().add(new IntegerEntryModel(this, ModTexts.DISPLAY_START_INTERPOLATION, data.getIntOr("start_interpolation", 0),
                this::setStartInterpolation));
        getEntries().add(new IntegerEntryModel(this, ModTexts.DISPLAY_TELEPORT_DURATION, data.getIntOr("teleport_duration", 0),
                this::setTeleportDuration, value -> value != null && value >= 0 && value <= 59));

        getEntries().add(new SpacerEntryModel(this));

        Display.BillboardConstraints billboard = billboard(data.getStringOr("billboard", "fixed"));
        EnumEntryModel<Display.BillboardConstraints> billboardEntry = new EnumEntryModel<>(this, ModTexts.DISPLAY_BILLBOARD,
                Display.BillboardConstraints.values(), billboard, this::setBillboard);
        billboardEntry.withTextFactory(ModTexts::displayBillboard);
        getEntries().add(billboardEntry);

        brightnessOverride = data.contains("brightness");
        CompoundTag brightness = data.getCompoundOrEmpty("brightness");
        brightnessBlock = brightness.getIntOr("block", 15);
        brightnessSky = brightness.getIntOr("sky", 15);
        getEntries().add(new BooleanEntryModel(this, ModTexts.DISPLAY_BRIGHTNESS_OVERRIDE, brightnessOverride, this::setBrightnessOverride));
        getEntries().add(new IntegerEntryModel(this, ModTexts.DISPLAY_BRIGHTNESS_BLOCK, brightnessBlock,
                this::setBrightnessBlock, value -> value != null && value >= 0 && value <= 15));
        getEntries().add(new IntegerEntryModel(this, ModTexts.DISPLAY_BRIGHTNESS_SKY, brightnessSky,
                this::setBrightnessSky, value -> value != null && value >= 0 && value <= 15));

        getEntries().add(new FloatEntryModel(this, ModTexts.DISPLAY_VIEW_RANGE, data.getFloatOr("view_range", 1f),
                this::setViewRange, value -> value != null && value >= 0f));
        getEntries().add(new FloatEntryModel(this, ModTexts.DISPLAY_SHADOW_RADIUS, data.getFloatOr("shadow_radius", 0f),
                this::setShadowRadius, value -> value != null && value >= 0f));
        getEntries().add(new FloatEntryModel(this, ModTexts.DISPLAY_SHADOW_STRENGTH, data.getFloatOr("shadow_strength", 1f),
                this::setShadowStrength, value -> value != null && value >= 0f && value <= 1f));
        getEntries().add(new FloatEntryModel(this, ModTexts.DISPLAY_WIDTH, data.getFloatOr("width", 0f),
                this::setWidth, value -> value != null && value >= 0f));
        getEntries().add(new FloatEntryModel(this, ModTexts.DISPLAY_HEIGHT, data.getFloatOr("height", 0f),
                this::setHeight, value -> value != null && value >= 0f));
        getEntries().add(new IntegerEntryModel(this, ModTexts.DISPLAY_GLOW_COLOR_OVERRIDE, data.getIntOr("glow_color_override", -1),
                this::setGlowColorOverride));
    }

    protected static DynamicOps<Tag> ops() {
        return ClientUtil.registryAccess().createSerializationContext(NbtOps.INSTANCE);
    }

    protected static <T> Tag encode(Codec<T> codec, T value) {
        return codec.encodeStart(ops(), value).result().orElse(null);
    }

    private void readTransformation() {
        CompoundTag tag = getData().getCompoundOrEmpty("transformation");
        Transformation transformation = Transformation.IDENTITY;
        if (!tag.isEmpty()) {
            transformation = Transformation.EXTENDED_CODEC.parse(ops(), tag).result().orElse(Transformation.IDENTITY);
        }
        Vector3fc translation = transformation.translation();
        Quaternionfc leftRotation = transformation.leftRotation();
        Vector3fc scale = transformation.scale();
        Quaternionfc rightRotation = transformation.rightRotation();
        translationX = translation.x();
        translationY = translation.y();
        translationZ = translation.z();
        scaleX = scale.x();
        scaleY = scale.y();
        scaleZ = scale.z();
        leftRotationX = leftRotation.x();
        leftRotationY = leftRotation.y();
        leftRotationZ = leftRotation.z();
        leftRotationW = leftRotation.w();
        rightRotationX = rightRotation.x();
        rightRotationY = rightRotation.y();
        rightRotationZ = rightRotation.z();
        rightRotationW = rightRotation.w();
    }

    private void writeTransformation() {
        CompoundTag data = getData();
        if (isIdentityTransformation()) {
            data.remove("transformation");
            return;
        }
        Transformation transformation = new Transformation(
                new Vector3f(translationX, translationY, translationZ),
                normalized(leftRotationX, leftRotationY, leftRotationZ, leftRotationW),
                new Vector3f(scaleX, scaleY, scaleZ),
                normalized(rightRotationX, rightRotationY, rightRotationZ, rightRotationW));
        Tag encoded = encode(Transformation.EXTENDED_CODEC, transformation);
        if (encoded != null) {
            data.put("transformation", encoded);
        }
    }

    private boolean isIdentityTransformation() {
        return near(translationX, 0f) && near(translationY, 0f) && near(translationZ, 0f)
                && near(scaleX, 1f) && near(scaleY, 1f) && near(scaleZ, 1f)
                && near(leftRotationX, 0f) && near(leftRotationY, 0f) && near(leftRotationZ, 0f) && near(leftRotationW, 1f)
                && near(rightRotationX, 0f) && near(rightRotationY, 0f) && near(rightRotationZ, 0f) && near(rightRotationW, 1f);
    }

    private static boolean near(float value, float reference) {
        return Math.abs(value - reference) <= 1.0e-6f;
    }

    private static Quaternionf normalized(float x, float y, float z, float w) {
        Quaternionf quaternion = new Quaternionf(x, y, z, w);
        if (quaternion.lengthSquared() < 1.0e-12f) {
            quaternion.identity();
        } else {
            quaternion.normalize();
        }
        return quaternion;
    }

    private void setTranslationX(Float value) {
        translationX = value == null ? 0f : value;
        writeTransformation();
    }

    private void setTranslationY(Float value) {
        translationY = value == null ? 0f : value;
        writeTransformation();
    }

    private void setTranslationZ(Float value) {
        translationZ = value == null ? 0f : value;
        writeTransformation();
    }

    private void setScaleX(Float value) {
        scaleX = value == null ? 1f : value;
        writeTransformation();
    }

    private void setScaleY(Float value) {
        scaleY = value == null ? 1f : value;
        writeTransformation();
    }

    private void setScaleZ(Float value) {
        scaleZ = value == null ? 1f : value;
        writeTransformation();
    }

    private void setLeftRotationX(Float value) {
        leftRotationX = value == null ? 0f : value;
        writeTransformation();
    }

    private void setLeftRotationY(Float value) {
        leftRotationY = value == null ? 0f : value;
        writeTransformation();
    }

    private void setLeftRotationZ(Float value) {
        leftRotationZ = value == null ? 0f : value;
        writeTransformation();
    }

    private void setLeftRotationW(Float value) {
        leftRotationW = value == null ? 1f : value;
        writeTransformation();
    }

    private void setRightRotationX(Float value) {
        rightRotationX = value == null ? 0f : value;
        writeTransformation();
    }

    private void setRightRotationY(Float value) {
        rightRotationY = value == null ? 0f : value;
        writeTransformation();
    }

    private void setRightRotationZ(Float value) {
        rightRotationZ = value == null ? 0f : value;
        writeTransformation();
    }

    private void setRightRotationW(Float value) {
        rightRotationW = value == null ? 1f : value;
        writeTransformation();
    }

    private void setInterpolationDuration(Integer value) {
        int duration = value == null ? 0 : Math.max(0, value);
        CompoundTag data = getData();
        if (duration == 0) {
            data.remove("interpolation_duration");
        } else {
            data.putInt("interpolation_duration", duration);
        }
    }

    private void setStartInterpolation(Integer value) {
        int start = value == null ? 0 : value;
        CompoundTag data = getData();
        if (start == 0) {
            data.remove("start_interpolation");
        } else {
            data.putInt("start_interpolation", start);
        }
    }

    private void setTeleportDuration(Integer value) {
        int duration = value == null ? 0 : Math.max(0, Math.min(59, value));
        CompoundTag data = getData();
        if (duration == 0) {
            data.remove("teleport_duration");
        } else {
            data.putInt("teleport_duration", duration);
        }
    }

    private static Display.BillboardConstraints billboard(String name) {
        for (Display.BillboardConstraints constraint : Display.BillboardConstraints.values()) {
            if (constraint.getSerializedName().equals(name)) {
                return constraint;
            }
        }
        return Display.BillboardConstraints.FIXED;
    }

    private void setBillboard(Display.BillboardConstraints value) {
        CompoundTag data = getData();
        if (value == null || value == Display.BillboardConstraints.FIXED) {
            data.remove("billboard");
        } else {
            data.putString("billboard", value.getSerializedName());
        }
    }

    private void setBrightnessOverride(boolean value) {
        brightnessOverride = value;
        writeBrightness();
    }

    private void setBrightnessBlock(Integer value) {
        brightnessBlock = value == null ? 15 : Math.max(0, Math.min(15, value));
        writeBrightness();
    }

    private void setBrightnessSky(Integer value) {
        brightnessSky = value == null ? 15 : Math.max(0, Math.min(15, value));
        writeBrightness();
    }

    private void writeBrightness() {
        CompoundTag data = getData();
        if (!brightnessOverride) {
            data.remove("brightness");
            return;
        }
        CompoundTag brightness = new CompoundTag();
        brightness.putInt("block", brightnessBlock);
        brightness.putInt("sky", brightnessSky);
        data.put("brightness", brightness);
    }

    private void setViewRange(Float value) {
        float range = value == null ? 1f : Math.max(0f, value);
        CompoundTag data = getData();
        if (near(range, 1f)) {
            data.remove("view_range");
        } else {
            data.putFloat("view_range", range);
        }
    }

    private void setShadowRadius(Float value) {
        float radius = value == null ? 0f : Math.max(0f, value);
        CompoundTag data = getData();
        if (near(radius, 0f)) {
            data.remove("shadow_radius");
        } else {
            data.putFloat("shadow_radius", radius);
        }
    }

    private void setShadowStrength(Float value) {
        float strength = value == null ? 1f : Math.max(0f, Math.min(1f, value));
        CompoundTag data = getData();
        if (near(strength, 1f)) {
            data.remove("shadow_strength");
        } else {
            data.putFloat("shadow_strength", strength);
        }
    }

    private void setWidth(Float value) {
        float width = value == null ? 0f : Math.max(0f, value);
        CompoundTag data = getData();
        if (near(width, 0f)) {
            data.remove("width");
        } else {
            data.putFloat("width", width);
        }
    }

    private void setHeight(Float value) {
        float height = value == null ? 0f : Math.max(0f, value);
        CompoundTag data = getData();
        if (near(height, 0f)) {
            data.remove("height");
        } else {
            data.putFloat("height", height);
        }
    }

    private void setGlowColorOverride(Integer value) {
        int color = value == null ? -1 : value;
        CompoundTag data = getData();
        if (color == -1) {
            data.remove("glow_color_override");
        } else {
            data.putInt("glow_color_override", color);
        }
    }
}
