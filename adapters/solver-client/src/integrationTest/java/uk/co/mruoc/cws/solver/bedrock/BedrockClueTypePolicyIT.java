package uk.co.mruoc.cws.solver.bedrock;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.mruoc.cws.solver.stub.StubClueExtractor;
import uk.co.mruoc.cws.usecase.ImageDownloader;
import uk.co.mruoc.cws.entity.ClueType;
import uk.co.mruoc.cws.usecase.ClueTypePolicy;
import uk.co.mruoc.cws.usecase.StubImageDownloader;
import uk.co.mruoc.junit.EnvVarsPresent;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.mruoc.cws.image.ImageUrlBuilder.toUrl;
import static uk.co.mruoc.cws.solver.bedrock.BedrockRuntimeClientFactory.buildClient;
import static uk.co.mruoc.cws.entity.ClueType.CRYPTIC;
import static uk.co.mruoc.cws.entity.ClueType.STANDARD;

@EnvVarsPresent(values = {"AWS_ACCESS_KEY_ID", "AWS_SECRET_ACCESS_KEY"})
@Slf4j
public class BedrockClueTypePolicyIT {

  private final ImageDownloader downloader = new StubImageDownloader();
  private final ClueTypePolicy policy = new BedrockClueTypePolicy(buildClient());

  @ParameterizedTest
  @MethodSource("imageUrlAndExpectedPuzzleType")
  void shouldExtractCluesFromPuzzleImage(String imageUrl, ClueType expectedType) {
    var image = downloader.downloadImage(imageUrl);
    var clues = new StubClueExtractor().extractClues(image);

    var type = policy.determineClueType(clues);

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
