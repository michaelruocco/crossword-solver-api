package uk.co.mruoc.cws.app.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.mruoc.cws.solver.stub.FakeAnswerFinder;
import uk.co.mruoc.cws.solver.stub.Puzzle1FakeAnswers;
import uk.co.mruoc.cws.solver.stub.StubClueExtractor;
import uk.co.mruoc.cws.solver.stub.StubGridExtractor;
import uk.co.mruoc.cws.usecase.AnswerFinder;
import uk.co.mruoc.cws.usecase.ClueExtractor;
import uk.co.mruoc.cws.usecase.GridExtractor;

@Configuration
public class StubSolverClientConfig {

  @ConditionalOnProperty(name = "stub.cellExtractorEnabled", havingValue = "true")
  @Bean
  public GridExtractor stubCellExtractor() {
    return new StubGridExtractor();
  }

  @ConditionalOnProperty(name = "stub.clueExtractorEnabled", havingValue = "true")
  @Bean
  public ClueExtractor stubClueExtractor() {
    return new StubClueExtractor();
  }

  @ConditionalOnProperty(name = "stub.answerFinderEnabled", havingValue = "true")
  @Bean
  public AnswerFinder stubAnswerFinder() {
    return new FakeAnswerFinder(new Puzzle1FakeAnswers());
  }
}
