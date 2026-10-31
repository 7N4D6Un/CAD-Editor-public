package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.FloatEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.SpacerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.StringEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.BlockTransformerSelectionEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.UseCooldown;
import net.minecraft.world.item.component.UseEffects;

public class ItemUseBehaviorCategoryModel extends ItemEditorCategoryModel {
    private BooleanEntryModel useCooldownToggleEntry;
    private StringEntryModel useCooldownGroupEntry;
    private FloatEntryModel useCooldownSecondsEntry;
    private boolean useCooldownEnabled;
    private String useCooldownGroupId;
    private float useCooldownSeconds;
    private BooleanEntryModel useEffectsToggleEntry;
    private BooleanEntryModel useEffectsCanSprintEntry;
    private BooleanEntryModel useEffectsInteractVibrationsEntry;
    private FloatEntryModel useEffectsSpeedMultiplierEntry;
    private boolean useEffectsEnabled;
    private boolean useEffectsCanSprint;
    private boolean useEffectsInteractVibrations;
    private float useEffectsSpeedMultiplier;
    private BlockTransformerSelectionEntryModel blockTransformerEntry;
    private String blockTransformerId;

    public ItemUseBehaviorCategoryModel(ItemEditorModel editor) {
        super(ModTexts.gui("item_use_behavior"), editor);
    }

    @Override
    protected void setupEntries() {
        ItemStack stack = getParent().getContext().getItemStack();
        UseCooldown cooldown = stack.get(DataComponents.USE_COOLDOWN);
        this.useCooldownEnabled = cooldown != null;
        this.useCooldownGroupId = cooldown != null && cooldown.cooldownGroup().isPresent() ? cooldown.cooldownGroup().get().toString() : "";
        this.useCooldownSeconds = cooldown != null ? cooldown.seconds() : 1.0f;
        this.useCooldownToggleEntry = new BooleanEntryModel(this, ModTexts.gui("use_cooldown_enabled"), this.useCooldownEnabled, value -> setUseCooldownEnabled(value));
        getEntries().add(this.useCooldownToggleEntry);
        this.useCooldownGroupEntry = new StringEntryModel(this, ModTexts.gui("use_cooldown_group"), this.useCooldownGroupId, this::setUseCooldownGroupId);
        this.useCooldownGroupEntry.setPlaceholder("namespace:id (可留空)");
        getEntries().add(this.useCooldownGroupEntry);
        this.useCooldownSecondsEntry = new FloatEntryModel(this, ModTexts.gui("use_cooldown_seconds"), this.useCooldownSeconds, this::setUseCooldownSeconds, value -> value != null && value > 0.0f);
        getEntries().add(this.useCooldownSecondsEntry);
        getEntries().add(new SpacerEntryModel(this));
        UseEffects useEffects = stack.get(DataComponents.USE_EFFECTS);
        this.useEffectsEnabled = useEffects != null;
        this.useEffectsCanSprint = useEffects != null ? useEffects.canSprint() : UseEffects.DEFAULT.canSprint();
        this.useEffectsInteractVibrations = useEffects != null ? useEffects.interactVibrations() : UseEffects.DEFAULT.interactVibrations();
        this.useEffectsSpeedMultiplier = useEffects != null ? useEffects.speedMultiplier() : UseEffects.DEFAULT.speedMultiplier();
        this.useEffectsToggleEntry = new BooleanEntryModel(this, ModTexts.gui("use_partial_behavior_enabled"), this.useEffectsEnabled, value -> setUseEffectsEnabled(value));
        getEntries().add(this.useEffectsToggleEntry);
        this.useEffectsInteractVibrationsEntry = new BooleanEntryModel(this, ModTexts.gui("use_effects_interact_vibrations"), this.useEffectsInteractVibrations, value -> setUseEffectsInteractVibrations(value));
        getEntries().add(this.useEffectsInteractVibrationsEntry);
        this.useEffectsCanSprintEntry = new BooleanEntryModel(this, ModTexts.gui("use_effects_can_sprint"), this.useEffectsCanSprint, value -> setUseEffectsCanSprint(value));
        getEntries().add(this.useEffectsCanSprintEntry);
        this.useEffectsSpeedMultiplierEntry = new FloatEntryModel(this, ModTexts.gui("use_effects_speed_multiplier"), this.useEffectsSpeedMultiplier, this::setUseEffectsSpeedMultiplier, value -> value != null && value > 0.0f);
        getEntries().add(this.useEffectsSpeedMultiplierEntry);
        syncCooldownEntriesEnabled();
        syncUseEffectsEntriesEnabled();
        getEntries().add(new SpacerEntryModel(this));
        Holder<BlockTransformer> blockTransformer = stack.get(DataComponents.BLOCK_TRANSFORMER);
        this.blockTransformerId = blockTransformer != null ? blockTransformer.unwrapKey().map(key -> key.identifier().toString()).orElse("") : "";
        this.blockTransformerEntry = new BlockTransformerSelectionEntryModel(this, this.blockTransformerId, this::setBlockTransformerId);
        this.blockTransformerEntry.setValidator(this::isValidBlockTransformerId);
        getEntries().add(this.blockTransformerEntry);
    }

    private void syncCooldownEntriesEnabled() {
        this.useCooldownGroupEntry.setEnabled(this.useCooldownEnabled);
        this.useCooldownSecondsEntry.setEnabled(this.useCooldownEnabled);
    }

    private void syncUseEffectsEntriesEnabled() {
        this.useEffectsInteractVibrationsEntry.setEnabled(this.useEffectsEnabled);
        this.useEffectsCanSprintEntry.setEnabled(this.useEffectsEnabled);
        this.useEffectsSpeedMultiplierEntry.setEnabled(this.useEffectsEnabled);
    }

    private void setUseCooldownEnabled(boolean value) {
        this.useCooldownEnabled = value;
        syncCooldownEntriesEnabled();
        applyUseCooldownComponent();
    }

    private void setUseCooldownGroupId(String value) {
        this.useCooldownGroupId = value == null ? "" : value.trim();
        if (this.useCooldownEnabled) {
            applyUseCooldownComponent();
        }
    }

    private void setUseCooldownSeconds(Float value) {
        this.useCooldownSeconds = value == null ? 0.0f : Math.max(0.0f, value);
        if (this.useCooldownEnabled) {
            applyUseCooldownComponent();
        }
    }

    private void setUseEffectsEnabled(boolean value) {
        this.useEffectsEnabled = value;
        syncUseEffectsEntriesEnabled();
        applyUseEffectsComponent();
    }

    private void setUseEffectsCanSprint(boolean value) {
        this.useEffectsCanSprint = value;
        if (this.useEffectsEnabled) {
            applyUseEffectsComponent();
        }
    }

    private void setUseEffectsInteractVibrations(boolean value) {
        this.useEffectsInteractVibrations = value;
        if (this.useEffectsEnabled) {
            applyUseEffectsComponent();
        }
    }

    private void setUseEffectsSpeedMultiplier(Float value) {
        this.useEffectsSpeedMultiplier = value == null ? UseEffects.DEFAULT.speedMultiplier() : value;
        if (this.useEffectsEnabled) {
            applyUseEffectsComponent();
        }
    }

    private void applyUseCooldownComponent() {
        Identifier parsed;
        ItemStack stack = getParent().getContext().getItemStack();
        if (!this.useCooldownEnabled || this.useCooldownSeconds <= 0.0f) {
            stack.remove(DataComponents.USE_COOLDOWN);
            getParent().removeComponentFromDataTag("minecraft:use_cooldown");
            return;
        }
        Optional<Identifier> group = Optional.empty();
        String sanitized = sanitizeId(this.useCooldownGroupId);
        if (!sanitized.isBlank() && (parsed = tryParse(sanitized)) != null) {
            group = Optional.of(parsed);
        }
        UseCooldown useCooldown;
        if (group.isPresent()) {
            useCooldown = new UseCooldown(this.useCooldownSeconds, group);
        } else {
            useCooldown = new UseCooldown(this.useCooldownSeconds);
        }
        stack.set(DataComponents.USE_COOLDOWN, useCooldown);
    }

    private void applyUseEffectsComponent() {
        ItemStack stack = getParent().getContext().getItemStack();
        if (!this.useEffectsEnabled) {
            stack.remove(DataComponents.USE_EFFECTS);
            getParent().removeComponentFromDataTag("minecraft:use_effects");
            if (this.useEffectsSpeedMultiplierEntry != null) {
                this.useEffectsSpeedMultiplierEntry.setValid(true);
                return;
            }
            return;
        }
        if (this.useEffectsSpeedMultiplierEntry != null) {
            this.useEffectsSpeedMultiplierEntry.setValid(this.useEffectsSpeedMultiplier > 0.0f);
        }
        if (this.useEffectsSpeedMultiplier <= 0.0f) {
            return;
        }
        stack.set(DataComponents.USE_EFFECTS, new UseEffects(this.useEffectsCanSprint, this.useEffectsInteractVibrations, this.useEffectsSpeedMultiplier));
    }

    private void setBlockTransformerId(String value) {
        this.blockTransformerId = value == null ? "" : value.trim();
        applyBlockTransformerComponent();
    }

    private boolean isValidBlockTransformerId(String value) {
        if (value == null || value.isBlank()) {
            return true;
        }
        Identifier parsed = tryParse(value);
        if (parsed == null) {
            return false;
        }
        HolderLookup.RegistryLookup<BlockTransformer> lookup = ClientUtil.registryAccess().lookup(Registries.BLOCK_TRANSFORMER).orElse(null);
        return lookup != null && lookup.get(ResourceKey.create(Registries.BLOCK_TRANSFORMER, parsed)).isPresent();
    }

    private void applyBlockTransformerComponent() {
        ItemStack stack = getParent().getContext().getItemStack();
        if (this.blockTransformerId.isBlank()) {
            this.blockTransformerEntry.setValid(true);
            stack.remove(DataComponents.BLOCK_TRANSFORMER);
            getParent().removeComponentFromDataTag("minecraft:block_transformer");
            return;
        }
        Identifier parsed = tryParse(this.blockTransformerId);
        HolderLookup.RegistryLookup<BlockTransformer> lookup = parsed == null ? null : ClientUtil.registryAccess().lookup(Registries.BLOCK_TRANSFORMER).orElse(null);
        Optional<Holder.Reference<BlockTransformer>> holder = lookup == null ? Optional.empty() : lookup.get(ResourceKey.create(Registries.BLOCK_TRANSFORMER, parsed));
        if (holder.isEmpty()) {
            this.blockTransformerEntry.setValid(false);
            return;
        }
        this.blockTransformerEntry.setValid(true);
        stack.set(DataComponents.BLOCK_TRANSFORMER, holder.get());
    }

    private String sanitizeId(String id) {
        return id == null ? "" : stripQuotes(id.trim());
    }

    private String stripQuotes(String value) {
        if (value == null) {
            return "";
        }
        String result = value.trim();
        if (((result.startsWith("\"") && result.endsWith("\"")) || (result.startsWith("'") && result.endsWith("'"))) && result.length() >= 2) {
            result = result.substring(1, result.length() - 1);
        }
        return result.trim();
    }

    private Identifier tryParse(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String sanitized = value.trim();
        try {
            return Identifier.parse(sanitized);
        } catch (Exception e) {
            if (!sanitized.contains(":")) {
                try {
                    return Identifier.parse("minecraft:" + sanitized);
                } catch (Exception ignored) {
                    return null;
                }
            }
            return null;
        }
    }
}
