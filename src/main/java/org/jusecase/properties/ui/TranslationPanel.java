package org.jusecase.properties.ui;

import net.miginfocom.swing.MigLayout;
import org.jusecase.properties.entities.Property;
import org.jusecase.properties.usecases.EditValue;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TranslationPanel extends JPanel {
    private final Application application;

    private final String fileName;
    private JCheckBox isAvailable;
    private JTextArea textArea;
    private DocumentListener textAreaListener;
    private Property property;

    public TranslationPanel(Application application, String fileName) {
        super(new MigLayout("insets 0"));
        this.application = application;
        this.fileName = fileName;
        init();
    }

    private void init() {
        JLabel label = new JLabel(fileName);
        add(label, "align left");

        isAvailable = new JCheckBox("Available");
        isAvailable.addActionListener(event -> editValue(isAvailable.isSelected() ? "" : null));
        add(isAvailable, "align right,wrap");

        textArea = new JTextArea();
        textAreaListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                editValue();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                editValue();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                editValue();
            }
        };
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, "span,growx,pushx,h 44!");
    }

    private void editValue() {
        editValue(textArea.getText());
    }

    private void editValue(String value) {
        EditValue.Request request = new EditValue.Request();
        request.property = property;
        request.value = value;
        application.getUsecaseExecutor().execute(request);
    }

    public String getFileName() {
        return fileName;
    }

    public void reset() {
        textArea.getDocument().removeDocumentListener(textAreaListener);

        this.property = null;
        textArea.setText("");
        textArea.setEnabled(false);
        isAvailable.setSelected(false);
    }

    public void setProperty(Property property) {
        this.property = property;
        textArea.setText(property.value);
        textArea.setEnabled(true);
        isAvailable.setSelected(true);

        textArea.getDocument().addDocumentListener(textAreaListener);
    }
}
