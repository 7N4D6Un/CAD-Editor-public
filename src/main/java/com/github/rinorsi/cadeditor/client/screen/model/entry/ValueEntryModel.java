package com.github.rinorsi.cadeditor.client.screen.model.entry;

import com.github.franckyi.databindings.api.BooleanProperty;
import com.github.franckyi.databindings.api.ObjectProperty;
import com.github.franckyi.databindings.api.ObservableBooleanValue;
import com.github.franckyi.databindings.api.event.ObservableValueChangeListener;
import com.github.rinorsi.cadeditor.client.screen.model.category.CategoryModel;
import net.minecraft.network.chat.MutableComponent;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class ValueEntryModel<T> extends LabeledEntryModel {
    protected T defaultValue;
    private T factoryDefault;
    private final ObjectProperty<T> valueProperty;
    private final ObservableBooleanValue valueChangedProperty = new ObservableBooleanValue() {
        @Override
        public Boolean get() {
            return !valuesEqual(valueProperty.get(), defaultValue);
        }

        @Override
        public void addListener(ObservableValueChangeListener<? super Boolean> listener) {
        }

        @Override
        public void removeListener(ObservableValueChangeListener<? super Boolean> listener) {
        }
    };
    private final BooleanProperty modifiedFromFactoryProperty = BooleanProperty.create(false);
    protected Predicate<T> validator;
    protected final Consumer<T> action;

    protected ValueEntryModel(CategoryModel category, MutableComponent label, T value, Consumer<T> action) {
        this(category, label, value, action, t -> true);
    }

    protected ValueEntryModel(CategoryModel category, MutableComponent label, T value, Consumer<T> action, Predicate<T> validator) {
        super(category, label);
        defaultValue = value;
        valueProperty = ObjectProperty.create(value);
        valueProperty.addListener(v -> updateModifiedFromFactory());
        this.validator = validator;
        this.action = action;
    }

    protected boolean valuesEqual(T a, T b) {
        return Objects.equals(a, b);
    }

    public void setFactoryDefault(T def) {
        this.factoryDefault = def;
        updateModifiedFromFactory();
    }

    private void updateModifiedFromFactory() {
        modifiedFromFactoryProperty.setValue(isModifiedFromFactory(getValue()));
    }

    private T resetTarget() {
        return factoryDefault != null ? factoryDefault : defaultValue;
    }

    private boolean isModifiedFromFactory(T value) {
        return !valuesEqual(value, resetTarget());
    }

    public ObservableBooleanValue modifiedFromResetTargetProperty() {
        return modifiedFromFactoryProperty;
    }

    @Override
    public void reset() {
        valueProperty.setValue(resetTarget());
    }

    public void markClean() {
        defaultValue = getValue();
    }

    public T getValue() {
        return valueProperty().getValue();
    }

    public ObjectProperty<T> valueProperty() {
        return valueProperty;
    }

    public void setValue(T value) {
        valueProperty().setValue(value);
    }

    public ObservableBooleanValue valueChangedProperty() {
        return valueChangedProperty;
    }

    public boolean validate(T value) {
        return validator.test(value);
    }

    @Override
    public void apply() {
        if (valueChangedProperty().getValue()) {
            action.accept(getValue());
            defaultValue = getValue();
        }
    }
}
