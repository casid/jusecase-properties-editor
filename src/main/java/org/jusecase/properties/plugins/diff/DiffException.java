package org.jusecase.properties.plugins.diff;

public class DiffException extends RuntimeException {

   public DiffException( String message ) {
      super(message);
   }

   public DiffException( String message, Exception cause ) {
      super(message, cause);
   }
}
