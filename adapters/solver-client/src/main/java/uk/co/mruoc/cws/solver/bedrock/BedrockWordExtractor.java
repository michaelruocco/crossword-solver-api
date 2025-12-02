package uk.co.mruoc.cws.solver.bedrock;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
import uk.co.mruoc.cws.entity.Words;
import uk.co.mruoc.cws.solver.JsonMapper;
import uk.co.mruoc.cws.usecase.DefaultImageDownloader;
import uk.co.mruoc.cws.usecase.ImageCompressor;
import uk.co.mruoc.cws.usecase.WordExtractor;

@RequiredArgsConstructor
@Slf4j
public class BedrockWordExtractor implements WordExtractor {

  private final BedrockRuntimeClient client;
  private final String modelId;
  private final InvokeModelRequestBodyFactory requestBodyFactory;
  private final DefaultImageDownloader downloader;
  private final ImageProcessor processor;
  private final ImageCompressor compressor;
  private final JsonMapper mapper;

  public BedrockWordExtractor(BedrockRuntimeClient client, String modelId) {
    this(
        client,
        modelId,
        new WordExtractorRequestBodyFactory(),
        new DefaultImageDownloader(),
        new ImageProcessor(),
        new ImageCompressor(),
        new JsonMapper());
  }

  @Override
  public Words extractWords(String imageUrl) {
    var image = downloader.downloadImage(imageUrl);
    var grid = processor.process(image);
    var compressedImageBytes = compressor.compressAndResize(toBufferedImage(grid));
    var requestBody = requestBodyFactory.toInvokeModelRequestBody(compressedImageBytes);
    var request =
        InvokeModelRequest.builder()
            .modelId(modelId)
            .body(SdkBytes.fromString(requestBody, StandardCharsets.UTF_8))
            .contentType("application/json")
            .accept("application/json")
            .build();
    var response = client.invokeModel(request);
    var wordsJson = toWordsJson(response);
    log.debug("extracted words json {}", wordsJson);
    return mapper.toWords(wordsJson);
  }

  private static BufferedImage toBufferedImage(Mat mat) {
    Mat tmp = new Mat();
    if (mat.channels() == 1) {
      Imgproc.cvtColor(mat, tmp, Imgproc.COLOR_GRAY2BGR);
    } else {
      tmp = mat;
    }

    MatOfByte mob = new MatOfByte();
    Imgcodecs.imencode(".png", tmp, mob);
    byte[] byteArray = mob.toArray();

    try {
      return ImageIO.read(new ByteArrayInputStream(byteArray));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String toWordsJson(InvokeModelResponse response) {
    var responseBody = response.body().asString(StandardCharsets.UTF_8);
    return mapper.extractFirstContentText(responseBody);
  }
}
