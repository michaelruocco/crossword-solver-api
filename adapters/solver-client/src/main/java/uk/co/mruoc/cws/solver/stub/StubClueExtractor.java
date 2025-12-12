package uk.co.mruoc.cws.solver.stub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Clues;
import uk.co.mruoc.cws.solver.JsonMapper;
import uk.co.mruoc.cws.usecase.ClueExtractor;
import uk.co.mruoc.cws.usecase.Image;
import uk.co.mruoc.file.FileLoader;

@RequiredArgsConstructor
@Slf4j
public class StubClueExtractor implements ClueExtractor {

  private final StubJsonPathFactory cluePathFactory;
  private final JsonMapper mapper;

  public StubClueExtractor() {
    this(new StubJsonPathFactory(), new JsonMapper());
  }

  @Override
  public Clues extractClues(Image image) {
    var path = cluePathFactory.toClueJsonPath(image.getName());
    var json = FileLoader.loadContentFromClasspath(path);
    return mapper.toClues(json);
  }
}
