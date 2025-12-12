package uk.co.mruoc.cws.solver.stub;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StubJsonPathFactory {

  private final String cluesPathTemplate;
  private final String cellsPathTemplate;

  public StubJsonPathFactory() {
    this("examples/%s/clues.json", "examples/%s/cells.json");
  }

  public String toClueJsonPath(String filename) {
    return toJsonPath(cluesPathTemplate, filename);
  }

  public String toCellJsonPath(String filename) {
    return toJsonPath(cellsPathTemplate, filename);
  }

  private String toJsonPath(String template, String filename) {
    return String.format(template, filename);
  }
}
