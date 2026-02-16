package uk.co.mruoc.cws.solver.bedrock;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.mruoc.cws.entity.Puzzle;
import uk.co.mruoc.cws.solver.stub.StubClueExtractor;
import uk.co.mruoc.cws.usecase.ImageDownloader;
import uk.co.mruoc.cws.usecase.PuzzleType;
import uk.co.mruoc.cws.usecase.PuzzleTypePolicy;
import uk.co.mruoc.cws.usecase.StubImageDownloader;
import uk.co.mruoc.junit.EnvVarsPresent;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.mruoc.cws.image.ImageUrlBuilder.toUrl;
import static uk.co.mruoc.cws.solver.bedrock.BedrockRuntimeClientFactory.buildClient;
import static uk.co.mruoc.cws.usecase.PuzzleType.CRYPTIC;
import static uk.co.mruoc.cws.usecase.PuzzleType.STANDARD;

@EnvVarsPresent(values = {"AWS_ACCESS_KEY_ID", "AWS_SECRET_ACCESS_KEY"})
@Slf4j
public class BedrockPuzzleTypePolicyIT {

  private final ImageDownloader downloader = new StubImageDownloader();
  private final PuzzleTypePolicy policy = new BedrockPuzzleTypePolicy(buildClient());

  @ParameterizedTest
  @MethodSource("imageUrlAndExpectedPuzzleType")
  void shouldExtractCluesFromPuzzleImage(String imageUrl, PuzzleType expectedType) {
    var image = downloader.downloadImage(imageUrl);
    var clues = new StubClueExtractor().extractClues(image);
    var puzzle = Puzzle.builder().clues(clues).build();

    var type = policy.determinePuzzleType(puzzle);

    assertThat(type).isEqualTo(expectedType);
  }

  private static Stream<Arguments> imageUrlAndExpectedPuzzleType() {
    return Stream.of(
            Arguments.of(toUrl("puzzle1.png"), STANDARD),
            Arguments.of(toUrl("puzzle2.png"), STANDARD),
            Arguments.of(toUrl("puzzle3.png"), STANDARD),
            Arguments.of(toUrl("puzzle4.png"), STANDARD),
            Arguments.of(toUrl("puzzle5.png"), STANDARD),
            Arguments.of(toUrl("puzzle9.jpg"), STANDARD),
            Arguments.of(toUrl("puzzle14.jpg"), STANDARD),
            Arguments.of(toUrl("puzzle24.jpg"), CRYPTIC)
    );
  }
}
