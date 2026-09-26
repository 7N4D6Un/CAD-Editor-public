package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.franckyi.guapi.api.util.DebugMode;
import com.github.rinorsi.cadeditor.client.ClientConfiguration;
import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.HideFlagEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import com.mojang.serialization.Codec;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashSet;
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
        for (HideFlag flag : HideFlag.values()) {
            boolean selected = initial.contains(flag);
            if (selected) {
                this.selectedFlags.add(flag);
            }
            getEntries().add(new HideFlagEntryModel(this, flag, selected, value -> {
                setFlag(flag, value.booleanValue());
            }));
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
        EnumSet<HideFlag> componentFlags = EnumSet.copyOf((EnumSet) this.selectedFlags);
        boolean hideTooltip = componentFlags.remove(HideFlag.OTHER);
        Set<DataComponentType<?>> hiddenComponents = new LinkedHashSet<>();
        for (HideFlag flag : componentFlags) {
            hiddenComponents.addAll(flag.hiddenComponents());
        }
        featureLog("apply.targets", (Supplier<String>) () -> {
            return "hideTooltip=" + hideTooltip + ", componentFlags=" + componentFlags + ", hiddenComponents=" + describeComponents(hiddenComponents);
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
        ENCHANTMENTS(DataComponents.ENCHANTMENTS, DataComponents.STORED_ENCHANTMENTS),
        ATTRIBUTE_MODIFIERS(DataComponents.ATTRIBUTE_MODIFIERS),
        UNBREAKABLE(DataComponents.UNBREAKABLE),
        CAN_DESTROY(DataComponents.CAN_BREAK),
        CAN_PLACE_ON(DataComponents.CAN_PLACE_ON),
        OTHER(new DataComponentType[0]),
        DYED(DataComponents.DYED_COLOR),
        ARMOR_TRIMS(DataComponents.TRIM),
        JUKEBOX(DataComponents.JUKEBOX_PLAYABLE),
        LORE(DataComponents.LORE);

        private static final Map<DataComponentType<?>, HideFlag> COMPONENT_TO_FLAG = new HashMap<>();
        private final Set<DataComponentType<?>> hiddenComponents;

        static {
            for (HideFlag flag : values()) {
                for (DataComponentType<?> type : flag.hiddenComponents) {
                    COMPONENT_TO_FLAG.put(type, flag);
                }
            }
        }

        HideFlag(DataComponentType... dataComponentTypeArr) {
            this.hiddenComponents = Set.of(dataComponentTypeArr);
        }

        public MutableComponent getName() {
            return ModTexts.gui(name().toLowerCase(Locale.ROOT));
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
                flags.add(HideFlag.OTHER);
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
