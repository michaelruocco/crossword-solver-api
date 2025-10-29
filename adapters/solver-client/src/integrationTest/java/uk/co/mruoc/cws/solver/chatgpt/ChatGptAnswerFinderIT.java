package uk.co.mruoc.cws.solver.chatgpt;

import static com.openai.models.ChatModel.GPT_3_5_TURBO;
import static org.assertj.core.api.Assertions.assertThat;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import uk.co.mruoc.cws.solver.stub.Puzzle1StubClueExtractor;
import uk.co.mruoc.cws.usecase.AnswerFinder;
import uk.co.mruoc.cws.usecase.ClueExtractor;

@Slf4j
public class ChatGptAnswerFinderIT {

  private final OpenAIClient client = buildOpenAIClient();

  private final ClueExtractor clueExtractor = new Puzzle1StubClueExtractor();
  private final AnswerFinder finder = new ChatGptAnswerFinder(client, GPT_3_5_TURBO);

  @Test
  void shouldFindBatchOfAnswers() {
    var clues = clueExtractor.extractClues("any-url");

    var answers = finder.findAnswers(clues);

    answers.stream().forEach(answer -> log.info(answer.toString()));
    assertThat(answers).hasSize(clues.size());
  }

  private static OpenAIClient buildOpenAIClient() {
    var apiKey = System.getenv("OPEN_AI_API_KEY");
    return OpenAIOkHttpClient.builder().apiKey(apiKey).build();
  }
}
