package uk.co.mruoc.cws.solver.bedrock;

import static java.nio.charset.StandardCharsets.UTF_8;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
import uk.co.mruoc.cws.entity.Clues;
import uk.co.mruoc.cws.image.DefaultImageCompressor;
import uk.co.mruoc.cws.solver.CrosswordJsonMapper;
import uk.co.mruoc.cws.usecase.ClueExtractor;
import uk.co.mruoc.cws.usecase.Image;
import uk.co.mruoc.cws.usecase.ImageCompressor;

@RequiredArgsConstructor
@Slf4j
public class BedrockClueExtractor implements ClueExtractor {

  private final BedrockRuntimeClient client;
  private final String modelId;
  private final ClueExtractorRequestBodyFactory requestBodyFactory;
  private final ImageCompressor compressor;
  private final CrosswordJsonMapper mapper;

  public BedrockClueExtractor(BedrockRuntimeClient client) {
    this(client, ModelId.DEFAULT);
  }

  public BedrockClueExtractor(BedrockRuntimeClient client, String modelId) {
    this(
        client,
        modelId,
        new ClueExtractorRequestBodyFactory(),
        new DefaultImageCompressor(),
        new CrosswordJsonMapper());
  }

  @Override
  public Clues extractClues(Image image) {
    var bytes = compressor.compressAndResize(image.getBufferedImage());
    return extractClues(bytes);
  }

  private Clues extractClues(byte[] bytes) {
    var requestBody = requestBodyFactory.toInvokeModelRequestBody(bytes);
    var request = toInvokeModelRequest(requestBody);
    var response = client.invokeModel(request);
    var cluesJson = toCluesJson(response);
    log.debug("extracted clues json {}", cluesJson);
    return mapper.toClues(cluesJson);
  }

  private InvokeModelRequest toInvokeModelRequest(String requestBody) {
    return InvokeModelRequest.builder()
        .modelId(modelId)
        .body(SdkBytes.fromString(requestBody, UTF_8))
        .contentType("application/json")
        .accept("application/json")
        .build();
  }

  private String toCluesJson(InvokeModelResponse response) {
    var responseBody = response.body().asString(UTF_8);
    return mapper.extractFirstContentText(responseBody);
  }
}
