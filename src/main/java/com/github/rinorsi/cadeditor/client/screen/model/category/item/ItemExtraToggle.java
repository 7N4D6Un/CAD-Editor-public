package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.category.EditorCategoryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import java.util.function.Function;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.SpawnEggItem;

public enum ItemExtraToggle {
    ARMOR_COLOR(ModTexts.ARMOR_COLOR, ItemDyeableCategoryModel.class, ItemDyeableCategoryModel::new),
    DYE(ModTexts.gui("dye_category"), ItemDyeColorCategoryModel.class, ItemDyeColorCategoryModel::new),
    SPAWN_EGG(ModTexts.SPAWN_EGG, ItemSpawnEggCategoryModel.class, editor -> new ItemSpawnEggCategoryModel(editor, editor.getContext().getItemStack().getItem() instanceof SpawnEggItem spawnEgg ? spawnEgg : null)),
    SPAWNER(ModTexts.gui("spawner"), ItemSpawnerCategoryModel.class, ItemSpawnerCategoryModel::new),
    TRIAL_SPAWNER(ModTexts.gui("trial_spawner"), ItemTrialSpawnerCategoryModel.class, ItemTrialSpawnerCategoryModel::new),
    KNOWLEDGE_BOOK(ModTexts.gui("knowledge_book"), ItemRecipesCategoryModel.class, ItemRecipesCategoryModel::new),
    COMMAND_BLOCK(ModTexts.gui("command_block"), ItemCommandBlockCategoryModel.class, ItemCommandBlockCategoryModel::new),
    SIGN(ModTexts.gui("sign"), ItemSignCategoryModel.class, ItemSignCategoryModel::new),
    MAP(ModTexts.MAP, ItemMapCategoryModel.class, ItemMapCategoryModel::new),
    MAP_DECORATION(ModTexts.MAP_DECORATION, ItemMapDecorationsCategoryModel.class, ItemMapDecorationsCategoryModel::new),
    CROSSBOW(ModTexts.CROSSBOW, ItemCrossbowCategoryModel.class, ItemCrossbowCategoryModel::new),
    ARROW(ModTexts.gui("arrow"), ItemArrowCategoryModel.class, ItemArrowCategoryModel::new),
    BUNDLE_CONTENTS(ModTexts.BUNDLE_CONTENTS, ItemBundleContentsCategoryModel.class, ItemBundleContentsCategoryModel::new),
    LODESTONE(ModTexts.LODESTONE, ItemLodestoneCategoryModel.class, ItemLodestoneCategoryModel::new),
    SUSPICIOUS_STEW(ModTexts.gui("suspicious_stew_effects"), ItemSuspiciousStewEffectsCategoryModel.class, ItemSuspiciousStewEffectsCategoryModel::new),
    PROFILE(ModTexts.gui("profile"), ItemProfileCategoryModel.class, ItemProfileCategoryModel::new),
    CONTAINER(ModTexts.CONTAINER, ItemContainerCategoryModel.class, ItemContainerCategoryModel::new),
    CONTAINER_GRID(ModTexts.CONTAINER_GRID, ItemContainerGridCategoryModel.class, ItemContainerGridCategoryModel::new),
    BEEHIVE(ModTexts.gui("beehive"), ItemBeehiveCategoryModel.class, ItemBeehiveCategoryModel::new),
    BANNER(ModTexts.BANNER, ItemBannerPatternCategoryModel.class, ItemBannerPatternCategoryModel::new),
    TRIM_MATERIAL(ModTexts.gui("provides_trim_material"), ItemProvidesTrimMaterialCategoryModel.class, ItemProvidesTrimMaterialCategoryModel::new),
    LOOM_PATTERNS(ModTexts.gui("provides_banner_patterns"), ItemProvidesBannerPatternCategoryModel.class, ItemProvidesBannerPatternCategoryModel::new),
    BUCKET_ENTITY(ModTexts.BUCKET_ENTITY, ItemBucketEntityCategoryModel.class, ItemBucketEntityCategoryModel::new),
    INSTRUMENT(ModTexts.INSTRUMENT, ItemInstrumentCategoryModel.class, ItemInstrumentCategoryModel::new),
    POT_DECORATIONS(ModTexts.POT_DECORATIONS, ItemPotDecorationsCategoryModel.class, ItemPotDecorationsCategoryModel::new),
    FIREWORK_STAR(ModTexts.FIREWORK_STAR, ItemFireworkStarCategoryModel.class, ItemFireworkStarCategoryModel::new),
    FIREWORK_ROCKET(ModTexts.FIREWORK_ROCKET, ItemFireworksCategoryModel.class, ItemFireworksCategoryModel::new),
    OMINOUS_BOTTLE(ModTexts.OMINOUS_BOTTLE, ItemOminousBottleCategoryModel.class, ItemOminousBottleCategoryModel::new),
    CUSTOM_DATA(ModTexts.gui("custom_data"), ItemCustomDataCategoryModel.class, ItemCustomDataCategoryModel::new),
    WRITABLE_BOOK(ModTexts.gui("writable_book_content"), ItemWritableBookPagesCategoryModel.class, ItemWritableBookPagesCategoryModel::new),
    WRITTEN_BOOK(ModTexts.gui("written_book"), ItemWrittenBookCategoryModel.class, ItemWrittenBookCategoryModel::new),
    POTION_EFFECTS(ModTexts.POTION_EFFECTS, ItemPotionEffectsCategoryModel.class, ItemPotionEffectsCategoryModel::new);

    private final MutableComponent label;
    private final Class<? extends EditorCategoryModel> categoryClass;
    private final Function<ItemEditorModel, EditorCategoryModel> factory;

    ItemExtraToggle(MutableComponent label, Class<? extends EditorCategoryModel> categoryClass, Function<ItemEditorModel, EditorCategoryModel> factory) {
        this.label = label;
        this.categoryClass = categoryClass;
        this.factory = factory;
    }

    public MutableComponent coloredLabel() {
        return label.copy();
    }

    public Class<? extends EditorCategoryModel> getCategoryClass() {
        return categoryClass;
    }

    public EditorCategoryModel create(ItemEditorModel editor) {
        return factory.apply(editor);
    }
}
