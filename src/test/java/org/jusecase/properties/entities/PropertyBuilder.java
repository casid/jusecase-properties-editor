package org.jusecase.properties.entities;

public class PropertyBuilder implements PropertyBuilderMethods<Property, PropertyBuilder> {
    private Property property = new Property();

    public PropertyBuilder() {
        withFileName("resources.properties").withKey("key").withValue("value");
    }

    @Override
    public Property getEntity() {
        return property;
    }
}
