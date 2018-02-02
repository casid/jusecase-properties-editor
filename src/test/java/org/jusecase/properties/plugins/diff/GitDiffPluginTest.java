package org.jusecase.properties.plugins.diff;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jusecase.Builders.a;
import static org.jusecase.Builders.an;
import static org.jusecase.Builders.inputStream;
import static org.jusecase.Builders.list;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;


public class GitDiffPluginTest {

   GitDiffPlugin plugin = new GitDiffPlugin();

   @Ignore // Manual test
   @Test
   public void testChangedFiles() {
      List<Diff> changedFiles = plugin.getChangedFiles( //
            Paths.get("/Users/andreas/dev/ws/c24-frontend"), //
            a(list(Paths.get("src/main/resources/c24/resources.properties"), Paths.get("src/main/resources/c24/resources_de.properties")))
      );

      for ( Diff changedFile : changedFiles ) {
         System.out.println(changedFile.file);
         System.out.println(changedFile.addedLines);
         System.out.println(changedFile.deletedLines);
      }
   }

   @Ignore // Manual test
   @Test
   public void testGetRepositoryDirectory() {
      System.out.println(plugin.getRepositoryDirectory(Paths.get("/Users/andreas/dev/ws/c24-frontend/src/main/resources/c24/resources_de.properties")));
   }

   @Test
   public void testExtractChangedFiles() throws IOException {
      InputStream inputStream = givenDiffWithDuplicatedKey();

      List<Diff> diffs = plugin.extractChangedFiles(inputStream);

      assertThat(diffs).hasSize(22);
      assertThat(diffs.get(0).addedLines).hasSize(1);
      assertThat(diffs.get(0).addedLines.get(0)).isEqualTo("about-us.about.button2=More about us");
      assertThat(diffs.get(0).deletedLines).isEmpty();
   }

   private InputStream givenDiffWithDuplicatedKey() {
      return an(inputStream().withString("diff --git a/src/main/resources/c24/resources.properties b/src/main/resources/c24/resources.properties\n"
               + "index 8e3b6f0fbd..7dea046d03 100644\n" + "--- a/src/main/resources/c24/resources.properties\n"
               + "+++ b/src/main/resources/c24/resources.properties\n" + "@@ -14,0 +15 @@ about-us.about.button=More about us\n"
               + "+about-us.about.button2=More about us\n"
               + "diff --git a/src/main/resources/c24/resources_cz.properties b/src/main/resources/c24/resources_cz.properties\n"
               + "index 1a38556389..f095ca9306 100644\n" + "--- a/src/main/resources/c24/resources_cz.properties\n"
               + "+++ b/src/main/resources/c24/resources_cz.properties\n" + "@@ -13,0 +14 @@ about-us.about.button=V\\u00EDce o n\\u00E1s\n"
               + "+about-us.about.button2=V\\u00EDce o n\\u00E1s\n"
               + "diff --git a/src/main/resources/c24/resources_da.properties b/src/main/resources/c24/resources_da.properties\n"
               + "index c620f27479..22eca5b0c8 100644\n" + "--- a/src/main/resources/c24/resources_da.properties\n"
               + "+++ b/src/main/resources/c24/resources_da.properties\n" + "@@ -14,0 +15 @@ about-us.about.button=Mere om os\n"
               + "+about-us.about.button2=Mere om os\n"
               + "diff --git a/src/main/resources/c24/resources_de.properties b/src/main/resources/c24/resources_de.properties\n"
               + "index aa6f9f55a5..5c507ad0e3 100644\n" + "--- a/src/main/resources/c24/resources_de.properties\n"
               + "+++ b/src/main/resources/c24/resources_de.properties\n" + "@@ -14,0 +15 @@ about-us.about.button=Mehr \\u00FCber uns\n"
               + "+about-us.about.button2=Mehr \\u00FCber uns\n"
               + "diff --git a/src/main/resources/c24/resources_el.properties b/src/main/resources/c24/resources_el.properties\n"
               + "index 80fbdd3667..821b0b5dd2 100644\n" + "--- a/src/main/resources/c24/resources_el.properties\n"
               + "+++ b/src/main/resources/c24/resources_el.properties\n"
               + "@@ -14,0 +15 @@ about-us.about.button=\\u03A0\\u03B5\\u03C1\\u03B9\\u03C3\\u03C3\\u03CC\\u03C4\\u03B5\\u03\n"
               + "+about-us.about.button2=\\u03A0\\u03B5\\u03C1\\u03B9\\u03C3\\u03C3\\u03CC\\u03C4\\u03B5\\u03C1\\u03B1 \\u03C3\\u03C7\\u03B5\\u03C4\\u03B9\\u03BA\\u03AC \\u03BC\\u03B5 \\u03B5\\u03BC\\u03AC\\u03C2\n"
               + "diff --git a/src/main/resources/c24/resources_es.properties b/src/main/resources/c24/resources_es.properties\n"
               + "index 6d511b3937..20fe2a61cb 100644\n" + "--- a/src/main/resources/c24/resources_es.properties\n"
               + "+++ b/src/main/resources/c24/resources_es.properties\n" + "@@ -14,0 +15 @@ about-us.about.button=M\\u00E1s sobre nosotros\n"
               + "+about-us.about.button2=M\\u00E1s sobre nosotros\n"
               + "diff --git a/src/main/resources/c24/resources_fr.properties b/src/main/resources/c24/resources_fr.properties\n"
               + "index 0457c365ec..9f2eb085bd 100644\n" + "--- a/src/main/resources/c24/resources_fr.properties\n"
               + "+++ b/src/main/resources/c24/resources_fr.properties\n" + "@@ -14,0 +15 @@ about-us.about.button=En savoir plus\n"
               + "+about-us.about.button2=En savoir plus\n"
               + "diff --git a/src/main/resources/c24/resources_hr.properties b/src/main/resources/c24/resources_hr.properties\n"
               + "index 30e0d7d632..541e773ac3 100644\n" + "--- a/src/main/resources/c24/resources_hr.properties\n"
               + "+++ b/src/main/resources/c24/resources_hr.properties\n" + "@@ -14,0 +15 @@ about-us.about.button=Vi\\u0161e o nama\n"
               + "+about-us.about.button2=Vi\\u0161e o nama\n"
               + "diff --git a/src/main/resources/c24/resources_hu.properties b/src/main/resources/c24/resources_hu.properties\n"
               + "index 79fe297446..e2404f71ab 100644\n" + "--- a/src/main/resources/c24/resources_hu.properties\n"
               + "+++ b/src/main/resources/c24/resources_hu.properties\n"
               + "@@ -14,0 +15 @@ about-us.about.button=Tov\\u00E1bbi inform\\u00E1ci\\u00F3k r\\u00F3lunk\n"
               + "+about-us.about.button2=Tov\\u00E1bbi inform\\u00E1ci\\u00F3k r\\u00F3lunk\n"
               + "diff --git a/src/main/resources/c24/resources_it.properties b/src/main/resources/c24/resources_it.properties\n"
               + "index 6f7d8db768..9e971ab40b 100644\n" + "--- a/src/main/resources/c24/resources_it.properties\n"
               + "+++ b/src/main/resources/c24/resources_it.properties\n" + "@@ -14,0 +15 @@ about-us.about.button=Per saperne di pi\\u00F9\n"
               + "+about-us.about.button2=Per saperne di pi\\u00F9\n"
               + "diff --git a/src/main/resources/c24/resources_jp.properties b/src/main/resources/c24/resources_jp.properties\n"
               + "index ce7a235fa8..b302c542e3 100644\n" + "--- a/src/main/resources/c24/resources_jp.properties\n"
               + "+++ b/src/main/resources/c24/resources_jp.properties\n" + "@@ -14,0 +15 @@ about-us.about.button=\\u5F53\\u793E\\u306E\\u8A73\\u7D30\n"
               + "+about-us.about.button2=\\u5F53\\u793E\\u306E\\u8A73\\u7D30\n"
               + "diff --git a/src/main/resources/c24/resources_ko.properties b/src/main/resources/c24/resources_ko.properties\n"
               + "index a66c8e96e2..67cd5cceeb 100644\n" + "--- a/src/main/resources/c24/resources_ko.properties\n"
               + "+++ b/src/main/resources/c24/resources_ko.properties\n"
               + "@@ -14,0 +15 @@ about-us.about.button=\\uC790\\uC138\\uD55C \\uD68C\\uC0AC \\uC18C\\uAC1C\n"
               + "+about-us.about.button2=\\uC790\\uC138\\uD55C \\uD68C\\uC0AC \\uC18C\\uAC1C\n"
               + "diff --git a/src/main/resources/c24/resources_nb.properties b/src/main/resources/c24/resources_nb.properties\n"
               + "index 1ed661d598..1d47900868 100644\n" + "--- a/src/main/resources/c24/resources_nb.properties\n"
               + "+++ b/src/main/resources/c24/resources_nb.properties\n" + "@@ -14,0 +15 @@ about-us.about.button=Mer om oss\n"
               + "+about-us.about.button2=Mer om oss\n"
               + "diff --git a/src/main/resources/c24/resources_nl.properties b/src/main/resources/c24/resources_nl.properties\n"
               + "index b9053f0467..40ed990c3a 100644\n" + "--- a/src/main/resources/c24/resources_nl.properties\n"
               + "+++ b/src/main/resources/c24/resources_nl.properties\n" + "@@ -14,0 +15 @@ about-us.about.button=Meer over ons\n"
               + "+about-us.about.button2=Meer over ons\n"
               + "diff --git a/src/main/resources/c24/resources_pl.properties b/src/main/resources/c24/resources_pl.properties\n"
               + "index 268f06228d..3fedc36b1c 100644\n" + "--- a/src/main/resources/c24/resources_pl.properties\n"
               + "+++ b/src/main/resources/c24/resources_pl.properties\n" + "@@ -14,0 +15 @@ about-us.about.button=Wi\\u0119cej o nas\n"
               + "+about-us.about.button2=Wi\\u0119cej o nas\n"
               + "diff --git a/src/main/resources/c24/resources_pt.properties b/src/main/resources/c24/resources_pt.properties\n"
               + "index da202fd66e..517071d1b9 100644\n" + "--- a/src/main/resources/c24/resources_pt.properties\n"
               + "+++ b/src/main/resources/c24/resources_pt.properties\n" + "@@ -14,0 +15 @@ about-us.about.button=Mais sobre n\\u00F3s\n"
               + "+about-us.about.button2=Mais sobre n\\u00F3s\n"
               + "diff --git a/src/main/resources/c24/resources_ro.properties b/src/main/resources/c24/resources_ro.properties\n"
               + "index 308de55d03..44094b025b 100644\n" + "--- a/src/main/resources/c24/resources_ro.properties\n"
               + "+++ b/src/main/resources/c24/resources_ro.properties\n" + "@@ -14,0 +15 @@ about-us.about.button=Mai multe despre noi\n"
               + "+about-us.about.button2=Mai multe despre noi\n"
               + "diff --git a/src/main/resources/c24/resources_ru.properties b/src/main/resources/c24/resources_ru.properties\n"
               + "index b17423aa72..492a0cd030 100644\n" + "--- a/src/main/resources/c24/resources_ru.properties\n"
               + "+++ b/src/main/resources/c24/resources_ru.properties\n"
               + "@@ -14,0 +15 @@ about-us.about.button=\\u0411\\u043E\\u043B\\u044C\\u0448\\u0435 \\u0438\\u043D\\u0444\\u0\n"
               + "+about-us.about.button2=\\u0411\\u043E\\u043B\\u044C\\u0448\\u0435 \\u0438\\u043D\\u0444\\u043E\\u0440\\u043C\\u0430\\u0446\\u0438\\u0438 \\u043E \\u043D\\u0430\\u0441\n"
               + "diff --git a/src/main/resources/c24/resources_se.properties b/src/main/resources/c24/resources_se.properties\n"
               + "index 9e9bb8d455..4025d4e63a 100644\n" + "--- a/src/main/resources/c24/resources_se.properties\n"
               + "+++ b/src/main/resources/c24/resources_se.properties\n" + "@@ -14,0 +15 @@ about-us.about.button=Mer om oss\n"
               + "+about-us.about.button2=Mer om oss\n"
               + "diff --git a/src/main/resources/c24/resources_tr.properties b/src/main/resources/c24/resources_tr.properties\n"
               + "index 1d70c673db..5f51d98130 100644\n" + "--- a/src/main/resources/c24/resources_tr.properties\n"
               + "+++ b/src/main/resources/c24/resources_tr.properties\n" + "@@ -14,0 +15 @@ about-us.about.button=Hakk\\u0131m\\u0131zda daha fazla bilgi\n"
               + "+about-us.about.button2=Hakk\\u0131m\\u0131zda daha fazla bilgi\n"
               + "diff --git a/src/main/resources/c24/resources_zh.properties b/src/main/resources/c24/resources_zh.properties\n"
               + "index 4923970c63..6f15186700 100644\n" + "--- a/src/main/resources/c24/resources_zh.properties\n"
               + "+++ b/src/main/resources/c24/resources_zh.properties\n" + "@@ -14,0 +15 @@ about-us.about.button=\\u66F4\\u591A\\u95DC\\u65BC\\u6211\\u5011\n"
               + "+about-us.about.button2=\\u66F4\\u591A\\u95DC\\u65BC\\u6211\\u5011\n"
               + "diff --git a/src/main/resources/c24/resources_zh_CN.properties b/src/main/resources/c24/resources_zh_CN.properties\n"
               + "index d58a14cdc0..bc262f816d 100644\n" + "--- a/src/main/resources/c24/resources_zh_CN.properties\n"
               + "+++ b/src/main/resources/c24/resources_zh_CN.properties\n"
               + "@@ -14,0 +15 @@ about-us.about.button=\\u5173\\u4E8E\\u6211\\u4EEC\\u7684\\u66F4\\u591A\\u4FE1\\u606F\n"
               + "+about-us.about.button2=\\u5173\\u4E8E\\u6211\\u4EEC\\u7684\\u66F4\\u591A\\u4FE1\\u606F"));
   }
}
