package uk.co.mruoc.cws.solver.wordplays;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Id;

public class WordPlaysAnswerFinderIT {

  @Test
  void shouldFindAnswerToClue() {
    var driver = buildWebDriver();
    try {
      var finder = new WordPlaysAnswerFinder(driver);
      var clue =
          Clue.builder()
              .id(Id.across(11))
              .text("Small trumpet (5)")
              .lengths(List.of(5))
              .pattern("??G??")
              .build();

      var answer = finder.findAnswer(clue);

      assertThat(answer.value()).isEqualTo("BUGLE");
      assertThat(answer.confidenceScore()).isEqualTo(100);
      assertThat(answer.confirmed()).isFalse();
    } finally {
      driver.close();
    }
  }

  @Test
  void shouldFindAnswerToTrickyClue() {
    var driver = buildWebDriver();
    try {
      var finder = new WordPlaysAnswerFinder(driver);
      var clue =
          Clue.builder().id(Id.down(1)).text("Ram (3)").lengths(List.of(3)).pattern("T?P").build();

      var answer = finder.findAnswer(clue);

      assertThat(answer.value()).isEqualTo("TUP");
      assertThat(answer.confidenceScore()).isEqualTo(100);
      assertThat(answer.confirmed()).isFalse();
    } finally {
      driver.close();
    }
  }

  private static WebDriver buildWebDriver() {
    var options = new ChromeOptions();
    options.addArguments("--headless=new");
    return new ChromeDriver(options);
  }
}
