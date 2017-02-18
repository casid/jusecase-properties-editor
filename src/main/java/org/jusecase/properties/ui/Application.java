package org.jusecase.properties.ui;

import net.miginfocom.swing.MigLayout;
import org.jusecase.properties.BusinessLogic;
import org.jusecase.properties.usecases.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;

public class Application {

    private final BusinessLogic businessLogic = new BusinessLogic();

    private JFrame frame;
    private JPanel panel;
    private ApplicationMenuBar menuBar;
    private JList<String> keyList;
    KeyListModel keyListModel = new KeyListModel();
    private JTextField searchField;
    private JPanel keyPanel;
    private TranslationsPanel translationsPanel;

    public static void main(String args[]) throws Exception {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

        macSetup("Properties Editor");
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        SwingUtilities.invokeLater(() -> {
            new Application().start();
        });
    }

    private static void macSetup(String appName) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("mac")) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name",
                    appName);
        }
    }

    public <Request> void execute(Request request) {
        execute(request, null);
    }

    public <Request, Response> void execute(Request request, Consumer<Response> responseConsumer) {
        Response response = businessLogic.execute(request);
        if (responseConsumer != null && response != null) {
            responseConsumer.accept(response);
        }
    }

    public void loadProperties(File file) {
        LoadBundle.Request request = new LoadBundle.Request();
        request.propertiesFile = file.toPath();
        execute(request, this::onLoadPropertiesComplete);
    }

    public void save() {
        execute(new SaveBundle.Request());
    }

    public void saveAll() {
        SaveBundle.Request request = new SaveBundle.Request();
        request.saveAll = true;
        execute(request);
    }

    public void search(String query) {
        Search.Request request = new Search.Request();
        request.query = query;
        execute(request, (Consumer<Search.Response>) response -> {
            updateKeyList(response.keys);
            translationsPanel.setSearchQuery(query);
        });
    }

    private void updateKeyList( List<String> keys) {
        String selectedValue = keyList.getSelectedValue();
        keyListModel.setKeys(keys);

        int index = keys.indexOf(selectedValue);
        if (index >= 0) {
            keyList.setSelectedIndex(index);
            keyList.ensureIndexIsVisible(index);
        } else {
            keyList.clearSelection();
            keyList.ensureIndexIsVisible(0);
        }
    }

    private void onLoadPropertiesComplete(LoadBundle.Response response) {
        if (response.keys != null) {
            updateKeyList(response.keys);
            translationsPanel.setFileNames(response.fileNames);
        }
    }

    private void start() {
        initFrame();
        initMenuBar();
        initPanel();

        execute(new Initialize.Request(), this::onLoadPropertiesComplete);
    }

    private void initPanel() {
        panel = new JPanel(new MigLayout("insets 0"));

        initKeyPanel();
        initTranslationsPanel();

        JScrollPane translationsPanelScrollPane = new JScrollPane(translationsPanel);
        translationsPanelScrollPane.setBorder(null);
        translationsPanelScrollPane.getVerticalScrollBar().setUnitIncrement(8);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, keyPanel, translationsPanelScrollPane);
        splitPane.setDividerLocation(0.3);
        panel.add(splitPane, "push,grow");

        frame.add(panel);
    }

    private void initKeyPanel() {
        keyPanel = new JPanel(new MigLayout("insets 2,fill"));
        initSearchField();
        initKeyList();
    }

    private void initKeyList() {
        keyList = new JList<>(keyListModel);
        keyList.addListSelectionListener(e -> updateTranslationPanel(keyList.getSelectedValue()));
        keyList.setComponentPopupMenu(new KeyListMenu(this));

        JScrollPane scrollPane = new JScrollPane(keyList);
        keyPanel.add(scrollPane, "wrap,push,grow");
    }

    public void onNewKeyAdded(String key) {
        Search.Request request = new Search.Request();
        request.query = searchField.getText();
        execute(request, (Consumer<Search.Response>) response -> {
            if (response.keys.contains(key)) {
                keyListModel.setKeys(response.keys);
            } else {
                resetSearch();
            }
            keyList.setSelectedValue(key, true);
        });
    }

    private void resetSearch() {
        searchField.setText("");
        search("");
    }

    public void onKeyRenamed() {
        refreshSearch();
    }

    public void refreshSearch() {
        Search.Request request = new Search.Request();
        request.query = searchField.getText();
        execute(request, (Consumer<Search.Response>) response -> {
            int previousSelectedIndex = keyList.getSelectedIndex();
            keyListModel.setKeys(response.keys);
            keyList.setSelectedIndex(Math.max(0, previousSelectedIndex));
        });
    }

    private void updateTranslationPanel(String key) {
        if (key != null) {
            GetProperties.Request request = new GetProperties.Request();
            request.key = key;
            execute(request, (Consumer<GetProperties.Response>) response -> {
                translationsPanel.setProperties(response.properties);
            });
        } else {
            translationsPanel.reset();
        }
    }

    private void initSearchField() {
        searchField = new JTextField();
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                search(searchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                search(searchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                search(searchField.getText());
            }
        });
        keyPanel.add(searchField, "wrap,growx");
    }

    private void initMenuBar() {
        menuBar = new ApplicationMenuBar(this);
        frame.setJMenuBar(menuBar);
    }

    private void initFrame() {
        frame = new JFrame("Properties Editor");
        frame.setVisible(true);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
    }

    private void initTranslationsPanel() {
        translationsPanel = new TranslationsPanel(this);
    }

    public JFrame getFrame() {
        return frame;
    }

    public String getSelectedKey() {
        return keyList.getSelectedValue();
    }

    public List<String> getSelectedValues() {
        return keyList.getSelectedValuesList();
    }

    public void undo() {
        execute(new Undo.Request(), (Undo.Response response) -> {
            handleUndoOrRedoResponse(response.undoRequest, response.undoResponse);
        });
    }

    public void redo() {
        execute(new Redo.Request(), (Redo.Response response) -> {
            handleUndoOrRedoResponse(response.redoRequest, response.redoResponse);
        });
    }

    private void handleUndoOrRedoResponse(Object request, Object response) {
        if (request instanceof EditValue.Request) {
            EditValue.Request editValue = (EditValue.Request)request;
            if (editValue.property.key.equals(getSelectedKey())) {
                updateTranslationPanel(editValue.property.key);
            }
        }

        if (request instanceof NewKey.Request) {
            NewKey.Request newKey = (NewKey.Request) request;
            if (newKey.undo) {
                onKeyDeleted();
            } else {
                onNewKeyAdded(newKey.key);
            }
        }

        if (request instanceof DeleteKey.Request) {
            DeleteKey.Request deleteKey = (DeleteKey.Request) request;
            if (deleteKey.undo) {
                onNewKeyAdded(deleteKey.key);
            } else {
                onKeyDeleted();
            }
        }

        if (request instanceof DuplicateKey.Request) {
            DuplicateKey.Request duplicateKey = (DuplicateKey.Request) request;
            if (duplicateKey.undo) {
                onKeyDeleted();
            } else {
                onNewKeyAdded(duplicateKey.newKey);
            }
        }

        if (request instanceof RenameKey.Request) {
            onKeyRenamed();
        }
    }

    public void onKeyDeleted() {
        refreshSearch();
    }
}
