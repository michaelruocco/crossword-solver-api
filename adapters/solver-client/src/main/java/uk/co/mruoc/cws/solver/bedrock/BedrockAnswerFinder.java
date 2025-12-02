package uk.co.mruoc.cws.solver.bedrock;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ConversationRole;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseRequest;
import software.amazon.awssdk.services.bedrockruntime.model.Message;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Answers;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;
import uk.co.mruoc.cws.solver.FindAnswerPromptTextFactory;
import uk.co.mruoc.cws.solver.FindAnswerResponseConverter;
import uk.co.mruoc.cws.usecase.AnswerFinder;

@RequiredArgsConstructor
public class BedrockAnswerFinder implements AnswerFinder {

  private final BedrockRuntimeClient client;
  private final String modelId;
  private final FindAnswerPromptTextFactory promptTextFactory;
  private final FindAnswerResponseConverter responseConverter;

  public BedrockAnswerFinder(BedrockRuntimeClient client) {
    this(client, ModelId.DEFAULT);
  }

  public BedrockAnswerFinder(BedrockRuntimeClient client, String modelId) {
    this(client, modelId, new FindAnswerPromptTextFactory(), new FindAnswerResponseConverter());
  }

  @Override
  public Answers findAnswers(Clues clues) {
    if (clues.size() == 1) {
      return new Answers(findAnswer(IterableUtils.get(clues, 0)));
    }
    var promptText = promptTextFactory.toPromptText(clues);
    var responseText = execute(promptText);
    return responseConverter.toAnswers(responseText);
  }

  @Override
  public Answer findAnswer(Clue clue) {
    var promptText = promptTextFactory.toPromptText(clue);
    var responseText = execute(promptText);
    return responseConverter.toAnswer(responseText);
  }

  private String execute(String promptText) {
    var message =
        Message.builder()
            .content(ContentBlock.fromText(promptText))
            .role(ConversationRole.USER)
            .build();
    var request =
        ConverseRequest.builder()
            .modelId(modelId)
            .messages(message)
            .inferenceConfig(config -> config.temperature(0.2f).maxTokens(2024))
            .build();
    var response = client.converse(request);
    return response.output().message().content().getFirst().text();
  }
}
