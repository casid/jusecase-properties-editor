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
    }

    private JMenu createFileMenu() {
        JMenu file = new JMenu("File");
        file.add(createFileOpenMenuItem());
        file.add(createFileSaveMenuItem());
        file.addSeparator();
        file.add(createFileSaveAllMenuItem());
        return file;
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

    private JMenuItem createItem(String name, int accelerator) {
        JMenuItem item = new JMenuItem(name);
        item.setAccelerator(KeyStroke.getKeyStroke(accelerator, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        return item;
    }
}
