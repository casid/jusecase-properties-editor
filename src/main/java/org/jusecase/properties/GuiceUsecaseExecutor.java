package org.jusecase.properties;

import org.jusecase.Usecase;
import org.jusecase.executors.AbstractUsecaseExecutor;

import com.google.inject.Injector;


public class GuiceUsecaseExecutor extends AbstractUsecaseExecutor {
   private Injector injector;

   public GuiceUsecaseExecutor() {
   }

   protected Injector getInjector() {
      return injector;
   }

   protected void setInjector(Injector injector) {
      this.injector = injector;
   }

   public void addUsecase(Class<? extends Usecase> usecaseClass) {
      addUsecase(getRequestClass(usecaseClass), usecaseClass);
   }

   @Override
   protected Object resolveUsecase(Object usecase) {
      return injector.getInstance((Class<?>)usecase);
   }

}
