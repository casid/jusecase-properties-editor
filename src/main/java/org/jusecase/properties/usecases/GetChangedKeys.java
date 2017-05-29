package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.properties.entities.UndoableRequest;
import org.jusecase.properties.gateways.UndoableRequestGateway;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


@Singleton
public class GetChangedKeys implements Usecase<GetChangedKeys.Request, GetChangedKeys.Response> {

   private final UndoableRequestGateway undoableRequestGateway;
   private final List<ChangeDetector>   changeDetectors;


   @Inject
   public GetChangedKeys( UndoableRequestGateway undoableRequestGateway ) {
      this.undoableRequestGateway = undoableRequestGateway;

      changeDetectors = new ArrayList<>();
      changeDetectors.add(new NewKeyDetector());
      changeDetectors.add(new DeleteKeyDetector());
      changeDetectors.add(new RenameKeyDetector());
      changeDetectors.add(new DuplicateKeyDetector());
      changeDetectors.add(new EditValueDetector());
   }

   @Override
   public Response execute( Request request ) {
      Response response = new Response();

      Map<String, Integer> keys = new TreeMap<>();
      for ( UndoableRequest undoableRequest : undoableRequestGateway.getAll() ) {
         for ( ChangeDetector changeDetector : changeDetectors ) {
            if ( changeDetector.canHandle(undoableRequest) ) {
               changeDetector.handle(undoableRequest, keys);
               break;
            }
         }
      }

      response.keys = keys.keySet();
      return response;
   }


   public static class Request {
   }

   public static class Response {
      Collection<String> keys;
   }

   private interface ChangeDetector {
      boolean canHandle( UndoableRequest request );
      void handle( UndoableRequest request, Map<String, Integer> keys );

      default void remove(Map<String, Integer> keys, String key) {
         keys.compute(key, ( s, count ) -> {
            if (count != null) {
               if (--count > 0) {
                  return count;
               } else {
                  return null;
               }
            }
            return null;
         });
      }

      default void add(Map<String, Integer> keys, String key) {
         keys.compute(key, ( s, count ) -> {
            if ( count != null ) {
               return ++count;
            }
            return 1;
         });
      }
   }

   private static class NewKeyDetector implements ChangeDetector {
      @Override
      public boolean canHandle( UndoableRequest request ) {
         return request instanceof NewKey.Request;
      }

      @Override
      public void handle( UndoableRequest request, Map<String, Integer> keys ) {
         NewKey.Request r = (NewKey.Request)request;
         if ( r.undo ) {
            remove(keys, r.key);
         } else {
            add(keys, r.key);
         }
      }
   }

   private static class DeleteKeyDetector implements ChangeDetector {
      @Override
      public boolean canHandle( UndoableRequest request ) {
         return request instanceof DeleteKey.Request;
      }

      @Override
      public void handle( UndoableRequest request, Map<String, Integer> keys ) {
         DeleteKey.Request r = (DeleteKey.Request)request;
         if ( !r.undo ) {
            remove(keys, r.key);
         }
      }
   }

   private static class RenameKeyDetector implements ChangeDetector {
      @Override
      public boolean canHandle( UndoableRequest request ) {
         return request instanceof RenameKey.Request;
      }

      @Override
      public void handle( UndoableRequest request, Map<String, Integer> keys ) {
         RenameKey.Request r = (RenameKey.Request)request;
         if ( r.undo ) {
            remove(keys, r.newKey);
            add(keys, r.key);
         } else {
            remove(keys, r.key);
            add(keys, r.newKey);
         }
      }
   }

   private static class DuplicateKeyDetector implements ChangeDetector {
      @Override
      public boolean canHandle( UndoableRequest request ) {
         return request instanceof DuplicateKey.Request;
      }

      @Override
      public void handle( UndoableRequest request, Map<String, Integer> keys ) {
         DuplicateKey.Request r = (DuplicateKey.Request)request;
         if ( r.undo ) {
            remove(keys, r.newKey);
         } else {
            add(keys, r.newKey);
         }
      }
   }

   private static class EditValueDetector implements ChangeDetector {
      @Override
      public boolean canHandle( UndoableRequest request ) {
         return request instanceof EditValue.Request;
      }

      @Override
      public void handle( UndoableRequest request, Map<String, Integer> keys ) {
         EditValue.Request r = (EditValue.Request)request;
         if ( r.undo ) {
            remove(keys, r.property.key);
         } else {
            add(keys, r.property.key);
         }
      }
   }
}