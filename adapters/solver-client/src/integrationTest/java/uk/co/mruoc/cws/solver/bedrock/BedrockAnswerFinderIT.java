package uk.co.mruoc.cws.solver.bedrock;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.mruoc.cws.solver.bedrock.BedrockRuntimeClientFactory.buildClient;

import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Id;
import uk.co.mruoc.cws.usecase.AnswerFinder;
import uk.co.mruoc.junit.EnvVarsPresent;

@EnvVarsPresent(values = {"AWS_ACCESS_KEY_ID", "AWS_SECRET_ACCESS_KEY"})
@Slf4j
public class BedrockAnswerFinderIT {

  private final AnswerFinder finder = new BedrockAnswerFinder(buildClient());

  @ParameterizedTest
  @MethodSource("easyCluesAndCorrectAnswers")
  void shouldFindAnswerToEasyClues(Clue clue, String correctAnswer) {
    var answer = finder.findAnswer(clue);

    log.info(answer.toString());
    assertThat(answer.value()).isEqualTo(correctAnswer);
  }

  @ParameterizedTest
  @MethodSource("easyCluesAndCorrectAnswers")
  void shouldFindCandidateAnswersForEasyClues(Clue clue, String correctAnswer) {
    var candidates = finder.findCandidates(clue, 5);

    log.info(candidates.asString());
    assertThat(candidates.valuesAsString()).contains(correctAnswer);
  }

  @Disabled
  @ParameterizedTest
  @MethodSource("trickyCluesAndCorrectAnswers")
  void shouldFindAnswerToTrickyClues(Clue clue, String correctAnswer) {
    var answer = finder.findAnswer(clue);

    log.info(answer.toString());
    assertThat(answer.value()).isEqualTo(correctAnswer);
  }

  @Disabled
  @ParameterizedTest
  @MethodSource("trickyCluesAndCorrectAnswers")
  void shouldFindCandidateAnswersForTrickyClues(Clue clue, String correctAnswer) {
    var candidates = finder.findCandidates(clue, 5);

    log.info(candidates.asString());
    assertThat(candidates.valuesAsString()).contains(correctAnswer);
  }

  private static Stream<Arguments> easyCluesAndCorrectAnswers() {
    var clue1 =
        Clue.builder()
            .id(new Id("8D"))
            .text("Aubergine and tomato dish (11)")
            .lengths(List.of(11))
            .pattern("???????????")
            .build();
    return Stream.of(Arguments.of(clue1, "RATATOUILLE"));
  }

  private static Stream<Arguments> trickyCluesAndCorrectAnswers() {
    var clue1 =
        Clue.builder()
            .id(new Id("27D"))
            .text("Pilot, - - - Johnson (3)")
            .lengths(List.of(3))
            .pattern("A??")
            .build();
    var clue2 =
        Clue.builder()
            .id(new Id("28D"))
            .text("Remnant (6)")
            .lengths(List.of(6))
            .pattern("???C?T")
            .build();
    var clue3 =
        Clue.builder().id(new Id("1D")).text("Ram (3)").lengths(List.of(3)).pattern("T?P").build();
    return Stream.of(
        Arguments.of(clue1, "AMY"), Arguments.of(clue2, "OFFCUT"), Arguments.of(clue3, "TUP"));
  }
}
