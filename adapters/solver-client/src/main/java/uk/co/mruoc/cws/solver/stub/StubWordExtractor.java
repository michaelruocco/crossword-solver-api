package uk.co.mruoc.cws.solver.stub;

import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Words;
import uk.co.mruoc.cws.solver.JsonMapper;
import uk.co.mruoc.cws.usecase.WordExtractor;
import uk.co.mruoc.file.FileLoader;

@RequiredArgsConstructor
public class StubWordExtractor implements WordExtractor {

  private final StubJsonPathFactory cluePathFactory;
  private final JsonMapper mapper;

  public StubWordExtractor() {
    this(new StubJsonPathFactory(), new JsonMapper());
  }

  @Override
  public Words extractWords(String imageUrl) {
    var path = cluePathFactory.toWordJsonPath(imageUrl);
    var json = FileLoader.loadContentFromClasspath(path);
    return mapper.toWords(json);
  }
}
