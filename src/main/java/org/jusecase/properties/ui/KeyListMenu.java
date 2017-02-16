package org.jusecase.properties.ui;

import javax.swing.*;

public class KeyListMenu extends JPopupMenu {
    public KeyListMenu() {
        JMenuItem newKey = new JMenuItem("New");
        add(newKey);

        JMenuItem duplicateKey = new JMenuItem("Duplicate");
        add(duplicateKey);

        JMenuItem renameKey = new JMenuItem("Rename");
        add(renameKey);
    }
}
