package org.jusecase.properties.ui;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class KeyListModel extends AbstractListModel<String> {

    private List<String> keys = new ArrayList<>();

    public void setKeys(List<String> keys) {
        this.keys = keys;
        fireContentsChanged(this, 0, keys.size());
    }

    @Override
    public int getSize() {
        return keys.size();
    }

    @Override
    public String getElementAt(int index) {
        return keys.get(index);
    }
}
