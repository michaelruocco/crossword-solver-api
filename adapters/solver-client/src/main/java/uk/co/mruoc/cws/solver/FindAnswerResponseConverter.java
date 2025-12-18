package uk.co.mruoc.cws.solver;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Answers;
import uk.co.mruoc.cws.entity.Candidates;
import uk.co.mruoc.cws.entity.Id;

@Slf4j
public class FindAnswerResponseConverter {

  public Answers toAnswers(String answersAndScores) {
    log.debug("got answers and scores {}", answersAndScores);
    return new Answers(toCollection(answersAndScores));
  }

  public Answer toAnswer(String answerAndScore) {
    log.debug("got answer and score {}", answerAndScore);
    return convert(answerAndScore);
  }

  public Candidates toCandidates(String answersAndScores) {
    log.debug("got candidate answers and scores {}", answersAndScores);
    return new Candidates(toCollection(answersAndScores));
  }

  private Collection<Answer> toCollection(String answersAndScore) {
    return toCorrectlyFormattedLines(answersAndScore).map(this::toAnswer).toList();
  }

  private Stream<String> toCorrectlyFormattedLines(String input) {
    return Arrays.stream(input.split(System.lineSeparator()))
        .filter(StringUtils::isNotEmpty)
        .filter(line -> StringUtils.countMatches(line, '|') == 2);
  }

  private Answer convert(String line) {
    var parts = line.split("\\|");
    var id = new Id(parts[0]);
    try {
      return Answer.builder()
          .id(id)
          .value(parts[1].replaceAll("\\s+", "").toUpperCase())
          .confidenceScore(Integer.parseInt(parts[2].trim()))
          .confirmed(false)
          .build();
    } catch (NumberFormatException e) {
      log.debug(e.getMessage(), e);
      return Answer.noMatchBuilder().id(id).build();
    }
  }
}
