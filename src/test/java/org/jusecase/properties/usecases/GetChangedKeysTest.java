package org.jusecase.properties.usecases;

import org.junit.Before;
import org.junit.Test;
import org.jusecase.UsecaseExecutor;
import org.jusecase.UsecaseTest;
import org.jusecase.properties.gateways.UndoableRequestGateway;

import static org.assertj.core.api.Assertions.assertThat;


@SuppressWarnings("SameParameterValue")
public class GetChangedKeysTest extends UsecaseTest<GetChangedKeys.Request, GetChangedKeys.Response> {

   UndoableRequestGateway undoableRequestGateway = new UndoableRequestGateway();
   Undo undo = new Undo(new UsecaseExecutor() {
      @Override
      public <Request, Response> Response execute(Request request) {
         return null;
      }
   }, undoableRequestGateway);

   @Before
   public void before() {
      usecase = new GetChangedKeys(undoableRequestGateway);
   }

   @Test
   public void noChanges() {
      whenRequestIsExecuted();
      thenKeysAre();
   }

   @Test
   public void oneAdded() {
      givenKeyWasAdded("key1");
      whenRequestIsExecuted();
      thenKeysAre("key1");
   }

   @Test
   public void twoAdded() {
      givenKeyWasAdded("key1");
      givenKeyWasAdded("key2");

      whenRequestIsExecuted();

      thenKeysAre("key1", "key2");
   }

   @Test
   public void oneAddedAndUndone() {
      givenKeyWasAdded("key1");
      givenUndoIsCalled();

      whenRequestIsExecuted();

      thenKeysAre();
   }

   @Test
   public void oneAddedAndRemoved() {
      givenKeyWasAdded("key1");
      givenKeyWasRemoved("key1");

      whenRequestIsExecuted();

      thenKeysAre();
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
   public void noDuplicates() {
      givenKeyWasDuplicated("key", "key2");
      givenKeyWasRenamed("key2", "key22");
      givenUndoIsCalled();

      whenRequestIsExecuted();

      thenKeysAre("key2");
   }

   private void givenKeyWasAdded(String key) {
      NewKey.Request request = new NewKey.Request();
      request.key = key;
      undoableRequestGateway.add(request);
   }

   private void givenKeyWasRemoved(String key) {
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

   private void givenKeyWasDuplicated( String key, String newKey ) {
      DuplicateKey.Request request = new DuplicateKey.Request();
      request.key = key;
      request.newKey = newKey;
      undoableRequestGateway.add(request);
   }

   private void givenUndoIsCalled() {
      undo.execute(new Undo.Request());
   }

   private void thenKeysAre( String... keys ) {
      assertThat(response.keys).containsExactly(keys);
   }
}