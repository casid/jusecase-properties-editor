package org.jusecase.properties.ui;

import org.jusecase.properties.plugins.Plugin;
import org.jusecase.properties.plugins.exporter.PropertiesExporter;
import org.jusecase.properties.plugins.importer.PropertiesImporter;
import org.jusecase.properties.usecases.GetPlugins;
import org.jusecase.properties.usecases.GetUndoStatus;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;


public class ApplicationMenuBar extends JMenuBar {
    private final Application application;
    private final JFrame frame;
    private JMenuItem undoMenuItem;
    private JMenuItem redoMenuItem;

    public ApplicationMenuBar(Application application) {
        this.application = application;
        this.frame = application.getFrame();
        init();
    }

    private void init() {
        add(createFileMenu());
        add(createEditMenu());
    }

    private JMenu createFileMenu() {
        JMenu file = new JMenu("File");
        file.add(createFileOpenMenuItem());
        file.addSeparator();
        file.add(createFileImportMenu());
        file.add(createFileExportMenu());
        file.addSeparator();
        file.add(createFileSaveMenuItem());
        file.add(createFileSaveAllMenuItem());
        return file;
    }

    private JMenu createEditMenu() {
        JMenu edit = new JMenu("Edit");
        edit.add(undoMenuItem = createUndoMenuItem());
        edit.add(redoMenuItem = createRedoMenuItem());

        edit.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                updateUndoAndRedoItems();
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });
        return edit;
    }

    public void updateUndoAndRedoItems() {
        application.execute(new GetUndoStatus.Request(), (GetUndoStatus.Response response) -> {
            updateUndoOrRedoItem(undoMenuItem, response.undoAction, "Undo");
            updateUndoOrRedoItem(redoMenuItem, response.redoAction, "Redo");
        });
    }

    private void updateUndoOrRedoItem(JMenuItem item, String action, String actionFallback) {
        if (action == null) {
            item.setEnabled(false);
            item.setText(actionFallback);
        } else {
            item.setEnabled(true);
            item.setText(action);
        }
    }

    private JMenuItem createFileOpenMenuItem() {
        JMenuItem open = createItem("Open", KeyEvent.VK_O);
        open.addActionListener(event -> {
            File file = NativeFileDialog.open("Open Resource File");
            if (file != null) {
                application.loadProperties(file);
            }
        });
        return open;
    }

    private JMenuItem createFileSaveMenuItem() {
        JMenuItem save = createItem("Save", KeyEvent.VK_S);
        save.addActionListener(event -> application.save());
        return save;
    }

    private JMenuItem createFileSaveAllMenuItem() {
        JMenuItem save = new JMenuItem("Save All");
        save.addActionListener(event -> application.saveAll());
        return save;
    }

    private JMenu createFileImportMenu() {
        JMenu menu = new JMenu("Import");

        GetPlugins.Request request = new GetPlugins.Request();
        request.pluginClass = PropertiesImporter.class;
        application.execute(request, (GetPlugins.Response response) -> {
            for ( Plugin plugin : response.plugins ) {
                menu.add(createFileImportMenuItem(plugin));
            }
        });

        return menu;
    }

    private JMenu createFileExportMenu() {
        JMenu menu = new JMenu("Export");

        GetPlugins.Request request = new GetPlugins.Request();
        request.pluginClass = PropertiesExporter.class;
        application.execute(request, (GetPlugins.Response response) -> {
            for ( Plugin plugin : response.plugins ) {
                menu.add(createFileExportMenuItem(plugin));
            }
        });

        return menu;
    }

    private JMenuItem createFileImportMenuItem(Plugin plugin) {
        JMenuItem item = new JMenuItem(plugin.getPluginName());
        item.addActionListener(event -> application.importProperties(plugin));
        return item;
    }

    private JMenuItem createFileExportMenuItem(Plugin plugin) {
        JMenuItem item = new JMenuItem(plugin.getPluginName());
        item.addActionListener(event -> application.exportProperties(plugin));
        return item;
    }

    private JMenuItem createUndoMenuItem() {
        JMenuItem undo = createItem("Undo", KeyEvent.VK_Z);
        undo.setName("Undo");
        undo.addActionListener(event -> {
            application.undo();
            updateUndoAndRedoItems();
        });
        return undo;
    }

    private JMenuItem createRedoMenuItem() {
        JMenuItem redo = createItem("Redo", KeyEvent.VK_Y);
        redo.setName("Redo");
        redo.addActionListener(event -> {
            application.redo();
            updateUndoAndRedoItems();
        });
        return redo;
    }

    private JMenuItem createItem(String name, int accelerator) {
        JMenuItem item = new JMenuItem(name);
        item.setAccelerator(KeyStroke.getKeyStroke(accelerator, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        return item;
    }
}
