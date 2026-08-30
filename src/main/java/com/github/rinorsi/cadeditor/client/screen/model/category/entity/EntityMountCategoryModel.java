package com.github.rinorsi.cadeditor.client.screen.model.category.entity;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.screen.model.EntityEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.ActionEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.DoubleEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntityEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.IntegerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.StringEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.entity.EntitySingleItemEntryModel;
import com.github.rinorsi.cadeditor.client.util.NbtUuidHelper;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.equine.AbstractChestedHorse;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.equine.Llama;
import net.minecraft.world.entity.animal.golem.CopperGolem;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;


public class EntityMountCategoryModel extends EntityCategoryModel {
    private static final String EQUIPMENT_TAG = "equipment";
    private static final String SADDLE_EQUIPMENT_KEY = EquipmentSlot.SADDLE.getSerializedName();
    private BooleanEntryModel saddledEntry;
    private EntitySingleItemEntryModel saddleItemEntry;
    private BooleanEntryModel chestedEntry;
    private StringEntryModel leashHolderEntry;
    private BooleanEntryModel leashAnchorEntry;
    private DoubleEntryModel leashXEntry;
    private DoubleEntryModel leashYEntry;
    private DoubleEntryModel leashZEntry;
    private IntegerEntryModel temperEntry;
    private IntegerEntryModel strengthEntry;
    private int passengerListStart;

    public EntityMountCategoryModel(EntityEditorModel editor) {
        super(ModTexts.ENTITY_MOUNT, editor);
        this.passengerListStart = -1;
    }

    @Override 
    protected void setupEntries() {
        CompoundTag data = getData();
        if (data == null) {
            return;
        }
        this.chestedEntry = null;
        this.temperEntry = null;
        this.strengthEntry = null;
        this.saddledEntry = null;
        this.saddleItemEntry = null;
        boolean supportsSaddle = supportsSaddle();
        ItemStack saddleStack = readSaddleItem(data);
        boolean saddled = !saddleStack.isEmpty() || data.getBooleanOr("Saddled", false) || data.getBooleanOr("Saddle", false);
        String leashHolder = readLeashHolder(data);
        boolean hasLeashAnchor = false;
        double leashX = 0.0d;
        double leashY = 0.0d;
        double leashZ = 0.0d;
        Tag leashTag = data.get("leash");
        if (leashTag instanceof IntArrayTag leashArray) {
            int[] coords = leashArray.getAsIntArray();
            if (coords.length >= 3) {
                hasLeashAnchor = true;
                leashX = coords[0];
                leashY = coords[1];
                leashZ = coords[2];
            }
        }
        if (supportsSaddle) {
            this.saddledEntry = new BooleanEntryModel(this, ModTexts.SADDLED, saddled, value -> {
            });
            this.saddleItemEntry = new EntitySingleItemEntryModel(this, ModTexts.SADDLE_ITEM, saddleStack);
        }
        if (getEntity() instanceof AbstractChestedHorse) {
            boolean chestedHorse = data.getBooleanOr("ChestedHorse", false);
            this.chestedEntry = new BooleanEntryModel(this, ModTexts.CHESTED_HORSE, chestedHorse, value -> {
            });
        }
        this.leashHolderEntry = new StringEntryModel(this, ModTexts.LEASH_HOLDER, leashHolder, value -> {
        });
        this.leashAnchorEntry = new BooleanEntryModel(this, ModTexts.LEASH_ANCHOR, hasLeashAnchor, value -> {
        });
        this.leashXEntry = new DoubleEntryModel(this, ModTexts.LEASH_POS_X, leashX, value -> {
        });
        this.leashYEntry = new DoubleEntryModel(this, ModTexts.LEASH_POS_Y, leashY, value -> {
        });
        this.leashZEntry = new DoubleEntryModel(this, ModTexts.LEASH_POS_Z, leashZ, value -> {
        });
        if (this.saddledEntry != null) {
            getEntries().add(this.saddledEntry);
        }
        if (this.chestedEntry != null) {
            getEntries().add(this.chestedEntry);
        }
        if (this.saddleItemEntry != null) {
            getEntries().add(this.saddleItemEntry);
        }
        if (hasTemper()) {
            int temper = data.getIntOr("Temper", 0);
            this.temperEntry = new IntegerEntryModel(this, ModTexts.MOUNT_TEMPER, temper, value -> {
            });
            getEntries().add(this.temperEntry);
        }
        if (hasStrength()) {
            int strength = data.getIntOr("Strength", 1);
            this.strengthEntry = new IntegerEntryModel(this, ModTexts.MOUNT_STRENGTH, strength, value -> {
            });
            getEntries().add(this.strengthEntry);
        }
        getEntries().add(this.leashHolderEntry);
        getEntries().add(new ActionEntryModel(this, ModTexts.USE_SELF_UUID, this::setLeashHolderSelf));
        getEntries().add(this.leashAnchorEntry);
        getEntries().add(this.leashXEntry);
        getEntries().add(this.leashYEntry);
        getEntries().add(this.leashZEntry);
        this.passengerListStart = getEntries().size();
        ListTag passengers = (ListTag) data.getList("Passengers").orElseGet(ListTag::new);
        for (Tag tag : passengers) {
            if (tag instanceof CompoundTag) {
                CompoundTag passengerTag = (CompoundTag) tag;
                getEntries().add(createPassengerEntry(passengerTag));
            }
        }
    }

    private void setLeashHolderSelf() {
        if (Minecraft.getInstance().player == null) {
            return;
        }
        this.leashHolderEntry.setValue(Minecraft.getInstance().player.getUUID().toString());
    }

    private EntityEntryModel createPassengerEntry(CompoundTag passengerTag) {
        EntityType<?> type = null;
        String id = passengerTag.getString("id").orElse("");
        if (!id.isEmpty()) {
            type = (EntityType) BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.parse(id)).orElse(null);
        }
        EntityEntryModel model = new EntityEntryModel(this, type, passengerTag, value -> {
        });
        model.setReorderable(false);
        return model;
    }

    private ItemStack readSaddleItem(CompoundTag data) {
        ItemStack fromEquipment = data.getCompound(EQUIPMENT_TAG).flatMap(equipment -> equipment.getCompound(SADDLE_EQUIPMENT_KEY)).map(tag -> ClientUtil.parseItemStack(ClientUtil.registryAccess(), tag)).orElse(ItemStack.EMPTY);
        if (!fromEquipment.isEmpty()) {
            return fromEquipment;
        }
        return data.getCompound("SaddleItem").map(ClientUtil::parseItemStack).orElse(ItemStack.EMPTY);
    }

    private String readLeashHolder(CompoundTag data) {
        CompoundTag leash = (CompoundTag) data.getCompound("leash").map(value -> value.copy()).orElse(null);
        if (leash == null) {
            return "";
        }
        UUID uuid = NbtUuidHelper.getUuid(leash, "UUID");
        return uuid != null ? uuid.toString() : "";
    }

    @Override 
    public int getEntryListStart() {
        return this.passengerListStart;
    }

    @Override 
    public boolean canAddEntryInList() {
        return true;
    }

    @Override 
    public EntityEntryModel createNewListEntry() {
        return createPassengerEntry(new CompoundTag());
    }

    @Override 
    protected MutableComponent getAddListEntryButtonTooltip() {
        return ModTexts.PASSENGERS;
    }

    @Override 
    public void apply() {
        super.apply();
        CompoundTag data = getData();
        if (data == null) {
            return;
        }
        applySaddleData(data);
        applyChestData(data);
        applyMountStats(data);
        applyLeashData(data);
        applyPassengers(data);
    }

    private void applySaddleData(CompoundTag data) {
        if (this.saddledEntry == null || this.saddleItemEntry == null) {
            return;
        }
        boolean saddled = Boolean.TRUE.equals(this.saddledEntry.getValue());
        ItemStack saddleStack = this.saddleItemEntry.getItemStack();
        CompoundTag equipmentTag = (CompoundTag) data.getCompound(EQUIPMENT_TAG).orElseGet(CompoundTag::new);
        if (saddled) {
            if (saddleStack.isEmpty()) {
                saddleStack = new ItemStack(Items.SADDLE);
                this.saddleItemEntry.setItemStack(saddleStack.copy());
            }
            CompoundTag saddleTag = ClientUtil.saveItemStack(ClientUtil.registryAccess(), saddleStack);
            equipmentTag.put(SADDLE_EQUIPMENT_KEY, saddleTag);
            data.put(EQUIPMENT_TAG, equipmentTag);
        } else {
            equipmentTag.remove(SADDLE_EQUIPMENT_KEY);
            if (equipmentTag.isEmpty()) {
                data.remove(EQUIPMENT_TAG);
            } else {
                data.put(EQUIPMENT_TAG, equipmentTag);
            }
            data.remove("Saddle");
            data.remove("Saddled");
            data.remove("SaddleItem");
            if (!saddleStack.isEmpty()) {
                this.saddleItemEntry.setItemStack(ItemStack.EMPTY);
            }
        }
        data.remove("Saddle");
        data.remove("Saddled");
        data.remove("SaddleItem");
    }

    private boolean supportsSaddle() {
        return getEntity() instanceof Pig
                || getEntity() instanceof Strider
                || getEntity() instanceof Camel
                || getEntity() instanceof AbstractHorse
                || getEntity() instanceof AbstractNautilus
                || getEntity() instanceof CopperGolem;
    }

    
    private void applyMountStats(CompoundTag data) {
        if (this.temperEntry != null) {
            Integer value = this.temperEntry.getValue();
            if (value == null) {
                data.remove("Temper");
            } else {
                data.putInt("Temper", clamp(value, 0, 100));
            }
        }
        if (this.strengthEntry != null) {
            Integer value = this.strengthEntry.getValue();
            if (value == null) {
                data.remove("Strength");
            } else {
                data.putInt("Strength", clamp(value, 1, 5));
            }
        }
    }

    private void applyChestData(CompoundTag data) {
        if (this.chestedEntry == null) {
            return;
        }
        if (Boolean.TRUE.equals(this.chestedEntry.getValue())) {
            data.putBoolean("ChestedHorse", true);
        } else {
            data.remove("ChestedHorse");
        }
    }

    
    private void applyLeashData(CompoundTag data) {
        data.remove("Leash");
        data.remove("LeashHolder");
        String leashHolder = normalize(this.leashHolderEntry.getValue());
        if (!leashHolder.isEmpty() && isUuidString(leashHolder)) {
            CompoundTag leash = new CompoundTag();
            NbtUuidHelper.putUuid(leash, "UUID", UUID.fromString(leashHolder));
            data.put("leash", leash);
            return;
        }
        boolean anchorEnabled = Boolean.TRUE.equals(this.leashAnchorEntry.getValue());
        if (!anchorEnabled) {
            data.remove("leash");
            return;
        }
        int[] coords = new int[]{
                toBlockCoord(this.leashXEntry.getValue()),
                toBlockCoord(this.leashYEntry.getValue()),
                toBlockCoord(this.leashZEntry.getValue())
        };
        data.put("leash", new IntArrayTag(coords));
    }

    private static int toBlockCoord(Double value) {
        double v = value == null ? 0.0d : value.doubleValue();
        return (int) Math.round(v);
    }

    private void applyPassengers(CompoundTag data) {
        ListTag passengers = new ListTag();
        int listStart = getEntryListStart();
        if (listStart >= 0) {
            for (int i = listStart; i < getEntries().size(); i++) {
                EntryModel entry = getEntries().get(i);
                if (entry instanceof EntityEntryModel) {
                    EntityEntryModel passenger = (EntityEntryModel) entry;
                    CompoundTag tag = passenger.copyValue();
                    if (!tag.isEmpty()) {
                        passengers.add(tag);
                    }
                }
            }
        }
        if (passengers.isEmpty()) {
            data.remove("Passengers");
        } else {
            data.put("Passengers", passengers);
        }
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
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

    private boolean hasTemper() {
        if (getEntity() instanceof AbstractHorse) {
            return true;
        }
        CompoundTag data = getData();
        return data != null && data.getInt("Temper").isPresent();
    }

    private boolean hasStrength() {
        if (getEntity() instanceof Llama) {
            return true;
        }
        CompoundTag data = getData();
        return data != null && data.getInt("Strength").isPresent();
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
