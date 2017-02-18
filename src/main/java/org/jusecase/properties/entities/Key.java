package org.jusecase.properties.entities;

public class Key implements Comparable<Key> {
    private final String key;

    public void setPopulation(KeyPopulation population) {
        this.population = population;
    }

    private KeyPopulation population;

    public Key(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public KeyPopulation getPopulation() {
        return population;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Key key1 = (Key) o;

        return key.equals(key1.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public int compareTo(Key o) {
        return key.compareTo(o.key);
    }

    @Override
    public String toString() {
        return key;
    }
}
