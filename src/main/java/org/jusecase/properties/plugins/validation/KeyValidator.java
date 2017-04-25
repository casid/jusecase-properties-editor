package org.jusecase.properties.plugins.validation;

import org.jusecase.properties.usecases.UsecaseException;


public class KeyValidator {

   public void validate( String key ) {
      if (key == null || key.isEmpty()) {
         throw new UsecaseException("Key name must not be empty");
      }

      if (key.contains(" ")) {
         throw new UsecaseException("Key name must not contain whitespaces");
      }

      if (key.contains("\t")) {
         throw new UsecaseException("Key name must not contain tabs");
      }
   }
}
