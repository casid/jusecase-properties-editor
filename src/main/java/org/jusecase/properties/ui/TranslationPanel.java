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
        isAvailable = new JCheckBox(fileName);
        isAvailable.addActionListener(event -> {
            if (isAvailable.isSelected()) {
                editValue("");
                enableEditing();
            } else {
                editValue(null);
                disableEditing();
            }
        });
        add(isAvailable, "align left,wrap");

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
        add(scrollPane, "span,growx,pushx,h 54!");
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

    public void setProperty(Property property) {
        this.property = property;

        disableEditing();
        if (property.value != null) {
            enableEditing();
        }
    }

    private void enableEditing() {
        textArea.setText(property.value);
        textArea.setCaretPosition(0);
        textArea.setEnabled(true);
        isAvailable.setSelected(true);
        textArea.getDocument().addDocumentListener(textAreaListener);
    }

    public void disableEditing() {
        textArea.getDocument().removeDocumentListener(textAreaListener);
        textArea.setText("");
        textArea.setEnabled(false);
        isAvailable.setSelected(false);
    }
}
