package com.github.rinorsi.cadeditor.client.screen.model.entry.item;

import com.github.franckyi.databindings.api.BooleanProperty;
import com.github.franckyi.databindings.api.IntegerProperty;
import com.github.rinorsi.cadeditor.client.ClientCache;
import com.github.rinorsi.cadeditor.client.screen.model.category.CategoryModel;
import com.github.rinorsi.cadeditor.client.screen.model.entry.SelectionEntryModel;
import com.github.rinorsi.cadeditor.client.screen.model.selection.element.ListSelectionElementModel;
import com.github.rinorsi.cadeditor.common.ModTexts;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class PotionEffectEntryModel extends SelectionEntryModel {
    private final IntegerProperty amplifierProperty;
    private final IntegerProperty durationProperty;
    private final BooleanProperty ambientProperty;
    private final BooleanProperty showParticlesProperty;
    private final BooleanProperty showIconProperty;
    private final BooleanProperty useSecondsProperty;
    private final PotionEffectConsumer callback;
    private boolean defaultUseSeconds;

    public PotionEffectEntryModel(CategoryModel category, String id, int amplifier, int duration, boolean ambient,
                                  boolean showParticles, boolean showIcon, PotionEffectConsumer callback) {
        super(category, null, id, s -> {
        });
        amplifierProperty = IntegerProperty.create(amplifier);
        durationProperty = IntegerProperty.create(duration);
        ambientProperty = BooleanProperty.create(ambient);
        showParticlesProperty = BooleanProperty.create(showParticles);
        showIconProperty = BooleanProperty.create(showIcon);
        boolean useSeconds = duration % 20 == 0;
        useSecondsProperty = BooleanProperty.create(useSeconds);
        this.callback = callback;
        defaultUseSeconds = useSeconds;
    }

    @Override
    public void apply() {
        callback.consume(this);
        super.apply();
        defaultUseSeconds = isUseSeconds();
    }

    @Override
    public void reset() {
        super.reset();
        setUseSeconds(defaultUseSeconds);
    }

    public int getAmplifier() {
        return amplifierProperty().getValue();
    }

    public IntegerProperty amplifierProperty() {
        return amplifierProperty;
    }

    public void setAmplifier(int value) {
        amplifierProperty().setValue(value);
    }

    public int getDuration() {
        return durationProperty().getValue();
    }

    public IntegerProperty durationProperty() {
        return durationProperty;
    }

    public void setDuration(int value) {
        durationProperty().setValue(value);
    }

    public boolean isAmbient() {
        return ambientProperty().getValue();
    }

    public BooleanProperty ambientProperty() {
        return ambientProperty;
    }

    public void setAmbient(boolean value) {
        ambientProperty().setValue(value);
    }

    public boolean isShowParticles() {
        return showParticlesProperty().getValue();
    }

    public BooleanProperty showParticlesProperty() {
        return showParticlesProperty;
    }

    public void setShowParticles(boolean value) {
        showParticlesProperty().setValue(value);
    }

    public boolean isShowIcon() {
        return showIconProperty().getValue();
    }

    public BooleanProperty showIconProperty() {
        return showIconProperty;
    }

    public void setShowIcon(boolean value) {
        showIconProperty().setValue(value);
    }

    public boolean isUseSeconds() {
        return useSecondsProperty().getValue();
    }

    public BooleanProperty useSecondsProperty() {
        return useSecondsProperty;
    }

    public void setUseSeconds(boolean value) {
        useSecondsProperty().setValue(value);
    }

    @Override
    public Type getType() {
        return Type.POTION_EFFECT;
    }

    @Override
    public List<String> getSuggestions() {
        return ClientCache.getEffectSuggestions();
    }

    @Override
    public MutableComponent getSelectionScreenTitle() {
        return ModTexts.EFFECTS;
    }

    @Override
    public List<? extends ListSelectionElementModel> getSelectionItems() {
        return ClientCache.getEffectSelectionItems();
    }

    public CompoundTag toCompoundTag() {
        CompoundTag tag = new CompoundTag();
        String id = getValue();
        if (id != null && !id.isBlank()) {
            tag.putString("id", id);
        }
        tag.putInt("amplifier", getAmplifier());
        tag.putInt("duration", getDuration() == -1 ? -1 : Math.max(1, getDuration()));
        tag.putBoolean("ambient", isAmbient());
        tag.putBoolean("show_particles", isShowParticles());
        tag.putBoolean("show_icon", isShowIcon());
        return tag;
    }

    @FunctionalInterface
    public interface PotionEffectConsumer {
        void consume(PotionEffectEntryModel entry);
    }
}
