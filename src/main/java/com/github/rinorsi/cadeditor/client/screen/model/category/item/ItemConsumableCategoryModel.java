package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.franckyi.databindings.api.ObservableList;
import com.github.rinorsi.cadeditor.client.ClientCache;
import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.ModScreenHandler;
import com.github.rinorsi.cadeditor.client.ModTextures;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EnumEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.FloatEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.StringWithActionsEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.FoodEffectEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.SoundEventSelectionEntryModel;
import com.github.rinorsi.cadeditor.client.util.CompatFood;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemUseAnimation;


public class ItemConsumableCategoryModel extends ItemEditorCategoryModel {
    private static final int MAX_DURATION_TICKS = 72000;
    private static final int MAX_AMPLIFIER = 255;
    private final ConsumableState state;
    private BooleanEntryModel behaviorToggle;
    private SoundEventSelectionEntryModel soundEntry;
    private SoundEventSelectionEntryModel playSoundEntry;
    private StringWithActionsEntryModel removeEffectsEntry;
    private List<ConsumableState.ConsumableEffectData> stagedEffects;
    private boolean enableConsumableBehavior;

    public ItemConsumableCategoryModel(ItemEditorModel editor) {
        super(ModTexts.gui("consumable_behavior"), editor);
        this.state = editor.getConsumableState();
        this.stagedEffects = List.of();
    }

    @Override
    public void initalize() {
        super.initalize();
        syncEntriesEnabled();
    }

    @Override
    protected void setupEntries() {
        ObservableList<EntryModel> entries = getEntries();
        this.enableConsumableBehavior = getParent().getContext().getItemStack().has(DataComponents.CONSUMABLE);
        this.behaviorToggle = new BooleanEntryModel(this, ModTexts.gui("consumable_behaviour_enabled"), this.enableConsumableBehavior, value -> {
            boolean enabled = value != null && value;
            if (enabled && !this.enableConsumableBehavior) {
                applyConsumeSoundId("minecraft:entity.generic.eat");
            }
            this.enableConsumableBehavior = enabled;
            syncEntriesEnabled();
        });
        entries.add(this.behaviorToggle);
        boolean hasConsumeParticles = this.state.hasConsumeParticles();
        entries.add(new BooleanEntryModel(this, ModTexts.gui("consume_particles"), hasConsumeParticles, this.state::setHasConsumeParticles));
        float consumeSeconds = this.state.getConsumeSeconds();
        entries.add(new FloatEntryModel(this, ModTexts.gui("consume_seconds"), consumeSeconds, this.state::setConsumeSeconds));
        List<ItemUseAnimation> listAsList = Arrays.asList(ItemUseAnimation.values());
        ItemUseAnimation animation = this.state.getAnimation();
        EnumEntryModel<ItemUseAnimation> animationEntry = new EnumEntryModel<>(this, ModTexts.gui("use_animation"), listAsList, animation, this.state::setAnimation).withTextFactory(ModTexts::useAnimationOption);
        entries.add(animationEntry);
        String soundId = this.state.getConsumeSoundId();
        String namespace = soundId.contains(":") ? soundId.substring(0, soundId.indexOf(58)) : "minecraft";
        this.soundEntry = new SoundEventSelectionEntryModel(this, ModTexts.gui("consume_sound"), soundId, this::applyConsumeSoundId, "namespace:" + namespace);
        entries.add(this.soundEntry);
        entries.add(new BooleanEntryModel(this, ModTexts.gui("consumable_clear_all_effects"), this.state.isClearAllEffects(), this.state::setClearAllEffects));
        this.removeEffectsEntry = new StringWithActionsEntryModel(this, ModTexts.gui("consumable_remove_effects"), this.state.getRemoveEffectsText(), this.state::setRemoveEffectsText);
        this.removeEffectsEntry.setPlaceholder("minecraft:poison 或 #minecraft:disease");
        this.removeEffectsEntry.addButton(new StringWithActionsEntryModel.ActionButton(ModTextures.SEARCH, ModTexts.gui("select_effect"), this::openRemoveEffectSelection));
        this.removeEffectsEntry.addButton(new StringWithActionsEntryModel.ActionButton(ModTextures.SEARCH, ModTexts.gui("select_tag"), this::openRemoveEffectTagSelection));
        entries.add(this.removeEffectsEntry);
        String playSoundId = this.state.getPlaySoundId();
        String playNamespace = playSoundId.contains(":") ? playSoundId.substring(0, playSoundId.indexOf(58)) : "minecraft";
        this.playSoundEntry = new SoundEventSelectionEntryModel(this, ModTexts.gui("consumable_play_sound"), playSoundId, this::applyPlaySoundId, "namespace:" + playNamespace);
        entries.add(this.playSoundEntry);
        entries.add(new BooleanEntryModel(this, ModTexts.gui("consumable_teleport_randomly"), this.state.isTeleportRandomlyEnabled(), this.state::setTeleportRandomlyEnabled));
        entries.add(new FloatEntryModel(this, ModTexts.gui("consumable_teleport_diameter"), this.state.getTeleportDiameter(), this.state::setTeleportDiameter));
        this.state.getEffects().forEach(effect -> {
            getEntries().add(createFoodEffectEntry(effect));
        });
        syncEntriesEnabled();
    }

    private void syncEntriesEnabled() {
        List<EntryModel> list = getEntries();
        for (int i = 0; i < list.size(); i++) {
            EntryModel entry = list.get(i);
            if (entry == this.behaviorToggle) {
                continue;
            }
            entry.setEnabled(this.enableConsumableBehavior);
        }
    }

    @Override
    public int getEntryListStart() {
        return 11;
    }

    @Override
    public EntryModel createNewListEntry() {
        return createFoodEffectEntry(null);
    }

    @Override
    public void addEntryInList() {
        openEffectSelection();
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
        getParent().setConsumableBehaviorEnabled(this.enableConsumableBehavior);
        this.stagedEffects = new ArrayList<>();
        super.apply();
        this.state.setEffects(this.stagedEffects);
    }

    private void applyConsumeSoundId(String id) {
        String sanitized = id == null ? "" : id.trim();
        boolean success = this.state.setConsumeSoundId(sanitized);
        this.soundEntry.setValid(success);
    }

    private void applyPlaySoundId(String id) {
        String sanitized = id == null ? "" : id.trim();
        boolean success = this.state.setPlaySoundId(sanitized);
        this.playSoundEntry.setValid(success);
    }

    private void openRemoveEffectSelection() {
        Set<Identifier> initiallySelected = extractPlainIds(this.state.getRemoveEffectsText());
        ModScreenHandler.openListSelectionScreen(ModTexts.gui("select_effect"), "consumable_remove_effects", ClientCache.getEffectSelectionItems(), null, true, selected -> {
            List<String> entries = new ArrayList<>();
            for (String entry : parseIdentifierList(this.state.getRemoveEffectsText())) {
                if (entry.startsWith("#")) {
                    entries.add(entry);
                }
            }
            for (Identifier rl : selected) {
                entries.add(rl.toString());
            }
            String joined = String.join(", ", entries).trim();
            this.state.setRemoveEffectsText(joined);
            this.removeEffectsEntry.setValue(joined);
        }, initiallySelected);
    }

    private void openRemoveEffectTagSelection() {
        Set<Identifier> initiallySelected = extractTagIds(this.state.getRemoveEffectsText());
        ModScreenHandler.openListSelectionScreen(ModTexts.gui("select_tag"), "consumable_remove_effects_tags", ClientCache.getEffectTagSelectionItems(), null, true, selected -> {
            List<String> entries = new ArrayList<>();
            for (String entry : parseIdentifierList(this.state.getRemoveEffectsText())) {
                if (!entry.startsWith("#")) {
                    entries.add(entry);
                }
            }
            for (Identifier rl : selected) {
                entries.add("#" + rl);
            }
            String joined = String.join(", ", entries).trim();
            this.state.setRemoveEffectsText(joined);
            this.removeEffectsEntry.setValue(joined);
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

    private EntryModel createFoodEffectEntry(ConsumableState.ConsumableEffectData effect) {
        if (effect != null) {
            MobEffectInstance instance = effect.effect();
            String id = instance.getEffect().unwrapKey().map(key -> key.identifier().toString()).orElse("minecraft:empty");
            return new FoodEffectEntryModel(this, id, instance.getAmplifier(), instance.getDuration(), instance.isAmbient(), instance.isVisible(), instance.showIcon(), effect.probability(), this::addFoodEffect);
        }
        String defaultId = MobEffects.SPEED.unwrapKey().map(key -> key.identifier().toString()).orElse("minecraft:speed");
        return new FoodEffectEntryModel(this, defaultId, 0, 1, false, true, true, 1.0d, this::addFoodEffect);
    }

    private FoodEffectEntryModel createFoodEffectEntryFor(Identifier id) {
        Optional<? extends HolderLookup.RegistryLookup<MobEffect>> registryOpt = ClientUtil.registryAccess().lookup(Registries.MOB_EFFECT);
        if (registryOpt.isPresent()) {
            Optional<Holder.Reference<MobEffect>> holder = ((HolderLookup.RegistryLookup) registryOpt.get()).get(ResourceKey.create(Registries.MOB_EFFECT, id));
            if (holder.isPresent()) {
                MobEffectInstance instance = new MobEffectInstance((Holder) holder.get(), 160, 0, false, true, true);
                return (FoodEffectEntryModel) createFoodEffectEntry(new ConsumableState.ConsumableEffectData(instance, 1.0f));
            }
        }
        FoodEffectEntryModel entry = (FoodEffectEntryModel) createFoodEffectEntry(null);
        entry.setValue(id.toString());
        return entry;
    }

    private void addFoodEffect(String id, int amplifier, int duration, boolean ambient, boolean showParticles, boolean showIcon, float probability) {
        Identifier rl;
        Holder<MobEffect> holder;
        List<ConsumableState.ConsumableEffectData> list;
        Optional<? extends HolderLookup.RegistryLookup<MobEffect>> registryOpt = ClientUtil.registryAccess().lookup(Registries.MOB_EFFECT);
        if (registryOpt.isEmpty() || (rl = Identifier.tryParse(id)) == null || (holder = (Holder) ((HolderLookup.RegistryLookup) registryOpt.get()).get(ResourceKey.create(Registries.MOB_EFFECT, rl)).orElse(null)) == null) {
            return;
        }
        int clampedDuration = duration == -1 ? -1 : Math.max(1, Math.min(duration, MAX_DURATION_TICKS));
        int clampedAmplifier = Math.max(0, Math.min(amplifier, MAX_AMPLIFIER));
        MobEffectInstance instance = new MobEffectInstance(holder, clampedDuration, clampedAmplifier, ambient, showParticles, showIcon);
        List<ConsumableState.ConsumableEffectData> staged;
        if (this.stagedEffects instanceof ArrayList<ConsumableState.ConsumableEffectData> existing) {
            staged = existing;
        } else {
            staged = new ArrayList<>();
            this.stagedEffects = staged;
        }
        CompatFood.makeApplyEffect(instance, probability).ifPresent(staged::add);
    }

    private void openEffectSelection() {
        Set<Identifier> current = collectEffectIds();
        ModScreenHandler.openListSelectionScreen(ModTexts.EFFECTS.copy(), "", ClientCache.getEffectSelectionItems(), value -> {
        }, true, this::applySelectedEffects, current);
    }

    private void applySelectedEffects(List<Identifier> selected) {
        Map<Identifier, FoodEffectEntryModel> existing = new LinkedHashMap<>();
        for (EntryModel entry : getEntries()) {
            if (entry instanceof FoodEffectEntryModel) {
                FoodEffectEntryModel effectEntry = (FoodEffectEntryModel) entry;
                Identifier id = Identifier.tryParse(effectEntry.getValue());
                if (id != null) {
                    existing.putIfAbsent(id, effectEntry);
                }
            }
        }
        List<FoodEffectEntryModel> desired = new ArrayList<>();
        if (selected != null) {
            for (Identifier id : selected) {
                FoodEffectEntryModel entry = existing.remove(id);
                if (entry == null) {
                    entry = createFoodEffectEntryFor(id);
                }
                desired.add(entry);
            }
        }
        replaceEffectEntries(desired);
    }

    private void replaceEffectEntries(List<FoodEffectEntryModel> entries) {
        int start = getEntryListStart();
        int endExclusive = getEntries().size() - (canAddEntryInList() ? 1 : 0);
        for (int i = endExclusive - 1; i >= start; i--) {
            getEntries().remove(i);
        }
        getEntries().addAll(start, entries);
        updateEntryListIndexes();
        syncEntriesEnabled();
    }

    private Set<Identifier> collectEffectIds() {
        Set<Identifier> ids = new LinkedHashSet<>();
        for (EntryModel entry : getEntries()) {
            if (entry instanceof FoodEffectEntryModel) {
                FoodEffectEntryModel effectEntry = (FoodEffectEntryModel) entry;
                Identifier id = Identifier.tryParse(effectEntry.getValue());
                if (id != null) {
                    ids.add(id);
                }
            }
        }
        return ids;
    }
}