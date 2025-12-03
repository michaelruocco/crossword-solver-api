package uk.co.mruoc.cws.solver.stub;

import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Cells;
import uk.co.mruoc.cws.solver.JsonMapper;
import uk.co.mruoc.cws.usecase.CellExtractor;
import uk.co.mruoc.file.FileLoader;

@RequiredArgsConstructor
public class StubCellExtractor implements CellExtractor {

  private final StubJsonPathFactory cluePathFactory;
  private final JsonMapper mapper;

  public StubCellExtractor() {
    this(new StubJsonPathFactory(), new JsonMapper());
  }

  @Override
  public Cells extractCells(String imageUrl) {
    var path = cluePathFactory.toWordJsonPath(imageUrl);
    var json = FileLoader.loadContentFromClasspath(path);
    return mapper.toCells(json);
  }
}
