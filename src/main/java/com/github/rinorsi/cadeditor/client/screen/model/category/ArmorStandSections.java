package com.github.rinorsi.cadeditor.client.screen.model.category;

import com.github.rinorsi.cadeditor.client.screen.model.category.entity.EntityEquipmentCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntityPreviewEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EnumEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.SliderEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ArmorStandSections {
    public static final int PREVIEW_HEIGHT = 124;
    private static final String[] AXIS_LABELS = {"X", "Y", "Z"};
    public static final EntityEquipmentCategoryModel.Slot[] EQUIPMENT_SLOTS = {
            EntityEquipmentCategoryModel.Slot.MAIN_HAND,
            EntityEquipmentCategoryModel.Slot.OFF_HAND,
            EntityEquipmentCategoryModel.Slot.HEAD,
            EntityEquipmentCategoryModel.Slot.CHEST,
            EntityEquipmentCategoryModel.Slot.LEGS,
            EntityEquipmentCategoryModel.Slot.FEET,
    };
    private static final String[] DISABLED_ACTIONS = {"lock", "take", "put"};

    private final CategoryModel category;
    private final Supplier<CompoundTag> data;
    private EntityPreviewEntryModel previewEntry;
    private int previewEntityId;
    private PosePart selectedPart = PosePart.HEAD;
    private EntityEquipmentCategoryModel.Slot selectedDisabledSlot = EntityEquipmentCategoryModel.Slot.MAIN_HAND;

    public ArmorStandSections(CategoryModel category, Supplier<CompoundTag> data) {
        this.category = category;
        this.data = data;
    }

    public EntityPreviewEntryModel buildPreviewEntry() {
        this.previewEntry = new EntityPreviewEntryModel(category);
        return previewEntry;
    }

    public void refreshPreview() {
        if (previewEntry == null) {
            return;
        }
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        CompoundTag tag = data.get().copy();
        tag.remove("id");
        Entity entity = EntityType.loadEntityRecursive(EntityTypes.ARMOR_STAND, tag, level, EntitySpawnReason.COMMAND, e -> e);
        if (entity != null) {
            entity.setId(--previewEntityId);
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                entity.absSnapTo(player.getX(), player.getY(), player.getZ());
            }
        }
        previewEntry.setEntity(entity);
    }

    public List<EntryModel> buildPoseEntries() {
        List<EntryModel> entries = new ArrayList<>();
        EnumEntryModel<PosePart> partEntry = new EnumEntryModel<>(category, ModTexts.gui("armor_stand.pose_part"),
                PosePart.values(), selectedPart, part -> selectedPart = part);
        partEntry.withTextFactory(part -> ModTexts.gui("armor_stand." + part.langKey));
        entries.add(partEntry);
        SliderEntryModel[] axisEntries = new SliderEntryModel[AXIS_LABELS.length];
        for (int axis = 0; axis < AXIS_LABELS.length; axis++) {
            int a = axis;
            SliderEntryModel entry = new SliderEntryModel(category, Component.literal(AXIS_LABELS[axis]),
                    getPoseAxis(selectedPart, a), -180, 180, 0.1, v -> setPoseAxis(selectedPart, a, v));
            entry.setFactoryDefault(selectedPart.defaults[a]);
            entry.valueProperty().addListener(v -> entry.apply());
            axisEntries[axis] = entry;
            entries.add(entry);
        }
        partEntry.valueProperty().addListener(v -> {
            selectedPart = partEntry.getValue();
            for (int axis = 0; axis < AXIS_LABELS.length; axis++) {
                axisEntries[axis].setValue(getPoseAxis(selectedPart, axis));
                axisEntries[axis].setFactoryDefault(selectedPart.defaults[axis]);
            }
        });
        return entries;
    }

    public List<EntryModel> buildFlagEntries() {
        List<EntryModel> entries = new ArrayList<>();
        addFlag(entries, "Small", "armor_stand.small");
        addFlag(entries, "ShowArms", "armor_stand.show_arms");
        addFlag(entries, "NoBasePlate", "armor_stand.no_base_plate");
        addFlag(entries, "Marker", "armor_stand.marker");
        addFlag(entries, "Invisible", "armor_stand.invisible");
        return entries;
    }

    private void addFlag(List<EntryModel> entries, String tag, String key) {
        entries.add(new BooleanEntryModel(category, ModTexts.gui(key), data.get().getBooleanOr(tag, false),
                b -> putBooleanOrRemove(tag, b)));
    }

    public void putBooleanOrRemove(String tag, boolean value) {
        if (value) {
            data.get().putBoolean(tag, true);
        } else {
            data.get().remove(tag);
        }
        refreshPreview();
    }

    public List<EntryModel> buildDisabledSlotEntries() {
        List<EntryModel> entries = new ArrayList<>();
        EnumEntryModel<EntityEquipmentCategoryModel.Slot> slotEntry = new EnumEntryModel<>(category, ModTexts.gui("armor_stand.disabled_slots.slot"),
                EQUIPMENT_SLOTS, selectedDisabledSlot, slot -> selectedDisabledSlot = slot);
        slotEntry.withTextFactory(EntityEquipmentCategoryModel.Slot::label);
        entries.add(slotEntry);
        BooleanEntryModel[] actionEntries = new BooleanEntryModel[DISABLED_ACTIONS.length];
        for (int action = 0; action < DISABLED_ACTIONS.length; action++) {
            String key = DISABLED_ACTIONS[action];
            BooleanEntryModel entry = new BooleanEntryModel(category, ModTexts.gui("armor_stand.disabled_slots." + key),
                    isDisabledBit(disabledBit(selectedDisabledSlot, key)), v -> setDisabledBit(disabledBit(selectedDisabledSlot, key), v));
            actionEntries[action] = entry;
            entries.add(entry);
        }
        slotEntry.valueProperty().addListener(v -> {
            selectedDisabledSlot = slotEntry.getValue();
            for (int action = 0; action < DISABLED_ACTIONS.length; action++) {
                boolean state = isDisabledBit(disabledBit(selectedDisabledSlot, DISABLED_ACTIONS[action]));
                actionEntries[action].setValue(state);
                actionEntries[action].setFactoryDefault(state);
                actionEntries[action].markClean();
            }
        });
        return entries;
    }

    private float getPoseAxis(PosePart part, int axis) {
        CompoundTag pose = data.get().getCompound("Pose").orElse(null);
        if (pose == null) {
            return part.defaults[axis];
        }
        ListTag rotations = pose.getList(part.tagKey).orElse(null);
        if (rotations == null || rotations.size() < 3) {
            return part.defaults[axis];
        }
        return rotations.getFloatOr(axis, part.defaults[axis]);
    }

    private void setPoseAxis(PosePart part, int axis, float value) {
        CompoundTag pose = data.get().getCompound("Pose").orElseGet(() -> {
            CompoundTag created = new CompoundTag();
            data.get().put("Pose", created);
            return created;
        });
        float[] current = new float[]{getPoseAxis(part, 0), getPoseAxis(part, 1), getPoseAxis(part, 2)};
        current[axis] = value;
        ListTag rotations = new ListTag();
        rotations.add(FloatTag.valueOf(current[0]));
        rotations.add(FloatTag.valueOf(current[1]));
        rotations.add(FloatTag.valueOf(current[2]));
        pose.put(part.tagKey, rotations);
        refreshPreview();
    }

    private static int disabledBit(EntityEquipmentCategoryModel.Slot slot, String action) {
        int offset = switch (action) {
            case "take" -> 8;
            case "put" -> 16;
            default -> 0;
        };
        return 1 << equipmentSlot(slot).getFilterBit(offset);
    }

    private static EquipmentSlot equipmentSlot(EntityEquipmentCategoryModel.Slot slot) {
        return switch (slot) {
            case MAIN_HAND -> EquipmentSlot.MAINHAND;
            case OFF_HAND -> EquipmentSlot.OFFHAND;
            case HEAD -> EquipmentSlot.HEAD;
            case CHEST -> EquipmentSlot.CHEST;
            case LEGS -> EquipmentSlot.LEGS;
            case FEET -> EquipmentSlot.FEET;
            default -> EquipmentSlot.BODY;
        };
    }

    private boolean isDisabledBit(int bit) {
        return (data.get().getIntOr("DisabledSlots", 0) & bit) != 0;
    }

    private void setDisabledBit(int bit, boolean enabled) {
        int mask = data.get().getIntOr("DisabledSlots", 0);
        int updated = enabled ? mask | bit : mask & ~bit;
        if (updated == 0) {
            data.get().remove("DisabledSlots");
        } else {
            data.get().putInt("DisabledSlots", updated);
        }
    }

    private enum PosePart {
        HEAD("Head", "head", 0f, 0f, 0f),
        BODY("Body", "body", 0f, 0f, 0f),
        LEFT_ARM("LeftArm", "left_arm", -10f, 0f, -10f),
        RIGHT_ARM("RightArm", "right_arm", -15f, 0f, 10f),
        LEFT_LEG("LeftLeg", "left_leg", -1f, 0f, -1f),
        RIGHT_LEG("RightLeg", "right_leg", 1f, 0f, 1f);

        private final String tagKey;
        private final String langKey;
        private final float[] defaults;

        PosePart(String tagKey, String langKey, float defaultX, float defaultY, float defaultZ) {
            this.tagKey = tagKey;
            this.langKey = langKey;
            this.defaults = new float[]{defaultX, defaultY, defaultZ};
        }
    }
}
