package org.jusecase.properties.ui;

import net.miginfocom.swing.MigLayout;
import org.jusecase.properties.entities.Property;
import org.jusecase.properties.usecases.EditValue;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


public class TranslationPanel extends JPanel {
    private final Application application;
    private final String fileName;

    private JCheckBox isAvailable;
    private JTextArea textArea;
    private DocumentListener textAreaListener;
    private Property property;
    private String searchQuery = "";
    private Color searchHighlightColor = new Color(230, 230, 255);
    private Color transparentBackgroundColor;
    private Highlighter.HighlightPainter hightlightPainter = new DefaultHighlighter.DefaultHighlightPainter(searchHighlightColor);
    private TranslationTextScrollPane scrollPane;
    private boolean documentListenerAdded;

    public TranslationPanel(Application application, String fileName) {
        super(new MigLayout("insets 0", "", "[][grow]"));
        this.application = application;
        this.fileName = fileName;
        this.transparentBackgroundColor = getBackground();
        init();
    }

    public void setSearchQuery( String searchQuery ) {
        this.searchQuery = searchQuery;
        highlightSearchQuery();
    }

    private void init() {
        isAvailable = new JCheckBox(fileName);
        isAvailable.addActionListener(event -> {
            if (isAvailable.isSelected()) {
                editValue("");
                enableTextEditing();
            } else {
                disableTextEditing();
                editValue(null);
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
        scrollPane = new TranslationTextScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, "pushx,growx,wmax 100%");
    }

    private void editValue() {
        editValue(textArea.getText());

        int currentRows = textArea.getRows();
        int requiredRows = calculateRowCount();
        if (currentRows != requiredRows) {
            textArea.setRows(requiredRows);
            revalidate();
        }
    }

    private void editValue(String value) {
        EditValue.Request request = new EditValue.Request();
        request.property = property;
        request.value = value;
        application.execute(request);
    }

    public void setProperty(Property property) {
        this.property = property;

        if (property.value != null) {
            enableTextEditing();
        } else {
            disableTextEditing();
        }
    }

    private void enableTextEditing() {
        stopListeningForDocumentChanges();

        textArea.setText(property.value);
        textArea.setCaretPosition(0);
        textArea.setEnabled(true);
        textArea.setRows(calculateRowCount());
        isAvailable.setSelected(true);

        startListeningForDocumentChanges();
        highlightSearchQuery();
    }

    private void startListeningForDocumentChanges() {
        if (!documentListenerAdded) {
            textArea.getDocument().addDocumentListener(textAreaListener);
            documentListenerAdded = true;
        }
    }

    private int calculateRowCount() {
        int rowCount = textArea.getLineCount();
        if (scrollPane.getWidth() > 0 && textArea.getPreferredSize().width > scrollPane.getWidth()) {
            rowCount += 1;
        }
        return rowCount;
    }

    public void disableTextEditing() {
        stopListeningForDocumentChanges();

        textArea.setText("");
        textArea.setEnabled(false);
        textArea.setRows(1);
        isAvailable.setSelected(false);
        isAvailable.setBackground(transparentBackgroundColor);
    }

    private void stopListeningForDocumentChanges() {
        if (documentListenerAdded) {
            textArea.getDocument().removeDocumentListener(textAreaListener);
            documentListenerAdded = false;
        }
    }

    private void highlightSearchQuery() {
        Highlighter highlighter = textArea.getHighlighter();
        highlighter.removeAllHighlights();

        boolean didHighlight = false;
        if (this.property != null && this.property.value != null && !searchQuery.isEmpty()) {
            try {
                Matcher m = Pattern.compile(searchQuery.toLowerCase()).matcher(textArea.getText().toLowerCase());
                while ( m.find() ) {
                    try {
                        didHighlight = true;
                        highlighter.addHighlight(m.start(), m.end(), hightlightPainter);
                    }
                    catch ( BadLocationException e ) {
                        e.printStackTrace(); // Log and ignore silently
                    }
                }
            } catch ( PatternSyntaxException e ) {
                e.printStackTrace(); // Log and ignore silently
            }
        }
        isAvailable.setBackground(didHighlight ? searchHighlightColor : transparentBackgroundColor);
    }
}
