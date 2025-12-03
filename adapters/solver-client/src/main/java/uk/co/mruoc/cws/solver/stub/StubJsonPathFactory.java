package uk.co.mruoc.cws.solver.stub;

import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.usecase.UrlConverter;

@RequiredArgsConstructor
public class StubJsonPathFactory {

  private final String cluesPathTemplate;
  private final String cellsPathTemplate;
  private final UrlConverter urlConverter;

  public StubJsonPathFactory() {
    this("examples/%s/clues.json", "examples/%s/cells.json", new UrlConverter());
  }

  public String toClueJsonPath(String imageUrl) {
    return toJsonPath(cluesPathTemplate, imageUrl);
  }

  public String toWordJsonPath(String imageUrl) {
    return toJsonPath(cellsPathTemplate, imageUrl);
  }

  private String toJsonPath(String template, String imageUrl) {
    var name = urlConverter.toFilenameExcludingExtension(imageUrl);
    return String.format(template, name);
  }
}
