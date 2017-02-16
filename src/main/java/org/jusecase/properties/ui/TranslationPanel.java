package org.jusecase.properties.ui;

import net.miginfocom.swing.MigLayout;
import org.jusecase.properties.gateways.Property;

import javax.swing.*;

public class TranslationPanel extends JPanel {


    private final String fileName;
    private JCheckBox isAvailable;
    private JTextArea textArea;

    public TranslationPanel(String fileName) {
        super(new MigLayout("insets 0"));
        this.fileName = fileName;
        init();
    }

    private void init() {
        JLabel label = new JLabel(fileName);
        add(label, "align left");

        isAvailable = new JCheckBox("Available");
        add(isAvailable, "align right,wrap");

        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, "span,growx,pushx,h 44!");
    }

    public String getFileName() {
        return fileName;
    }

    public void reset() {
        textArea.setText("");
        textArea.setEnabled(false);
        isAvailable.setSelected(false);
    }

    public void setProperty(Property property) {
        textArea.setText(property.value);
        textArea.setEnabled(true);
        isAvailable.setSelected(true);
    }
}
