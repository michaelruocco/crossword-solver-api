package uk.co.mruoc.cws.solver.stub;

import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Grid;
import uk.co.mruoc.cws.solver.CrosswordJsonMapper;
import uk.co.mruoc.cws.usecase.GridExtractor;
import uk.co.mruoc.cws.usecase.Image;
import uk.co.mruoc.file.FileLoader;

@RequiredArgsConstructor
public class StubGridExtractor implements GridExtractor {

  private final StubJsonPathFactory cluePathFactory;
  private final CrosswordJsonMapper mapper;

  public StubGridExtractor() {
    this(new StubJsonPathFactory(), new CrosswordJsonMapper());
  }

  @Override
  public Grid extractGrid(Image image) {
    var path = cluePathFactory.toCellJsonPath(image.getName());
    var json = FileLoader.loadContentFromClasspath(path);
    return new Grid(mapper.toCells(json), 125, 125);
  }
}
