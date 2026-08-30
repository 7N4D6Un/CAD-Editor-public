package com.github.rinorsi.cadeditor.client.screen.model.category.entity.player;

import com.github.rinorsi.cadeditor.client.screen.model.EntityEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.entity.EntityCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.DoubleEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.FloatEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.IntegerEntryModel;
import com.github.rinorsi.cadeditor.client.util.NbtHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;


public class EntityPlayerPositionCategoryModel extends EntityCategoryModel {
    private double posX;
    private double posY;
    private double posZ;
    private double motionX;
    private double motionY;
    private double motionZ;
    private float rotY;
    private float rotX;
    private int fireTicks;
    private int airTicks;
    private float fallDistance;

    public EntityPlayerPositionCategoryModel(EntityEditorModel editor) {
        super(Component.translatable("cadeditor.gui.player_position"), editor);
    }

    @Override 
    protected void setupEntries() {
        readValues();
        getEntries().add(new DoubleEntryModel(this, Component.translatable("cadeditor.gui.player_pos_x"), this.posX, value -> this.posX = value));
        getEntries().add(new DoubleEntryModel(this, Component.translatable("cadeditor.gui.player_pos_y"), this.posY, value -> this.posY = value));
        getEntries().add(new DoubleEntryModel(this, Component.translatable("cadeditor.gui.player_pos_z"), this.posZ, value -> this.posZ = value));
        getEntries().add(new FloatEntryModel(this, Component.translatable("cadeditor.gui.player_rot_yaw"), this.rotY, value -> this.rotY = value));
        getEntries().add(new FloatEntryModel(this, Component.translatable("cadeditor.gui.player_rot_pitch"), this.rotX, value -> this.rotX = value));
        getEntries().add(new DoubleEntryModel(this, Component.translatable("cadeditor.gui.player_motion_x"), this.motionX, value -> this.motionX = value));
        getEntries().add(new DoubleEntryModel(this, Component.translatable("cadeditor.gui.player_motion_y"), this.motionY, value -> this.motionY = value));
        getEntries().add(new DoubleEntryModel(this, Component.translatable("cadeditor.gui.player_motion_z"), this.motionZ, value -> this.motionZ = value));
        getEntries().add(new IntegerEntryModel(this, Component.translatable("cadeditor.gui.player_fire"), this.fireTicks, value -> this.fireTicks = Math.max(0, value)));
        getEntries().add(new IntegerEntryModel(this, Component.translatable("cadeditor.gui.player_air"), this.airTicks, value -> this.airTicks = Math.max(0, value)));
        getEntries().add(new FloatEntryModel(this, Component.translatable("cadeditor.gui.player_fall_distance"), this.fallDistance, value -> this.fallDistance = Math.max(0.0f, value)));
    }

    @Override 
    public void apply() {
        super.apply();
        writeValues();
        syncPlayerInstance();
    }

    private void readValues() {
        CompoundTag data = ensurePlayerTag();
        ListTag posList = NbtHelper.getListOrEmpty(data, "Pos");
        if (posList.size() == 3) {
            this.posX = NbtHelper.getListDouble(posList, 0, this.posX);
            this.posY = NbtHelper.getListDouble(posList, 1, this.posY);
            this.posZ = NbtHelper.getListDouble(posList, 2, this.posZ);
        }
        ListTag rotList = NbtHelper.getListOrEmpty(data, "Rotation");
        if (rotList.size() == 2) {
            this.rotY = NbtHelper.getListFloat(rotList, 0, this.rotY);
            this.rotX = NbtHelper.getListFloat(rotList, 1, this.rotX);
        }
        ListTag motionList = NbtHelper.getListOrEmpty(data, "Motion");
        if (motionList.size() == 3) {
            this.motionX = NbtHelper.getListDouble(motionList, 0, this.motionX);
            this.motionY = NbtHelper.getListDouble(motionList, 1, this.motionY);
            this.motionZ = NbtHelper.getListDouble(motionList, 2, this.motionZ);
        }
        this.fireTicks = NbtHelper.getInt(data, "Fire", 0);
        this.airTicks = NbtHelper.getInt(data, "Air", 0);
        this.fallDistance = NbtHelper.getFloat(data, "FallDistance", 0.0f);
    }

    private void writeValues() {
        CompoundTag data = ensurePlayerTag();
        ListTag pos = new ListTag();
        pos.add(DoubleTag.valueOf(this.posX));
        pos.add(DoubleTag.valueOf(this.posY));
        pos.add(DoubleTag.valueOf(this.posZ));
        data.put("Pos", pos);
        ListTag rotation = new ListTag();
        rotation.add(FloatTag.valueOf(this.rotY));
        rotation.add(FloatTag.valueOf(this.rotX));
        data.put("Rotation", rotation);
        ListTag motion = new ListTag();
        motion.add(DoubleTag.valueOf(this.motionX));
        motion.add(DoubleTag.valueOf(this.motionY));
        motion.add(DoubleTag.valueOf(this.motionZ));
        data.put("Motion", motion);
        data.putInt("Fire", Math.max(0, this.fireTicks));
        data.putInt("Air", Math.max(0, this.airTicks));
        if (this.fallDistance > 0.0f) {
            data.putFloat("FallDistance", this.fallDistance);
        } else {
            data.remove("FallDistance");
        }
    }

    private void syncPlayerInstance() {
        Player player = getEntity() instanceof Player p ? p : null;
        if (player == null) {
            return;
        }
        player.setPos(this.posX, this.posY, this.posZ);
        player.setDeltaMovement(this.motionX, this.motionY, this.motionZ);
        player.setYRot(this.rotY);
        player.setXRot(this.rotX);
        player.setYHeadRot(this.rotY);
        player.setYBodyRot(this.rotY);
        player.setRemainingFireTicks(Math.max(0, this.fireTicks));
        player.setAirSupply(Math.max(0, this.airTicks));
        player.fallDistance = Math.max(0.0f, this.fallDistance);
    }

    private CompoundTag ensurePlayerTag() {
        CompoundTag data = getData();
        if (data == null) {
            data = new CompoundTag();
            getContext().setTag(data);
        }
        return data;
    }
}
