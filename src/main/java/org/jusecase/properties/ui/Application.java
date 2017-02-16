package org.jusecase.properties.ui;

import net.miginfocom.swing.MigLayout;
import org.jusecase.properties.usecases.GetProperties;
import org.jusecase.properties.usecases.Initialize;
import org.jusecase.properties.usecases.LoadBundle;
import org.jusecase.properties.usecases.Search;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.File;
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

    public static void main(String args[]) {
        // TODO adjust for swing
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler());

        try {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Test");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            throw new RuntimeException("Startup failed!", e);
        }

        SwingUtilities.invokeLater(() -> {
            new Application().start();
        });
    }

    public void loadProperties(File file) {
        LoadBundle.Request request = new LoadBundle.Request();
        request.propertiesFile = file.toPath();
        usecaseExecutor.execute(request, this::onLoadPropertiesComplete);
    }

    public void search(String query) {
        Search.Request request = new Search.Request();
        request.query = query;
        usecaseExecutor.execute(request, (Consumer<Search.Response>) response -> {
            keyListModel.setKeys(response.keys);
        });
    }

    private void onLoadPropertiesComplete(LoadBundle.Response response) {
        if (response.keys != null) {
            keyListModel.setKeys(response.keys);
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

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, keyPanel, translationsPanel);
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
        keyList.setVisibleRowCount(40);
        JScrollPane scrollPane = new JScrollPane(keyList);
        keyList.addListSelectionListener(e -> {
            String key = keyList.getSelectedValue();
            if (key != null) {
                GetProperties.Request request = new GetProperties.Request();
                request.key = key;
                usecaseExecutor.execute(request, (Consumer<GetProperties.Response>) response -> {
                    translationsPanel.setProperties(response.properties);
                });
            } else {
                translationsPanel.reset();
            }
        });
        keyList.setComponentPopupMenu(new KeyListMenu());
        keyPanel.add(scrollPane, "wrap,push,grow");
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
        frame.setSize(800, 600);
        frame.setVisible(true);
    }

    private void initTranslationsPanel() {
        translationsPanel = new TranslationsPanel(new MigLayout("insets 2"), this);
    }

    public JFrame getFrame() {
        return frame;
    }

    public UsecaseExecutor getUsecaseExecutor() {
        return usecaseExecutor;
    }
}
