package org.jusecase.properties.ui;

import org.jusecase.properties.entities.Key;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class KeyListModel extends AbstractListModel<Key> {

    private List<Key> keys = new ArrayList<>();

    public void setKeys(List<Key> keys) {
        this.keys = keys;
        fireContentsChanged(this, 0, keys.size());
    }

    @Override
    public int getSize() {
        return keys.size();
    }

    @Override
    public Key getElementAt(int index) {
        return keys.get(index);
    }
}
