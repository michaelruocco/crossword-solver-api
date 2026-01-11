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
import uk.co.mruoc.cws.entity.Puzzle;

@RequiredArgsConstructor
public class HackathonSolveAttemptFactory {

  private final ObjectMapper mapper;
  private final String teamName;

  public HackathonSolveAttemptFactory(ObjectMapper mapper) {
    this(mapper,"Michael Ruocco");
  }

  public String toHackathonAttemptJson(Attempt attempt) {
    var hackathonAttempt = toHackathonAttempt(attempt);
    return mapper.writeValueAsString(hackathonAttempt);
  }

  public HackathonSolveAttempt toHackathonAttempt(Attempt attempt) {
    return HackathonSolveAttempt.builder()
        .imageName(toImageName(attempt.puzzle()))
        .teamName(teamName)
        .down(toAnswers(attempt, DOWN))
        .across(toAnswers(attempt, ACROSS))
        .build();
  }

  private static String toImageName(Puzzle puzzle) {
    return String.format("%s.%s", puzzle.getName(), puzzle.getFormat());
  }

  private static Map<String, String> toAnswers(Attempt attempt, Direction direction) {
    return attempt.getConfirmedAnswers().byDirection(direction).sortByNumericId().stream()
        .collect(
            Collectors.toMap(
                a -> Integer.toString(a.numericId()),
                a -> a.value().toLowerCase(),
                (x, y) -> y,
                LinkedHashMap::new));
  }
}
