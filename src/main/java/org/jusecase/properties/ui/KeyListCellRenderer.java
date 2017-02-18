package org.jusecase.properties.ui;

import org.jusecase.properties.entities.Key;
import org.jusecase.properties.entities.KeyPopulation;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class KeyListCellRenderer extends DefaultListCellRenderer {
    Map<KeyPopulation, Color> backgroundColorForPopulation = new HashMap<>();

    public KeyListCellRenderer() {
        backgroundColorForPopulation.put(KeyPopulation.Complete, new Color(230, 255, 230));
        backgroundColorForPopulation.put(KeyPopulation.Sparse, new Color(255, 255, 230));
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Key key = (Key) value;
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (!isSelected) {
            Color color = backgroundColorForPopulation.get(key.getPopulation());
            if (color != null) {
                label.setBackground(color);
            }
        }

        return label;
    }
}
