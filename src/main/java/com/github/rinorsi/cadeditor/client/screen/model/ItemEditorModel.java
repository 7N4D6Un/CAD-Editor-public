package com.github.rinorsi.cadeditor.client.screen.model;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.context.ItemEditorContext;
import com.github.rinorsi.cadeditor.client.debug.DebugLog;
import com.github.rinorsi.cadeditor.client.screen.model.category.CategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.EditorCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.FoodComponentState;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemAttributeModifiersCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemBeehiveCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemBannerPatternCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemBucketEntityCategoryModel;import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemBundleContentsCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemConsumableCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemContainerCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemContainerGridCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemCrossbowCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemArrowCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemCustomDataCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemDeathProtectionCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemDisplayCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemDyeColorCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemDyeableCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemEnchantmentsCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemEquippableCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemExtraComponentsCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemExtraToggle;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemFireworkStarCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemFireworksCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemFoodEffectsCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemGeneralCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemHideFlagsCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemInstrumentCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemLodestoneCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemMapCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemMapDecorationsCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemOminousBottleCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemPotDecorationsCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemProvidesBannerPatternCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemProvidesTrimMaterialCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemPotionEffectsCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemProfileCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemSpawnEggCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemSpawnerCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemSuspiciousStewEffectsCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemToolCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemWeaponCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemUseBehaviorCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemUseRemainderCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemTrialSpawnerCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemRecipesCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ConsumableState;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemCommandBlockCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.UseRemainderState;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemSignCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemWritableBookPagesCategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.item.ItemWrittenBookCategoryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TippedArrowItem;
import net.minecraft.world.item.component.Consumable;

import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.DropperBlock;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ItemEditorModel extends StandardEditorModel {
    private static final String KEY_COMPONENTS = "components";
    private static final String KEY_LEGACY_TAG = "tag";
    private static final String TOMBSTONE_PREFIX = "!";
    private static final String POTION_CONTENTS_COMPONENT_KEY = "minecraft:potion_contents";
    private static final String WRITABLE_BOOK_COMPONENT_KEY = "minecraft:writable_book_content";
    private static final String SHOW_IN_TOOLTIP_FIELD = "show_in_tooltip";
    private static final String LEVELS_FIELD = "levels";

    private static final int MAX_DURATION_TICKS = 72000;
    private static final int MAX_AMPLIFIER = 255;
    private final FoodComponentState foodState;
    private final ConsumableState consumableState;
    private final UseRemainderState useRemainderState;
    private final EnumMap<ItemExtraToggle, Boolean> extraComponentStates;
    private boolean desiredUseRemainderEnabled;
    private boolean consumableBehaviorEnabled;
    private boolean foodBehaviorEnabled;
    private boolean deathProtectionBehaviorEnabled;
    private boolean shouldSyncExtraComponentsFromStack;
    private ItemGeneralCategoryModel generalCategory;
    private ItemExtraComponentsCategoryModel extraComponentsCategory;
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String FOOD_COMPONENT_KEY = "minecraft:food";
    private static final String CONSUMABLE_COMPONENT_KEY = "minecraft:consumable";
    private static final String USE_REMAINDER_COMPONENT_KEY = "minecraft:use_remainder";

    private static final String HIDE_TOOLTIP_COMPONENT_KEY = "minecraft:hide_tooltip";
    private static final String TOOLTIP_DISPLAY_COMPONENT_KEY = "minecraft:tooltip_display";
    private static final Set<String> DELETE_IF_ABSENT_KEYS = Set.of(HIDE_TOOLTIP_COMPONENT_KEY, TOOLTIP_DISPLAY_COMPONENT_KEY, "minecraft:enchantment_glint_override", "minecraft:custom_name");
    private static final Set<String> COMPONENTS_WITH_TOOLTIP_BOOLEAN = Set.of("minecraft:enchantments", "minecraft:stored_enchantments", "minecraft:attribute_modifiers", "minecraft:unbreakable", "minecraft:can_break", "minecraft:can_place_on", "minecraft:dyed_color", "minecraft:trim", "minecraft:jukebox_playable");
    private static final Set<String> TOMBSTONE_COMPONENT_IDS = Set.of("minecraft:lore", "minecraft:attribute_modifiers", "minecraft:rarity", "minecraft:repair_cost", "minecraft:enchantments", "minecraft:stored_enchantments");

    public ItemEditorModel(ItemEditorContext context) {
        super(context);
        this.foodState = new FoodComponentState();
        this.consumableState = new ConsumableState();
        this.useRemainderState = new UseRemainderState();
        this.extraComponentStates = new EnumMap<>(ItemExtraToggle.class);
        this.shouldSyncExtraComponentsFromStack = true;
        resetExtraComponentStates();
    }

    @Override 
    public ItemEditorContext getContext() {
        return (ItemEditorContext) super.getContext();
    }

    @Override 
    public void initalize() {
        super.initalize();
        getCategories().forEach(category -> {
            category.initalize();
        });
    }

    @Override 
    protected void setupCategories() {
        ItemStack stack = getContext().getItemStack();
        Item item = stack.getItem();
        if (this.shouldSyncExtraComponentsFromStack) {
            syncExtraComponentStatesFromStack(stack);
            this.shouldSyncExtraComponentsFromStack = false;
        }
        this.foodBehaviorEnabled = stack.has(DataComponents.FOOD);
        this.consumableBehaviorEnabled = stack.has(DataComponents.CONSUMABLE);
        this.desiredUseRemainderEnabled = stack.has(DataComponents.USE_REMAINDER);
        this.deathProtectionBehaviorEnabled = stack.has(DataComponents.DEATH_PROTECTION);
        this.foodState.loadFrom(stack);
        this.consumableState.loadFrom(stack);
        this.useRemainderState.loadFrom(stack);
        this.generalCategory = new ItemGeneralCategoryModel(this);
        getCategories().add(this.generalCategory);
        getCategories().add(new ItemDisplayCategoryModel(this));
        getCategories().add(new ItemWeaponCategoryModel(this));
        getCategories().add(new ItemEquippableCategoryModel(this));
        getCategories().add(new ItemToolCategoryModel(this));
        getCategories().add(new ItemEnchantmentsCategoryModel(this));
        getCategories().add(new ItemFoodEffectsCategoryModel(this));
        getCategories().add(new ItemConsumableCategoryModel(this));
        getCategories().add(new ItemUseRemainderCategoryModel(this));
        getCategories().add(new ItemUseBehaviorCategoryModel(this));
        getCategories().add(new ItemDeathProtectionCategoryModel(this));
        getCategories().add(new ItemAttributeModifiersCategoryModel(this));
        getCategories().add(new ItemHideFlagsCategoryModel(this));
        this.extraComponentsCategory = new ItemExtraComponentsCategoryModel(this);
        getCategories().add(this.extraComponentsCategory);
        if (stack.has(DataComponents.DYED_COLOR)) {
            getCategories().add(new ItemDyeableCategoryModel(this));
        }
        if (item instanceof DyeItem) {
            getCategories().add(new ItemDyeColorCategoryModel(this));
        }
        if (item instanceof SpawnEggItem spawnEgg) {
            getCategories().add(new ItemSpawnEggCategoryModel(this, spawnEgg));
        }
        if (item == Items.SPAWNER) {
            getCategories().add(new ItemSpawnerCategoryModel(this));
        }
        if (item == Items.TRIAL_SPAWNER) {
            getCategories().add(new ItemTrialSpawnerCategoryModel(this));
        }
        if (item == Items.KNOWLEDGE_BOOK) {
            getCategories().add(new ItemRecipesCategoryModel(this));
        }
        if (isCommandBlockItem(item)) {
            getCategories().add(new ItemCommandBlockCategoryModel(this));
        }
        if (isSignItem(item)) {
            getCategories().add(new ItemSignCategoryModel(this));
        }
        if ((item instanceof MapItem) || stack.has(DataComponents.MAP_ID) || stack.has(DataComponents.MAP_COLOR) || stack.has(DataComponents.MAP_DECORATIONS) || stack.has(DataComponents.MAP_POST_PROCESSING)) {
            getCategories().add(new ItemMapCategoryModel(this));
            getCategories().add(new ItemMapDecorationsCategoryModel(this));
        }
        if ((item instanceof CrossbowItem) || stack.has(DataComponents.CHARGED_PROJECTILES)) {
            getCategories().add(new ItemCrossbowCategoryModel(this));
        }
        if (stack.is(ItemTags.ARROWS)) {
            getCategories().add(new ItemArrowCategoryModel(this));
        }
        if (stack.has(DataComponents.BUNDLE_CONTENTS) || item == Items.BUNDLE) {
            getCategories().add(new ItemBundleContentsCategoryModel(this));
        }
        if ((item instanceof CompassItem) || stack.has(DataComponents.LODESTONE_TRACKER)) {
            getCategories().add(new ItemLodestoneCategoryModel(this));
        }
        if (item == Items.SUSPICIOUS_STEW) {
            getCategories().add(new ItemSuspiciousStewEffectsCategoryModel(this));
        }
        if (item == Items.PLAYER_HEAD) {
            getCategories().add(new ItemProfileCategoryModel(this));
        }
        boolean isContainerBlockItem = false;
        if (item instanceof BlockItem) {
            BlockItem bi = (BlockItem) item;
            Block block = bi.getBlock();
            isContainerBlockItem = (block instanceof ShulkerBoxBlock) || (block instanceof ChestBlock) || (block instanceof BarrelBlock) || (block instanceof DispenserBlock) || (block instanceof DropperBlock) || (block instanceof HopperBlock);
        }
        if (stack.has(DataComponents.CONTAINER) || isContainerBlockItem) {
            getCategories().add(new ItemContainerCategoryModel(this));
            getCategories().add(new ItemContainerGridCategoryModel(this));
        }
        if ((item instanceof BlockItem) && ((BlockItem) item).getBlock() instanceof BeehiveBlock) {
            getCategories().add(new ItemBeehiveCategoryModel(this));
        }
        if (stack.has(DataComponents.BANNER_PATTERNS) || stack.has(DataComponents.BASE_COLOR) || (item instanceof BannerItem) || (item instanceof ShieldItem)) {
            getCategories().add(new ItemBannerPatternCategoryModel(this));
        }
        if (stack.is(ItemTags.TRIM_MATERIALS)) {
            getCategories().add(new ItemProvidesTrimMaterialCategoryModel(this));
        }
        if (stack.is(ItemTags.LOOM_PATTERNS)) {
            getCategories().add(new ItemProvidesBannerPatternCategoryModel(this));
        }
        if (stack.has(DataComponents.BUCKET_ENTITY_DATA) || (item instanceof MobBucketItem)) {
            getCategories().add(new ItemBucketEntityCategoryModel(this));
        }
        if (stack.has(DataComponents.INSTRUMENT) || item == Items.GOAT_HORN) {
            getCategories().add(new ItemInstrumentCategoryModel(this));
        }
        if (stack.has(DataComponents.POT_DECORATIONS) || item == Items.DECORATED_POT) {
            getCategories().add(new ItemPotDecorationsCategoryModel(this));
        }
        if (stack.has(DataComponents.FIREWORK_EXPLOSION) || item == Items.FIREWORK_STAR) {
            getCategories().add(new ItemFireworkStarCategoryModel(this));
        }
        if (stack.has(DataComponents.FIREWORKS) || item == Items.FIREWORK_ROCKET) {
            getCategories().add(new ItemFireworksCategoryModel(this));
        }
        if (stack.has(DataComponents.OMINOUS_BOTTLE_AMPLIFIER) || item == Items.OMINOUS_BOTTLE) {
            getCategories().add(new ItemOminousBottleCategoryModel(this));
        }
        if (stack.has(DataComponents.CUSTOM_DATA)) {
            getCategories().add(new ItemCustomDataCategoryModel(this));
        }
        if (item == Items.WRITABLE_BOOK) {
            getCategories().add(new ItemWritableBookPagesCategoryModel(this));
        }
        if (item == Items.WRITTEN_BOOK) {
            getCategories().add(new ItemWrittenBookCategoryModel(this));
        }
        if ((item instanceof PotionItem) || (item instanceof TippedArrowItem)) {
            getCategories().add(new ItemPotionEffectsCategoryModel(this));
        }
    }

    @Override 
    public void apply() {
        ItemEditorContext context = getContext();
        super.apply();
        applyFoodComponents();
        CompoundTag stagedLegacy = copyLegacyPayload(context.getTag());
        HolderLookup.Provider registryAccess = getRegistryAccess();
        CompoundTag rebuilt = ClientUtil.saveItemStack(registryAccess, context.getItemStack());
        if (rebuilt instanceof CompoundTag) {
            if (stagedLegacy == null || stagedLegacy.isEmpty()) {
                rebuilt.remove(KEY_LEGACY_TAG);
            } else {
                rebuilt.put(KEY_LEGACY_TAG, stagedLegacy);
            }
            context.setTag(rebuilt);
            ItemStack parsed = ClientUtil.parseItemStack(registryAccess, rebuilt);
            context.setItemStack(parsed.isEmpty() ? context.getItemStack().copy() : parsed);
        }
    }

    public FoodComponentState getFoodState() {
        return this.foodState;
    }

    public ConsumableState getConsumableState() {
        return this.consumableState;
    }

    public UseRemainderState getUseRemainderState() {
        return this.useRemainderState;
    }

    public boolean isConsumableActive() {
        return this.consumableBehaviorEnabled;
    }

    public void setConsumableBehaviorEnabled(boolean enabled) {
        this.consumableBehaviorEnabled = enabled;
    }

    public void setUseRemainderEnabled(boolean enabled) {
        this.desiredUseRemainderEnabled = enabled;
    }

    public boolean isExtraComponentEnabled(ItemExtraToggle toggle) {
        return (Boolean) this.extraComponentStates.getOrDefault(toggle, defaultExtraComponentState(toggle));
    }

    public void setExtraComponentEnabled(ItemExtraToggle toggle, boolean enabled) {
        boolean current = isExtraComponentEnabled(toggle);
        if (current == enabled) {
            return;
        }
        this.extraComponentStates.put(toggle, enabled);
        rebuildCategoriesPreservingSelection(null);
    }

    private void resetExtraComponentStates() {
        this.extraComponentStates.clear();
        for (ItemExtraToggle toggle : ItemExtraToggle.values()) {
            this.extraComponentStates.put(toggle, defaultExtraComponentState(toggle));
        }
    }

    
    
    private boolean defaultExtraComponentState(ItemExtraToggle toggle) {
        return false;
    }

    private void syncExtraComponentStatesFromStack(ItemStack stack) {
    }

    public void setFoodBehaviorEnabled(boolean enabled) {
        this.foodBehaviorEnabled = enabled;
    }

    public void setDeathProtectionEnabled(boolean enabled) {
        this.deathProtectionBehaviorEnabled = enabled;
    }

    public void applyFoodComponents() {
        ItemStack stack = getContext().getItemStack();
        this.foodState.setEnabled(this.foodBehaviorEnabled);
        if (this.foodBehaviorEnabled) {
            stack.set(DataComponents.FOOD, buildSafeFoodProperties());
        } else {
            stack.remove(DataComponents.FOOD);
        }
        if (this.consumableBehaviorEnabled) {
            writeConsumable(stack);
        } else {
            stack.remove(DataComponents.CONSUMABLE);
            DebugLog.infoKey("cadeditor.debug.food.consumable_removed", describeStackForLogs(stack));
        }
        applyUseRemainder(stack);
        syncContextSnapshot(stack);
    }

    private void applyUseRemainder(ItemStack stack) {
        if (this.desiredUseRemainderEnabled) {
            Optional<ItemStack> convertsTo = this.useRemainderState.resolveUsingConvertsTo();
            Optional<UseRemainder> useRemainder = this.useRemainderState.buildUseRemainder(convertsTo);
            if (useRemainder.isPresent()) {
                stack.set(DataComponents.USE_REMAINDER, useRemainder.get());
            } else {
                stack.remove(DataComponents.USE_REMAINDER);
            }
            this.useRemainderState.updateOriginalUsingConvertsTo(convertsTo);
        } else {
            stack.remove(DataComponents.USE_REMAINDER);
            this.useRemainderState.updateOriginalUsingConvertsTo(Optional.empty());
        }
    }

    private void writeConsumable(ItemStack stack) {
        try {
            List<ConsumeEffect> statusEffects = buildStatusEffects();
            Consumable consumable = this.consumableState.buildConsumable(statusEffects);
            stack.set(DataComponents.CONSUMABLE, consumable);
            DebugLog.infoKey("cadeditor.debug.food.applied_consumable", describeStackForLogs(stack), consumable.consumeSeconds(), statusEffects);
        } catch (Throwable t) {
            LOGGER.error("Building consumable component failed, falling back to minimal values. Cause: {}", t.toString());
            stack.set(DataComponents.CONSUMABLE, buildFallbackConsumable());
        }
    }

    private Consumable buildFallbackConsumable() {
        Consumable.Builder builder = Consumable.builder().consumeSeconds(this.consumableState.getConsumeSeconds()).hasConsumeParticles(this.consumableState.hasConsumeParticles()).animation(this.consumableState.getAnimation());
        this.consumableState.getConsumeSound().ifPresent(builder::sound);
        return builder.build();
    }

    private FoodProperties buildSafeFoodProperties() {
        int nutrition = Math.max(0, this.foodState.getNutrition());
        if (nutrition != this.foodState.getNutrition()) {
            this.foodState.setNutrition(nutrition);
        }
        float saturation = Math.max(0.0f, this.foodState.getSaturation());
        if (saturation != this.foodState.getSaturation()) {
            this.foodState.setSaturation(saturation);
        }
        return new FoodProperties(nutrition, saturation, this.foodState.isAlwaysEat());
    }

    private List<ConsumeEffect> buildStatusEffects() {
        Holder<MobEffect> effectHolder;
        List<ConsumableState.ConsumableEffectData> raw = this.consumableState.copyEffectsForComponent();
        if (raw == null || raw.isEmpty()) {
            return List.of();
        }
        List<ConsumeEffect> out = new ArrayList<>(raw.size());
        for (ConsumableState.ConsumableEffectData data : raw) {
            if (data != null) {
                try {
                    float probability = data.probability();
                    if (probability > 0.0f) {
                        if (probability > 1.0f) {
                            probability = 1.0f;
                        }
                        MobEffectInstance inst = data.effect();
                        if (inst != null && (effectHolder = inst.getEffect()) != null) {
                            int dur = Math.max(1, Math.min(inst.getDuration(), MAX_DURATION_TICKS));
                            int amp = Math.max(0, Math.min(inst.getAmplifier(), MAX_AMPLIFIER));
                            boolean ambient = inst.isAmbient();
                            boolean showParticles = inst.isVisible();
                            boolean showIcon = inst.showIcon();
                            MobEffectInstance rebuilt = new MobEffectInstance(effectHolder, dur, amp, ambient, showParticles, showIcon);
                            out.add(new ApplyStatusEffectsConsumeEffect(rebuilt, probability));
                        }
                    }
                } catch (Throwable t) {
                    LOGGER.warn("Skip invalid food effect {} due to {}", data, t);
                }
            }
        }
        return out.isEmpty() ? List.of() : List.copyOf(out);
    }

    public void handleStackReplaced(ItemStack newStack) {
        ItemStack stack = newStack == null ? ItemStack.EMPTY : newStack.copy();
        syncContextSnapshot(stack);
        resetExtraComponentStates();
        this.shouldSyncExtraComponentsFromStack = true;
        rebuildCategoriesPreservingSelection(null);
    }

    public boolean isExtraCategoryVisible(ItemExtraToggle toggle) {
        return getCategories().stream().anyMatch(category -> category.getClass() == toggle.getCategoryClass());
    }

    public void setExtraCategoryVisible(ItemExtraToggle toggle, boolean visible) {
        boolean present = isExtraCategoryVisible(toggle);
        if (visible == present) {
            return;
        }
        if (visible) {
            EditorCategoryModel category = toggle.create(this);
            getCategories().add(category);
            category.initalize();
            return;
        }
        List<CategoryModel> toRemove = new ArrayList<>();
        for (CategoryModel category : getCategories()) {
            if (category.getClass() == toggle.getCategoryClass()) {
                toRemove.add(category);
            }
        }
        for (CategoryModel category : toRemove) {
            getCategories().remove(category);
        }
        if (!getCategories().contains(getSelectedCategory()) && !getCategories().isEmpty()) {
            setSelectedCategory(getCategories().get(0));
        }
    }

    private void rebuildCategoriesPreservingSelection(Class<?> preferredCategoryClass) {
        Class<?> cls;
        CategoryModel previousSelection = getSelectedCategory();
        if (preferredCategoryClass != null) {
            cls = preferredCategoryClass;
        } else {
            cls = previousSelection == null ? null : previousSelection.getClass();
        }
        Class<?> targetClass = cls;
        getCategories().clear();
        setupCategories();
        getCategories().forEach((value -> {
            value.initalize();
        }));
        if (!getCategories().isEmpty()) {
            CategoryModel toSelect = getCategories().get(0);
            if (targetClass != null) {
                for (CategoryModel category : getCategories()) {
                    if (category.getClass() == targetClass) {
                        toSelect = category;
                        break;
                    }
                }
            }
            setSelectedCategory(toSelect);
        }
    }

    private void syncContextSnapshot(ItemStack stack) {
        ItemEditorContext context = getContext();
        HolderLookup.Provider registryAccess = getRegistryAccess();
        CompoundTag saved = ClientUtil.saveItemStack(registryAccess, stack);
        if (saved instanceof CompoundTag) {
            this.foodState.setEnabled(this.foodBehaviorEnabled);
            boolean wantsFood = this.foodBehaviorEnabled;
            boolean wantsConsumable = this.consumableBehaviorEnabled;
            boolean wantsUseRemainder = this.desiredUseRemainderEnabled;
            Set<String> suppressedKeys = suppressedComponentKeys();
            if (!suppressedKeys.isEmpty()) {
                CompoundTag components = ensureComponentsTag(saved);
                for (String key : suppressedKeys) {
                    components.put("!" + key, new CompoundTag());
                }
            }
            CompoundTag oldRoot = context.getTag();
            CompoundTag legacy = copyLegacyPayload(oldRoot);
            if (legacy == null || legacy.isEmpty()) {
                saved.remove(KEY_LEGACY_TAG);
            } else {
                saved.put(KEY_LEGACY_TAG, legacy);
            }
            Set<String> doNotCopyBack = suppressedKeys.isEmpty() ? Collections.emptySet() : suppressedKeys;
            CompoundTag compound = mergeComponentsPreservingUnknown(oldRoot, saved, doNotCopyBack);
            if (wantsFood && !hasComponent(compound, FOOD_COMPONENT_KEY)) {
                ensureComponentsTag(compound).put(FOOD_COMPONENT_KEY, buildMinimalFoodNbt());
            }
            context.setTag(compound);
            ItemStack parsed = ClientUtil.parseItemStack(registryAccess, compound);
            if (!parsed.isEmpty()) {
                if (wantsFood && parsed.get(DataComponents.FOOD) == null) {
                    parsed.set(DataComponents.FOOD, buildSafeFoodProperties());
                } else if (!wantsFood && parsed.get(DataComponents.FOOD) != null) {
                    parsed.remove(DataComponents.FOOD);
                }
                if (wantsConsumable) {
                    if (parsed.get(DataComponents.CONSUMABLE) == null) {
                        writeConsumable(parsed);
                    }
                } else if (parsed.get(DataComponents.CONSUMABLE) != null) {
                    parsed.remove(DataComponents.CONSUMABLE);
                }
                if (wantsUseRemainder) {
                    applyUseRemainder(parsed);
                } else if (parsed.get(DataComponents.USE_REMAINDER) != null) {
                    parsed.remove(DataComponents.USE_REMAINDER);
                }
                context.setItemStack(parsed);
                reloadComponentStates(parsed);
                return;
            }
            ItemStack fallback = stack.copy();
            if (wantsFood && fallback.get(DataComponents.FOOD) == null) {
                fallback.set(DataComponents.FOOD, buildSafeFoodProperties());
            } else if (!wantsFood && fallback.get(DataComponents.FOOD) != null) {
                fallback.remove(DataComponents.FOOD);
            }
            if (wantsConsumable) {
                if (fallback.get(DataComponents.CONSUMABLE) == null) {
                    writeConsumable(fallback);
                }
            } else if (fallback.get(DataComponents.CONSUMABLE) != null) {
                fallback.remove(DataComponents.CONSUMABLE);
            }
            if (wantsUseRemainder) {
                applyUseRemainder(fallback);
            } else if (fallback.get(DataComponents.USE_REMAINDER) != null) {
                fallback.remove(DataComponents.USE_REMAINDER);
            }
            context.setItemStack(fallback);
            reloadComponentStates(fallback);
        }
    }

    private void reloadComponentStates(ItemStack stack) {
        this.foodState.loadFrom(stack);
        this.consumableState.loadFrom(stack);
        this.useRemainderState.loadFrom(stack);
    }

    private CompoundTag buildMinimalFoodNbt() {
        CompoundTag food = new CompoundTag();
        int nutrition = Math.max(0, this.foodState.getNutrition());
        if (nutrition != this.foodState.getNutrition()) {
            this.foodState.setNutrition(nutrition);
        }
        float saturation = Math.max(0.0f, this.foodState.getSaturation());
        if (saturation != this.foodState.getSaturation()) {
            this.foodState.setSaturation(saturation);
        }
        food.putInt("nutrition", nutrition);
        food.putFloat("saturation", saturation);
        if (this.foodState.isAlwaysEat()) {
            food.putBoolean("can_always_eat", true);
        }
        return food;
    }

    
    static CompoundTag mergeComponentsPreservingUnknown(CompoundTag oldRoot, CompoundTag newRoot, Set<String> keysNotToCopy) {
        CompoundTag merged = newRoot.copy();
        if (!merged.contains(KEY_COMPONENTS) && oldRoot != null) {
            oldRoot.getCompound(KEY_COMPONENTS).ifPresent(legacyComps -> {
                merged.put(KEY_COMPONENTS, legacyComps.copy());
            });
        }
        CompoundTag newComps = merged.getCompound(KEY_COMPONENTS).orElseGet(() -> {
            CompoundTag created = new CompoundTag();
            merged.put(KEY_COMPONENTS, created);
            return created;
        });
        if (oldRoot != null && oldRoot.contains(KEY_COMPONENTS)) {
            CompoundTag oldComps = oldRoot.getCompound(KEY_COMPONENTS).orElse(null);
            if (oldComps != null) {
                for (String oldKey : oldComps.keySet()) {
                    if (!oldKey.startsWith(TOMBSTONE_PREFIX) && (keysNotToCopy == null || !keysNotToCopy.contains(oldKey))) {
                        boolean explicitlyRemoved = newComps.contains("!" + oldKey);
                        boolean explicitlySet = newComps.contains(oldKey);
                        if (!explicitlyRemoved && !explicitlySet) {
                            Tag compoundTag = oldComps.get(oldKey);
                            boolean isUnitLike = compoundTag instanceof CompoundTag c && c.isEmpty();
                            boolean respectDeletion = isUnitLike || DELETE_IF_ABSENT_KEYS.contains(oldKey);
                            if (!respectDeletion) {
                                newComps.put(oldKey, compoundTag.copy());
                            }
                        }
                    }
                }
            }
        }
        applyComponentMigrations(merged);
        return merged;
    }

    protected HolderLookup.Provider getRegistryAccess() {
        return ClientUtil.registryAccess();
    }

    private String describeStackForLogs(ItemStack stack) {
        if (stack.isEmpty()) {
            return "<empty>";
        }
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id + " tag=" + stackTagForLogs(stack);
    }

    private String stackTagForLogs(ItemStack stack) {
        if (stack.isEmpty()) {
            return "<empty>";
        }
        CompoundTag compound = ClientUtil.saveItemStack(getRegistryAccess(), stack);
        Set<String> doNotCopyBack = suppressedComponentKeys();
        CompoundTag merged = mergeComponentsPreservingUnknown(getContext().getTag(), compound.copy(), doNotCopyBack);
        return merged.toString();
    }

    private static CompoundTag copyLegacyPayload(CompoundTag container) {
        if (container == null) {
            return null;
        }
        return container.getCompound(KEY_LEGACY_TAG).filter(legacy -> !legacy.isEmpty()).map(value -> value.copy()).orElse(null);
    }

    private static void applyComponentMigrations(CompoundTag root) {
        migrateLegacyEnchantments(root);
        migrateLegacyHideFlags(root);
        migrateLegacyAttributeModifiers(root);
        migrateLegacyAdventurePredicates(root);
        migrateLegacyEntityTag(root);
        migrateLegacyPotionContents(root);
        migrateLegacyWritableBookPages(root);
    }

    private static void migrateLegacyEnchantments(CompoundTag root) {
        CompoundTag legacyTag = root.getCompound(KEY_LEGACY_TAG).orElse(null);
        if (legacyTag == null) {
            return;
        }
        migrateLegacyEnchantmentList(legacyTag, root, "Enchantments", "minecraft:enchantments");
        migrateLegacyEnchantmentList(legacyTag, root, "StoredEnchantments", "minecraft:stored_enchantments");
        if (legacyTag.isEmpty()) {
            root.remove(KEY_LEGACY_TAG);
        }
    }

    private static void migrateLegacyEnchantmentList(CompoundTag legacyTag, CompoundTag root, String legacyKey, String componentKey) {
        ListTag enchantList = legacyTag.getList(legacyKey).orElse(null);
        if (enchantList == null || enchantList.isEmpty()) {
            legacyTag.remove(legacyKey);
            return;
        }
        CompoundTag components = ensureComponentsTag(root);
        if (components.contains(componentKey)) {
            legacyTag.remove(legacyKey);
            return;
        }
        CompoundTag levels = new CompoundTag();
        boolean hasData = false;
        for (Tag element : enchantList) {
            if (!(element instanceof CompoundTag enchantment)) {
                continue;
            }
            String id = enchantment.getString("id").orElse("");
            if (!id.isEmpty()) {
                if (!id.contains(":")) {
                    id = "minecraft:" + id;
                }
                Identifier rl = Identifier.tryParse(id);
                int level = enchantment.getIntOr("lvl", 0);
                if (rl != null && level > 0) {
                    levels.putInt(rl.toString(), level);
                    hasData = true;
                }
            }
        }
        if (hasData) {
            CompoundTag component = new CompoundTag();
            component.put(LEVELS_FIELD, levels);
            components.put(componentKey, component);
        }
        legacyTag.remove(legacyKey);
    }

    private static void migrateLegacyHideFlags(CompoundTag root) {
        CompoundTag components = root.getCompound(KEY_COMPONENTS).orElse(null);
        CompoundTag legacyTag = root.getCompound(KEY_LEGACY_TAG).orElse(null);
        boolean hideTooltip = false;
        EnumSet<ItemHideFlagsCategoryModel.HideFlag> hiddenFlags = EnumSet.noneOf(ItemHideFlagsCategoryModel.HideFlag.class);
        if (components != null) {
            if (components.contains(HIDE_TOOLTIP_COMPONENT_KEY)) {
                hideTooltip = true;
                components.remove(HIDE_TOOLTIP_COMPONENT_KEY);
            }
            List<String> keys = List.copyOf(components.keySet());
            for (String key : keys) {
                if (key.startsWith(TOMBSTONE_PREFIX)) {
                    String target = key.substring(1);
                    if (TOOLTIP_DISPLAY_COMPONENT_KEY.equals(target) || HIDE_TOOLTIP_COMPONENT_KEY.equals(target) || TOMBSTONE_COMPONENT_IDS.contains(target)) {
                        components.remove(key);
                    }
                }
            }
        }
        if (legacyTag != null && legacyTag.contains("HideFlags")) {
            int mask = legacyTag.getIntOr("HideFlags", 0);
            if (mask != 0) {
                for (ItemHideFlagsCategoryModel.HideFlag flag : ItemHideFlagsCategoryModel.HideFlag.values()) {
                    if (flag == ItemHideFlagsCategoryModel.HideFlag.OTHER) {
                        if ((mask & flag.getValue()) != 0) {
                            hideTooltip = true;
                        }
                    } else if ((mask & flag.getValue()) != 0) {
                        hiddenFlags.add(flag);
                    }
                }
            }
            legacyTag.remove("HideFlags");
        }
        if (!hideTooltip && hiddenFlags.isEmpty()) {
            if (legacyTag != null && legacyTag.isEmpty()) {
                root.remove(KEY_LEGACY_TAG);
                return;
            }
            return;
        }
        CompoundTag comps = ensureComponentsTag(root);
        Set<String> hiddenComponentIds = new LinkedHashSet<>();
        for (ItemHideFlagsCategoryModel.HideFlag flag : hiddenFlags) {
            for (DataComponentType<?> type : flag.hiddenComponents()) {
                String id = componentId(type);
                if (id != null) {
                    hiddenComponentIds.add(id);
                    if (COMPONENTS_WITH_TOOLTIP_BOOLEAN.contains(id)) {
                        setComponentTooltipVisibility(comps, id, false);
                    }
                }
            }
        }
        if (!hiddenComponentIds.isEmpty() || hideTooltip || !comps.contains(TOOLTIP_DISPLAY_COMPONENT_KEY)) {
            writeTooltipDisplayTag(comps, hideTooltip, hiddenComponentIds);
        }
        if (legacyTag != null && legacyTag.isEmpty()) {
            root.remove(KEY_LEGACY_TAG);
        }
    }

    private static void migrateLegacyAttributeModifiers(CompoundTag root) {
        ListTag legacyList;
        CompoundTag legacyTag = root.getCompound(KEY_LEGACY_TAG).orElse(null);
        if (legacyTag == null || (legacyList = legacyTag.getList("AttributeModifiers").orElse(null)) == null) {
            return;
        }
        CompoundTag components = ensureComponentsTag(root);
        if (!components.contains("minecraft:attribute_modifiers")) {
            ListTag modifiers = new ListTag();
            Set<UUID> usedUuids = new HashSet<>();
            Set<Identifier> usedModifierIds = new HashSet<>();
            for (Tag element : legacyList) {
                if (!(element instanceof CompoundTag legacyModifier)) {
                    continue;
                }
                String attributeId = normalizeNamespacedId(legacyModifier.getStringOr("AttributeName", ""));
                Identifier attributeKey = Identifier.tryParse(attributeId);
                if (attributeKey != null) {
                    int operation = legacyModifier.getIntOr("Operation", 0);
                    String operationName = switch (operation) {
                        case 1 -> "add_multiplied_base";
                        case 2 -> "add_multiplied_total";
                        default -> "add_value";
                    };
                    double amount = legacyModifier.getDoubleOr("Amount", 0.0d);
                    String slot = legacyModifier.getStringOr("Slot", "").trim();
                    UUID uuid = readLegacyModifierUuid(legacyModifier);
                    if (uuid == null || !usedUuids.add(uuid)) {
                        uuid = generateModifierUuid(legacyModifier, usedUuids);
                    }
                    Identifier modifierId = createModifierId(uuid, usedModifierIds);
                    CompoundTag modifier = new CompoundTag();
                    modifier.putString("type", attributeKey.toString());
                    modifier.putDouble("amount", amount);
                    modifier.putString("operation", operationName);
                    modifier.putString("id", modifierId.toString());
                    if (!slot.isEmpty()) {
                        modifier.putString("slot", slot);
                    }
                    modifiers.add(modifier);
                }
            }
            if (!modifiers.isEmpty()) {
                CompoundTag component = new CompoundTag();
                component.put("modifiers", modifiers);
                components.put("minecraft:attribute_modifiers", component);
            }
        }
        legacyTag.remove("AttributeModifiers");
        if (legacyTag.isEmpty()) {
            root.remove(KEY_LEGACY_TAG);
        }
    }

    private static void migrateLegacyAdventurePredicates(CompoundTag root) {
        CompoundTag legacyTag = root.getCompound(KEY_LEGACY_TAG).orElse(null);
        if (legacyTag == null) {
            return;
        }
        migrateLegacyAdventurePredicateList(legacyTag, root, "CanDestroy", "minecraft:can_break");
        migrateLegacyAdventurePredicateList(legacyTag, root, "CanPlaceOn", "minecraft:can_place_on");
        if (legacyTag.isEmpty()) {
            root.remove(KEY_LEGACY_TAG);
        }
    }

    private static void migrateLegacyAdventurePredicateList(CompoundTag legacyTag, CompoundTag root, String legacyKey, String componentKey) {
        ListTag legacyList = legacyTag.getList(legacyKey).orElse(null);
        if (legacyList == null || legacyList.isEmpty()) {
            legacyTag.remove(legacyKey);
            return;
        }
        CompoundTag components = ensureComponentsTag(root);
        if (components.contains(componentKey)) {
            legacyTag.remove(legacyKey);
            return;
        }
        ListTag predicates = new ListTag();
        for (Tag element : legacyList) {
            if (!(element instanceof StringTag selectorTag)) {
                continue;
            }
            String selector = normalizeBlockSelector(selectorTag.value());
            if (selector != null && !selector.isEmpty()) {
                CompoundTag predicate = new CompoundTag();
                predicate.putString("blocks", selector);
                predicates.add(predicate);
            }
        }
        if (!predicates.isEmpty()) {
            CompoundTag component = new CompoundTag();
            component.put("predicates", predicates);
            components.put(componentKey, component);
        }
        legacyTag.remove(legacyKey);
    }

    private static void migrateLegacyEntityTag(CompoundTag root) {
        CompoundTag legacyTag = root.getCompound(KEY_LEGACY_TAG).orElse(null);
        if (legacyTag == null) {
            return;
        }
        CompoundTag entityTag = legacyTag.getCompound("EntityTag").orElse(null);
        if (entityTag != null) {
            CompoundTag components = ensureComponentsTag(root);
            if (!components.contains("minecraft:entity_data")) {
                components.put("minecraft:entity_data", entityTag.copy());
            }
            legacyTag.remove("EntityTag");
        }
        if (legacyTag.isEmpty()) {
            root.remove(KEY_LEGACY_TAG);
        }
    }

    private static void migrateLegacyPotionContents(CompoundTag root) {
        CompoundTag legacyTag = root.getCompound(KEY_LEGACY_TAG).orElse(null);
        if (legacyTag == null) {
            return;
        }
        boolean hasLegacyPotion = legacyTag.contains("Potion") || legacyTag.contains("CustomPotionColor") || legacyTag.contains("custom_potion_effects");
        if (!hasLegacyPotion) {
            return;
        }
        CompoundTag components = ensureComponentsTag(root);
        if (!components.contains(POTION_CONTENTS_COMPONENT_KEY)) {
            CompoundTag potionContents = new CompoundTag();
            String potionId = normalizeNamespacedId(legacyTag.getStringOr("Potion", "").trim());
            if (!potionId.isEmpty()) {
                potionContents.putString("potion", potionId);
            }
            legacyTag.getInt("CustomPotionColor").ifPresent(color -> {
                if (color != 0) {
                    potionContents.putInt("custom_color", color);
                }
            });
            ListTag customEffects = legacyTag.getList("custom_potion_effects").orElse(null);
            if (customEffects != null && !customEffects.isEmpty()) {
                ListTag migratedEffects = new ListTag();
                for (Tag element : customEffects) {
                    if (!(element instanceof CompoundTag effect)) {
                        continue;
                    }
                    String id = normalizeNamespacedId(effect.getStringOr("id", ""));
                    if (id.isEmpty()) {
                        id = normalizeNamespacedId(effect.getStringOr("Id", ""));
                    }
                    if (!id.isEmpty()) {
                        CompoundTag migrated = new CompoundTag();
                        migrated.putString("id", id);
                        migrated.putInt("amplifier", Math.max(0, effect.getIntOr("amplifier", effect.getIntOr("Amplifier", 0))));
                        migrated.putInt("duration", Math.max(1, effect.getIntOr("duration", effect.getIntOr("Duration", 1))));
                        migrated.putBoolean("ambient", effect.getBooleanOr("ambient", effect.getBooleanOr("Ambient", false)));
                        boolean showParticles = effect.getBoolean("show_particles").or(() -> effect.getBoolean("ShowParticles")).orElse(true);
                        boolean showIcon = effect.getBoolean("show_icon").or(() -> effect.getBoolean("ShowIcon")).orElse(true);
                        migrated.putBoolean("show_particles", showParticles);
                        migrated.putBoolean("show_icon", showIcon);
                        migratedEffects.add(migrated);
                    }
                }
                if (!migratedEffects.isEmpty()) {
                    potionContents.put("custom_effects", migratedEffects);
                }
            }
            if (!potionContents.isEmpty()) {
                components.put(POTION_CONTENTS_COMPONENT_KEY, potionContents);
            }
        }
        legacyTag.remove("Potion");
        legacyTag.remove("CustomPotionColor");
        legacyTag.remove("custom_potion_effects");
        if (legacyTag.isEmpty()) {
            root.remove(KEY_LEGACY_TAG);
        }
    }

    private static void migrateLegacyWritableBookPages(CompoundTag root) {
        CompoundTag legacyTag = root.getCompound(KEY_LEGACY_TAG).orElse(null);
        if (legacyTag == null || !legacyTag.contains("pages")) {
            return;
        }
        ListTag pages = legacyTag.getList("pages").orElse(null);
        if (pages == null) {
            legacyTag.remove("pages");
            if (legacyTag.isEmpty()) {
                root.remove(KEY_LEGACY_TAG);
            }
            return;
        }
        CompoundTag components = ensureComponentsTag(root);
        if (!components.contains(WRITABLE_BOOK_COMPONENT_KEY)) {
            ListTag migratedPages = new ListTag();
            for (int i = 0; i < pages.size() && migratedPages.size() < 100; i++) {
                String page = pages.getString(i).orElse("");
                if (page.contains("\r")) {
                    page = page.replace("\r", "");
                }
                if (page.length() > 1024) {
                    page = page.substring(0, 1024);
                }
                migratedPages.add(StringTag.valueOf(page));
            }
            if (!migratedPages.isEmpty()) {
                CompoundTag writableBook = new CompoundTag();
                writableBook.put("pages", migratedPages);
                components.put(WRITABLE_BOOK_COMPONENT_KEY, writableBook);
            }
        }
        legacyTag.remove("pages");
        if (legacyTag.isEmpty()) {
            root.remove(KEY_LEGACY_TAG);
        }
    }

    private static String normalizeBlockSelector(String raw) {
        if (raw == null) {
            return null;
        }
        String selector = raw.trim();
        if (selector.isEmpty()) {
            return null;
        }
        if (selector.startsWith("#")) {
            String value = selector.substring(1);
            Identifier rl = value.contains(":") ? Identifier.tryParse(value) : Identifier.tryParse("minecraft:" + value);
            if (rl == null) {
                return null;
            }
            return "#" + rl;
        }
        Identifier rl = Identifier.tryParse(selector.contains(":") ? selector : "minecraft:" + selector);
        if (rl == null) {
            return null;
        }
        return rl.toString();
    }

    private static String normalizeNamespacedId(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        String candidate = raw.trim();
        if (!candidate.contains(":")) {
            candidate = "minecraft:" + candidate;
        }
        Identifier rl = Identifier.tryParse(candidate);
        return rl == null ? "" : rl.toString();
    }

    private static UUID readLegacyModifierUuid(CompoundTag tag) {
        UUID parsed = parseUuidString(tag.getStringOr("UUID", ""));
        if (parsed != null) {
            return parsed;
        }
        parsed = parseUuidString(tag.getStringOr("id", ""));
        if (parsed != null) {
            return parsed;
        }
        parsed = parseUuidString(tag.getStringOr("uuid", ""));
        if (parsed != null) {
            return parsed;
        }
        parsed = tag.getIntArray("UUID").map(ItemEditorModel::uuidFromIntArray).orElse(null);
        if (parsed != null) {
            return parsed;
        }
        parsed = tag.getIntArray("id").map(ItemEditorModel::uuidFromIntArray).orElse(null);
        if (parsed != null) {
            return parsed;
        }
        return tag.getIntArray("uuid").map(ItemEditorModel::uuidFromIntArray).orElse(null);
    }

    private static UUID generateModifierUuid(CompoundTag legacyModifier, Set<UUID> usedIds) {
        CompoundTag seedTag = legacyModifier.copy();
        seedTag.remove("UUID");
        seedTag.remove("uuid");
        seedTag.remove("id");
        String seed = seedTag.toString();
        int salt = 0;
        while (true) {
            UUID generated = UUID.nameUUIDFromBytes((seed + "#" + salt).getBytes(StandardCharsets.UTF_8));
            if (usedIds.add(generated)) {
                return generated;
            }
            salt++;
        }
    }

    private static Identifier createModifierId(UUID uuid, Set<Identifier> usedIds) {
        Identifier withSuffix;
        String compact = uuid.toString().replace("-", "");
        String basePath = "m_" + compact.substring(0, 12);
        Identifier direct = Identifier.fromNamespaceAndPath("cadeditor", basePath);
        if (usedIds.add(direct)) {
            return direct;
        }
        int suffix = 1;
        do {
            int i = suffix;
            suffix++;
            withSuffix = Identifier.fromNamespaceAndPath("cadeditor", basePath + "_" + Integer.toHexString(i));
        } while (!usedIds.add(withSuffix));
        return withSuffix;
    }

    private static UUID parseUuidString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        try {
            return UUID.fromString(trimmed);
        } catch (IllegalArgumentException e) {
            String candidate = trimmed;
            int underscore = candidate.lastIndexOf('_');
            if (underscore >= 0 && underscore + 1 < candidate.length()) {
                candidate = candidate.substring(underscore + 1);
            } else if (candidate.contains(":")) {
                candidate = candidate.substring(candidate.lastIndexOf(':') + 1);
            }
            String hex = candidate.replace("-", "");
            if (hex.length() != 32) {
                return null;
            }
            try {
                long most = Long.parseUnsignedLong(hex.substring(0, 16), 16);
                long least = Long.parseUnsignedLong(hex.substring(16), 16);
                return new UUID(most, least);
            } catch (NumberFormatException nfe) {
                return null;
            }
        }
    }

    private static UUID uuidFromIntArray(int[] data) {
        if (data == null || data.length != 4) {
            return null;
        }
        long most = (((long) data[0]) << 32) | (((long) data[1]) & 4294967295L);
        long least = (((long) data[2]) << 32) | (((long) data[3]) & 4294967295L);
        return new UUID(most, least);
    }

    private static void writeTooltipDisplayTag(CompoundTag components, boolean hideTooltip, Set<String> hiddenComponentIds) {
        if (!hideTooltip && hiddenComponentIds.isEmpty()) {
            components.remove(TOOLTIP_DISPLAY_COMPONENT_KEY);
            return;
        }
        CompoundTag tooltip = components.getCompound(TOOLTIP_DISPLAY_COMPONENT_KEY).orElseGet(() -> {
            CompoundTag created = new CompoundTag();
            components.put(TOOLTIP_DISPLAY_COMPONENT_KEY, created);
            return created;
        });
        if (hideTooltip) {
            tooltip.putBoolean("hide_tooltip", true);
        } else {
            tooltip.remove("hide_tooltip");
        }
        if (!hiddenComponentIds.isEmpty()) {
            ListTag list = new ListTag();
            for (String id : hiddenComponentIds) {
                list.add(StringTag.valueOf(id));
            }
            tooltip.put("hidden_components", list);
        } else {
            tooltip.remove("hidden_components");
        }
        if (tooltip.isEmpty()) {
            components.remove(TOOLTIP_DISPLAY_COMPONENT_KEY);
        }
    }

    private static void setComponentTooltipVisibility(CompoundTag components, String componentId, boolean show) {
        components.getCompound(componentId).ifPresent(comp -> {
            if (show) {
                comp.remove(SHOW_IN_TOOLTIP_FIELD);
            } else {
                comp.putBoolean(SHOW_IN_TOOLTIP_FIELD, false);
            }
        });
    }

    private static String componentId(DataComponentType<?> type) {
        Identifier id;
        if (type == null || (id = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(type)) == null) {
            return null;
        }
        return id.toString();
    }

    private static boolean hasComponent(CompoundTag root, String key) {
        return root != null && root.contains(KEY_COMPONENTS) && root.getCompound(KEY_COMPONENTS).map(comp -> comp.contains(key)).orElse(false);
    }

    private static CompoundTag ensureComponentsTag(CompoundTag root) {
        if (!root.contains(KEY_COMPONENTS)) {
            root.put(KEY_COMPONENTS, new CompoundTag());
        }
        return root.getCompound(KEY_COMPONENTS).orElseGet(() -> {
            CompoundTag created = new CompoundTag();
            root.put(KEY_COMPONENTS, created);
            return created;
        });
    }

    private static boolean isCommandBlockItem(Item item) {
        Identifier blockId = BuiltInRegistries.ITEM.getKey(item);
        if (blockId == null || !"minecraft".equals(blockId.getNamespace())) {
            return false;
        }
        String path = blockId.getPath();
        return "command_block".equals(path) || "chain_command_block".equals(path) || "repeating_command_block".equals(path);
    }

    private static boolean isSignItem(Item item) {
        return (item instanceof BlockItem blockItem) && blockItem.getBlock() instanceof net.minecraft.world.level.block.SignBlock;
    }

    private Set<String> suppressedComponentKeys() {
        Set<String> suppressed = new HashSet<>();
        CompoundTag currentTag = getContext().getTag();
        if (!this.foodBehaviorEnabled && hasComponent(currentTag, FOOD_COMPONENT_KEY)) {
            suppressed.add(FOOD_COMPONENT_KEY);
        }
        if (!isConsumableActive() && hasComponent(currentTag, CONSUMABLE_COMPONENT_KEY)) {
            suppressed.add(CONSUMABLE_COMPONENT_KEY);
        }
        if (!this.desiredUseRemainderEnabled) {
            suppressed.add(USE_REMAINDER_COMPONENT_KEY);
        }
        if (!this.deathProtectionBehaviorEnabled) {
            suppressed.add("minecraft:death_protection");
        }
        return suppressed;
    }

    public void removeComponentFromDataTag(String componentId) {
        ItemEditorContext context = getContext();
        CompoundTag data = context.getTag();
        if (data == null) {
            return;
        }
        data.getCompound(KEY_COMPONENTS).ifPresent(components -> {
            components.remove(componentId);
            if (components.isEmpty()) {
                data.remove(KEY_COMPONENTS);
            }
        });
        context.setTag(data);
    }
}
