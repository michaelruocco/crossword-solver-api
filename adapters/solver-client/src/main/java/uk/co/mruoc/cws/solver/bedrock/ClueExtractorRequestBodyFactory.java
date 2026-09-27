package uk.co.mruoc.cws.solver.bedrock;

import uk.co.mruoc.cws.solver.CrosswordJsonMapper;

import static uk.co.mruoc.file.FileLoader.loadContentFromClasspath;

public class ClueExtractorRequestBodyFactory extends InvokeModelRequestBodyFactory {

  public ClueExtractorRequestBodyFactory() {
    super(
        new CrosswordJsonMapper()
            .jsonEscape(loadContentFromClasspath("prompts/extract-clues.txt")));
  }
}
