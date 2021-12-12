package org.jusecase.properties.ui;

import net.miginfocom.swing.MigLayout;
import org.jusecase.properties.entities.Property;
import org.jusecase.properties.usecases.Search;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

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

        for (String fileName : fileNames) {
            TranslationPanel translationPanel = new TranslationPanel(application, fileName);
            add(translationPanel, "wrap,pushx,growx");
            translationPanels.add(translationPanel);
        }
    }

    public void setProperties(List<Property> properties) {
        int index = 0;
        for (TranslationPanel translationPanel : translationPanels) {
            translationPanel.setVisible(true);
            translationPanel.setProperty(properties.get(index));
            ++index;
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
}
