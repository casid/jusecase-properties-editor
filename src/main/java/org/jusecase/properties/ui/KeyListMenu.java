package org.jusecase.properties.ui;

import org.jusecase.properties.usecases.DeleteKey;
import org.jusecase.properties.usecases.DuplicateKey;
import org.jusecase.properties.usecases.NewKey;
import org.jusecase.properties.usecases.RenameKey;

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
        JMenuItem item = new JMenuItem("Delete");
        item.addActionListener(event -> {
            String key = application.getSelectedKey();
            if (key != null) {
                DeleteKey.Request request = new DeleteKey.Request();
                request.key = key;
                application.getUsecaseExecutor().execute(request);
                application.onKeyDeleted(key);
            }

        });
        add(item);
    }

    private void addRename() {
        JMenuItem item = new JMenuItem("Rename");
        item.addActionListener(event -> {
            String key = application.getSelectedKey();
            String newKey = (String)JOptionPane.showInputDialog(null, "Rename key", "Rename key", JOptionPane.PLAIN_MESSAGE, null, null, key);
            if (newKey != null) {
                RenameKey.Request request = new RenameKey.Request();
                request.key = key;
                request.newKey = newKey;
                application.getUsecaseExecutor().execute(request);
                application.onKeyRenamed(key, newKey);
            }

        });
        add(item);
    }

    private void addDuplicate() {
        JMenuItem item = new JMenuItem("Duplicate");
        item.addActionListener(event -> {
            String key = application.getSelectedKey();
            String newKey = (String)JOptionPane.showInputDialog(null, "Duplicate key", "Duplicate key", JOptionPane.PLAIN_MESSAGE, null, null, key);
            if (newKey != null) {
                DuplicateKey.Request request = new DuplicateKey.Request();
                request.key = key;
                request.newKey = newKey;
                application.getUsecaseExecutor().execute(request);
                application.onNewKeyAdded(newKey);
            }

        });
        add(item);
    }

    private void addNew() {
        JMenuItem item = new JMenuItem("New");
        item.addActionListener(event -> {
            String keyNameSuggestion = application.getSelectedKey();
            String key = (String)JOptionPane.showInputDialog(null, "Enter new key name", "New key", JOptionPane.PLAIN_MESSAGE, null, null, keyNameSuggestion);
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
