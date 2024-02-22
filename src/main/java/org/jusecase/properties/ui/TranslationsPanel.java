package org.jusecase.properties.ui;

import net.miginfocom.swing.MigLayout;
import org.jusecase.properties.entities.Property;
import org.jusecase.properties.usecases.Search;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class TranslationsPanel extends JPanel {
    private final Application application;
    private final List<TranslationPanel> translationPanels = new ArrayList<>();

    public TranslationsPanel(Application application) {
        super(new MigLayout("insets 3 0 2 0", "[fill]"));
        this.application = application;
    }

    public void setFileNames(List<String> fileNames) {
        removeAll();
        translationPanels.clear();

        if (application.getTranslationPanelComparator() != null) {
            fileNames = new ArrayList<>(fileNames);
            fileNames.sort(application.getTranslationPanelComparator());
        }

        for (String fileName : fileNames) {
            TranslationPanel translationPanel = new TranslationPanel(application, fileName);
            add(translationPanel, "wrap,pushx,growx");
            translationPanels.add(translationPanel);
        }
    }

    public void setProperties(List<Property> properties) {
        for ( Property property : properties ) {
            TranslationPanel translationPanel = findTranslationPanel(property);
            if (translationPanel != null) {
                translationPanel.setVisible(true);
                translationPanel.setProperty(property);
            }
        }
        revalidate();
    }

    public void reset() {
        for (TranslationPanel translationPanel : translationPanels) {
            translationPanel.setVisible(false);
        }
        revalidate();
    }

    public void setSearchRequest(Search.Request request ) {
        for ( TranslationPanel translationPanel : translationPanels ) {
            translationPanel.setSearchRequest(request);
        }
    }

    private TranslationPanel findTranslationPanel(Property property) {
        for ( TranslationPanel translationPanel : translationPanels ) {
            if ( Objects.equals(property.fileName, translationPanel.getFileName())) {
                return translationPanel;
            }
        }

        return null;
    }
}
