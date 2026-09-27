package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.franckyi.guapi.api.util.DebugMode;
import com.github.rinorsi.cadeditor.client.ClientConfiguration;
import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.HideFlagEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.SpacerEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import com.mojang.serialization.Codec;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ItemHideFlagsCategoryModel extends ItemEditorCategoryModel {
    private static final Logger LOGGER = LogManager.getLogger("CAD-Editor/HideFlags");
    private final EnumSet<HideFlag> selectedFlags;

    public ItemHideFlagsCategoryModel(ItemEditorModel editor) {
        super(ModTexts.HIDE_FLAGS, editor);
        this.selectedFlags = EnumSet.noneOf(HideFlag.class);
    }

    @Override
    protected void setupEntries() {
        ItemStack stack = getParent().getContext().getItemStack();
        EnumSet<HideFlag> initial = EnumSet.noneOf(HideFlag.class);
        this.selectedFlags.clear();
        initial.addAll(TooltipDisplaySupport.INSTANCE.read(stack));
        List<List<HideFlag>> groups = List.of(
                List.of(HideFlag.HIDE_ALL_TOOLTIP, HideFlag.LORE),
                List.of(HideFlag.ENCHANTMENTS, HideFlag.STORED_ENCHANTMENTS),
                List.of(HideFlag.ATTRIBUTE_MODIFIERS, HideFlag.UNBREAKABLE, HideFlag.CAN_DESTROY, HideFlag.CAN_PLACE_ON, HideFlag.DAMAGE),
                List.of(HideFlag.DYED, HideFlag.ARMOR_TRIMS, HideFlag.BANNER_PATTERNS),
                List.of(HideFlag.CONTAINER, HideFlag.CONTAINER_LOOT),
                List.of(HideFlag.FIREWORKS, HideFlag.FIREWORK_EXPLOSION),
                List.of(HideFlag.POTION_CONTENTS, HideFlag.SUSPICIOUS_STEW_EFFECTS, HideFlag.OMINOUS_BOTTLE_AMPLIFIER),
                List.of(HideFlag.ENTITY_DATA, HideFlag.BLOCK_STATE, HideFlag.BLOCK_ENTITY_DATA),
                List.of(HideFlag.PROFILE, HideFlag.MAP_ID, HideFlag.WRITTEN_BOOK_CONTENT),
                List.of(HideFlag.JUKEBOX, HideFlag.CHARGED_PROJECTILES, HideFlag.BEES, HideFlag.POT_DECORATIONS, HideFlag.TROPICAL_FISH_PATTERN, HideFlag.INSTRUMENT, HideFlag.SULFUR_CUBE_CONTENT, HideFlag.INTANGIBLE_PROJECTILE));
        boolean first = true;
        for (List<HideFlag> group : groups) {
            if (!first) {
                getEntries().add(new SpacerEntryModel(this));
            }
            first = false;
            for (HideFlag flag : group) {
                boolean selected = initial.contains(flag);
                if (selected) {
                    this.selectedFlags.add(flag);
                }
                getEntries().add(new HideFlagEntryModel(this, flag, selected, value -> {
                    setFlag(flag, value.booleanValue());
                }));
            }
        }
    }

    @Override
    public void apply() {
        super.apply();
        refreshSelectedFlags();
        featureLog("apply.selected", (Supplier<String>) () -> {
            return "Selected flags: " + this.selectedFlags;
        });
        ItemStack stack = getParent().getContext().getItemStack();
        TooltipDisplaySupport.INSTANCE.clear(stack);
        Set<DataComponentType<?>> hiddenComponents = new LinkedHashSet<>();
        for (HideFlag flag : this.selectedFlags) {
            hiddenComponents.addAll(flag.hiddenComponents());
        }
        boolean hideTooltip = this.selectedFlags.contains(HideFlag.HIDE_ALL_TOOLTIP);
        featureLog("apply.targets", (Supplier<String>) () -> {
            return "hideTooltip=" + hideTooltip + ", hiddenComponents=" + describeComponents(hiddenComponents);
        });
        TooltipDisplaySupport.INSTANCE.apply(stack, hideTooltip, hiddenComponents);
        syncEntriesWithStack(stack);
    }

    private void refreshSelectedFlags() {
        this.selectedFlags.clear();
        for (EntryModel entry : getEntries()) {
            if (entry instanceof HideFlagEntryModel) {
                HideFlagEntryModel flagEntry = (HideFlagEntryModel) entry;
                if (Boolean.TRUE.equals(flagEntry.getValue())) {
                    this.selectedFlags.add(flagEntry.getHideFlag());
                }
            }
        }
    }

    private void syncEntriesWithStack(ItemStack stack) {
        EnumSet<HideFlag> actual = EnumSet.noneOf(HideFlag.class);
        actual.addAll(TooltipDisplaySupport.INSTANCE.read(stack));
        this.selectedFlags.clear();
        this.selectedFlags.addAll(actual);
        for (EntryModel entry : getEntries()) {
            if (entry instanceof HideFlagEntryModel) {
                HideFlagEntryModel flagEntry = (HideFlagEntryModel) entry;
                flagEntry.syncValue(this.selectedFlags.contains(flagEntry.getHideFlag()));
            }
        }
    }

    private void setFlag(HideFlag flag, boolean value) {
        if (value) {
            this.selectedFlags.add(flag);
        } else {
            this.selectedFlags.remove(flag);
        }
    }

    private static void featureLog(String stage, Supplier<String> messageSupplier) {
        if (isFeatureDebugEnabled()) {
            LOGGER.info("[CAD-Editor][HideFlags][{}] {}", stage, messageSupplier.get());
        }
    }

    private static String componentName(DataComponentType<?> type) {
        Identifier id = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(type);
        return id == null ? String.valueOf(type) : id.toString();
    }

    private static String describeComponents(Collection<DataComponentType<?>> components) {
        if (components == null || components.isEmpty()) {
            return "[]";
        }
        return (String) components.stream().map(ItemHideFlagsCategoryModel::componentName).collect(Collectors.joining(", ", "[", "]"));
    }

    private static boolean isFeatureDebugEnabled() {
        try {
            return ClientConfiguration.INSTANCE != null && ClientConfiguration.INSTANCE.getGuapiDebugMode() == DebugMode.FEATURE;
        } catch (Throwable th) {
            return false;
        }
    }

    public enum HideFlag {
        HIDE_ALL_TOOLTIP(null, 0),
        LORE(DataComponents.LORE, 0),
        ENCHANTMENTS(DataComponents.ENCHANTMENTS, 1),
        STORED_ENCHANTMENTS(DataComponents.STORED_ENCHANTMENTS, 32),
        ATTRIBUTE_MODIFIERS(DataComponents.ATTRIBUTE_MODIFIERS, 2),
        UNBREAKABLE(DataComponents.UNBREAKABLE, 4),
        CAN_DESTROY(DataComponents.CAN_BREAK, 8),
        CAN_PLACE_ON(DataComponents.CAN_PLACE_ON, 16),
        DAMAGE(DataComponents.DAMAGE, 0),
        DYED(DataComponents.DYED_COLOR, 64),
        ARMOR_TRIMS(DataComponents.TRIM, 128),
        BANNER_PATTERNS(DataComponents.BANNER_PATTERNS, 0),
        JUKEBOX(DataComponents.JUKEBOX_PLAYABLE, 0),
        CONTAINER(DataComponents.CONTAINER, 0),
        CONTAINER_LOOT(DataComponents.CONTAINER_LOOT, 0),
        FIREWORKS(DataComponents.FIREWORKS, 0),
        FIREWORK_EXPLOSION(DataComponents.FIREWORK_EXPLOSION, 0),
        POTION_CONTENTS(DataComponents.POTION_CONTENTS, 0),
        SUSPICIOUS_STEW_EFFECTS(DataComponents.SUSPICIOUS_STEW_EFFECTS, 0),
        OMINOUS_BOTTLE_AMPLIFIER(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, 0),
        WRITTEN_BOOK_CONTENT(DataComponents.WRITTEN_BOOK_CONTENT, 0),
        ENTITY_DATA(DataComponents.ENTITY_DATA, 0),
        BLOCK_STATE(DataComponents.BLOCK_STATE, 0),
        BLOCK_ENTITY_DATA(DataComponents.BLOCK_ENTITY_DATA, 0),
        PROFILE(DataComponents.PROFILE, 0),
        MAP_ID(DataComponents.MAP_ID, 0),
        CHARGED_PROJECTILES(DataComponents.CHARGED_PROJECTILES, 0),
        BEES(DataComponents.BEES, 0),
        POT_DECORATIONS(DataComponents.POT_DECORATIONS, 0),
        TROPICAL_FISH_PATTERN(DataComponents.TROPICAL_FISH_PATTERN, 0),
        INSTRUMENT(DataComponents.INSTRUMENT, 0),
        SULFUR_CUBE_CONTENT(DataComponents.SULFUR_CUBE_CONTENT, 0),
        INTANGIBLE_PROJECTILE(DataComponents.INTANGIBLE_PROJECTILE, 0);

        private static final Map<DataComponentType<?>, HideFlag> COMPONENT_TO_FLAG = new HashMap<>();
        private final Set<DataComponentType<?>> hiddenComponents;
        private final int legacyBit;

        static {
            for (HideFlag flag : values()) {
                for (DataComponentType<?> type : flag.hiddenComponents) {
                    COMPONENT_TO_FLAG.put(type, flag);
                }
            }
        }

        HideFlag(DataComponentType<?> component, int legacyBit) {
            this.hiddenComponents = component == null ? Set.of() : Set.of(component);
            this.legacyBit = legacyBit;
        }

        public MutableComponent getName() {
            return ModTexts.gui("hide_flags." + name().toLowerCase(Locale.ROOT));
        }

        public int getLegacyBit() {
            return this.legacyBit;
        }

        public Collection<DataComponentType<?>> hiddenComponents() {
            return this.hiddenComponents;
        }

        public static HideFlag fromComponent(DataComponentType<?> type) {
            return COMPONENT_TO_FLAG.get(type);
        }
    }


    private static final class TooltipDisplaySupport {
        static final TooltipDisplaySupport INSTANCE = new TooltipDisplaySupport();
        private final DataComponentType<Object> type;

        private TooltipDisplaySupport() {
            DataComponentType<Object> t = null;
            try {
                t = (DataComponentType) BuiltInRegistries.DATA_COMPONENT_TYPE.get(Identifier.withDefaultNamespace("tooltip_display")).map(value -> value.value()).map(value -> value).orElse(null);
            } catch (Exception e) {
            }
            this.type = t;
        }

        private boolean available() {
            return this.type != null && this.type.codec() != null;
        }

        private EnumSet<HideFlag> read(ItemStack stack) {
            EnumSet<HideFlag> flags = EnumSet.noneOf(HideFlag.class);
            if (!available()) {
                return flags;
            }
            Object value = stack.get(this.type);
            if (value == null) {
                return flags;
            }
            Codec<Object> codec = this.type.codec();
            RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, ClientUtil.registryAccess());
            Tag encoded = (Tag) codec.encodeStart(ops, value).result().orElse(null);
            if (!(encoded instanceof CompoundTag compound)) {
                return flags;
            }
            if (compound.getBooleanOr("hide_tooltip", false)) {
                flags.add(HideFlag.HIDE_ALL_TOOLTIP);
            }
            ListTag hiddenList = (ListTag) compound.getList("hidden_components").orElse(new ListTag());
            for (Tag tagElement : hiddenList) {
                if (tagElement instanceof StringTag stringTag) {
                    Identifier id = Identifier.tryParse(stringTag.value());
                    if (id != null && BuiltInRegistries.DATA_COMPONENT_TYPE.get(id).map(v -> v.value()).orElse(null) instanceof DataComponentType<?> dc) {
                        HideFlag flag = HideFlag.fromComponent(dc);
                        if (flag != null) {
                            flags.add(flag);
                        }
                    }
                }
            }
            return flags;
        }

        private boolean apply(ItemStack stack, boolean hideTooltip, Set<DataComponentType<?>> components) {
            if (!available()) {
                return false;
            }
            Codec<Object> codec = this.type.codec();
            RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, ClientUtil.registryAccess());
            if (!hideTooltip && components.isEmpty()) {
                stack.remove(this.type);
                return true;
            }
            CompoundTag payload = new CompoundTag();
            payload.putBoolean("hide_tooltip", hideTooltip);
            if (!components.isEmpty()) {
                ListTag hidden = new ListTag();
                for (DataComponentType<?> component : components) {
                    Identifier id = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(component);
                    if (id != null) {
                        hidden.add(StringTag.valueOf(id.toString()));
                    }
                }
                payload.put("hidden_components", hidden);
            }
            Object parsed = codec.parse(ops, payload).result().orElse(null);
            if (parsed == null) {
                return false;
            }
            stack.set(this.type, parsed);
            return true;
        }

        private void clear(ItemStack stack) {
            if (this.type != null) {
                stack.remove(this.type);
            }
        }
    }
}
