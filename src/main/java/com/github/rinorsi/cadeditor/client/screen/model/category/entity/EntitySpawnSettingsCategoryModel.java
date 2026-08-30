package com.github.rinorsi.cadeditor.client.screen.model.category.entity;

import com.github.rinorsi.cadeditor.client.ClientUtil;
import com.github.rinorsi.cadeditor.client.screen.model.EntityEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BlockSelectionEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.IntegerEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.StringEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.entity.SoundVariantEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.entity.VariantEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.animal.cow.MushroomCow;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.animal.equine.Llama;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.fish.Salmon;
import net.minecraft.world.entity.animal.fish.TropicalFish;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.nautilus.ZombieNautilus;
import net.minecraft.world.entity.animal.panda.Panda;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.decoration.painting.Painting;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.network.chat.MutableComponent;

public class EntitySpawnSettingsCategoryModel extends EntityCategoryModel {
    private static final String VARIANT_TAG = "variant";
    private static final String SOUND_VARIANT_TAG = "sound_variant";
    private static final String MAIN_GENE_TAG = "MainGene";
    private static final String HIDDEN_GENE_TAG = "HiddenGene";
    private static final List<String> PANDA_GENES = List.of("normal", "lazy", "worried", "playful", "brown", "weak", "aggressive");
    private static final String EQUIPMENT_TAG = "equipment";
    private BooleanEntryModel ageLockedEntry;

    public EntitySpawnSettingsCategoryModel(EntityEditorModel editor) {
        super(ModTexts.ENTITY_SPAWN, editor);
    }

    @Override
    protected void setupEntries() {
        CompoundTag data = getData();
        getEntries().add(new BooleanEntryModel(this, ModTexts.CAN_PICK_UP_LOOT, data.getBooleanOr("CanPickUpLoot", false), value -> setBoolean("CanPickUpLoot", value)));
        getEntries().add(new BooleanEntryModel(this, ModTexts.PERSISTENCE_REQUIRED, data.getBooleanOr("PersistenceRequired", false), value -> setBoolean("PersistenceRequired", value)));
        getEntries().add(new BooleanEntryModel(this, ModTexts.NO_AI, data.getBooleanOr("NoAI", false), value -> setBoolean("NoAI", value)));
        getEntries().add(new BooleanEntryModel(this, ModTexts.LEFT_HANDED, data.getBooleanOr("LeftHanded", false), value -> setBoolean("LeftHanded", value)));
        if (getEntity() instanceof Raider) {
            getEntries().add(new BooleanEntryModel(this, ModTexts.CAN_JOIN_RAID, data.getBooleanOr("CanJoinRaid", false), value -> setBoolean("CanJoinRaid", value)));
        }
        if (getEntity() instanceof PatrollingMonster) {
            getEntries().add(new BooleanEntryModel(this, ModTexts.PATROL_LEADER, data.getBooleanOr("PatrolLeader", false), this::setPatrolLeader));
        }

        String team = data.getString("Team").orElse("");
        getEntries().add(new StringEntryModel(this, ModTexts.TEAM, team, this::setTeam));

        this.ageLockedEntry = null;
        if (getEntity() instanceof AgeableMob ageable && ageable.isBaby()) {
            this.ageLockedEntry = new BooleanEntryModel(this, ModTexts.AGE_LOCKED, data.getBooleanOr("AgeLocked", false), value -> {
            });
            getEntries().add(this.ageLockedEntry);
        }
        addVariantEntry(data);
        addSoundVariantEntry(data);
        addPandaGeneEntries(data);
        addSimpleVariantEntries(data);
        addCarriedBlockEntry(data);
    }

    private void addCarriedBlockEntry(CompoundTag data) {
        if (!(getEntity() instanceof EnderMan)) {
            return;
        }
        CompoundTag carried = data.getCompound("carriedBlockState").orElse(null);
        String current = carried == null ? "" : carried.getString("Name").orElse("");
        getEntries().add(new BlockSelectionEntryModel(this, ModTexts.gui("carried_block"), current, this::setCarriedBlock));
    }

    private void setCarriedBlock(String value) {
        String text = value == null ? "" : value.trim();
        CompoundTag data = getData();
        if (text.isEmpty() || text.startsWith("#") || Identifier.tryParse(text) == null) {
            data.remove("carriedBlockState");
            return;
        }
        String normalized = text.contains(":") ? text : "minecraft:" + text;
        CompoundTag state = new CompoundTag();
        state.putString("Name", normalized);
        data.put("carriedBlockState", state);
    }

    private void addVariantEntry(CompoundTag data) {
        Entity entity = getEntity();
        ResourceKey<? extends Registry<?>> variantRegistry;
        if (entity instanceof Cow) {
            variantRegistry = Registries.COW_VARIANT;
        } else if (entity instanceof Chicken) {
            variantRegistry = Registries.CHICKEN_VARIANT;
        } else if (entity instanceof Pig) {
            variantRegistry = Registries.PIG_VARIANT;
        } else if (entity instanceof Wolf) {
            variantRegistry = Registries.WOLF_VARIANT;
        } else if (entity instanceof Cat) {
            variantRegistry = Registries.CAT_VARIANT;
        } else if (entity instanceof Frog) {
            variantRegistry = Registries.FROG_VARIANT;
        } else if (entity instanceof ZombieNautilus) {
            variantRegistry = Registries.ZOMBIE_NAUTILUS_VARIANT;
        } else if (entity instanceof Painting) {
            variantRegistry = Registries.PAINTING_VARIANT;
        } else {
            return;
        }
        List<String> ids = ClientUtil.registryAccess().lookupOrThrow(variantRegistry).listElements()
                .map(holder -> holder.unwrapKey().map(key -> key.identifier().toString()).orElse(""))
                .filter(id -> !id.isEmpty())
                .sorted()
                .toList();
        getEntries().add(new VariantEntryModel(this, ids, data.getString(VARIANT_TAG).orElse(""), this::setVariant));
    }

    private void addSoundVariantEntry(CompoundTag data) {
        ResourceKey<? extends Registry<?>> soundRegistry;
        if (getEntity() instanceof Wolf) {
            soundRegistry = Registries.WOLF_SOUND_VARIANT;
        } else if (getEntity() instanceof Pig) {
            soundRegistry = Registries.PIG_SOUND_VARIANT;
        } else if (getEntity() instanceof Chicken) {
            soundRegistry = Registries.CHICKEN_SOUND_VARIANT;
        } else if (getEntity() instanceof Cow) {
            soundRegistry = Registries.COW_SOUND_VARIANT;
        } else if (getEntity() instanceof Cat) {
            soundRegistry = Registries.CAT_SOUND_VARIANT;
        } else {
            return;
        }
        final ResourceKey<? extends Registry<?>> registry = soundRegistry;
        List<String> ids = ClientUtil.registryAccess().lookupOrThrow(registry).listElements()
                .map(holder -> holder.unwrapKey().map(key -> key.identifier().toString()).orElse(""))
                .filter(id -> !id.isEmpty())
                .sorted()
                .toList();
        getEntries().add(new SoundVariantEntryModel(this, ModTexts.gui("sound_variant"), ids,
                data.getString(SOUND_VARIANT_TAG).orElse(""), this::setSoundVariant));
    }

    private void addPandaGeneEntries(CompoundTag data) {
        if (!(getEntity() instanceof Panda)) {
            return;
        }
        getEntries().add(new SoundVariantEntryModel(this, ModTexts.gui("panda_main_gene"), PANDA_GENES,
                data.getString(MAIN_GENE_TAG).orElse(""), this::setGene));
        getEntries().add(new SoundVariantEntryModel(this, ModTexts.gui("panda_hidden_gene"), PANDA_GENES,
                data.getString(HIDDEN_GENE_TAG).orElse(""), this::setHiddenGene));
    }

    private static String stripNamespace(String value) {
        String text = value == null ? "" : value.trim();
        int idx = text.indexOf(':');
        return idx >= 0 ? text.substring(idx + 1) : text;
    }

    private void setGene(String value) {
        String text = stripNamespace(value);
        CompoundTag data = getData();
        if (text.isEmpty()) {
            data.remove(MAIN_GENE_TAG);
        } else if (PANDA_GENES.contains(text)) {
            data.putString(MAIN_GENE_TAG, text);
        }
    }

    private void setHiddenGene(String value) {
        String text = stripNamespace(value);
        CompoundTag data = getData();
        if (text.isEmpty()) {
            data.remove(HIDDEN_GENE_TAG);
        } else if (PANDA_GENES.contains(text)) {
            data.putString(HIDDEN_GENE_TAG, text);
        }
    }

    private void addSimpleVariantEntries(CompoundTag data) {
        if (getEntity() instanceof Sheep) {
            addDyeColorEntry(data, "Color");
        } else if (getEntity() instanceof Shulker) {
            addShulkerColorEntry(data);
        } else if (getEntity() instanceof Fox) {
            addStringVariantEntry(data, "Type", List.of("red", "snow"), "red");
        } else if (getEntity() instanceof MushroomCow) {
            addStringVariantEntry(data, "Type", List.of("red", "brown"), "red");
        } else if (getEntity() instanceof Axolotl) {
            addIdVariantEntry(data, "Variant", Axolotl.Variant.values(), Axolotl.Variant::getId, Axolotl.Variant::getSerializedName);
        } else if (getEntity() instanceof Llama) {
            addIdVariantEntry(data, "Variant", Llama.Variant.values(), Llama.Variant::getId, Llama.Variant::getSerializedName);
            addLlamaStrengthEntry(data);
        } else if (getEntity() instanceof Parrot) {
            addIdVariantEntry(data, "Variant", Parrot.Variant.values(), Parrot.Variant::getId, Parrot.Variant::getSerializedName);
        } else if (getEntity() instanceof Rabbit) {
            addIdVariantEntry(data, "RabbitType", Rabbit.Variant.values(), Rabbit.Variant::id, Rabbit.Variant::getSerializedName);
        } else if (getEntity() instanceof Salmon) {
            addSalmonTypeEntry(data);
        } else if (getEntity() instanceof TropicalFish) {
            addTropicalFishEntries(data);
        } else if (getEntity() instanceof Horse) {
            addHorseVariantEntry(data);
        }
    }

    private void addDyeColorEntry(CompoundTag data, String tagKey) {
        List<String> tokens = Arrays.stream(DyeColor.values()).map(DyeColor::getName).toList();
        int current = data.getByteOr(tagKey, (byte) 0) & 15;
        String curName = current >= 0 && current < tokens.size() ? DyeColor.byId(current).getName() : tokens.get(0);
        getEntries().add(new SoundVariantEntryModel(this, ModTexts.VARIANT, tokens, curName, value -> {
            String text = stripNamespace(value);
            CompoundTag root = getData();
            if (text.isEmpty()) {
                root.remove(tagKey);
                return;
            }
            DyeColor color = DyeColor.byName(text, DyeColor.WHITE);
            root.putByte(tagKey, (byte) color.getId());
        }));
    }

    private void addShulkerColorEntry(CompoundTag data) {
        List<String> tokens = new ArrayList<>();
        tokens.add("undyed");
        for (DyeColor color : DyeColor.values()) {
            tokens.add(color.getName());
        }
        int current = data.getByteOr("Color", (byte) 16);
        String curName = current >= 0 && current < 16 ? DyeColor.byId(current).getName() : "undyed";
        getEntries().add(new SoundVariantEntryModel(this, ModTexts.VARIANT, tokens, curName, value -> {
            String text = stripNamespace(value);
            CompoundTag root = getData();
            if (text.isEmpty()) {
                root.remove("Color");
                return;
            }
            if ("undyed".equals(text)) {
                root.putByte("Color", (byte) 16);
                return;
            }
            DyeColor color = DyeColor.byName(text, DyeColor.WHITE);
            root.putByte("Color", (byte) color.getId());
        }));
    }

    private void addStringVariantEntry(CompoundTag data, String tagKey, List<String> tokens, String def) {
        String current = data.getStringOr(tagKey, def);
        getEntries().add(new SoundVariantEntryModel(this, ModTexts.VARIANT, tokens, current, value -> {
            String text = stripNamespace(value);
            CompoundTag root = getData();
            if (text.isEmpty()) {
                root.remove(tagKey);
            } else if (tokens.contains(text)) {
                root.putString(tagKey, text);
            }
        }));
    }

    private <E> void addIdVariantEntry(CompoundTag data, String tagKey, E[] values,
                                       Function<E, Integer> idFn, Function<E, String> nameFn) {
        List<String> tokens = Arrays.stream(values).map(nameFn).toList();
        String current = nameForId(values, idFn, nameFn, data.getIntOr(tagKey, 0));
        getEntries().add(new SoundVariantEntryModel(this, ModTexts.VARIANT, tokens, current, value -> {
            String text = stripNamespace(value);
            CompoundTag root = getData();
            if (text.isEmpty()) {
                root.remove(tagKey);
                return;
            }
            E match = nameFnApply(values, nameFn, text);
            root.putInt(tagKey, idFn.apply(match));
        }));
    }

    private void addSalmonTypeEntry(CompoundTag data) {
        List<String> tokens = Arrays.stream(Salmon.Variant.values()).map(Salmon.Variant::getSerializedName).toList();
        String current = data.getString("type").orElse("medium");
        getEntries().add(new SoundVariantEntryModel(this, ModTexts.gui("salmon_size"), tokens, current, value -> {
            String text = stripNamespace(value);
            CompoundTag root = getData();
            if (text.isEmpty()) {
                root.remove("type");
            } else if (tokens.contains(text)) {
                root.putString("type", text);
            }
        }));
    }

    private void addTropicalFishEntries(CompoundTag data) {
        int packed = data.getIntOr("Variant", TropicalFish.DEFAULT_VARIANT.getPackedId());
        addTropicalFishPatternEntry(data, packed);
        addTropicalFishColorEntry(data, "tropical_fish_base_color", packed, true);
        addTropicalFishColorEntry(data, "tropical_fish_pattern_color", packed, false);
    }

    private void addTropicalFishPatternEntry(CompoundTag data, int packed) {
        List<String> tokens = Arrays.stream(TropicalFish.Pattern.values()).map(TropicalFish.Pattern::getSerializedName).toList();
        String current = TropicalFish.Pattern.byId(packed & 65535).getSerializedName();
        getEntries().add(new SoundVariantEntryModel(this, ModTexts.gui("tropical_fish_pattern"), tokens, current, value -> {
            String text = stripNamespace(value);
            CompoundTag root = getData();
            if (text.isEmpty()) {
                root.remove("Variant");
                return;
            }
            for (TropicalFish.Pattern pattern : TropicalFish.Pattern.values()) {
                if (pattern.getSerializedName().equals(text)) {
                    root.putInt("Variant", (root.getIntOr("Variant", packed) & 0xFFFF0000) | pattern.getPackedId());
                    return;
                }
            }
        }));
    }

    private void addTropicalFishColorEntry(CompoundTag data, String labelKey, int packed, boolean base) {
        List<String> tokens = Arrays.stream(DyeColor.values()).map(DyeColor::getName).toList();
        int colorId = base ? (packed >> 16) & 255 : (packed >> 24) & 255;
        String current = colorId >= 0 && colorId < tokens.size() ? tokens.get(colorId) : tokens.get(0);
        getEntries().add(new SoundVariantEntryModel(this, ModTexts.gui(labelKey), tokens, current, value -> {
            String text = stripNamespace(value);
            CompoundTag root = getData();
            if (text.isEmpty()) {
                root.remove("Variant");
                return;
            }
            DyeColor color = DyeColor.byName(text, DyeColor.WHITE);
            int currentPacked = root.getIntOr("Variant", packed);
            int updated = base ? (currentPacked & 0xFF00FFFF) | (color.getId() << 16) : (currentPacked & 0x00FFFFFF) | (color.getId() << 24);
            root.putInt("Variant", updated);
        }));
    }

    private void addHorseVariantEntry(CompoundTag data) {
        List<String> tokens = List.of("white", "creamy", "chestnut", "brown", "black", "gray", "darkbrown");
        int coatId = data.getIntOr("Variant", 0) & 255;
        String current = coatId >= 0 && coatId < tokens.size() ? tokens.get(coatId) : tokens.get(0);
        getEntries().add(new SoundVariantEntryModel(this, ModTexts.gui("horse_variant"), tokens, current, value -> {
            String text = stripNamespace(value);
            int index = tokens.indexOf(text);
            CompoundTag root = getData();
            if (index < 0) {
                return;
            }
            root.putInt("Variant", (root.getIntOr("Variant", 0) & 0xFFFFFF00) | index);
        }));
    }

    private void addLlamaStrengthEntry(CompoundTag data) {
        int strength = Math.max(0, Math.min(5, data.getIntOr("Strength", 0)));
        getEntries().add(new IntegerEntryModel(this, ModTexts.gui("llama_strength"), strength,
                value -> getData().putInt("Strength", Math.max(0, Math.min(5, value == null ? 0 : value))),
                value -> value != null && value >= 0 && value <= 5));
    }

    private static <E> String nameForId(E[] values, Function<E, Integer> idFn, Function<E, String> nameFn, int id) {
        for (E v : values) {
            if (idFn.apply(v) == id) {
                return nameFn.apply(v);
            }
        }
        return nameFn.apply(values[0]);
    }

    private static <E> E nameFnApply(E[] values, Function<E, String> nameFn, String token) {
        for (E v : values) {
            if (nameFn.apply(v).equals(token)) {
                return v;
            }
        }
        return values[0];
    }

    private void setSoundVariant(String value) {
        String text = value == null ? "" : value.trim();
        CompoundTag data = getData();
        if (text.isEmpty()) {
            data.remove(SOUND_VARIANT_TAG);
            return;
        }
        String normalized = text.contains(":") ? text : "minecraft:" + text;
        if (Identifier.tryParse(normalized) != null) {
            data.putString(SOUND_VARIANT_TAG, normalized);
        }
    }

    private void setVariant(String value) {
        String text = value == null ? "" : value.trim();
        CompoundTag data = getData();
        if (text.isEmpty()) {
            data.remove(VARIANT_TAG);
            return;
        }
        String normalized = text.contains(":") ? text : "minecraft:" + text;
        if (Identifier.tryParse(normalized) != null) {
            data.putString(VARIANT_TAG, normalized);
        }
    }

    @Override
    public void apply() {
        super.apply();
        if (this.ageLockedEntry != null) {
            setBoolean("AgeLocked", Boolean.TRUE.equals(this.ageLockedEntry.getValue()));
        }
    }

    private void setBoolean(String key, boolean value) {
        if (value) {
            getData().putBoolean(key, true);
        } else {
            getData().remove(key);
        }
    }

    private void setPatrolLeader(boolean value) {
        setBoolean("PatrolLeader", value);
        if (value) {
            ItemStack banner = Raid.getOminousBannerInstance(ClientUtil.registryAccess().lookupOrThrow(Registries.BANNER_PATTERN));
            setHeadItem(banner);
        } else {
            removeHeadItemIfBanner();
        }
    }

    private void setHeadItem(ItemStack stack) {
        CompoundTag data = getData();
        CompoundTag equipment = data.getCompound(EQUIPMENT_TAG).map(CompoundTag::copy).orElseGet(CompoundTag::new);
        equipment.put(EquipmentSlot.HEAD.getSerializedName(), ClientUtil.saveItemStack(ClientUtil.registryAccess(), stack));
        data.put(EQUIPMENT_TAG, equipment);
    }

    private void removeHeadItemIfBanner() {
        CompoundTag data = getData();
        CompoundTag equipment = data.getCompound(EQUIPMENT_TAG).orElse(null);
        if (equipment == null || !equipment.contains(EquipmentSlot.HEAD.getSerializedName())) {
            return;
        }
        ItemStack stack = ClientUtil.parseItemStack(ClientUtil.registryAccess(), equipment.getCompound(EquipmentSlot.HEAD.getSerializedName()).orElse(new CompoundTag()));
        if (stack.isEmpty() || !isOminousBanner(stack)) {
            return;
        }
        CompoundTag copy = equipment.copy();
        copy.remove(EquipmentSlot.HEAD.getSerializedName());
        if (copy.isEmpty()) {
            data.remove(EQUIPMENT_TAG);
        } else {
            data.put(EQUIPMENT_TAG, copy);
        }
    }

    private boolean isOminousBanner(ItemStack stack) {
        ItemStack candidate = Raid.getOminousBannerInstance(ClientUtil.registryAccess().lookupOrThrow(Registries.BANNER_PATTERN));
        return ItemStack.matches(stack, candidate);
    }

    private void setTeam(String value) {
        String trimmed = value == null ? "" : value.trim();
        if (trimmed.isEmpty()) {
            getData().remove("Team");
        } else {
            getData().putString("Team", trimmed);
        }
    }
}
