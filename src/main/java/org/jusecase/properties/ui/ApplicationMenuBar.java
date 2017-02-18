package org.jusecase.properties.ui;

import org.jusecase.properties.usecases.GetUndoStatus;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.KeyEvent;

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
        file.add(createFileSaveMenuItem());
        file.addSeparator();
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

    private void updateUndoAndRedoItems() {
        application.getUsecaseExecutor().execute(new GetUndoStatus.Request(), (GetUndoStatus.Response response) -> {
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
            JFileChooser fileChooser = new NativeJFileChooser();
            fileChooser.setDialogTitle("Open Resource File");
            if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                application.loadProperties(fileChooser.getSelectedFile());
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
