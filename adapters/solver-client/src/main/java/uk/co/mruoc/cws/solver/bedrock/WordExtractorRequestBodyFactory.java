package uk.co.mruoc.cws.solver.bedrock;

import static uk.co.mruoc.file.FileLoader.loadContentFromClasspath;

import uk.co.mruoc.cws.solver.JsonMapper;

public class WordExtractorRequestBodyFactory extends InvokeModelRequestBodyFactory {

  public WordExtractorRequestBodyFactory() {
    super(new JsonMapper().jsonEscape(loadContentFromClasspath("prompts/extract-words.txt")));
  }
}
