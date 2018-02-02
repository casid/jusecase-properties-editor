package org.jusecase.properties.plugins.diff;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


public class GitDiffPlugin implements DiffPlugin {

   @Override
   public Path getRepositoryDirectory( Path file ) {
      if ( file == null ) {
         throw new DiffException("No git repository found");
      }

      if ( Files.isDirectory(file) && Files.exists(file.resolve(".git")) ) {
         return file;
      }

      return getRepositoryDirectory(file.getParent());
   }

   @Override
   public List<Diff> getChangedFiles( Path repository, Collection<Path> files ) {
      try {
         Process process = Runtime.getRuntime().exec(createDiffCommand(files), null, repository.toFile());
         return extractChangedFiles(process.getInputStream());
      }
      catch ( Exception e ) {
         throw new DiffException("Failed to find out changed files.", e);
      }
   }

   private String createDiffCommand( Collection<Path> files ) {
      String command = "git diff --unified=0";
      if (!files.isEmpty()) {
         String filesAsString = String.join(" ", files.stream().map(Path::toString).collect(Collectors.toList()));
         command += " " + filesAsString;
      }
      return command;
   }

   List<Diff> extractChangedFiles( InputStream inputStream ) throws IOException {
      List<Diff> diffs = new ArrayList<>();

      try (BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream))) {
         String line;
         Diff diff = null;
         while ( (line = inputReader.readLine()) != null ) {
            if ( line.startsWith("+++") ) {
               diff = new Diff(Paths.get(line.substring(5)));
               diffs.add(diff);
               continue;
            }

            if ( line.startsWith("---") ) {
               continue;
            }

            if ( diff != null ) {
               if ( line.startsWith("+") ) {
                  diff.addedLines.add(line.substring(1));
               } else if ( line.startsWith("-") ) {
                  diff.deletedLines.add(line.substring(1));
               }
            }
         }
      }

      return diffs;
   }

}
