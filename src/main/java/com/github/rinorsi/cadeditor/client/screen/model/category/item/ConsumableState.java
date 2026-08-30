package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.consume_effects.PlaySoundConsumeEffect;
import net.minecraft.world.item.consume_effects.RemoveStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.TeleportRandomlyConsumeEffect;


public class ConsumableState {
    private static final float DEFAULT_CONSUME_SECONDS = 1.6f;
    private static final ItemUseAnimation DEFAULT_ANIMATION = ItemUseAnimation.EAT;
    private float consumeSeconds = DEFAULT_CONSUME_SECONDS;
    private ItemUseAnimation animation = DEFAULT_ANIMATION;
    private Optional<Holder<SoundEvent>> consumeSound = Optional.of(SoundEvents.GENERIC_EAT);
    private boolean hasConsumeParticles = true;
    private List<ConsumableEffectData> effects = new ArrayList<>();
    private boolean clearAllEffects = false;
    private String removeEffectsText = "";
    private Optional<Holder<SoundEvent>> playSound = Optional.empty();
    private boolean teleportRandomlyEnabled = false;
    private float teleportDiameter = 16.0f;

    public void loadFrom(ItemStack stack) {
        Consumable consumable = (Consumable) stack.get(DataComponents.CONSUMABLE);
        if (consumable == null) {
            resetToDefaults();
            return;
        }
        this.consumeSeconds = consumable.consumeSeconds();
        this.hasConsumeParticles = consumable.hasConsumeParticles();
        this.animation = consumable.animation();
        this.consumeSound = Optional.ofNullable(consumable.sound());
        this.clearAllEffects = consumable.onConsumeEffects().stream().anyMatch(effect -> effect instanceof ClearAllStatusEffectsConsumeEffect);
        this.removeEffectsText = serializeRemoveEffects(consumable.onConsumeEffects());
        this.playSound = consumable.onConsumeEffects().stream().filter(PlaySoundConsumeEffect.class::isInstance).map(PlaySoundConsumeEffect.class::cast).map(PlaySoundConsumeEffect::sound).findFirst();
        this.teleportRandomlyEnabled = false;
        this.teleportDiameter = 16.0f;
        for (ConsumeEffect effect : consumable.onConsumeEffects()) {
            if (effect instanceof TeleportRandomlyConsumeEffect teleport) {
                this.teleportRandomlyEnabled = true;
                this.teleportDiameter = teleport.diameter();
            }
        }
        Class<ApplyStatusEffectsConsumeEffect> cls = ApplyStatusEffectsConsumeEffect.class;
        this.effects = consumable.onConsumeEffects().stream()
                .filter(cls::isInstance)
                .map(cls::cast)
                .flatMap(apply -> apply.effects().stream().map(eff -> new ConsumableEffectData(new MobEffectInstance(eff), apply.probability())))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private static String serializeRemoveEffects(List<ConsumeEffect> effects) {
        for (ConsumeEffect effect : effects) {
            if (effect instanceof RemoveStatusEffectsConsumeEffect remove) {
                Optional<TagKey<MobEffect>> tagKey = remove.effects().unwrapKey();
                if (tagKey.isPresent()) {
                    return "#" + tagKey.get().location();
                }
                return remove.effects().stream().map(holder -> holder.unwrapKey().map(key -> key.identifier().toString()).orElse("")).filter(s -> !s.isEmpty()).collect(Collectors.joining(", "));
            }
        }
        return "";
    }

    private void resetToDefaults() {
        this.consumeSeconds = DEFAULT_CONSUME_SECONDS;
        this.hasConsumeParticles = true;
        this.animation = DEFAULT_ANIMATION;
        this.consumeSound = Optional.of(SoundEvents.GENERIC_EAT);
        this.effects = new ArrayList<>();
        this.clearAllEffects = false;
        this.removeEffectsText = "";
        this.playSound = Optional.empty();
        this.teleportRandomlyEnabled = false;
        this.teleportDiameter = 16.0f;
    }

    public float getConsumeSeconds() {
        return this.consumeSeconds;
    }

    public void setConsumeSeconds(float seconds) {
        this.consumeSeconds = Math.max(0.05f, seconds);
    }

    public boolean hasConsumeParticles() {
        return this.hasConsumeParticles;
    }

    public void setHasConsumeParticles(boolean value) {
        this.hasConsumeParticles = value;
    }

    public ItemUseAnimation getAnimation() {
        return this.animation;
    }

    public void setAnimation(ItemUseAnimation animation) {
        this.animation = animation == null ? DEFAULT_ANIMATION : animation;
    }

    public Optional<Holder<SoundEvent>> getConsumeSound() {
        return this.consumeSound;
    }

    public void setConsumeSound(Optional<Holder<SoundEvent>> sound) {
        this.consumeSound = sound == null ? Optional.empty() : sound;
    }

    public String getConsumeSoundId() {
        return (String) this.consumeSound.flatMap(holder -> holder.unwrapKey().map(value -> value.identifier())).map(value -> value.toString()).orElse("");
    }

    public boolean setConsumeSoundId(String id) {
        if (id == null || id.isBlank()) {
            this.consumeSound = Optional.empty();
            return true;
        }
        Identifier location = ClientUtil.parseResourceLocation(id);
        if (location == null) {
            return false;
        }
        Optional<? extends HolderLookup.RegistryLookup<SoundEvent>> lookupOpt = ClientUtil.registryAccess().lookup(Registries.SOUND_EVENT);
        if (lookupOpt.isEmpty()) {
            return false;
        }
        HolderLookup.RegistryLookup<SoundEvent> lookup = lookupOpt.get();
        Optional<Holder.Reference<SoundEvent>> holder = lookup.get(ResourceKey.create(Registries.SOUND_EVENT, location));
        if (holder.isEmpty()) {
            return false;
        }
        this.consumeSound = Optional.of((Holder) holder.get());
        return true;
    }

    public List<ConsumableEffectData> getEffects() {
        return this.effects.stream().map(value -> value.copy()).collect(Collectors.toUnmodifiableList());
    }

    public void setEffects(List<ConsumableEffectData> newEffects) {
        if (newEffects == null || newEffects.isEmpty()) {
            this.effects = new ArrayList<>();
        } else {
            this.effects = newEffects.stream().filter(value -> Objects.nonNull(value)).map(value -> value.copy()).collect(Collectors.toCollection(ArrayList::new));
        }
    }

    public List<ConsumableEffectData> copyEffectsForComponent() {
        return this.effects.stream().map(value -> value.copy()).collect(Collectors.toUnmodifiableList());
    }

    public boolean isClearAllEffects() {
        return this.clearAllEffects;
    }

    public void setClearAllEffects(boolean value) {
        this.clearAllEffects = value;
    }

    public String getRemoveEffectsText() {
        return this.removeEffectsText == null ? "" : this.removeEffectsText;
    }

    public void setRemoveEffectsText(String value) {
        this.removeEffectsText = value == null ? "" : value.trim();
    }

    public String getPlaySoundId() {
        return (String) this.playSound.flatMap(holder -> holder.unwrapKey().map(value -> value.identifier())).map(value -> value.toString()).orElse("");
    }

    public boolean setPlaySoundId(String id) {
        if (id == null || id.isBlank()) {
            this.playSound = Optional.empty();
            return true;
        }
        Identifier location = ClientUtil.parseResourceLocation(id);
        if (location == null) {
            return false;
        }
        Optional<? extends HolderLookup.RegistryLookup<SoundEvent>> lookupOpt = ClientUtil.registryAccess().lookup(Registries.SOUND_EVENT);
        if (lookupOpt.isEmpty()) {
            return false;
        }
        HolderLookup.RegistryLookup<SoundEvent> lookup = lookupOpt.get();
        Optional<Holder.Reference<SoundEvent>> holder = lookup.get(ResourceKey.create(Registries.SOUND_EVENT, location));
        if (holder.isEmpty()) {
            return false;
        }
        this.playSound = Optional.of((Holder) holder.get());
        return true;
    }

    public boolean isTeleportRandomlyEnabled() {
        return this.teleportRandomlyEnabled;
    }

    public void setTeleportRandomlyEnabled(boolean value) {
        this.teleportRandomlyEnabled = value;
    }

    public float getTeleportDiameter() {
        return this.teleportDiameter;
    }

    public void setTeleportDiameter(float value) {
        this.teleportDiameter = Float.isNaN(value) ? 16.0f : Math.max(0.05f, value);
    }

    public Consumable buildConsumable(List<ConsumeEffect> statusEffects) {
        Consumable.Builder builder = Consumable.builder().consumeSeconds(Math.max(0.05f, this.consumeSeconds)).hasConsumeParticles(this.hasConsumeParticles).animation(this.animation);
        this.consumeSound.ifPresent(builder::sound);
        if (this.clearAllEffects) {
            builder.onConsume(ClearAllStatusEffectsConsumeEffect.INSTANCE);
        }
        Optional<HolderSet<MobEffect>> removeSet = buildRemoveEffectSet();
        if (removeSet.isPresent()) {
            builder.onConsume(new RemoveStatusEffectsConsumeEffect(removeSet.get()));
        }
        this.playSound.ifPresent(holder -> builder.onConsume(new PlaySoundConsumeEffect(holder)));
        if (this.teleportRandomlyEnabled) {
            builder.onConsume(new TeleportRandomlyConsumeEffect(Math.max(0.05f, this.teleportDiameter)));
        }
        for (ConsumeEffect statusEffect : statusEffects) {
            builder.onConsume(statusEffect);
        }
        return builder.build();
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
                Identifier rl = Identifier.tryParse(entry.substring(1));
                if (rl != null) {
                    lookup.get(TagKey.create(Registries.MOB_EFFECT, rl)).ifPresent(holderSet -> holderSet.forEach(holders::add));
                }
            } else {
                Identifier rl = Identifier.tryParse(entry.startsWith("minecraft:") ? entry : "minecraft:" + entry);
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


    public static record ConsumableEffectData(MobEffectInstance effect, float probability) {

        public ConsumableEffectData(MobEffectInstance effect, float probability) {
            Objects.requireNonNull(effect, "effect");
            this.effect = effect;
            this.probability = Float.isFinite(probability) ? probability : 0.0f;
        }

        public ConsumableEffectData copy() {
            return new ConsumableEffectData(new MobEffectInstance(this.effect), this.probability);
        }
    }
}
