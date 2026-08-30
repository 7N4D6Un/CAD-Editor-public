package com.github.rinorsi.cadeditor.common.logic;

import com.github.franckyi.guapi.api.util.DebugMode;
import com.github.rinorsi.cadeditor.client.ClientConfiguration;
import com.github.rinorsi.cadeditor.common.CommonUtil;
import com.github.rinorsi.cadeditor.common.ModTexts;
import com.github.rinorsi.cadeditor.common.network.BlockEditorPacket;
import com.github.rinorsi.cadeditor.common.network.BlockInventoryItemEditorPacket;
import com.github.rinorsi.cadeditor.common.network.EntityEditorPacket;
import com.github.rinorsi.cadeditor.common.network.EntityInventoryItemEditorPacket;
import com.github.rinorsi.cadeditor.common.network.MainHandItemEditorPacket;
import com.github.rinorsi.cadeditor.common.network.PlayerInventoryItemEditorPacket;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetHealthPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntitySpawnRequest;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public final class ServerEditorUpdateLogic {
    private static final Component VERIFICATION_FAILED = Component.literal("Update verification failed; inventory re-synced. Please check the item.");
    private static final Logger LOGGER = LogManager.getLogger();

    private ServerEditorUpdateLogic() {
    }

    public static void onMainHandItemEditorUpdate(ServerPlayer player, MainHandItemEditorPacket.Update response) {
        ItemStack normalizedStack = normalize(response.getItemStack());
        if (!PermissionLogic.hasPermission(player)) {
            CommonUtil.showItemUpdateFailure(player, normalizedStack, ModTexts.errorPermissionDenied(ModTexts.ITEM));
            return;
        }
        try {
            int hotbarIdx = player.getInventory().getSelectedSlot();
            player.getInventory().setItem(hotbarIdx, normalizedStack.copy());
            player.setItemInHand(InteractionHand.MAIN_HAND, normalizedStack.copy());
            player.getInventory().setChanged();
            if (player.containerMenu != null) {
                player.containerMenu.broadcastChanges();
            }
            syncMainHand(player);
            forceInventorySync(player);
            queueMainHandVerification(player, normalizedStack);
        } catch (Exception e) {
            LOGGER.error("Failed to apply main hand item update for {}", player.getName().getString(), e);
            CommonUtil.showItemUpdateFailure(player, normalizedStack, ModTexts.Messages.ERROR_GENERIC);
        }
    }

    public static void onPlayerInventoryItemEditorUpdate(ServerPlayer player, PlayerInventoryItemEditorPacket.Update response) {
        ItemStack normalizedStack = normalize(response.getItemStack());
        if (!PermissionLogic.hasPermission(player)) {
            CommonUtil.showItemUpdateFailure(player, normalizedStack, ModTexts.errorPermissionDenied(ModTexts.ITEM));
            return;
        }
        try {
            player.getInventory().setItem(response.getSlot(), normalizedStack.copy());
            player.getInventory().setChanged();
            if (player.containerMenu != null) {
                player.containerMenu.broadcastChanges();
            }
            syncInventorySlot(player, response.getSlot());
            forceInventorySync(player);
            queueInventoryVerification(player, response.getSlot(), normalizedStack);
        } catch (Exception e) {
            LOGGER.error("Failed to apply inventory item update for {} (slot {})", player.getName().getString(), response.getSlot(), e);
            CommonUtil.showItemUpdateFailure(player, normalizedStack, ModTexts.Messages.ERROR_GENERIC);
        }
    }

    public static void onBlockInventoryItemEditorUpdate(ServerPlayer player, BlockInventoryItemEditorPacket.Update response) {
        ItemStack normalizedStack = normalize(response.getItemStack());
        if (!PermissionLogic.hasPermission(player)) {
            CommonUtil.showItemUpdateFailure(player, normalizedStack, ModTexts.errorPermissionDenied(ModTexts.ITEM));
            return;
        }
        ServerLevel level = player.level();
        BlockPos pos = response.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (level.getBlockEntity(pos) instanceof Container container) {
            try {
                container.setItem(response.getSlot(), normalizedStack.copy());
                container.setChanged();
                level.sendBlockUpdated(pos, state, state, 2);
                CommonUtil.showItemUpdateSuccess(player, normalizedStack);
                if (player.containerMenu != null) {
                    player.containerMenu.broadcastChanges();
                }
                return;
            } catch (Exception e) {
                LOGGER.error("Failed to update block inventory at {} (slot {}) for {}", pos, response.getSlot(), player.getName().getString(), e);
                CommonUtil.showItemUpdateFailure(player, normalizedStack, ModTexts.Messages.ERROR_GENERIC);
                return;
            }
        }
        CommonUtil.showItemUpdateFailure(player, normalizedStack, Component.translatable("cadeditor.message.no_target_found", new Object[]{ModTexts.ITEM}));
    }

    public static void onEntityInventoryItemEditorUpdate(ServerPlayer player, EntityInventoryItemEditorPacket.Update response) {
        ItemStack normalizedStack = normalize(response.getItemStack());
        if (!PermissionLogic.hasPermission(player)) {
            CommonUtil.showItemUpdateFailure(player, normalizedStack, ModTexts.errorPermissionDenied(ModTexts.ITEM));
            return;
        }
        ServerLevel level = player.level();
        if (level.getEntity(response.getEntityId()) instanceof Container container) {
        
            try {
                container.setItem(response.getSlot(), normalizedStack.copy());
                container.setChanged();
                CommonUtil.showItemUpdateSuccess(player, normalizedStack);
                if (player.containerMenu != null) {
                    player.containerMenu.broadcastChanges();
                }
                return;
            } catch (Exception e) {
                LOGGER.error("Failed to update entity inventory for entity {} (slot {})", response.getEntityId(), response.getSlot(), e);
                CommonUtil.showItemUpdateFailure(player, normalizedStack, ModTexts.Messages.ERROR_GENERIC);
                return;
            }
        }
        CommonUtil.showItemUpdateFailure(player, normalizedStack, Component.translatable("cadeditor.message.no_target_found", new Object[]{ModTexts.ITEM}));
    }

    public static void onBlockEditorUpdate(ServerPlayer player, BlockEditorPacket.Update update) {
        if (!PermissionLogic.hasPermission(player)) {
            CommonUtil.showPermissionError(player, ModTexts.BLOCK);
            return;
        }
        ServerLevel level = player.level();
        BlockPos pos = update.getBlockPos();
        BlockState oldState = level.getBlockState(pos);
        if (isInfoDebugEnabled()) {
            LOGGER.info("[CAD-Editor][BlockUpdate][incoming] player={} pos={} state={} {}", player.getName().getString(), pos, update.getBlockState(), summarizeBlockTag(update.getTag()));
        }
        try {
            int flags = selectBlockUpdateFlags(oldState, update.getBlockState());
            level.setBlock(pos, update.getBlockState(), flags);
            BlockState currentState = level.getBlockState(pos);
            if (update.getTag() != null) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity == null) {
                    CommonUtil.showTargetError(player, ModTexts.BLOCK);
                    return;
                }
                ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, player.registryAccess(), update.getTag());
                blockEntity.loadWithComponents(input);
                blockEntity.setChanged();
                if (isInfoDebugEnabled()) {
                    CompoundTag appliedTag = saveBlockEntityTag(player, blockEntity);
                    LOGGER.info("[CAD-Editor][BlockUpdate][applied] player={} pos={} {}", player.getName().getString(), pos, summarizeBlockTag(appliedTag));
                }
            }
            level.sendBlockUpdated(pos, oldState, currentState, 2);
            CommonUtil.showUpdateSuccess(player, ModTexts.BLOCK);
        } catch (Exception e) {
            LOGGER.error("Failed to update block at {} for {}", pos, player.getName().getString(), e);
            CommonUtil.showMessage(player, ModTexts.Messages.ERROR_GENERIC);
        }
    }

    public static void onEntityEditorUpdate(ServerPlayer player, EntityEditorPacket.Update update) {
        if (!PermissionLogic.hasPermission(player)) {
            CommonUtil.showPermissionError(player, ModTexts.ENTITY);
            return;
        }
        ServerLevel level = player.level();
        if (level.getEntity(update.getEntityId()) instanceof LivingEntity entity) {
        
            try {
                boolean passengersDefined = false;
                ListTag requestedPassengers = null;
                if (update.getTag() != null) {
                    Optional<ListTag> passengersOpt = update.getTag().getList("Passengers");
                    if (passengersOpt.isPresent()) {
                        passengersDefined = true;
                        ListTag list = passengersOpt.get();
                        requestedPassengers = list.isEmpty() ? null : list.copy();
                    }
                }
                float requestedHealth = update.getTag() == null ? Float.NaN : update.getTag().getFloatOr("Health", Float.NaN);
                if (entity instanceof LivingEntity) {
                    LivingEntity livingBefore = entity;
                    logLivingEntityState("before_load", livingBefore, requestedHealth);
                }
                ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, player.level().registryAccess(), update.getTag());
                entity.load(input);
                if (passengersDefined) {
                    if (entity.level() instanceof ServerLevel serverLevel) {
                        if (requestedPassengers != null) {
                            rebuildPassengers(serverLevel, entity, requestedPassengers);
                        } else {
                            clearPassengers(entity);
                        }
                    }
                }
                if (entity instanceof LivingEntity) {
                    LivingEntity livingAfterLoad = entity;
                    applyRequestedAttributes(livingAfterLoad, update.getTag());
                    logLivingEntityState("after_load", livingAfterLoad, requestedHealth);
                    if (!Float.isNaN(requestedHealth)) {
                        applyRequestedHealth(livingAfterLoad, requestedHealth);
                        logLivingEntityState("after_applied", livingAfterLoad, requestedHealth);
                    }
                }
                if (entity instanceof ServerPlayer) {
                    applyRequestedPlayerAbilities((ServerPlayer) entity, update.getTag());
                }
                try {
                    List<SynchedEntityData.DataValue<?>> nonDefaultValues = entity.getEntityData().getNonDefaultValues();
                    if (!nonDefaultValues.isEmpty()) {
                        ClientboundSetEntityDataPacket dataPacket = new ClientboundSetEntityDataPacket(entity.getId(), nonDefaultValues);
                        if (entity.level() instanceof ServerLevel serverLevel) {
                                                        serverLevel.getChunkSource().sendToTrackingPlayersAndSelf(entity, dataPacket);
                        }
                        if (entity instanceof ServerPlayer) {
                            ((ServerPlayer) entity).connection.send(dataPacket);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.warn("Failed to send entity data packet for entity {}", entity.getId(), e);
                }
                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity = entity;
                    try {
                        ClientboundUpdateAttributesPacket attributesPacket = new ClientboundUpdateAttributesPacket(entity.getId(), livingEntity.getAttributes().getSyncableAttributes());
                        if (entity.level() instanceof ServerLevel serverLevel) {
                                                        serverLevel.getChunkSource().sendToTrackingPlayersAndSelf(entity, attributesPacket);
                        }
                        if (entity instanceof ServerPlayer) {
                            ServerPlayer targetPlayer = (ServerPlayer) entity;
                            targetPlayer.connection.send(attributesPacket);
                            targetPlayer.connection.send(new ClientboundSetHealthPacket(targetPlayer.getHealth(), targetPlayer.getFoodData().getFoodLevel(), targetPlayer.getFoodData().getSaturationLevel()));
                        }
                    } catch (Exception e) {
                        LOGGER.warn("Failed to send attributes packet for entity {}", entity.getId(), e);
                    }
                }
                logLivingEntityState("after_packets", entity instanceof LivingEntity ? entity : null, requestedHealth);
                CommonUtil.showUpdateSuccess(player, ModTexts.ENTITY);
                return;
            } catch (Exception e) {
                LOGGER.error("Failed to update entity {} for {}", update.getEntityId(), player.getName().getString(), e);
                CommonUtil.showMessage(player, ModTexts.Messages.ERROR_GENERIC);
                return;
            }
        }
        CommonUtil.showTargetError(player, ModTexts.ENTITY);
    }

    private static int toMenuSlotIndex(int invIndex) {
        return (invIndex < 0 || invIndex >= 9) ? invIndex : 36 + invIndex;
    }

    private static void syncMainHand(ServerPlayer player) {
        AbstractContainerMenu menu = player.containerMenu != null ? player.containerMenu : player.inventoryMenu;
        int stateId = menu.incrementStateId();
        int hotbarIdx = player.getInventory().getSelectedSlot();
        int menuSlot = 36 + hotbarIdx;
        player.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, stateId, menuSlot, player.getMainHandItem().copy()));
        player.connection.send(new ClientboundSetEquipmentPacket(player.getId(), List.of(Pair.of(EquipmentSlot.MAINHAND, player.getMainHandItem().copy()))));
    }

    private static void syncInventorySlot(ServerPlayer player, int invIndex) {
        AbstractContainerMenu menu = player.containerMenu != null ? player.containerMenu : player.inventoryMenu;
        int stateId = menu.incrementStateId();
        int menuSlot = toMenuSlotIndex(invIndex);
        player.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, stateId, menuSlot, player.getInventory().getItem(invIndex).copy()));
    }

    private static void forceInventorySync(ServerPlayer player) {
        AbstractContainerMenu menu = player.containerMenu != null ? player.containerMenu : player.inventoryMenu;
        int stateId = menu.incrementStateId();
        player.connection.send(new ClientboundContainerSetContentPacket(menu.containerId, stateId, menu.getItems(), menu.getCarried()));
        player.connection.send(new ClientboundSetEquipmentPacket(player.getId(), List.of(Pair.of(EquipmentSlot.MAINHAND, player.getMainHandItem().copy()), Pair.of(EquipmentSlot.OFFHAND, player.getOffhandItem().copy()))));
    }

    private static void queueMainHandVerification(ServerPlayer player, ItemStack expected) {
        ItemStack expectedCopy = expected.copy();
        scheduleVerification(player, () -> {
            ItemStack current = player.getMainHandItem();
            if (areStacksEquivalent(current, expectedCopy)) {
                CommonUtil.showItemUpdateSuccess(player, expectedCopy);
                return;
            }
            logStackDiff("MainHand", current, expectedCopy, player);
            forceInventorySync(player);
            CommonUtil.showItemUpdateFailure(player, expectedCopy, VERIFICATION_FAILED);
        });
    }

    private static void queueInventoryVerification(ServerPlayer player, int slot, ItemStack expected) {
        int size = player.getInventory().getContainerSize();
        if (slot < 0 || slot >= size) {
            CommonUtil.showItemUpdateFailure(player, expected, Component.literal("Invalid slot: " + slot));
        } else {
            ItemStack expectedCopy = expected.copy();
            scheduleVerification(player, () -> {
                ItemStack current = player.getInventory().getItem(slot);
                if (areStacksEquivalent(current, expectedCopy)) {
                    CommonUtil.showItemUpdateSuccess(player, expectedCopy);
                    return;
                }
                logStackDiff("InventorySlot", current, expectedCopy, player);
                forceInventorySync(player);
                CommonUtil.showItemUpdateFailure(player, expectedCopy, VERIFICATION_FAILED);
            });
        }
    }

    private static void scheduleVerification(ServerPlayer player, Runnable action) {
        MinecraftServer server = player.level().getServer();
        server.execute(() -> {
            server.execute(action);
        });
    }

    private static void logStackDiff(String where, ItemStack actual, ItemStack expected, ServerPlayer player) {
        try {
            NbtOps ops = NbtOps.INSTANCE;
            Tag actualData = (Tag) ItemStack.CODEC.encodeStart(ops, actual).result().orElse(null);
            Tag expectedData = (Tag) ItemStack.CODEC.encodeStart(ops, expected).result().orElse(null);
            LOGGER.warn("[{}] stack mismatch for {}. Actual={}, Expected={}", where, player.getName().getString(), actualData, expectedData);
        } catch (Exception ex) {
            LOGGER.warn("Failed to diff stacks for {}", player.getName().getString(), ex);
        }
    }

    private static ItemStack normalize(ItemStack input) {
        if (input.isEmpty()) {
            return ItemStack.EMPTY;
        }
        NbtOps ops = NbtOps.INSTANCE;
        return (ItemStack) ItemStack.CODEC.encodeStart(ops, input).result().flatMap(data -> ItemStack.CODEC.parse(ops, data).result()).orElse(input.copy());
    }

    private static boolean areStacksEquivalent(ItemStack a, ItemStack b) {
        if (a.isEmpty() && b.isEmpty()) {
            return true;
        }
        return !a.isEmpty() && !b.isEmpty() && a.getCount() == b.getCount() && ItemStack.isSameItemSameComponents(a, b);
    }

    private static void logLivingEntityState(String stage, LivingEntity livingEntity, float requestedHealth) {
        if (!isFeatureDebugEnabled()) {
            return;
        }
        try {
            LOGGER.info("[CAD-Editor][EntityUpdate][{}] entity='{}' id={} uuid={} health={} max={} requestedHealth={}", stage, livingEntity.getName().getString(), livingEntity.getId(), livingEntity.getUUID(), livingEntity.getHealth(), livingEntity.getMaxHealth(), requestedHealth);
        } catch (Exception e) {
        }
    }

    private static void applyRequestedHealth(LivingEntity livingEntity, float requestedHealth) {
        float sanitized = requestedHealth <= 0.0f ? 1.0E-4f : requestedHealth;
        AttributeInstance maxHealth = livingEntity.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(sanitized);
        }
        livingEntity.setHealth(Math.min(sanitized, livingEntity.getMaxHealth()));
    }

    private static void applyRequestedAttributes(LivingEntity livingEntity, CompoundTag updateTag) {
        ListTag attributes;
        if (updateTag == null || (attributes = readRequestedAttributes(updateTag)) == null || attributes.isEmpty()) {
            return;
        }
        Optional<Registry<Attribute>> lookupOpt = livingEntity.registryAccess().lookup(Registries.ATTRIBUTE);
        if (lookupOpt.isEmpty()) {
            return;
        }
        HolderLookup.RegistryLookup<Attribute> lookup = (HolderLookup.RegistryLookup) lookupOpt.get();
        for (Tag attributeElement : attributes) {
    if (!(attributeElement instanceof CompoundTag compoundTag)) {
        continue;
    }
            if (compoundTag instanceof CompoundTag) {
                CompoundTag attributeTag = compoundTag;
                applyRequestedAttribute(livingEntity, lookup, attributeTag);
            }
        }
    }

    private static void applyRequestedAttribute(LivingEntity livingEntity, HolderLookup.RegistryLookup<Attribute> lookup, CompoundTag attributeTag) {
        Identifier id;
        AttributeInstance instance;
        String idString = readAttributeId(attributeTag);
        if (idString.isEmpty() || (id = Identifier.tryParse(idString)) == null) {
            return;
        }
        double baseValue = readAttributeBase(attributeTag);
        if (!Double.isFinite(baseValue)) {
            return;
        }
        ResourceKey<Attribute> key = ResourceKey.create(Registries.ATTRIBUTE, id);
        Optional<Holder.Reference<Attribute>> holderOpt = lookup.get(key);
        if (holderOpt.isEmpty() || (instance = livingEntity.getAttribute((Holder) holderOpt.get())) == null) {
            return;
        }
        instance.setBaseValue(baseValue);
    }

    private static ListTag readRequestedAttributes(CompoundTag root) {
        if (root.contains("attributes")) {
            return (ListTag) root.getList("attributes").orElse(null);
        }
        if (root.contains("Attributes")) {
            return (ListTag) root.getList("Attributes").orElse(null);
        }
        return null;
    }

    private static String readAttributeId(CompoundTag attributeTag) {
        String id = attributeTag.getStringOr("id", "");
        if (id.isBlank()) {
            id = attributeTag.getStringOr("Name", "");
        }
        if (id.startsWith("minecraft:generic.")) {
            return "minecraft:" + id.substring("minecraft:generic.".length());
        }
        if (id.startsWith("generic.")) {
            return "minecraft:" + id.substring("generic.".length());
        }
        return id;
    }

    private static double readAttributeBase(CompoundTag attributeTag) {
        if (attributeTag.contains("base")) {
            return attributeTag.getDoubleOr("base", Double.NaN);
        }
        return attributeTag.getDoubleOr("Base", Double.NaN);
    }

    private static void applyRequestedPlayerAbilities(ServerPlayer player, CompoundTag updateTag) {
        CompoundTag requested = readRequestedAbilities(updateTag);
        if (requested == null) {
            return;
        }
        Abilities abilities = player.getAbilities();
        if (requested.contains("invulnerable")) {
            abilities.invulnerable = requested.getBooleanOr("invulnerable", abilities.invulnerable);
        }
        if (requested.contains("mayfly")) {
            abilities.mayfly = requested.getBooleanOr("mayfly", abilities.mayfly);
        }
        if (requested.contains("flying")) {
            abilities.flying = requested.getBooleanOr("flying", abilities.flying) && abilities.mayfly;
        } else if (!abilities.mayfly) {
            abilities.flying = false;
        }
        if (requested.contains("mayBuild")) {
            abilities.mayBuild = requested.getBooleanOr("mayBuild", abilities.mayBuild);
        }
        if (requested.contains("instabuild")) {
            abilities.instabuild = requested.getBooleanOr("instabuild", abilities.instabuild);
        }
        if (requested.contains("walkSpeed")) {
            abilities.setWalkingSpeed(clampAbilitySpeed(requested.getFloatOr("walkSpeed", 0.1f)));
        }
        if (requested.contains("flySpeed")) {
            abilities.setFlyingSpeed(clampAbilitySpeed(requested.getFloatOr("flySpeed", 0.05f)));
        }
        player.onUpdateAbilities();
    }

    private static CompoundTag readRequestedAbilities(CompoundTag root) {
        if (root == null) {
            return null;
        }
        if (root.contains("abilities")) {
            return root.getCompound("abilities").map(value -> value.copy()).orElse(null);
        }
        if (root.contains("Abilities")) {
            return root.getCompound("Abilities").map(value -> value.copy()).orElse(null);
        }
        return null;
    }

    private static float clampAbilitySpeed(float value) {
        return Math.max(0.0f, Math.min(value, 1.0f));
    }

    private static int selectBlockUpdateFlags(BlockState oldState, BlockState newState) {
        if (oldState.getBlock() == newState.getBlock()) {
            return 2;
        }
        return 3;
    }

    private static final Set<String> VOLATILE_PASSENGER_KEYS = Set.of(
        "Pos",
        "Motion",
        "Rotation",
        "Air",
        "Fire",
        "FallDistance",
        "OnGround",
        "PortalCooldown",
        "TicksFrozen"
    );

    private static void rebuildPassengers(ServerLevel level, Entity root, ListTag passengers) {
        List<Entity> currentPassengers = List.copyOf(root.getPassengers());
        Map<UUID, Entity> existingById = new HashMap<>();
        for (Entity passenger : currentPassengers) {
            existingById.put(passenger.getUUID(), passenger);
        }
        Set<UUID> retainedIds = new HashSet<>();
        List<CompoundTag> tagsToSpawn = new ArrayList<>();
        for (int i = 0; i < passengers.size(); i++) {
            if (!(passengers.get(i) instanceof CompoundTag passengerTag)) {
                continue;
            }
            UUID uuid = readPassengerUuid(passengerTag);
            Entity existing = uuid == null ? null : existingById.get(uuid);
            if (existing != null && isPassengerUnchanged(level, existing, passengerTag)) {
                retainedIds.add(uuid);
            } else {
                tagsToSpawn.add(passengerTag);
            }
        }
        for (Entity passenger : currentPassengers) {
            if (retainedIds.contains(passenger.getUUID())) {
                continue;
            }
            passenger.stopRiding();
            if (!(passenger instanceof ServerPlayer)) {
                passenger.discard();
            }
        }
        for (CompoundTag passengerTag : tagsToSpawn) {
            Entity passenger = EntityType.loadEntityRecursive(passengerTag, level, new EntitySpawnRequest(EntitySpawnReason.COMMAND, false), entity -> {
                Vec3 attachPos = root.getPassengerRidingPosition(entity);
                Vec3 attachOffset = entity.getVehicleAttachmentPoint(root);
                entity.snapTo(attachPos.x - attachOffset.x, attachPos.y - attachOffset.y, attachPos.z - attachOffset.z, root.getYRot(), 0.0f);
                return level.addFreshEntity(entity) ? entity : null;
            });
            if (passenger != null) {
                passenger.startRiding(root, true, true);
            }
        }
    }

    private static boolean isPassengerUnchanged(ServerLevel level, Entity passenger, CompoundTag requestedTag) {
        try {
            TagValueOutput writer = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());
            passenger.saveWithoutId(writer);
            writer.putString("id", EntityType.getKey(passenger.getType()).toString());
            CompoundTag currentTag = writer.buildResult();
            stripVolatilePassengerTags(currentTag);
            CompoundTag requestedCopy = requestedTag.copy();
            stripVolatilePassengerTags(requestedCopy);
            return currentTag.equals(requestedCopy);
        } catch (Exception e) {
            return false;
        }
    }

    private static void stripVolatilePassengerTags(CompoundTag tag) {
        for (String key : VOLATILE_PASSENGER_KEYS) {
            tag.remove(key);
        }
    }

    private static UUID readPassengerUuid(CompoundTag tag) {
        int[] data = tag.getIntArray("UUID").orElse(null);
        if (data == null || data.length != 4) {
            return null;
        }
        long most = ((long) data[0] << 32) | (data[1] & 0xffffffffL);
        long least = ((long) data[2] << 32) | (data[3] & 0xffffffffL);
        return new UUID(most, least);
    }

    private static void clearPassengers(Entity root) {
        if (root.getPassengers().isEmpty()) {
            return;
        }
        for (Entity passenger : List.copyOf(root.getPassengers())) {
            passenger.stopRiding();
            if (!(passenger instanceof ServerPlayer)) {
                passenger.discard();
            }
        }
    }

    private static CompoundTag saveBlockEntityTag(ServerPlayer player, BlockEntity blockEntity) {
        if (blockEntity == null) {
            return null;
        }
        try {
            TagValueOutput writer = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, player.registryAccess());
            blockEntity.saveWithId(writer);
            return writer.buildResult();
        } catch (Exception e) {
            return null;
        }
    }

    private static String summarizeBlockTag(CompoundTag tag) {
        if (tag == null) {
            return "tag=null";
        }
        String blockEntityId = tag.getStringOr("id", "");
        CompoundTag spawnData = readCompound(tag, "spawn_data", "SpawnData");
        CompoundTag entityData = readCompound(spawnData, "entity", "Entity");
        String entityId = entityData.getStringOr("id", "");
        return "tagKeys=" + tag.size() + " blockEntityId=" + (blockEntityId.isEmpty() ? "-" : blockEntityId) + " spawnEntityId=" + (entityId.isEmpty() ? "-" : entityId) + " hasSpawnData=" + (!spawnData.isEmpty());
    }

    private static CompoundTag readCompound(CompoundTag tag, String primary, String secondary) {
        if (tag == null) {
            return new CompoundTag();
        }
        if (tag.contains(primary)) {
            return tag.getCompound(primary).map(value -> value.copy()).orElseGet(CompoundTag::new);
        }
        if (tag.contains(secondary)) {
            return tag.getCompound(secondary).map(value -> value.copy()).orElseGet(CompoundTag::new);
        }
        return new CompoundTag();
    }

    private static boolean isInfoDebugEnabled() {
        try {
            if (ClientConfiguration.INSTANCE == null) {
                return false;
            }
            DebugMode mode = ClientConfiguration.INSTANCE.getGuapiDebugMode();
            return mode == DebugMode.INFO || mode == DebugMode.FEATURE;
        } catch (Throwable th) {
            return false;
        }
    }

    private static boolean isFeatureDebugEnabled() {
        try {
            return ClientConfiguration.INSTANCE != null && ClientConfiguration.INSTANCE.getGuapiDebugMode() == DebugMode.FEATURE;
        } catch (Throwable th) {
            return false;
        }
    }
}