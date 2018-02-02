package org.jusecase.properties.usecases;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jusecase.Usecase;
import org.jusecase.properties.entities.UndoableRequest;
import org.jusecase.properties.gateways.PropertiesGateway;
import org.jusecase.properties.gateways.UndoableRequestGateway;
import org.jusecase.properties.plugins.diff.Diff;
import org.jusecase.properties.plugins.diff.DiffException;
import org.jusecase.properties.plugins.diff.DiffPlugin;


@Singleton
public class GetChangedKeys implements Usecase<GetChangedKeys.Request, GetChangedKeys.Response> {

   private final UndoableRequestGateway undoableRequestGateway;
   private final DiffPlugin             diffPlugin;
   private final PropertiesGateway      propertiesGateway;
   private final List<ChangeDetector>   changeDetectors;


   @Inject
   public GetChangedKeys( UndoableRequestGateway undoableRequestGateway, DiffPlugin diffPlugin, PropertiesGateway propertiesGateway ) {
      this.undoableRequestGateway = undoableRequestGateway;
      this.diffPlugin = diffPlugin;
      this.propertiesGateway = propertiesGateway;

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

      try {
         response.keys = getChangedKeysFromDiff();
      }
      catch ( DiffException e ) {
         response.keys = getChangedKeysFromHistory();
      }

      return response;
   }

   private Set<String> getChangedKeysFromDiff() {
      Set<String> keys = new TreeSet<>();

      List<Path> propertyFiles = propertiesGateway.getFiles();
      if (propertyFiles != null && !propertyFiles.isEmpty()) {
         Path repository = diffPlugin.getRepositoryDirectory(propertyFiles.get(0));
         Set<Path> paths = propertyFiles.stream().map(repository::relativize).collect(Collectors.toSet());

         for ( Diff diff : diffPlugin.getChangedFiles(repository, paths) ) {
            for ( String addedLine : diff.addedLines ) {
               keys.add(addedLine.substring(0, addedLine.indexOf('=')).trim());
            }
         }
      }

      return keys;
   }

   private Set<String> getChangedKeysFromHistory() {
      Map<String, Integer> keys = new TreeMap<>();
      for ( UndoableRequest undoableRequest : undoableRequestGateway.getAll() ) {
         for ( ChangeDetector changeDetector : changeDetectors ) {
            if ( changeDetector.canHandle(undoableRequest) ) {
               changeDetector.handle(undoableRequest, keys);
               break;
            }
         }
      }

      return keys.keySet();
   }


   private interface ChangeDetector {

      default void add( Map<String, Integer> keys, String key ) {
         keys.compute(key, ( s, count ) -> {
            if ( count != null ) {
               return ++count;
            }
            return 1;
         });
      }

      boolean canHandle( UndoableRequest request );

      void handle( UndoableRequest request, Map<String, Integer> keys );

      default void remove( Map<String, Integer> keys, String key ) {
         keys.compute(key, ( s, count ) -> {
            if ( count != null ) {
               if ( --count > 0 ) {
                  return count;
               } else {
                  return null;
               }
            }
            return null;
         });
      }
   }


   public static class Request {
   }


   public static class Response {

      Collection<String> keys;
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
}