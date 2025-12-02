package uk.co.mruoc.cws.solver.wordplays;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Answers;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;
import uk.co.mruoc.cws.entity.ValidAnswerPredicate;
import uk.co.mruoc.cws.usecase.AnswerFinder;
import uk.co.mruoc.cws.usecase.DefaultWaiter;
import uk.co.mruoc.cws.usecase.attempt.Waiter;

@RequiredArgsConstructor
@Slf4j
public class WordPlaysAnswerFinder implements AnswerFinder {

  private final WebDriver driver;
  private final Waiter waiter;

  public WordPlaysAnswerFinder(WebDriver driver) {
    this(driver, new DefaultWaiter());
  }

  @Override
  public Answers findAnswers(Clues clues) {
    var answers = new ArrayList<Answer>();
    for (var clue : clues) {
      answers.add(findAnswer(clue));
      waiter.wait(Duration.ofSeconds(5));
    }
    return new Answers(answers);
  }

  @Override
  public Answer findAnswer(Clue clue) {
    var url = toUrl(clue);
    log.info("finding text {} with pattern {} using url {}", clue.text(), clue.pattern(), url);
    driver.get(url);
    var wait = new WebDriverWait(driver, Duration.ofSeconds(4));

    try {
      var consent = driver.findElement(By.xpath("//button[@aria-label='Consent']"));
      wait.until(d -> consent.isDisplayed());
      consent.click();
    } catch (NoSuchElementException e) {
      log.debug(e.getMessage(), e);
    }

    var wordLists = driver.findElement(By.id("wordlists"));
    return toAnswer(clue, wordLists);
  }

  private Answer toAnswer(Clue clue, WebElement words) {
    var allRows = words.findElements(By.tagName("tr"));
    var rowIndex = 0;
    var isValid = new ValidAnswerPredicate(clue);
    for (var row : allRows) {
      if (rowIndex > 1) {
        var cells = row.findElements(By.tagName("td"));
        if (cells.size() == 3) {
          var answer = toAnswer(clue, cells);
          if (isValid.test(answer)) {
            return answer;
          }
        }
      }
      rowIndex++;
    }
    return Answer.noMatch(clue);
  }

  private Answer toAnswer(Clue clue, List<WebElement> cells) {
    return Answer.builder()
        .id(clue.id())
        .value(cells.get(1).getText())
        .confidenceScore(toConfidenceScore(cells.get(0)))
        .confirmed(false)
        .build();
  }

  private int toConfidenceScore(WebElement cell) {
    var stars = cell.findElements(By.xpath("child::*[1]/child::*")).size();
    return (int) ((stars / 5d) * 100);
  }

  private String toUrl(Clue clue) {
    return String.format("https://www.wordplays.com/crossword-solver/%s", toUrlPath(clue));
  }

  private String toUrlPath(Clue clue) {
    return clue.text().replace(" ", "-");
  }
}
