package org.jusecase.properties.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.util.tinyfd.TinyFileDialogs;


public class NativeFileDialog {
   public static File open(String title) {
      String file = TinyFileDialogs.tinyfd_openFileDialog(title, "", null, null, false);
      if (file == null) {
         return null;
      }

      return new File(file);
   }

   public static List<File> openMultiple(String title) {
      List<File> result = new ArrayList<>();
      String file = TinyFileDialogs.tinyfd_openFileDialog(title, "", null, null, true);
      if (file != null) {
         String[] files = file.split("\\|");
         for ( String f : files ) {
            result.add(new File(f));
         }
      }

      return result;
   }

   public static File save(String title, String defaultFileName) {
      String file = TinyFileDialogs.tinyfd_saveFileDialog(title, defaultFileName, null, null);
      if (file == null) {
         return null;
      }

      return new File(file);
   }
}
