package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.IntegerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.SpacerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.EnchantmentEntryModel;
import com.github.rinorsi.cadeditor.client.util.NbtHelper;
import com.github.rinorsi.cadeditor.common.ModTexts;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayList;
import net.minecraft.nbt.Tag;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ItemEnchantmentsCategoryModel extends ItemEditorCategoryModel {
    private static final Logger LOGGER = LogManager.getLogger();
    private ListTag newEnch;
    private boolean editingStored;
    private BooleanEntryModel enchantableToggleEntry;
    private IntegerEntryModel enchantableValueEntry;
    private boolean enchantableEnabled;
    private int enchantableValue;

    public ItemEnchantmentsCategoryModel(ItemEditorModel editor) {
        super(ModTexts.ENCHANTMENTS, editor);
    }

    @Override
    protected void setupEntries() {
        ItemStack stack = getStack();
        getEntries().add(new BooleanEntryModel(this, ModTexts.gui("glint_override"), getGlintOverride(stack), value -> setGlintOverride(value)));
        Enchantable appliedEnchantable = stack.get(DataComponents.ENCHANTABLE);
        Enchantable baseEnchantable = stack.getItem().components().get(DataComponents.ENCHANTABLE);
        this.enchantableEnabled = appliedEnchantable != null;
        this.enchantableValue = appliedEnchantable != null ? appliedEnchantable.value() : baseEnchantable != null ? baseEnchantable.value() : 1;
        if (this.enchantableValue < 1) {
            this.enchantableValue = 1;
        }
        getEntries().add(this.enchantableToggleEntry = new BooleanEntryModel(this, ModTexts.gui("enchantable_enabled"), this.enchantableEnabled, value -> setEnchantableEnabled(value)));
        this.enchantableValueEntry = new IntegerEntryModel(this, ModTexts.gui("enchantable_value"), this.enchantableValue, this::setEnchantableValue, value -> value != null && value >= 1);
        this.editingStored = shouldEditStored(stack);
        ItemEnchantments target = stack.get(this.editingStored ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS);
        boolean any = false;
        if (target != null && !target.isEmpty()) {
            any = true;
            target.entrySet().stream().map(this::createEnchantment).forEach(getEntries()::add);
        }
        if (!any) {
            CompoundTag data = getData();
            CompoundTag legacyTag = data != null ? data.getCompound("tag").orElse(null) : null;
            if (legacyTag != null) {
                String legacyKey = this.editingStored ? "StoredEnchantments" : "Enchantments";
                ListTag enchantList = legacyTag.getList(legacyKey).orElse(null);
                if (enchantList != null) {
                    Class<CompoundTag> cls = CompoundTag.class;
                    enchantList.stream().filter(cls::isInstance).map(cls::cast).map(this::createEnchantment).forEach(getEntries()::add);
                }
            }
        }
        if (this.enchantableEnabled) {
            insertEnchantableValueEntry();
        }
    }

    @Override
    public int getEntryListStart() {
        int fixed = 2;
        if (this.enchantableEnabled) {
            fixed++;
        }
        return fixed;
    }

    @Override
    protected MutableComponent getAddListEntryButtonTooltip() {
        return ModTexts.ENCHANTMENTS;
    }

    @Override
    public EntryModel createNewListEntry() {
        return createEnchantment("", 1);
    }

    private EnchantmentEntryModel createEnchantment(Object2IntMap.Entry<Holder<Enchantment>> entry) {
        Holder<Enchantment> holder = (Holder) entry.getKey();
        String id = holder.unwrapKey().map(key -> key.identifier().toString()).orElse("");
        return createEnchantment(id, entry.getIntValue());
    }

    private EnchantmentEntryModel createEnchantment(CompoundTag tag) {
        return createEnchantment(NbtHelper.getString(tag, "id", ""), tag.getIntOr("lvl", 0));
    }

    private EnchantmentEntryModel createEnchantment(String id, int level) {
        return new EnchantmentEntryModel(this, id, level, (entryId, entryLevel) -> addEnchantment(entryId, entryLevel));
    }

    public Set<Identifier> getExistingEnchantmentIds() {
        Set<Identifier> set = new HashSet<>();
        Class<EnchantmentEntryModel> cls = EnchantmentEntryModel.class;
        getEntries().stream().filter(cls::isInstance).map(cls::cast).map(EnchantmentEntryModel::getValue).map(this::normalizeId).filter(Objects::nonNull).forEach(set::add);
        return set;
    }

    public void addEnchantmentEntryIfAbsent(String id, int level) {
        Identifier rl = normalizeId(id);
        if (rl == null || getExistingEnchantmentIds().contains(rl)) {
            return;
        }
        EnchantmentEntryModel entry = createEnchantment(rl.toString(), level);
        int insertIndex = canAddEntryInList() ? Math.max(getEntries().size() - 1, getEntryListStart()) : getEntries().size();
        getEntries().add(insertIndex, entry);
        updateEntryListIndexes();
    }

    public void syncSelection(Set<Identifier> selectedIds, EnchantmentEntryModel currentEntry) {
        Set<Identifier> selected = selectedIds == null ? Set.of() : selectedIds.stream().filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new));
        Class<EnchantmentEntryModel> cls = EnchantmentEntryModel.class;
        List<EnchantmentEntryModel> allEntries = getEntries().stream().filter(cls::isInstance).map(cls::cast).toList();
        List<EnchantmentEntryModel> toRemove = new ArrayList<>();
        for (EnchantmentEntryModel entry : allEntries) {
            Identifier id = normalizeId(entry.getValue());
            if (entry != currentEntry && id != null && !selected.contains(id)) {
                toRemove.add(entry);
            }
        }
        if (!toRemove.isEmpty()) {
            getEntries().removeAll(toRemove);
        }
        if (currentEntry != null) {
            Identifier currentId = normalizeId(currentEntry.getValue());
            if (selected.isEmpty()) {
                currentEntry.setValue("");
            } else if (currentId == null || !selected.contains(currentId)) {
                Set<Identifier> ownedByOthers = getExistingEnchantmentIds();
                if (currentId != null) {
                    ownedByOthers.remove(currentId);
                }
                Identifier candidate = selected.stream().filter(id -> !ownedByOthers.contains(id)).findFirst().orElse(null);
                if (candidate != null) {
                    currentEntry.setValue(candidate.toString());
                } else {
                    getEntries().remove(currentEntry);
                }
            }
        }
        int level = currentEntry == null ? 1 : Math.max(1, currentEntry.getLevel());
        Identifier currentResolved = currentEntry == null ? null : normalizeId(currentEntry.getValue());
        Set<Identifier> existing = getExistingEnchantmentIds();
        for (Identifier id : selected) {
            if (currentResolved == null || !currentResolved.equals(id)) {
                if (!existing.contains(id)) {
                    addEnchantmentEntryIfAbsent(id.toString(), level);
                    existing.add(id);
                }
            }
        }
        updateEntryListIndexes();
    }

    private Identifier normalizeId(String id) {
        String value = id.contains(":") ? id : "minecraft:" + id;
        return Identifier.tryParse(value);
    }

    private void addEnchantment(String id, int lvl) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id);
        tag.putInt("lvl", lvl);
        this.newEnch.add(tag);
    }

    @Override
    public void apply() {
        Identifier rl;
        this.newEnch = new ListTag();
        super.apply();
        ItemStack stack = getStack();
        Optional<? extends HolderLookup.RegistryLookup<Enchantment>> lookupOpt = ClientUtil.registryAccess().lookup(Registries.ENCHANTMENT);
        if (lookupOpt.isEmpty()) {
            LOGGER.error("Missing enchantment registry; cannot apply enchantments to {}", stack);
            return;
        }
        HolderLookup.RegistryLookup<Enchantment> lookup = lookupOpt.get();
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        for (Tag tagElement : this.newEnch) {
            if (!(tagElement instanceof CompoundTag compoundTag)) {
                continue;
            }
            String id = NbtHelper.getString(compoundTag, "id", "");
            int lvl = compoundTag.getIntOr("lvl", 0);
            if (lvl > 0 && (rl = Identifier.tryParse(id)) != null) {
                ResourceKey<Enchantment> key = ResourceKey.create(Registries.ENCHANTMENT, rl);
                lookup.get(key).ifPresent(holder -> {
                    mutable.set(holder, lvl);
                });
            }
        }
        ItemEnchantments applied = mutable.toImmutable();
        if (this.editingStored) {
            if (applied.isEmpty()) {
                stack.remove(DataComponents.STORED_ENCHANTMENTS);
            } else {
                stack.set(DataComponents.STORED_ENCHANTMENTS, applied);
            }
        } else if (applied.isEmpty()) {
            stack.remove(DataComponents.ENCHANTMENTS);
        } else {
            stack.set(DataComponents.ENCHANTMENTS, applied);
        }
        clearLegacyEnchantments();
    }

    private ItemStack getStack() {
        return getParent().getContext().getItemStack();
    }

    private void clearLegacyEnchantments() {
        CompoundTag tag;
        CompoundTag data = getData();
        if (data == null || (tag = data.getCompound("tag").orElse(null)) == null) {
            return;
        }
        String key = this.editingStored ? "StoredEnchantments" : "Enchantments";
        if (tag.contains(key)) {
            tag.remove(key);
        }
    }

    private boolean shouldEditStored(ItemStack stack) {
        if (stack.is(Items.ENCHANTED_BOOK)) {
            return true;
        }
        ItemEnchantments stored = (ItemEnchantments) stack.get(DataComponents.STORED_ENCHANTMENTS);
        ItemEnchantments normal = (ItemEnchantments) stack.get(DataComponents.ENCHANTMENTS);
        return stored != null && (normal == null || normal.isEmpty());
    }

    private boolean getGlintOverride(ItemStack stack) {
        return stack.hasFoil();
    }

    private void setGlintOverride(boolean value) {
        ItemStack stack = getParent().getContext().getItemStack();
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, value);
    }

    private void setEnchantableEnabled(boolean value) {
        this.enchantableEnabled = value;
        updateEnchantableValueEntry();
        applyEnchantableComponent();
    }

    private void setEnchantableValue(Integer value) {
        this.enchantableValue = value == null ? 1 : Math.max(1, value);
        if (this.enchantableEnabled) {
            applyEnchantableComponent();
        }
    }

    private void updateEnchantableValueEntry() {
        if (this.enchantableEnabled) {
            insertEnchantableValueEntry();
        } else {
            getEntries().remove(this.enchantableValueEntry);
        }
    }

    private void insertEnchantableValueEntry() {
        if (getEntries().contains(this.enchantableValueEntry)) {
            return;
        }
        int toggleIndex = getEntries().indexOf(this.enchantableToggleEntry);
        if (toggleIndex < 0) {
            getEntries().add(this.enchantableValueEntry);
        } else {
            getEntries().add(toggleIndex + 1, this.enchantableValueEntry);
        }
    }

    private void applyEnchantableComponent() {
        ItemStack stack = getParent().getContext().getItemStack();
        if (this.enchantableEnabled) {
            try {
                stack.set(DataComponents.ENCHANTABLE, new Enchantable(this.enchantableValue));
                return;
            } catch (IllegalArgumentException e) {
                this.enchantableValue = Math.max(1, this.enchantableValue);
                stack.set(DataComponents.ENCHANTABLE, new Enchantable(this.enchantableValue));
                return;
            }
        }
        stack.remove(DataComponents.ENCHANTABLE);
        getParent().removeComponentFromDataTag("minecraft:enchantable");
    }
}