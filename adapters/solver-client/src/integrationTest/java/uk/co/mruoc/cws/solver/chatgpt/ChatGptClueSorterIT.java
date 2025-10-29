package uk.co.mruoc.cws.solver.chatgpt;

import static com.openai.models.ChatModel.GPT_3_5_TURBO;
import static org.assertj.core.api.Assertions.assertThat;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import uk.co.mruoc.cws.solver.stub.Puzzle1StubClueExtractor;
import uk.co.mruoc.cws.usecase.ClueExtractor;
import uk.co.mruoc.cws.usecase.ClueSorter;

@Slf4j
public class ChatGptClueSorterIT {

  private final OpenAIClient client = buildOpenAIClient();

  private final ClueExtractor clueExtractor = new Puzzle1StubClueExtractor();
  private final ClueSorter finder = new ChatGptClueSorter(client, GPT_3_5_TURBO);

  @Test
  void shouldFindBatchOfAnswers() {
    var clues = clueExtractor.extractClues("any-url");

    var sortedClues = finder.sort(clues);

    sortedClues.stream().forEach(clue -> log.info(clue.toString()));
    assertThat(sortedClues).containsExactlyInAnyOrderElementsOf(clues);
  }

  private static OpenAIClient buildOpenAIClient() {
    var apiKey = System.getenv("OPEN_AI_API_KEY");
    return OpenAIOkHttpClient.builder().apiKey(apiKey).build();
  }
}
