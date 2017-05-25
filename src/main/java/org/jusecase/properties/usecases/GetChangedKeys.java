package org.jusecase.properties.usecases;

import org.jusecase.Usecase;
import org.jusecase.properties.entities.UndoableRequest;
import org.jusecase.properties.gateways.UndoableRequestGateway;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;


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
   }

   @Override
   public Response execute( Request request ) {
      Response response = new Response();
      response.keys = new TreeSet<>();

      for ( UndoableRequest undoableRequest : undoableRequestGateway.getAll() ) {
         for ( ChangeDetector changeDetector : changeDetectors ) {
            if ( changeDetector.canHandle(undoableRequest) ) {
               changeDetector.handle(undoableRequest, response.keys);
               break;
            }
         }
      }

      return response;
   }


   public static class Request {
   }

   public static class Response {
      Collection<String> keys;
   }

   private interface ChangeDetector {
      boolean canHandle( UndoableRequest request );
      void handle( UndoableRequest request, Collection<String> keys );
   }

   private static class NewKeyDetector implements ChangeDetector {
      @Override
      public boolean canHandle( UndoableRequest request ) {
         return request instanceof NewKey.Request;
      }

      @Override
      public void handle( UndoableRequest request, Collection<String> keys ) {
         NewKey.Request r = (NewKey.Request)request;
         if ( r.undo ) {
            keys.remove(r.key);
         } else {
            keys.add(r.key);
         }
      }
   }

   private static class DeleteKeyDetector implements ChangeDetector {
      @Override
      public boolean canHandle( UndoableRequest request ) {
         return request instanceof DeleteKey.Request;
      }

      @Override
      public void handle( UndoableRequest request, Collection<String> keys ) {
         DeleteKey.Request r = (DeleteKey.Request)request;
         if ( r.undo ) {
            keys.add(r.key);
         } else {
            keys.remove(r.key);
         }
      }
   }

   private static class RenameKeyDetector implements ChangeDetector {
      @Override
      public boolean canHandle( UndoableRequest request ) {
         return request instanceof RenameKey.Request;
      }

      @Override
      public void handle( UndoableRequest request, Collection<String> keys ) {
         RenameKey.Request r = (RenameKey.Request)request;
         if ( r.undo ) {
            keys.remove(r.newKey);
            keys.add(r.key);
         } else {
            keys.remove(r.key);
            keys.add(r.newKey);
         }
      }
   }

   private static class DuplicateKeyDetector implements ChangeDetector {
      @Override
      public boolean canHandle( UndoableRequest request ) {
         return request instanceof DuplicateKey.Request;
      }

      @Override
      public void handle( UndoableRequest request, Collection<String> keys ) {
         DuplicateKey.Request r = (DuplicateKey.Request)request;
         if ( r.undo ) {
            keys.remove(r.newKey);
         } else {
            keys.add(r.newKey);
         }
      }
   }
}