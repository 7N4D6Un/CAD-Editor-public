package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.ClientCache;
import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.ModTextures;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.FloatEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.StringWithActionsEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.FoodEffectEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.SoundEventSelectionEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DeathProtection;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.consume_effects.PlaySoundConsumeEffect;
import net.minecraft.world.item.consume_effects.RemoveStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.TeleportRandomlyConsumeEffect;


public class ItemDeathProtectionCategoryModel extends ItemEditorCategoryModel {
    private final List<ConsumableState.ConsumableEffectData> initialEffects;
    private final List<ConsumableState.ConsumableEffectData> stagedEffects;
    private boolean clearStatusEffects;
    private String removeEffectsText = "";
    private String playSoundId = "";
    private boolean teleportRandomlyEnabled = false;
    private float teleportDiameter = 16.0f;
    private BooleanEntryModel behaviorToggle;
    private SoundEventSelectionEntryModel playSoundEntry;
    private StringWithActionsEntryModel removeEffectsEntry;
    private boolean enableDeathProtection;

    public ItemDeathProtectionCategoryModel(ItemEditorModel editor) {
        super(ModTexts.gui("death_protection"), editor);
        this.initialEffects = new ArrayList<>();
        this.stagedEffects = new ArrayList<>();
    }

    @Override 
    protected void setupEntries() {
        loadStateFromStack();
        this.enableDeathProtection = getParent().getContext().getItemStack().has(DataComponents.DEATH_PROTECTION);
        this.behaviorToggle = new BooleanEntryModel(this, ModTexts.gui("death_protection_behavior_enabled"), this.enableDeathProtection, value -> {
            this.enableDeathProtection = value != null && value;
            syncEntriesEnabled();
        });
        getEntries().add(this.behaviorToggle);
        getEntries().add(new BooleanEntryModel(this, ModTexts.gui("death_protection_clear_effects"), this.clearStatusEffects, value -> this.clearStatusEffects = value != null && value));
        this.removeEffectsEntry = new StringWithActionsEntryModel(this, ModTexts.gui("death_protection_remove_effects"), this.removeEffectsText, value -> this.removeEffectsText = value == null ? "" : value.trim());
        this.removeEffectsEntry.setPlaceholder("minecraft:poison 或 #minecraft:disease");
        this.removeEffectsEntry.addButton(new StringWithActionsEntryModel.ActionButton(ModTextures.SEARCH, ModTexts.gui("select_effect"), this::openRemoveEffectSelection));
        this.removeEffectsEntry.addButton(new StringWithActionsEntryModel.ActionButton(ModTextures.SEARCH, ModTexts.gui("select_tag"), this::openRemoveEffectTagSelection));
        getEntries().add(this.removeEffectsEntry);
        String namespace = this.playSoundId.contains(":") ? this.playSoundId.substring(0, this.playSoundId.indexOf(58)) : "minecraft";
        this.playSoundEntry = new SoundEventSelectionEntryModel(this, ModTexts.gui("death_protection_play_sound"), this.playSoundId, this::applyPlaySoundId, "namespace:" + namespace);
        getEntries().add(this.playSoundEntry);
        getEntries().add(new BooleanEntryModel(this, ModTexts.gui("death_protection_teleport_randomly"), this.teleportRandomlyEnabled, value -> this.teleportRandomlyEnabled = value != null && value));
        getEntries().add(new FloatEntryModel(this, ModTexts.gui("death_protection_teleport_diameter"), this.teleportDiameter, value -> this.teleportDiameter = value == null ? 16.0f : Math.max(0.05f, value)));
        this.initialEffects.forEach(effect -> {
            getEntries().add(createEffectEntry(effect));
        });
        syncEntriesEnabled();
    }

    @Override
    public void initalize() {
        super.initalize();
        syncEntriesEnabled();
    }

    @Override
    public void addEntryInList() {
        super.addEntryInList();
        syncEntriesEnabled();
    }

    private void syncEntriesEnabled() {
        for (EntryModel entry : getEntries()) {
            if (entry != this.behaviorToggle) {
                entry.setEnabled(this.enableDeathProtection);
            }
        }
    }

    @Override 
    public int getEntryListStart() {
        return 6;
    }

    @Override 
    public EntryModel createNewListEntry() {
        return createEffectEntry(null);
    }

    @Override 
    public int getEntryHeight() {
        return 24;
    }

    @Override
    public int getEntryHeight(EntryModel entry) {
        return entry instanceof FoodEffectEntryModel ? 50 : 24;
    }

    @Override 
    protected MutableComponent getAddListEntryButtonTooltip() {
        return ModTexts.EFFECT;
    }

    @Override 
    public void apply() {
        getParent().setDeathProtectionEnabled(this.enableDeathProtection);
        ItemStack stack = getParent().getContext().getItemStack();
        if (!this.enableDeathProtection) {
            stack.remove(DataComponents.DEATH_PROTECTION);
            removeComponentFromData();
            return;
        }
        this.stagedEffects.clear();
        super.apply();
        List<ConsumeEffect> effects = new ArrayList<>();
        if (this.clearStatusEffects) {
            effects.add(ClearAllStatusEffectsConsumeEffect.INSTANCE);
        }
        for (ConsumableState.ConsumableEffectData data : this.stagedEffects) {
            if (data == null) {
                continue;
            }
            float probability = data.probability();
            if (probability > 0.0f) {
                if (probability > 1.0f) {
                    probability = 1.0f;
                }
                MobEffectInstance inst = data.effect();
                if (inst != null) {
                    effects.add(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(inst), probability));
                }
            }
        }
        Optional<HolderSet<MobEffect>> removeSet = buildRemoveEffectSet();
        if (removeSet.isPresent()) {
            effects.add(new RemoveStatusEffectsConsumeEffect(removeSet.get()));
        }
        Optional<? extends HolderLookup.RegistryLookup<SoundEvent>> soundLookup = ClientUtil.registryAccess().lookup(Registries.SOUND_EVENT);
        if (!this.playSoundId.isBlank() && soundLookup.isPresent()) {
            Identifier srl = ClientUtil.parseResourceLocation(this.playSoundId);
            if (srl != null) {
                Optional<Holder.Reference<SoundEvent>> holder = soundLookup.get().get(ResourceKey.create(Registries.SOUND_EVENT, srl));
                if (holder.isPresent()) {
                    effects.add(new PlaySoundConsumeEffect(holder.get()));
                }
            }
        }
        if (this.teleportRandomlyEnabled) {
            effects.add(new TeleportRandomlyConsumeEffect(Math.max(0.05f, this.teleportDiameter)));
        }
        if (effects.isEmpty()) {
            stack.remove(DataComponents.DEATH_PROTECTION);
            removeComponentFromData();
        } else {
            stack.set(DataComponents.DEATH_PROTECTION, new DeathProtection(List.copyOf(effects)));
        }
    }

    private void loadStateFromStack() {
        this.initialEffects.clear();
        this.clearStatusEffects = false;
        this.removeEffectsText = "";
        this.playSoundId = "";
        this.teleportRandomlyEnabled = false;
        this.teleportDiameter = 16.0f;
        ItemStack stack = getParent().getContext().getItemStack();
        DeathProtection component = stack.get(DataComponents.DEATH_PROTECTION);
        if (component != null) {
            for (ConsumeEffect effectElement : component.deathEffects()) {
                if (effectElement instanceof ClearAllStatusEffectsConsumeEffect) {
                    this.clearStatusEffects = true;
                } else if (effectElement instanceof ApplyStatusEffectsConsumeEffect apply) {
                    float probability = apply.probability();
                    apply.effects().forEach(inst -> {
                        this.initialEffects.add(new ConsumableState.ConsumableEffectData(new MobEffectInstance(inst), probability));
                    });
                } else if (effectElement instanceof RemoveStatusEffectsConsumeEffect remove) {
                    Optional<TagKey<MobEffect>> tagKey = remove.effects().unwrapKey();
                    if (tagKey.isPresent()) {
                        this.removeEffectsText = "#" + tagKey.get().location();
                    } else {
                        this.removeEffectsText = remove.effects().stream().map(holder -> holder.unwrapKey().map(key -> key.identifier().toString()).orElse("")).filter(s -> !s.isEmpty()).collect(java.util.stream.Collectors.joining(", "));
                    }
                } else if (effectElement instanceof PlaySoundConsumeEffect playSound) {
                    this.playSoundId = playSound.sound().unwrapKey().map(key -> key.identifier().toString()).orElse("");
                } else if (effectElement instanceof TeleportRandomlyConsumeEffect teleport) {
                    this.teleportRandomlyEnabled = true;
                    this.teleportDiameter = teleport.diameter();
                }
            }
        } else {
            this.clearStatusEffects = true;
        }
    }

    private Optional<HolderSet<MobEffect>> buildRemoveEffectSet() {
        if (this.removeEffectsText.isBlank()) {
            return Optional.empty();
        }
        Optional<? extends HolderLookup.RegistryLookup<MobEffect>> lookupOpt = ClientUtil.registryAccess().lookup(Registries.MOB_EFFECT);
        if (lookupOpt.isEmpty()) {
            return Optional.empty();
        }
        HolderLookup.RegistryLookup<MobEffect> lookup = lookupOpt.get();
        Set<Holder<MobEffect>> holders = new LinkedHashSet<>();
        for (String token : this.removeEffectsText.split(",")) {
            String entry = token.trim();
            if (entry.isEmpty()) {
                continue;
            }
            if (entry.startsWith("#")) {
                Identifier rl = ClientUtil.parseResourceLocation(entry.substring(1));
                if (rl != null) {
                    lookup.get(TagKey.create(Registries.MOB_EFFECT, rl)).ifPresent(holderSet -> holderSet.forEach(holders::add));
                }
            } else {
                Identifier rl = ClientUtil.parseResourceLocation(entry);
                if (rl != null) {
                    lookup.get(ResourceKey.create(Registries.MOB_EFFECT, rl)).ifPresent(holders::add);
                }
            }
        }
        if (holders.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(HolderSet.direct(List.copyOf(holders)));
    }

    private void applyPlaySoundId(String id) {
        String sanitized = id == null ? "" : id.trim();
        this.playSoundId = sanitized;
        if (this.playSoundEntry != null) {
            Optional<? extends HolderLookup.RegistryLookup<SoundEvent>> lookupOpt = ClientUtil.registryAccess().lookup(Registries.SOUND_EVENT);
            boolean valid = sanitized.isBlank() || (ClientUtil.parseResourceLocation(sanitized) != null && lookupOpt.isPresent());
            this.playSoundEntry.setValid(valid);
        }
    }

    private void openRemoveEffectSelection() {
        Set<Identifier> initiallySelected = extractPlainIds(this.removeEffectsText);
        ModScreenHandler.openListSelectionScreen(ModTexts.gui("select_effect"), "death_protection_remove_effects", ClientCache.getEffectSelectionItems(), null, true, selected -> {
            List<String> entries = new ArrayList<>();
            for (String entry : parseIdentifierList(this.removeEffectsText)) {
                if (entry.startsWith("#")) {
                    entries.add(entry);
                }
            }
            for (Identifier rl : selected) {
                entries.add(rl.toString());
            }
            this.removeEffectsText = String.join(", ", entries).trim();
            this.removeEffectsEntry.setValue(this.removeEffectsText);
        }, initiallySelected);
    }

    private void openRemoveEffectTagSelection() {
        Set<Identifier> initiallySelected = extractTagIds(this.removeEffectsText);
        ModScreenHandler.openListSelectionScreen(ModTexts.gui("select_tag"), "death_protection_remove_effects_tags", ClientCache.getEffectTagSelectionItems(), null, true, selected -> {
            List<String> entries = new ArrayList<>();
            for (String entry : parseIdentifierList(this.removeEffectsText)) {
                if (!entry.startsWith("#")) {
                    entries.add(entry);
                }
            }
            for (Identifier rl : selected) {
                entries.add("#" + rl);
            }
            this.removeEffectsText = String.join(", ", entries).trim();
            this.removeEffectsEntry.setValue(this.removeEffectsText);
        }, initiallySelected);
    }

    private List<String> parseIdentifierList(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        List<String> out = new ArrayList<>();
        for (String part : raw.split("[,\\n]")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                out.add(trimmed);
            }
        }
        return out;
    }

    private Set<Identifier> extractTagIds(String raw) {
        Set<Identifier> set = new LinkedHashSet<>();
        for (String entry : parseIdentifierList(raw)) {
            if (entry.startsWith("#")) {
                Identifier rl = ClientUtil.parseResourceLocation(entry.substring(1));
                if (rl != null) {
                    set.add(rl);
                }
            }
        }
        return set;
    }

    private Set<Identifier> extractPlainIds(String raw) {
        Set<Identifier> set = new LinkedHashSet<>();
        for (String entry : parseIdentifierList(raw)) {
            if (!entry.startsWith("#")) {
                Identifier rl = ClientUtil.parseResourceLocation(entry);
                if (rl != null) {
                    set.add(rl);
                }
            }
        }
        return set;
    }

    private EntryModel createEffectEntry(ConsumableState.ConsumableEffectData effect) {
        if (effect != null) {
            MobEffectInstance instance = effect.effect();
            String id = instance.getEffect().unwrapKey().map(key -> key.identifier().toString()).orElse("minecraft:empty");
            return new FoodEffectEntryModel(this, id, instance.getAmplifier(), instance.getDuration(), instance.isAmbient(), instance.isVisible(), instance.showIcon(), effect.probability(), this::collectEffect);
        }
        String defaultId = MobEffects.REGENERATION.unwrapKey().map(effectKey -> effectKey.identifier().toString()).orElse("minecraft:regeneration");
        return new FoodEffectEntryModel(this, defaultId, 1, 900, false, true, true, 1.0d, this::collectEffect);
    }

    private void collectEffect(String id, int amplifier, int duration, boolean ambient, boolean showParticles, boolean showIcon, float probability) {
        Optional<? extends HolderLookup.RegistryLookup<MobEffect>> registryOpt = ClientUtil.registryAccess().lookup(Registries.MOB_EFFECT);
        if (registryOpt.isEmpty()) {
            return;
        }
        Identifier parsed = id == null ? null : ClientUtil.parseResourceLocation(id);
        if (parsed == null) {
            return;
        }
        ResourceKey<MobEffect> key = ResourceKey.create(Registries.MOB_EFFECT, parsed);
        Holder<MobEffect> holder = (Holder) ((HolderLookup.RegistryLookup) registryOpt.get()).get(key).orElse(null);
        if (holder == null) {
            return;
        }
        int clampedDuration = duration == -1 ? -1 : Math.max(1, Math.min(duration, 72000));
        int clampedAmplifier = Math.max(0, Math.min(amplifier, 255));
        MobEffectInstance instance = new MobEffectInstance(holder, clampedDuration, clampedAmplifier, ambient, showParticles, showIcon);
        this.stagedEffects.add(new ConsumableState.ConsumableEffectData(instance, probability));
    }

    private void removeComponentFromData() {
        getParent().removeComponentFromDataTag("minecraft:death_protection");
    }

}
