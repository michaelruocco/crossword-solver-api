package uk.co.mruoc.cws.solver.bedrock;

import static uk.co.mruoc.file.FileLoader.loadContentFromClasspath;

import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class InvokeModelRequestBodyFactory {

  private final String promptText;

  public String toInvokeModelRequestBody(byte[] imageBytes) {
    var requestTemplate = loadContentFromClasspath("requests/bedrock-image-prompt-request.json");
    var base64Image = base64Encode(imageBytes);
    log.debug("building invoke model request body from template {}", requestTemplate);
    log.debug("using prompt text {}", promptText);
    log.debug("and base 64 encoded image string with length {}", base64Image.length());
    return requestTemplate
        .replace("\"%PROMPT_TEXT%\"", promptText)
        .replace("%BASE_64_IMAGE%", base64Image);
  }

  private String base64Encode(byte[] bytes) {
    return Base64.getEncoder().encodeToString(bytes);
  }
}
