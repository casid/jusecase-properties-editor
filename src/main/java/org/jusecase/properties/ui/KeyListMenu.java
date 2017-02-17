package org.jusecase.properties.ui;

import org.jusecase.properties.usecases.NewKey;

import javax.swing.*;

public class KeyListMenu extends JPopupMenu {
    private final Application application;

    public KeyListMenu(Application application) {
        this.application = application;

        addNew();
        addDuplicate();
        addSeparator();
        addRename();
        addSeparator();
        addDelete();
    }

    private void addDelete() {
        JMenuItem deleteKey = new JMenuItem("Delete");
        add(deleteKey);
    }

    private void addRename() {
        JMenuItem renameKey = new JMenuItem("Rename");
        add(renameKey);
    }

    private void addDuplicate() {
        JMenuItem duplicateKey = new JMenuItem("Duplicate");
        add(duplicateKey);
    }

    private void addNew() {
        JMenuItem item = new JMenuItem("New");
        item.addActionListener(event -> {
            String key = JOptionPane.showInputDialog(null, "Enter new key name", "New key", JOptionPane.PLAIN_MESSAGE);
            if (key != null) {
                NewKey.Request request = new NewKey.Request();
                request.key = key;
                application.getUsecaseExecutor().execute(request);
                application.onNewKeyAdded(key);
            }

        });
        add(item);
    }
}
