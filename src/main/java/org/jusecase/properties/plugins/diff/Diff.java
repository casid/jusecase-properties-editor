package org.jusecase.properties.plugins.diff;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class Diff {
   public final Path file;
   public final List<String> addedLines = new ArrayList<>();
   public final List<String> deletedLines = new ArrayList<>();

   public Diff( Path file ) {
      this.file = file;
   }
}
