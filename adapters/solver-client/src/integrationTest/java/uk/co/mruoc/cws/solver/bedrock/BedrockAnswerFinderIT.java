package uk.co.mruoc.cws.solver.bedrock;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.mruoc.cws.solver.bedrock.BedrockRuntimeClientFactory.buildClient;

import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.ClueType;
import uk.co.mruoc.cws.entity.Id;
import uk.co.mruoc.cws.usecase.AnswerFinder;
import uk.co.mruoc.junit.EnvVarsPresent;

@EnvVarsPresent(values = {"AWS_ACCESS_KEY_ID", "AWS_SECRET_ACCESS_KEY"})
@Slf4j
public class BedrockAnswerFinderIT {

  private final AnswerFinder finder = new BedrockAnswerFinder(buildClient());

  @ParameterizedTest
  @MethodSource("easyCluesAndCorrectAnswers")
  void shouldFindCandidateAnswersForEasyClues(Clue clue, String correctAnswer) {
    var candidates = finder.findCandidates(clue, 5);

    log.info(candidates.asString());
    assertThat(candidates.valuesAsString()).contains(correctAnswer);
  }

  @ParameterizedTest
  @MethodSource("trickyStandardCluesAndCorrectAnswers")
  void shouldFindCandidateAnswersForTrickyStandardClues(Clue clue, String correctAnswer) {
    var candidates = finder.findCandidates(clue, 5);

    log.info(candidates.asString());
    assertThat(candidates.valuesAsString()).contains(correctAnswer);
  }

  @ParameterizedTest
  @MethodSource("crypticCluesAndCorrectAnswers")
  void shouldFindCandidateAnswersForCrypticClues(Clue clue, String correctAnswer) {
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

  private static Stream<Arguments> trickyStandardCluesAndCorrectAnswers() {
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
    var clue4 =
        Clue.builder()
            .id(new Id("11D"))
            .text("Sharp Bark (3)")
            .lengths(List.of(3))
            .pattern("Y?P")
            .build();
    var clue5 =
        Clue.builder()
            .id(new Id("15D"))
            .text("Obvious (7)")
            .lengths(List.of(7))
            .pattern("???????")
            .build();
    return Stream.of(
        Arguments.of(clue1, "AMY"),
        Arguments.of(clue2, "OFFCUT"),
        Arguments.of(clue3, "TUP"),
        Arguments.of(clue4, "YAP"),
        Arguments.of(clue5, "BLATANT"));
  }

  private static Stream<Arguments> crypticCluesAndCorrectAnswers() {
    var clue1 =
        Clue.builder()
            .type(ClueType.CRYPTIC)
            .id(new Id("3A"))
            .text("Prophet comes back into house someday (5)")
            .lengths(List.of(5))
            .pattern("M?S?S")
            .build();
    var clue2 =
        Clue.builder()
            .type(ClueType.CRYPTIC)
            .id(new Id("21A"))
            .text("Possibly train as singer (7)")
            .lengths(List.of(7))
            .pattern("????T??")
            .build();
    var clue3 =
        Clue.builder()
            .type(ClueType.CRYPTIC)
            .id(new Id("12D"))
            .text("Madman left girl with twitch (7)")
            .lengths(List.of(7))
            .pattern("???????")
            .build();
    return Stream.of(
        Arguments.of(clue1, "MOSES"),
        Arguments.of(clue2, "SINATRA"),
        Arguments.of(clue3, "LUNATIC"));
  }
}
