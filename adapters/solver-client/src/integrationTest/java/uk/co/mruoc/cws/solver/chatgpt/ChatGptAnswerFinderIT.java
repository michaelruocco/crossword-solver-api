package uk.co.mruoc.cws.solver.chatgpt;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.mruoc.cws.solver.chatgpt.ChatGptClientFactory.buildClient;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import uk.co.mruoc.cws.solver.stub.StubClueExtractor;
import uk.co.mruoc.cws.usecase.AnswerFinder;
import uk.co.mruoc.cws.usecase.ClueExtractor;
import uk.co.mruoc.cws.usecase.ImageDownloader;
import uk.co.mruoc.cws.usecase.StubImageDownloader;
import uk.co.mruoc.cws.usecase.UrlConverter;
import uk.co.mruoc.junit.EnvVarsPresent;

@EnvVarsPresent(values = {"OPEN_AI_API_KEY"})
@Slf4j
public class ChatGptAnswerFinderIT {

  private final ImageDownloader downloader = new StubImageDownloader();
  private final UrlConverter urlConverter = new UrlConverter();
  private final ClueExtractor clueExtractor = new StubClueExtractor();

  private final AnswerFinder finder = new ChatGptAnswerFinder(buildClient());

  @EnvVarsPresent(values = {"OPEN_AI_API_KEY"})
  @Test
  void shouldFindBatchOfAnswers() {
    var url = "http://any-host/puzzle1.png";
    var image = downloader.downloadImage(url);
    var clues = clueExtractor.extractClues(image);

    var answers = finder.findAnswers(clues);

    answers.stream().forEach(answer -> log.info(answer.toString()));
    assertThat(answers).hasSize(clues.size());
  }
}
