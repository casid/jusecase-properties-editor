package org.jusecase.properties.ui;

import org.jusecase.properties.usecases.*;

import javax.swing.*;

import java.util.List;
import java.util.stream.Collectors;


public class KeyListMenu extends JPopupMenu {
    private final Application application;

    public KeyListMenu(Application application) {
        this.application = application;

        addNew();
        addDuplicate();
        addDuplicateAndSplitContent();
        addSeparator();
        addRename();
        addSeparator();
        addDelete();
    }

    private void addDelete() {
        JMenuItem item = new JMenuItem("Delete");
        item.addActionListener(event -> {
            List<String> keys = application.getSelectedValues();
            if (!keys.isEmpty()) {
                for ( String key : keys ) {
                    DeleteKey.Request request = new DeleteKey.Request();
                    request.key = key;
                    application.execute(request);
                }
                application.onKeyDeleted();
            }

        });
        add(item);
    }

    private void addRename() {
        JMenuItem item = new JMenuItem("Rename");
        item.addActionListener(event -> {
            List<String> selectedKeys = application.getSelectedValues();
            if (selectedKeys.size() > 1) {
                rename(selectedKeys);
            } else {
                String key = application.getSelectedKey();
                rename(key);
            }
        });
        add(item);
    }

    private void rename(String key) {
        String newKey = (String)JOptionPane.showInputDialog(null, "Rename key", "Rename key", JOptionPane.PLAIN_MESSAGE, null, null, key);
        if (newKey != null) {
            RenameKey.Request request = new RenameKey.Request();
            request.key = key;
            request.newKey = newKey;
            application.execute(request);
            application.onKeyRenamed();
        }
    }

    private void rename(List<String> selectedKeys) {
        String replace = (String)JOptionPane
                .showInputDialog(null, "You are about to rename more than one key. First, select what key part should be replaced:", "Rename keys", JOptionPane.PLAIN_MESSAGE, null, null, "");
        if (replace == null) {
            return;
        }
        String replaceWith = (String)JOptionPane.showInputDialog(null, "Replace " + replace +  " with", "Rename keys", JOptionPane.PLAIN_MESSAGE, null, null, "");
        if (replaceWith == null) {
            return;
        }

        RenameKeys.Request request = new RenameKeys.Request();
        request.keys = selectedKeys;
        request.newKeys = selectedKeys.stream().map(s -> s.replaceAll(replace, replaceWith)).collect(Collectors.toList());
        application.execute(request);
        application.onKeyRenamed();
    }

    private void addDuplicate() {
        JMenuItem item = new JMenuItem("Duplicate");
        item.addActionListener(event -> {
            List<String> selectedKeys = application.getSelectedValues();
            if (selectedKeys.size() > 1) {
                addDuplicates(selectedKeys);
            } else {
                String key = application.getSelectedKey();
                addDuplicate(key);
            }
        });
        add(item);
    }

    private void addDuplicateAndSplitContent() {
        JMenuItem item = new JMenuItem("Duplicate and split content");
        item.addActionListener(event -> {
            String key = application.getSelectedKey();

            DuplicateKeyAndSplitContentDialog dialog = new DuplicateKeyAndSplitContentDialog(key);
            dialog.setModal(true);
            dialog.setVisible(true);
            dialog.dispose();

            DuplicateKeyAndSplitContent.Request request = dialog.getRequest();
            if ( request != null) {
                request.key = key;
                application.execute(request);
                application.onNewKeyAdded(request.newKey);
            }

        });
        add(item);
    }

    private void addDuplicate( String key ) {
        String newKey = (String)JOptionPane.showInputDialog(null, "Duplicate key", "Duplicate key", JOptionPane.PLAIN_MESSAGE, null, null, key);
        if ( newKey != null ) {
            DuplicateKey.Request request = new DuplicateKey.Request();
            request.key = key;
            request.newKey = newKey;
            application.execute(request);
            application.onNewKeyAdded(newKey);
        }
    }

    private void addDuplicates( List<String> selectedKeys ) {
        String replace = (String)JOptionPane
              .showInputDialog(null, "You are about to duplicate more than one key. First, select what key part should be replaced:", "Duplicate keys", JOptionPane.PLAIN_MESSAGE, null, null, "");
        if (replace == null) {
            return;
        }
        String replaceWith = (String)JOptionPane.showInputDialog(null, "Replace " + replace +  " with", "Duplicate keys", JOptionPane.PLAIN_MESSAGE, null, null, "");
        if (replaceWith == null) {
            return;
        }

        DuplicateKeys.Request request = new DuplicateKeys.Request();
        request.keys = selectedKeys;
        request.newKeys = selectedKeys.stream().map(s -> s.replaceAll(replace, replaceWith)).collect(Collectors.toList());
        application.execute(request);
        request.keys.forEach(application::onNewKeyAdded);
    }

    private void addNew() {
        JMenuItem item = new JMenuItem("New");
        item.addActionListener(event -> {
            String keyNameSuggestion = application.getSelectedKey();
            String key = (String)JOptionPane.showInputDialog(null, "Enter new key name", "New key", JOptionPane.PLAIN_MESSAGE, null, null, keyNameSuggestion);
            if (key != null) {
                NewKey.Request request = new NewKey.Request();
                request.key = key;
                application.execute(request);
                application.onNewKeyAdded(key);
            }

        });
        add(item);
    }
}
