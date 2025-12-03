package uk.co.mruoc.cws.solver.bedrock;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
import uk.co.mruoc.cws.entity.Clues;
import uk.co.mruoc.cws.image.DefaultImageCompressor;
import uk.co.mruoc.cws.image.DefaultImageDownloader;
import uk.co.mruoc.cws.solver.JsonMapper;
import uk.co.mruoc.cws.usecase.ClueExtractor;
import uk.co.mruoc.cws.usecase.ImageCompressor;
import uk.co.mruoc.cws.usecase.ImageDownloader;

@RequiredArgsConstructor
@Slf4j
public class BedrockClueExtractor implements ClueExtractor {

  private final BedrockRuntimeClient client;
  private final String modelId;
  private final ClueExtractorRequestBodyFactory requestBodyFactory;
  private final ImageDownloader downloader;
  private final ImageCompressor compressor;
  private final JsonMapper mapper;

  public BedrockClueExtractor(BedrockRuntimeClient client) {
    this(client, ModelId.DEFAULT);
  }

  public BedrockClueExtractor(BedrockRuntimeClient client, String modelId) {
    this(
        client,
        modelId,
        new ClueExtractorRequestBodyFactory(),
        new DefaultImageDownloader(),
        new DefaultImageCompressor(),
        new JsonMapper());
  }

  @Override
  public Clues extractClues(String imageUrl) {
    var image = downloader.downloadImage(imageUrl);
    return extractClues(image);
  }

  private Clues extractClues(BufferedImage image) {
    var bytes = compressor.compressAndResize(image);
    return extractClues(bytes);
  }

  private Clues extractClues(byte[] bytes) {
    var requestBody = requestBodyFactory.toInvokeModelRequestBody(bytes);
    var request =
        InvokeModelRequest.builder()
            .modelId(modelId)
            .body(SdkBytes.fromString(requestBody, StandardCharsets.UTF_8))
            .contentType("application/json")
            .accept("application/json")
            .build();
    var response = client.invokeModel(request);
    var cluesJson = toCluesJson(response);
    log.debug("extracted clues json {}", cluesJson);
    return mapper.toClues(cluesJson);
  }

  private String toCluesJson(InvokeModelResponse response) {
    var responseBody = response.body().asString(StandardCharsets.UTF_8);
    return mapper.extractFirstContentText(responseBody);
  }
}
