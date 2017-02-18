package org.jusecase.properties.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class ApplicationMenuBar extends JMenuBar {
    private final Application application;
    private final JFrame frame;

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
        edit.add(createUndoMenuItem());
        edit.add(createRedoMenuItem());
        return edit;
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
        });
        return undo;
    }

    private JMenuItem createRedoMenuItem() {
        JMenuItem redo = createItem("Redo", KeyEvent.VK_Y);
        redo.setName("Redo");
        redo.addActionListener(event -> {
            application.redo();
        });
        return redo;
    }

    private JMenuItem createItem(String name, int accelerator) {
        JMenuItem item = new JMenuItem(name);
        item.setAccelerator(KeyStroke.getKeyStroke(accelerator, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        return item;
    }
}
