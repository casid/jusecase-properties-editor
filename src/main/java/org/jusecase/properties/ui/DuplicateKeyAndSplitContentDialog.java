package org.jusecase.properties.ui;

import java.awt.*;

import javax.swing.*;

import org.jusecase.properties.usecases.DuplicateKeyAndSplitContent;

import net.miginfocom.swing.MigLayout;


public class DuplicateKeyAndSplitContentDialog extends JDialog {

   private final JTextField newKeyText;
   private final JTextField splitRegexText;
   private final JTextField splitIndexText;

   private DuplicateKeyAndSplitContent.Request request;

   public DuplicateKeyAndSplitContentDialog( String key ) {
      adjustDialogSize();
      setTitle("Duplicate key and split content");
      setLayout(new MigLayout("insets 10"));

      newKeyText = new JTextField();
      newKeyText.setText(key);
      getContentPane().add(new JLabel("New key"), "pushx,growx,wrap");
      getContentPane().add(newKeyText, "pushx,growx,wrap");

      splitRegexText = new JTextField();
      getContentPane().add(new JLabel("Split regex (delimiter to split the content)"), "pushx,growx,wrap");
      getContentPane().add(splitRegexText, "pushx,growx,wrap");

      splitIndexText = new JTextField();
      splitIndexText.setText("1");
      getContentPane().add(new JLabel("Split index (index of the split result to keep)"), "pushx,growx,wrap");
      getContentPane().add(splitIndexText, "pushx,growx,wrap");

      JButton submitButton = new JButton("Submit");
      submitButton.addActionListener(e -> onSubmit());
      getContentPane().add(submitButton, "wrap");
   }

   public DuplicateKeyAndSplitContent.Request getRequest() {
      return request;
   }

   private void adjustDialogSize() {
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      screenSize.width = (int)(0.2 * screenSize.width);
      screenSize.height = (int)(0.2 * screenSize.height);
      setPreferredSize(screenSize);
      setSize(screenSize);

      screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      setLocation((screenSize.width - getWidth()) / 2, (screenSize.height - getHeight()) / 2);
   }

   private void onSubmit() {
      request = new DuplicateKeyAndSplitContent.Request();
      request.newKey = newKeyText.getText();
      request.splitRegex = splitRegexText.getText();
      try {
         request.splitIndex = Integer.parseInt(splitIndexText.getText());
      } catch ( NumberFormatException e ) {
         request.splitIndex = 0;
      }

      dispose();
   }
}