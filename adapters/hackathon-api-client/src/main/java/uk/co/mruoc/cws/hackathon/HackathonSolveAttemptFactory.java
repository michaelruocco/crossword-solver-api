package uk.co.mruoc.cws.hackathon;

import static uk.co.mruoc.cws.entity.Direction.ACROSS;
import static uk.co.mruoc.cws.entity.Direction.DOWN;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.Direction;

@RequiredArgsConstructor
public class HackathonSolveAttemptFactory {

  private final ObjectMapper mapper;
  private final String teamName;
  private final AttemptAnswerReplacer answerReplacer;

  public HackathonSolveAttemptFactory(ObjectMapper mapper) {
    this(mapper, "Michael Ruocco", new AttemptAnswerReplacer());
  }

  public String toHackathonAttemptJson(Attempt attempt) {
    var replacedAttempt = answerReplacer.replaceAnswersIfRequired(attempt);
    var hackathonAttempt = toHackathonAttempt(replacedAttempt);
    return mapper.writeValueAsString(hackathonAttempt);
  }

  public HackathonSolveAttempt toHackathonAttempt(Attempt attempt) {
    return HackathonSolveAttempt.builder()
        .imageName(attempt.puzzle().getNameAndFormat())
        .teamName(teamName)
        .down(toAnswers(attempt, DOWN))
        .across(toAnswers(attempt, ACROSS))
        .build();
  }

  private static Map<String, String> toAnswers(Attempt attempt, Direction direction) {
    return attempt.getConfirmedAnswers().byDirection(direction).sortByNumericId().stream()
        .collect(
            Collectors.toMap(
                answer -> Integer.toString(answer.numericId()),
                answer -> attempt.getGridAnswerValue(answer.id()),
                (x, y) -> y,
                LinkedHashMap::new));
  }
}
