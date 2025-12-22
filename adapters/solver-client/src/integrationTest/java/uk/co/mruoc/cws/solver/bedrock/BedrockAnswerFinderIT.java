package uk.co.mruoc.cws.solver.bedrock;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.mruoc.cws.solver.bedrock.BedrockRuntimeClientFactory.buildClient;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Id;
import uk.co.mruoc.cws.usecase.AnswerFinder;
import uk.co.mruoc.junit.EnvVarsPresent;

@EnvVarsPresent(values = {"AWS_ACCESS_KEY_ID", "AWS_SECRET_ACCESS_KEY"})
@Slf4j
public class BedrockAnswerFinderIT {

  private final AnswerFinder finder = new BedrockAnswerFinder(buildClient());

  @Test
  void shouldFindAnswerToClue() {
    var clue =
        Clue.builder()
            .id(new Id("8D"))
            .text("Aubergine and tomato dish (11)")
            .lengths(List.of(11))
            .pattern("???????????")
            .build();

    var answer = finder.findAnswer(clue);

    log.info(answer.toString());
    assertThat(answer.value()).isEqualTo("RATATOUILLE");
  }

  @Test
  void shouldFindCandidateAnswersForClue1() {
    var clue =
        Clue.builder()
            .id(new Id("28D"))
            .text("Remnant (6)")
            .lengths(List.of(6))
            .pattern("???C?T")
            .build();

    var candidates = finder.findCandidates(clue, 5);

    log.info(candidates.asString());
    assertThat(candidates.valuesAsString()).contains("OFFCUT");
  }

  @Test
  void shouldFindCandidateAnswersForClue2() {
    var clue =
            Clue.builder()
                    .id(new Id("27D"))
                    .text("Pilot, - - - Johnson (3)")
                    .lengths(List.of(3))
                    .pattern("A??")
                    .build();

    var candidates = finder.findCandidates(clue, 5);

    log.info(candidates.asString());
    assertThat(candidates.valuesAsString()).contains("AMY");
  }

  @Disabled
  @Test
  void shouldFindAnswerToTrickyClue() {
    var clue =
        Clue.builder().id(new Id("1D")).text("Ram (3)").lengths(List.of(3)).pattern("T?P").build();

    var answer = finder.findAnswer(clue);

    log.info(answer.toString());
    assertThat(answer.value()).isEqualTo("TUP");
  }
}
