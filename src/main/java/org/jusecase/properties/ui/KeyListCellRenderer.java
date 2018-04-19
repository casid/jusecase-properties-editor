package org.jusecase.properties.ui;

import org.jusecase.properties.entities.Key;
import org.jusecase.properties.entities.KeyPopulation;

import javax.swing.*;
import java.awt.*;

public class KeyListCellRenderer extends DefaultListCellRenderer {
    private final Application application;

    public KeyListCellRenderer(Application application) {
        this.application = application;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Key key = (Key) value;
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (!isSelected) {
            if (key.getPopulation() == KeyPopulation.Sparse) {
                label.setBackground(application.getLookAndFeel().sparseKeyBackgroundColor);
            }
        }

        return label;
    }
}
