package org.jusecase.properties.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jusecase.Builders.a;
import static org.jusecase.Builders.list;
import static org.jusecase.properties.entities.Builders.property;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.jusecase.UsecaseExecutor;
import org.jusecase.properties.gateways.PropertiesGatewayTrainer;
import org.jusecase.properties.gateways.UndoableRequestGateway;
import org.jusecase.properties.plugins.diff.Diff;
import org.jusecase.properties.plugins.diff.DiffPluginTrainer;


@SuppressWarnings("SameParameterValue")
public class GetChangedKeys_FromChangesTest extends UsecaseTest<GetChangedKeys.Request, GetChangedKeys.Response> {

   UndoableRequestGateway   undoableRequestGateway   = new UndoableRequestGateway();
   Undo                     undo                     = new Undo(new UsecaseExecutor() {

      @Override
      public <Request, Response> Response execute( Request request ) {
         return null;
      }

      @Override
      public <Request> void executeVoid(Request request) {

      }
   }, undoableRequestGateway);
   DiffPluginTrainer        diffPluginTrainer        = new DiffPluginTrainer();
   PropertiesGatewayTrainer propertiesGatewayTrainer = new PropertiesGatewayTrainer();


   @Before
   public void before() {
      usecase = new GetChangedKeys(undoableRequestGateway, diffPluginTrainer, propertiesGatewayTrainer);
      diffPluginTrainer.givenDiffNotSupported();
      propertiesGatewayTrainer.givenFiles(a(list(Paths.get("/resources.properties"))));
   }

   @Test
   public void noChanges() {
      whenRequestIsExecuted();
      thenKeysAre();
   }

   @Test
   public void noDuplicates() {
      givenKeyWasDuplicated("key", "key2");
      givenKeyWasRenamed("key2", "key22");
      givenUndoIsCalled();

      whenRequestIsExecuted();

      thenKeysAre("key2");
   }

   @Test
   public void oneAdded() {
      givenKeyWasAdded("key1");
      whenRequestIsExecuted();
      thenKeysAre("key1");
   }

   @Test
   public void oneAddedAndDuplicated() {
      givenKeyWasAdded("key1");
      givenKeyWasDuplicated("key1", "bazinga!");

      whenRequestIsExecuted();

      thenKeysAre("bazinga!", "key1");
   }

   @Test
   public void oneAddedAndDuplicatedAndUndone() {
      givenKeyWasAdded("key1");
      givenKeyWasDuplicated("key1", "bazinga!");
      givenUndoIsCalled();

      whenRequestIsExecuted();

      thenKeysAre("key1");
   }

   @Test
   public void oneAddedAndRemoved() {
      givenKeyWasAdded("key1");
      givenKeyWasRemoved("key1");

      whenRequestIsExecuted();

      thenKeysAre();
   }

   @Test
   public void oneAddedAndRenamed() {
      givenKeyWasAdded("key1");
      givenKeyWasRenamed("key1", "bazinga!");

      whenRequestIsExecuted();

      thenKeysAre("bazinga!");
   }

   @Test
   public void oneAddedAndRenamedAndUndone() {
      givenKeyWasAdded("key1");
      givenKeyWasRenamed("key1", "bazinga!");
      givenUndoIsCalled();

      whenRequestIsExecuted();

      thenKeysAre("key1");
   }

   @Test
   public void oneAddedAndUndone() {
      givenKeyWasAdded("key1");
      givenUndoIsCalled();

      whenRequestIsExecuted();

      thenKeysAre();
   }

   @Test
   public void oneRemoved_noChange() {
      givenKeyWasRemoved("key1");
      whenRequestIsExecuted();
      thenKeysAre();
   }

   @Test
   public void oneRemoved_undo_noChange() {
      givenKeyWasRemoved("key1");
      givenUndoIsCalled();
      whenRequestIsExecuted();
      thenKeysAre();
   }

   @Test
   public void oneValueEdited() {
      givenValueWasEdited("key", "a");
      givenValueWasEdited("key", "a new");
      givenValueWasEdited("key", "a new value");

      whenRequestIsExecuted();

      thenKeysAre("key");
   }

   @Test
   public void oneValueEdited_undo() {
      givenValueWasEdited("key", "a");
      givenValueWasEdited("key", "a new");
      givenValueWasEdited("key", "a new value");
      givenUndoIsCalled();

      whenRequestIsExecuted();

      thenKeysAre("key");
   }

   @Test
   public void oneValueEdited_undoAll() {
      givenValueWasEdited("key", "a");
      givenValueWasEdited("key", "a new");
      givenValueWasEdited("key", "a new value");
      givenUndoIsCalled();
      givenUndoIsCalled();
      givenUndoIsCalled();

      whenRequestIsExecuted();

      thenKeysAre();
   }

   @Test
   public void twoAdded() {
      givenKeyWasAdded("key1");
      givenKeyWasAdded("key2");

      whenRequestIsExecuted();

      thenKeysAre("key1", "key2");
   }

   @Test
   public void twoAddedAndRemovedAndOneUndone() {
      givenKeyWasAdded("key1");
      givenKeyWasAdded("key2");
      givenKeyWasRemoved("key2");
      givenKeyWasRemoved("key1");
      givenUndoIsCalled();

      whenRequestIsExecuted();

      thenKeysAre("key1");
   }

   @Test
   public void fromChangesAndFromDiff() {
      givenKeyWasAdded("key-from-changes");
      givenKeyFromDiff("key-from-diff");

      whenRequestIsExecuted();

      thenKeysAre("key-from-changes", "key-from-diff");
   }

   private void givenKeyFromDiff( String key ) {
      diffPluginTrainer.givenRepositoryDirectory(Paths.get("/dev/ws/project"));
      Path file = Paths.get("src/main/resources/resources.properties");
      Diff diff = new Diff(file);
      diff.addedLines.add(key + "=value");
      diffPluginTrainer.givenChangedFiles(a(list(diff)));
   }

   private void givenKeyWasAdded( String key ) {
      NewKey.Request request = new NewKey.Request();
      request.key = key;
      undoableRequestGateway.add(request);
   }

   private void givenKeyWasDuplicated( String key, String newKey ) {
      DuplicateKey.Request request = new DuplicateKey.Request();
      request.key = key;
      request.newKey = newKey;
      undoableRequestGateway.add(request);
   }

   private void givenKeyWasRemoved( String key ) {
      DeleteKey.Request request = new DeleteKey.Request();
      request.key = key;
      undoableRequestGateway.add(request);
   }

   private void givenKeyWasRenamed( String key, String newKey ) {
      RenameKey.Request request = new RenameKey.Request();
      request.key = key;
      request.newKey = newKey;
      undoableRequestGateway.add(request);
   }

   private void givenUndoIsCalled() {
      undo.execute(new Undo.Request());
   }

   private void givenValueWasEdited( String key, String value ) {
      EditValue.Request request = new EditValue.Request();
      request.property = a(property().withKey(key));
      request.oldValue = "value";
      request.value = value;
      undoableRequestGateway.add(request);
   }

   private void thenKeysAre( String... keys ) {
      assertThat(response.keys).containsExactly(keys);
   }
}