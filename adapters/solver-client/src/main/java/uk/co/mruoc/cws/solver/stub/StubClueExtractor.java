package uk.co.mruoc.cws.solver.stub;

import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Clues;
import uk.co.mruoc.cws.solver.JsonMapper;
import uk.co.mruoc.cws.usecase.ClueExtractor;
import uk.co.mruoc.file.FileLoader;

@RequiredArgsConstructor
public class StubClueExtractor implements ClueExtractor {

  private final String cluesJsonPath;
  private final JsonMapper mapper;

  public StubClueExtractor(String cluesJsonPath) {
    this(cluesJsonPath, new JsonMapper());
  }

  @Override
  public Clues extractClues(String imageUrl) {
    var json = FileLoader.loadContentFromClasspath(cluesJsonPath);
    return mapper.toClues(json);
  }
}
