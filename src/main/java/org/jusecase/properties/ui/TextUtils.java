package org.jusecase.properties.ui;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;


public class TextUtils {

   public static void enableDefaultShortcuts( JTextComponent textComponent) {
      KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
      textComponent.getInputMap().put(keyStroke, new AbstractAction() {

         @Override
         public void actionPerformed( ActionEvent e ) {
            try {
               textComponent.getDocument().insertString(textComponent.getCaretPosition(), " !", null);
            }
            catch ( BadLocationException ex ) {
               // Ignore
            }
         }
      });
   }
}
