package org.jusecase.properties.plugins.diff;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;


public class DiffPluginTrainer implements DiffPlugin {

   private boolean    supported    = true;
   private List<Diff> changedFiles;
   private Path       repositoryDirectory;

   @Override
   public List<Diff> getChangedFiles( Path repository, Collection<Path> files ) {
      if ( !supported ) {
         throw new DiffException("not supported");
      }
      return changedFiles;
   }

   @Override
   public Path getRepositoryDirectory( Path file ) {
      if ( !supported ) {
         throw new DiffException("not supported");
      }
      return repositoryDirectory;
   }

   public void givenChangedFiles( List<Diff> changedFiles ) {
      this.supported = true;
      this.changedFiles = changedFiles;
   }

   public void givenRepositoryDirectory( Path path ) {
      this.supported = true;
      repositoryDirectory = path;
   }

   public void givenDiffNotSupported() {
      supported = false;
   }
}