package com.github.franckyi.databindings.api;

import com.github.franckyi.databindings.api.factory.MappingFactory;
import com.github.franckyi.databindings.api.factory.ObservableListFactory;
import com.github.franckyi.databindings.api.factory.PropertyFactory;

public final class DataBindings {
    private static PropertyFactory propertyFactory;
    private static MappingFactory mappingFactory;
    private static ObservableListFactory observableListFactory;

    public static PropertyFactory getPropertyFactory() {
        return propertyFactory;
    }

    public static void setPropertyFactory(PropertyFactory propertyFactory) {
        DataBindings.propertyFactory = propertyFactory;
    }

    public static MappingFactory getMappingFactory() {
        return mappingFactory;
    }

    public static void setMappingFactory(MappingFactory mappingFactory) {
        DataBindings.mappingFactory = mappingFactory;
    }

    public static ObservableListFactory getObservableListFactory() {
        return observableListFactory;
    }

    public static void setObservableListFactory(ObservableListFactory observableListFactory) {
        DataBindings.observableListFactory = observableListFactory;
    }
}
