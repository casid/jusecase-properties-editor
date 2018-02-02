package org.jusecase.properties.plugins.diff;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;


public interface DiffPlugin {

   List<Diff> getChangedFiles( Path repository, Collection<Path> files );

   Path getRepositoryDirectory( Path file );
}
