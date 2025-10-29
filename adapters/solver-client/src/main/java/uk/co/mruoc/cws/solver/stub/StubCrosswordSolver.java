package uk.co.mruoc.cws.solver.stub;

import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Clues;
import uk.co.mruoc.cws.entity.Words;
import uk.co.mruoc.cws.solver.JsonMapper;
import uk.co.mruoc.cws.usecase.ClueExtractor;
import uk.co.mruoc.cws.usecase.CrosswordSolver;
import uk.co.mruoc.file.FileLoader;

@RequiredArgsConstructor
public class StubCrosswordSolver implements CrosswordSolver {

  private final String wordsJsonPath;
  private final ClueExtractor clueExtractor;
  private final JsonMapper mapper;

  public StubCrosswordSolver(String wordsJsonPath, String cluesJsonPath) {
    this(wordsJsonPath, cluesJsonPath, new JsonMapper());
  }

  public StubCrosswordSolver(String wordsJsonPath, String cluesJsonPath, JsonMapper mapper) {
    this(wordsJsonPath, new StubClueExtractor(cluesJsonPath), mapper);
  }

  @Override
  public Clues extractClues(String imageUrl) {
    return clueExtractor.extractClues(imageUrl);
  }

  @Override
  public Words extractWords(String imageUrl) {
    var json = FileLoader.loadContentFromClasspath(wordsJsonPath);
    return mapper.toWords(json);
  }
}
