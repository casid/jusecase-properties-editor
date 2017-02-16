package org.jusecase.properties.ui;

import net.miginfocom.swing.MigLayout;
import org.jusecase.properties.gateways.Property;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class TranslationsPanel extends JPanel {
    private final Application application;
    private List<TranslationPanel> translationPanels = new ArrayList<>();

    public TranslationsPanel(MigLayout layout, Application application) {
        super(layout);
        this.application = application;
    }

    public void setFileNames(List<String> fileNames) {
        removeAll();
        translationPanels.clear();

        for (String fileName : fileNames) {
            TranslationPanel translationPanel = new TranslationPanel(fileName);
            add(translationPanel, "wrap,pushx,growx");
            translationPanels.add(translationPanel);
        }
    }

    public void setProperties(List<Property> properties) {
        for (TranslationPanel translationPanel : translationPanels) {
            translationPanel.reset();
            for (Property property : properties) {
                if (property.fileName.equals(translationPanel.getFileName())) {
                    translationPanel.setProperty(property);
                }
            }
        }
        validate();
    }

    public void reset() {
        for (TranslationPanel translationPanel : translationPanels) {
            translationPanel.reset();
        }
        validate();
    }
}
