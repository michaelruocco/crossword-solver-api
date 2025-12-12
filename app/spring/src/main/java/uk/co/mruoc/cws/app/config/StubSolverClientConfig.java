package uk.co.mruoc.cws.app.config;

import org.springframework.context.annotation.Bean;
import uk.co.mruoc.cws.solver.stub.FakeAnswerFinder;
import uk.co.mruoc.cws.solver.stub.Puzzle1FakeAnswers;
import uk.co.mruoc.cws.solver.stub.StubCellExtractor;
import uk.co.mruoc.cws.solver.stub.StubClueExtractor;
import uk.co.mruoc.cws.solver.stub.StubClueRanker;
import uk.co.mruoc.cws.usecase.AnswerFinder;
import uk.co.mruoc.cws.usecase.CellExtractor;
import uk.co.mruoc.cws.usecase.ClueExtractor;
import uk.co.mruoc.cws.usecase.ClueRanker;

// @Configuration
public class StubSolverClientConfig {

  @Bean
  public CellExtractor stubCellExtractor() {
    return new StubCellExtractor();
  }

  @Bean
  public ClueExtractor stubClueExtractor() {
    return new StubClueExtractor();
  }

  @Bean
  public AnswerFinder stubAnswerFinder() {
    return new FakeAnswerFinder(new Puzzle1FakeAnswers());
  }

  @Bean
  public ClueRanker stubClueRanker() {
    return new StubClueRanker();
  }
}
