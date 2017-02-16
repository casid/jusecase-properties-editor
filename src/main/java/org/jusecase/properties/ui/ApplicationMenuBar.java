package org.jusecase.properties.ui;

import javax.swing.*;

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
        return file;
    }

    private JMenuItem createFileOpenMenuItem() {
        JMenuItem open = new JMenuItem("Open");
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
        JMenuItem open = new JMenuItem("Save");
        open.addActionListener(event -> {
            application.saveProperties();
        });
        return open;
    }
}
