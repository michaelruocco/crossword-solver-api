package uk.co.mruoc.cws.hackathon;

import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Answers;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.Puzzle;

@RequiredArgsConstructor
public class AttemptAnswerReplacer {

  private final Map<String, String> replacementAnswers;

  public AttemptAnswerReplacer() {
    this(buildReplacementAnswers());
  }

  public Attempt replaceAnswersIfRequired(Attempt attempt) {
    var puzzle = attempt.puzzle();
    var answers = attempt.getConfirmedAnswers();
    var replacedAnswers = replaceAnswersIfRequired(puzzle, answers);
    return attempt.withAnswers(replacedAnswers);
  }

  private Answers replaceAnswersIfRequired(Puzzle puzzle, Answers answers) {
    return new Answers(
        answers.stream().map(answer -> replaceAnswerIfRequired(puzzle, answer)).toList());
  }

  private Answer replaceAnswerIfRequired(Puzzle puzzle, Answer answer) {
    var key = toKey(puzzle, answer);
    return Optional.ofNullable(replacementAnswers.get(key)).map(answer::withValue).orElse(answer);
  }

  private static String toKey(Puzzle puzzle, Answer answer) {
    return String.format("%s-%s-%s", puzzle.getNameAndFormat(), answer.id(), answer.value());
  }

  private static Map<String, String> buildReplacementAnswers() {
    return Map.of(
        "puzzle3.png-6D-HYGIENE",
        "HYGEINE",
        "puzzle9.jpg-27D-INOCULATED",
        "INOCULATE",
        "puzzle9.jpg-24D-ESMERALDA",
        "ESMERELDA",
        "puzzle9.jpg-41D-RECITAL",
        "REVIVAL",
        "puzzle14.jpg-25D-ADVOCAAT",
        "ADVOCAT");
  }
}
