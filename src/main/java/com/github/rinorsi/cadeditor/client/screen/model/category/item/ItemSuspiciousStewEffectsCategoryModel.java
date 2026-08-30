package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.ClientCache;
import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.PotionEffectEntryModel;
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
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SuspiciousStewEffects;


public class ItemSuspiciousStewEffectsCategoryModel extends ItemEditorCategoryModel {
    private final List<SuspiciousStewEffects.Entry> stagedEffects;

    public ItemSuspiciousStewEffectsCategoryModel(ItemEditorModel editor) {
        super(ModTexts.gui("suspicious_stew_effects"), editor);
        this.stagedEffects = new ArrayList<>();
    }

    @Override 
    protected void setupEntries() {
        List<EffectData> effects = readEffects();
        if (effects.isEmpty()) {
            getEntries().add(createEffectEntry(null));
            return;
        }
        for (EffectData e : effects) {
            getEntries().add(createEffectEntry(e));
        }
    }

    private List<EffectData> readEffects() {
        CompoundTag comps;
        ListTag list;
        ItemStack stack = getParent().getContext().getItemStack();
        SuspiciousStewEffects component = stack.get(DataComponents.SUSPICIOUS_STEW_EFFECTS);
        if (component != null) {
            List<EffectData> effects = new ArrayList<>();
            component.effects().forEach(entry -> {
                String id = entry.effect().unwrapKey().map(key -> key.identifier().toString()).orElse("minecraft:empty");
                effects.add(new EffectData(id, entry.duration()));
            });
            if (!effects.isEmpty()) {
                return effects;
            }
        }
        CompoundTag data = getData();
        if (data != null && (comps = data.getCompound("components").orElse(null)) != null && (list = comps.getList("minecraft:suspicious_stew_effects").orElse(null)) != null) {
            List<EffectData> out = new ArrayList<>(list.size());
            for (int i = 0; i < list.size(); i++) {
                CompoundTag c = list.getCompound(i).orElse(null);
                if (c != null) {
                    String id = NbtHelper.getString(c, "id", "");
                    if (!id.isEmpty()) {
                        int duration = c.getIntOr("duration", 160);
                        out.add(new EffectData(id, duration));
                    }
                }
            }
            return out;
        }
        return List.of();
    }

    @Override 
    public int getEntryListStart() {
        return 0;
    }

    @Override 
    public EntryModel createNewListEntry() {
        return createEffectEntry(null);
    }

    @Override 
    public void addEntryInList() {
        openEffectSelection();
    }

    @Override 
    public int getEntryHeight() {
        return 50;
    }

    @Override 
    public void apply() {
        this.stagedEffects.clear();
        super.apply();
        ItemStack stack = getParent().getContext().getItemStack();
        if (this.stagedEffects.isEmpty()) {
            stack.remove(DataComponents.SUSPICIOUS_STEW_EFFECTS);
        } else {
            stack.set(DataComponents.SUSPICIOUS_STEW_EFFECTS, new SuspiciousStewEffects(List.copyOf(this.stagedEffects)));
        }
    }

    private EntryModel createEffectEntry(EffectData data) {
        if (data != null) {
            return new PotionEffectEntryModel(this, data.id(), 0, data.duration(), false, true, true, this::collectEffect);
        }
        String defaultId = MobEffects.SPEED.unwrapKey().map(key -> key.identifier().toString()).orElse("minecraft:movement_speed");
        return new PotionEffectEntryModel(this, defaultId, 0, 160, false, true, true, this::collectEffect);
    }

    private void collectEffect(PotionEffectEntryModel entry) {
        Identifier rl = Identifier.tryParse(entry.getValue());
        if (rl == null) {
            return;
        }
        Optional<? extends HolderLookup.RegistryLookup<MobEffect>> lookupOpt = ClientUtil.registryAccess().lookup(Registries.MOB_EFFECT);
        if (lookupOpt.isEmpty()) {
            return;
        }
        Optional<Holder.Reference<MobEffect>> holder = ((HolderLookup.RegistryLookup) lookupOpt.get()).get(ResourceKey.create(Registries.MOB_EFFECT, rl));
        if (holder.isEmpty()) {
            return;
        }
        int duration = entry.getDuration() == -1 ? -1 : Math.max(1, entry.getDuration());
        this.stagedEffects.add(new SuspiciousStewEffects.Entry((Holder) holder.get(), duration));
    }

    
    private static record EffectData(String id, int duration) {
    }

    private void openEffectSelection() {
        Set<Identifier> current = collectEffectIds();
        ModScreenHandler.openListSelectionScreen(ModTexts.EFFECTS.copy(), "", ClientCache.getEffectSelectionItems(), value -> {
        }, true, this::applySelectedEffects, current);
    }

    private void applySelectedEffects(List<Identifier> selected) {
        Map<Identifier, PotionEffectEntryModel> existing = new LinkedHashMap<>();
        for (EntryModel entry : getEntries()) {
            if (entry instanceof PotionEffectEntryModel) {
                PotionEffectEntryModel effect = (PotionEffectEntryModel) entry;
                Identifier id = Identifier.tryParse(effect.getValue());
                if (id != null) {
                    existing.putIfAbsent(id, effect);
                }
            }
        }
        List<PotionEffectEntryModel> desired = new ArrayList<>();
        if (selected != null) {
            for (Identifier id : selected) {
                PotionEffectEntryModel entry = existing.remove(id);
                if (entry == null) {
                    entry = (PotionEffectEntryModel) createEffectEntry(new EffectData(id.toString(), 160));
                }
                desired.add(entry);
            }
        }
        replaceEffectEntries(desired);
    }

    private void replaceEffectEntries(List<PotionEffectEntryModel> entries) {
        int start = getEntryListStart();
        int endExclusive = getEntries().size() - (canAddEntryInList() ? 1 : 0);
        for (int i = endExclusive - 1; i >= start; i--) {
            getEntries().remove(i);
        }
        getEntries().addAll(start, entries);
        updateEntryListIndexes();
    }

    private Set<Identifier> collectEffectIds() {
        Set<Identifier> ids = new LinkedHashSet<>();
        for (EntryModel entry : getEntries()) {
            if (entry instanceof PotionEffectEntryModel) {
                PotionEffectEntryModel effect = (PotionEffectEntryModel) entry;
                Identifier id = Identifier.tryParse(effect.getValue());
                if (id != null) {
                    ids.add(id);
                }
            }
        }
        return ids;
    }
}