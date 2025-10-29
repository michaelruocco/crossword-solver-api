package uk.co.mruoc.cws.solver.wordplays;

import java.time.Duration;
import java.util.ArrayList;
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

    var pattern = driver.findElement(By.name("pattern"));
    wait.until(d -> pattern.isDisplayed());
    pattern.sendKeys(clue.pattern());

    driver.findElement(By.id("cwsfrm")).submit();
    var wordLists = driver.findElement(By.id("wordlists"));
    var bestWord = toBestAnswer(wordLists);
    if (bestWord.length() != clue.getTotalLength()) {
      log.debug("answer {} is not correct length {}, rejecting", bestWord, clue.getTotalLength());
      return Answer.noMatch(clue);
    }
    var confidenceScore = toConfidenceScore(wordLists);
    log.info("best word {} confidence score {}", bestWord, confidenceScore);
    return new Answer(clue.id(), bestWord, confidenceScore, false);
  }

  private String toBestAnswer(WebElement wordLists) {
    return wordLists.findElement(By.xpath("//tbody/tr[2]/td[2]/a")).getText();
  }

  private int toConfidenceScore(WebElement wordLists) {
    var stars = wordLists.findElements(By.xpath("//tbody/tr[2]/td[1]/child::*")).size();
    return (int) ((stars / 6d) * 100);
  }

  private String toUrl(Clue clue) {
    return String.format("https://www.wordplays.com/crossword-solver/%s", toUrlPath(clue));
  }

  private String toUrlPath(Clue clue) {
    return clue.text().replace(" ", "-");
  }
}
