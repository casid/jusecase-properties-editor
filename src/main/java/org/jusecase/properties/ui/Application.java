package org.jusecase.properties.ui;

import net.miginfocom.swing.MigLayout;
import org.jusecase.properties.usecases.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;

public class Application {
    private UsecaseExecutor usecaseExecutor = new UsecaseExecutor();
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

    public void loadProperties(File file) {
        LoadBundle.Request request = new LoadBundle.Request();
        request.propertiesFile = file.toPath();
        usecaseExecutor.execute(request, this::onLoadPropertiesComplete);
    }

    public void save() {
        usecaseExecutor.execute(new SaveBundle.Request());
    }

    public void saveAll() {
        SaveBundle.Request request = new SaveBundle.Request();
        request.saveAll = true;
        usecaseExecutor.execute(request);
    }

    public void search(String query) {
        Search.Request request = new Search.Request();
        request.query = query;
        usecaseExecutor.execute(request, (Consumer<Search.Response>) response -> {
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

        usecaseExecutor.execute(new Initialize.Request(), this::onLoadPropertiesComplete);
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
        usecaseExecutor.execute(request, (Consumer<Search.Response>) response -> {
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

    public void onKeyRenamed( String key, String newKey ) {
        refreshSearch();
    }

    public void refreshSearch() {
        Search.Request request = new Search.Request();
        request.query = searchField.getText();
        usecaseExecutor.execute(request, (Consumer<Search.Response>) response -> {
            int previousSelectedIndex = keyList.getSelectedIndex();
            keyListModel.setKeys(response.keys);
            keyList.setSelectedIndex(Math.max(0, previousSelectedIndex));
        });
    }

    private void updateTranslationPanel(String key) {
        if (key != null) {
            GetProperties.Request request = new GetProperties.Request();
            request.key = key;
            usecaseExecutor.execute(request, (Consumer<GetProperties.Response>) response -> {
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

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds(0, 0, screenSize.width, screenSize.height);
        frame.setVisible(true);
    }

    private void initTranslationsPanel() {
        translationsPanel = new TranslationsPanel(this);
    }

    public JFrame getFrame() {
        return frame;
    }

    public UsecaseExecutor getUsecaseExecutor() {
        return usecaseExecutor;
    }

    public String getSelectedKey() {
        return keyList.getSelectedValue();
    }

    public List<String> getSelectedValues() {
        return keyList.getSelectedValuesList();
    }
}
