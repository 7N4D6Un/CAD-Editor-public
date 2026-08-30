package com.github.rinorsi.cadeditor.client.screen.model.category.item;

import com.github.franckyi.guapi.api.Color;
import com.github.rinorsi.cadeditor.client.screen.model.ItemEditorModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.ActionEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.BooleanEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.EnumEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.item.FireworkColorEntryModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.FireworkExplosion;


public class ItemFireworkStarCategoryModel extends ItemEditorCategoryModel {
    private final FireworkExplosionEditor explosion;
    private boolean loadedFromStack;

    public ItemFireworkStarCategoryModel(ItemEditorModel editor) {
        super(ModTexts.FIREWORK_STAR, editor);
        this.explosion = new FireworkExplosionEditor();
    }

    @Override 
    protected void setupEntries() {
        if (!this.loadedFromStack) {
            loadFromStack();
            this.loadedFromStack = true;
        }
        MutableComponent shapeLabel = ModTexts.FIREWORK_SHAPE;
        FireworkExplosion.Shape[] shapeArrValues = FireworkExplosion.Shape.values();
        FireworkExplosion.Shape shape = this.explosion.getShape();
        EnumEntryModel<FireworkExplosion.Shape> shapeEntry = new EnumEntryModel<>(this, shapeLabel, shapeArrValues, shape, this.explosion::setShape);
        MutableComponent trailLabel = ModTexts.FIREWORK_TRAIL;
        boolean hasTrail = this.explosion.hasTrail();
        BooleanEntryModel trailEntry = new BooleanEntryModel(this, trailLabel, hasTrail, this.explosion::setHasTrail);
        MutableComponent twinkleLabel = ModTexts.FIREWORK_TWINKLE;
        boolean hasTwinkle = this.explosion.hasTwinkle();
        BooleanEntryModel twinkleEntry = new BooleanEntryModel(this, twinkleLabel, hasTwinkle, this.explosion::setHasTwinkle);
        getEntries().add(shapeEntry);
        getEntries().add(trailEntry);
        getEntries().add(twinkleEntry);
        addColorEntries(this.explosion.getColors(), true);
        getEntries().add(new ActionEntryModel(this, ModTexts.FIREWORK_ADD_PRIMARY_COLOR, () -> {
            addColor(this.explosion.getColors());
        }));
        addColorEntries(this.explosion.getFadeColors(), false);
        getEntries().add(new ActionEntryModel(this, ModTexts.FIREWORK_ADD_FADE_COLOR, () -> {
            addColor(this.explosion.getFadeColors());
        }));
    }

    private void loadFromStack() {
        ItemStack stack = getParent().getContext().getItemStack();
        FireworkExplosion current = stack.get(DataComponents.FIREWORK_EXPLOSION);
        if (current != null) {
            this.explosion.setShape(current.shape());
            copyList(current.colors(), this.explosion.getColors());
            copyList(current.fadeColors(), this.explosion.getFadeColors());
            this.explosion.setHasTrail(current.hasTrail());
            this.explosion.setHasTwinkle(current.hasTwinkle());
            return;
        }
        this.explosion.getColors().clear();
        this.explosion.getFadeColors().clear();
        this.explosion.setShape(FireworkExplosion.Shape.SMALL_BALL);
        this.explosion.setHasTrail(false);
        this.explosion.setHasTwinkle(false);
    }

    private void addColorEntries(List<Integer> colors, boolean primary) {
        for (int i = 0; i < colors.size(); i++) {
            int index = i;
            FireworkColorEntryModel entry = new FireworkColorEntryModel(this, primary ? ModTexts.fireworkPrimaryColor(index + 1) : ModTexts.fireworkFadeColor(index + 1), colors.get(i).intValue(), value -> {
                colors.set(index, value);
            }, () -> {
                removeColor(colors, index);
            });
            getEntries().add(entry);
        }
    }

    private void addColor(List<Integer> colors) {
        colors.add(Color.NONE);
        initalize();
    }

    private void removeColor(List<Integer> colors, int index) {
        if (index >= 0 && index < colors.size()) {
            colors.remove(index);
            initalize();
        }
    }

    @Override 
    public void apply() {
        super.apply();
        ItemStack stack = getParent().getContext().getItemStack();
        if (this.explosion.getColors().isEmpty()) {
            stack.remove(DataComponents.FIREWORK_EXPLOSION);
        } else {
            stack.set(DataComponents.FIREWORK_EXPLOSION, this.explosion.toComponent());
        }
    }

    private void copyList(IntList source, List<Integer> destination) {
        destination.clear();
        if (source == null) {
            return;
        }
        for (int i = 0; i < source.size(); i++) {
            destination.add(source.getInt(i));
        }
    }
}
