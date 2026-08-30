package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.franckyi.guapi.api.Color;
import com.github.rinorsi.cadeditor.client.ClientCache;
import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.FloatEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.PotionEffectEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.PotionSelectionEntryModel;
import com.github.rinorsi.cadeditor.client.util.NbtHelper;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;


public class ItemPotionEffectsCategoryModel extends ItemEditorCategoryModel {
    private List<CompoundTag> collectedCustomEffects;
    private String selectedPotionId;
    private int selectedCustomColor;
    private FloatEntryModel potionDurationScaleEntry;

    public ItemPotionEffectsCategoryModel(ItemEditorModel editor) {
        super(ModTexts.POTION_EFFECTS, editor);
        this.selectedPotionId = "";
        this.selectedCustomColor = Color.NONE;
    }

    @Override
    protected void setupEntries() {
        PotionContents contents = getParent().getContext().getItemStack().get(DataComponents.POTION_CONTENTS);
        String potionId = "";
        int customColor = Color.NONE;
        if (contents != null) {
            potionId = contents.potion().flatMap(h -> h.unwrapKey().map(k -> Optional.of(k.identifier().toString())).orElse(Optional.empty())).orElse("");
            customColor = contents.customColor().orElse(Color.NONE);
        }
        this.selectedPotionId = potionId;
        this.selectedCustomColor = customColor;
        Float durationScale = getStack().get(DataComponents.POTION_DURATION_SCALE);
        this.potionDurationScaleEntry = new FloatEntryModel(this, ModTexts.gui("potion_duration_scale"),
                durationScale == null ? 1.0f : durationScale, value -> {
            float scale = value == null ? 1.0f : Math.max(0.0f, value);
            ItemStack potionStack = getStack();
            if (scale == 1.0f) {
                potionStack.remove(DataComponents.POTION_DURATION_SCALE);
            } else {
                potionStack.set(DataComponents.POTION_DURATION_SCALE, scale);
            }
        }, value -> value != null && value >= 0.0f);
        getEntries().add(new PotionSelectionEntryModel(this, ModTexts.DEFAULT_POTION, potionId, customColor, this::setPotionId, value -> setCustomPotionColor(value)));
        getEntries().add(this.potionDurationScaleEntry);
        if (contents != null) {
            contents.customEffects().forEach(e -> getEntries().add(createCustomEffectEntry(toTag(e))));
        }
    }

    @Override
    public void apply() {
        this.collectedCustomEffects = new ArrayList<>();
        super.apply();
        ItemStack stack = getParent().getContext().getItemStack();
        HolderLookup.Provider registry = ClientUtil.registryAccess();
        List<MobEffectInstance> effects = new ArrayList<>();
        registry.lookup(Registries.MOB_EFFECT).ifPresent(effectLookup -> {
            for (CompoundTag c : this.collectedCustomEffects) {
                String id = NbtHelper.getString(c, "id", "");
                Identifier rl = Identifier.tryParse(id);
                if (rl != null) {
                    effectLookup.get(ResourceKey.create(Registries.MOB_EFFECT, rl)).ifPresent(holder ->
                            effects.add(new MobEffectInstance(holder, c.getIntOr("duration", 1), c.getIntOr("amplifier", 0),
                                    c.getBooleanOr("ambient", false),
                                    !c.contains("show_particles") || c.getBooleanOr("show_particles", true),
                                    c.getBooleanOr("show_icon", false))));
                }
            }
        });
        String potionStr = this.selectedPotionId == null ? "" : this.selectedPotionId.trim();
        Optional<Holder<Potion>> pot = Optional.empty();
        if (!potionStr.isEmpty()) {
            Identifier rl = Identifier.tryParse(potionStr);
            if (rl != null && registry.lookup(Registries.POTION).isPresent()) {
                HolderLookup.RegistryLookup<Potion> potionLookup = registry.lookup(Registries.POTION).get();
                Optional<Holder.Reference<Potion>> holderOpt = potionLookup.get(ResourceKey.create(Registries.POTION, rl));
                if (holderOpt.isPresent()) {
                    pot = Optional.of(holderOpt.get());
                }
            }
        }
        int customColor = getCustomPotionColor();
        boolean hasCustomColor = customColor != Color.NONE;
        if (pot.isEmpty() && effects.isEmpty() && !hasCustomColor) {
            stack.remove(DataComponents.POTION_CONTENTS);
        } else {
            stack.set(DataComponents.POTION_CONTENTS, new PotionContents(pot,
                    hasCustomColor ? Optional.of(customColor) : Optional.empty(), effects, Optional.empty()));
        }
        CompoundTag data = getData();
        CompoundTag legacy = data == null ? null : data.getCompound("tag").orElse(null);
        if (legacy != null) {
            legacy.remove("Potion");
            legacy.remove("CustomPotionColor");
            legacy.remove("custom_potion_effects");
            if (legacy.isEmpty()) {
                data.remove("tag");
            }
        }
    }

    @Override
    public int getEntryListStart() {
        return 2;
    }

    @Override
    public EntryModel createNewListEntry() {
        return createCustomEffectEntry(null);
    }

    @Override
    public void addEntryInList() {
        openCustomEffectSelection();
    }

    @Override
    public int getEntryHeight() {
        return 50;
    }

    @Override
    protected MutableComponent getAddListEntryButtonTooltip() {
        return ModTexts.EFFECT;
    }

    private PotionEffectEntryModel createCustomEffectEntry(CompoundTag tag) {
        if (tag == null) {
            String defaultId = MobEffects.SPEED.unwrapKey().map(key -> key.identifier().toString()).orElse("minecraft:movement_speed");
            return new PotionEffectEntryModel(this, defaultId, 0, 1, false, true, true, this::collectPotionEffect);
        }
        return new PotionEffectEntryModel(this,
                NbtHelper.getString(tag, "id", ""),
                tag.getIntOr("amplifier", 0),
                tag.getIntOr("duration", 1),
                tag.getBooleanOr("ambient", false),
                !tag.contains("show_particles") || tag.getBooleanOr("show_particles", true),
                tag.getBooleanOr("show_icon", false),
                this::collectPotionEffect);
    }

    private void collectPotionEffect(PotionEffectEntryModel entry) {
        this.collectedCustomEffects.add(entry.toCompoundTag());
    }

    private void setPotionId(String potionId) {
        this.selectedPotionId = potionId == null ? "" : potionId.trim();
    }

    private int getCustomPotionColor() {
        return this.selectedCustomColor;
    }

    private ItemStack getStack() {
        return getParent().getContext().getItemStack();
    }

    private void setCustomPotionColor(int color) {
        this.selectedCustomColor = color;
    }

    private static CompoundTag toTag(MobEffectInstance e) {
        CompoundTag tag = new CompoundTag();
        String id = e.getEffect().unwrapKey().map(k -> k.identifier().toString()).orElse("");
        tag.putString("id", id);
        tag.putInt("amplifier", e.getAmplifier());
        tag.putInt("duration", e.getDuration());
        tag.putBoolean("ambient", e.isAmbient());
        tag.putBoolean("show_particles", e.isVisible());
        tag.putBoolean("show_icon", e.showIcon());
        return tag;
    }

    private void openCustomEffectSelection() {
        Set<Identifier> current = collectCustomEffectIds();
        ModScreenHandler.openListSelectionScreen(ModTexts.EFFECTS.copy(), "", ClientCache.getEffectSelectionItems(), value -> {
        }, true, this::applyCustomSelection, current);
    }

    private void applyCustomSelection(List<Identifier> selected) {
        Identifier id;
        Map<Identifier, PotionEffectEntryModel> existing = new LinkedHashMap<>();
        for (EntryModel entry : getEntries()) {
            if (entry instanceof PotionEffectEntryModel) {
                PotionEffectEntryModel effect = (PotionEffectEntryModel) entry;
                if ((id = Identifier.tryParse(effect.getValue())) != null) {
                    existing.putIfAbsent(id, effect);
                }
            }
        }
        List<PotionEffectEntryModel> desired = new ArrayList<>();
        if (selected != null) {
            for (Identifier selectedId : selected) {
                PotionEffectEntryModel entry = existing.remove(selectedId);
                if (entry == null) {
                    entry = (PotionEffectEntryModel) createCustomEffectEntry(null);
                    entry.setValue(selectedId.toString());
                }
                desired.add(entry);
            }
        }
        replaceCustomEffectEntries(desired);
    }

    private void replaceCustomEffectEntries(List<PotionEffectEntryModel> customEntries) {
        int start = getEntryListStart();
        int endExclusive = getEntries().size() - (canAddEntryInList() ? 1 : 0);
        for (int i = endExclusive - 1; i >= start; i--) {
            getEntries().remove(i);
        }
        getEntries().addAll(start, customEntries);
        updateEntryListIndexes();
    }

    private Set<Identifier> collectCustomEffectIds() {
        Identifier id;
        Set<Identifier> ids = new LinkedHashSet<>();
        for (EntryModel entry : getEntries()) {
            if (entry instanceof PotionEffectEntryModel) {
                PotionEffectEntryModel effect = (PotionEffectEntryModel) entry;
                if ((id = Identifier.tryParse(effect.getValue())) != null) {
                    ids.add(id);
                }
            }
        }
        return ids;
    }
}
