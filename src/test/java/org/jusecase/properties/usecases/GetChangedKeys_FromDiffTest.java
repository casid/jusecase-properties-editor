package org.jusecase.properties.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jusecase.Builders.a;
import static org.jusecase.Builders.list;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.jusecase.properties.gateways.PropertiesGatewayTrainer;
import org.jusecase.properties.gateways.UndoableRequestGateway;
import org.jusecase.properties.plugins.diff.Diff;
import org.jusecase.properties.plugins.diff.DiffPluginTrainer;


@SuppressWarnings("SameParameterValue")
public class GetChangedKeys_FromDiffTest extends UsecaseTest<GetChangedKeys.Request, GetChangedKeys.Response> {

   UndoableRequestGateway   undoableRequestGateway   = new UndoableRequestGateway();
   DiffPluginTrainer        diffPluginTrainer        = new DiffPluginTrainer();
   PropertiesGatewayTrainer propertiesGatewayTrainer = new PropertiesGatewayTrainer();


   @Before
   public void before() {
      usecase = new GetChangedKeys(undoableRequestGateway, diffPluginTrainer, propertiesGatewayTrainer);
      propertiesGatewayTrainer.givenFiles(a(list(Paths.get("/dev/ws/project/src/main/resources/resources.properties"))));
      diffPluginTrainer.givenRepositoryDirectory(Paths.get("/dev/ws/project"));
   }

   @Test
   public void lineAdded() {
      Path file = Paths.get("src/main/resources/resources.properties");
      Diff diff = new Diff(file);
      diff.addedLines.add("key=value");
      diffPluginTrainer.givenChangedFiles(a(list(diff)));

      whenRequestIsExecuted();

      thenKeysAre("key");
   }

   @Test
   public void lineAdded_spaces() {
      Path file = Paths.get("src/main/resources/resources.properties");
      Diff diff = new Diff(file);
      diff.addedLines.add("key = value");
      diffPluginTrainer.givenChangedFiles(a(list(diff)));

      whenRequestIsExecuted();

      thenKeysAre("key");
   }

   @Test
   public void lineDeleted() {
      Path file = Paths.get("src/main/resources/resources.properties");
      Diff diff = new Diff(file);
      diff.deletedLines.add("key=value");
      diffPluginTrainer.givenChangedFiles(a(list(diff)));

      whenRequestIsExecuted();

      thenKeysAre();
   }

   @Test
   public void lineMoved() {
      Path file = Paths.get("src/main/resources/resources.properties");
      Diff diff = new Diff(file);
      diff.deletedLines.add("key=value");
      diff.addedLines.add("key=value");
      diffPluginTrainer.givenChangedFiles(a(list(diff)));

      whenRequestIsExecuted();

      thenKeysAre();
   }

   private void thenKeysAre( String... keys ) {
      assertThat(response.keys).containsExactly(keys);
   }
}