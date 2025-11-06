package uk.co.mruoc.cws.solver.stub;

import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Id;

@RequiredArgsConstructor
public class FakeAnswers {

  private final Map<String, Answer> answers;

  public static void addAnswer(Map<String, Answer> map, String pattern, Answer answer) {
    var key = toKey(answer.id(), pattern);
    map.put(key, answer);
  }

  public Answer getAnswer(Clue clue) {
    var key = toKey(clue);
    return Optional.ofNullable(answers.get(key)).orElse(Answer.noMatch(clue));
  }

  private static String toKey(Clue clue) {
    return toKey(clue.id(), clue.pattern());
  }

  private static String toKey(Id id, String pattern) {
    return String.format("%s-%s", id.toString(), pattern);
  }
}
