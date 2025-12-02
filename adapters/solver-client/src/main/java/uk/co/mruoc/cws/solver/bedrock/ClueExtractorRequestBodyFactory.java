package uk.co.mruoc.cws.solver.bedrock;

import static uk.co.mruoc.file.FileLoader.loadContentFromClasspath;

import uk.co.mruoc.cws.solver.JsonMapper;

public class ClueExtractorRequestBodyFactory extends InvokeModelRequestBodyFactory {

  public ClueExtractorRequestBodyFactory() {
    super(new JsonMapper().jsonEscape(loadContentFromClasspath("prompts/extract-clues.txt")));
  }
}
