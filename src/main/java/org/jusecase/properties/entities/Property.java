package org.jusecase.properties.entities;

public class Property {
    public String fileName;
    public String key;
    public String value;
    public String valueLowercase;

    @Override
    public String toString() {
        return "Property{" +
                "fileName='" + fileName + '\'' +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
