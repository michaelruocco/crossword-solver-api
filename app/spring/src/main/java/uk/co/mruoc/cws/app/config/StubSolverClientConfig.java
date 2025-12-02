package uk.co.mruoc.cws.app.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.mruoc.cws.solver.stub.FakeAnswerFinder;
import uk.co.mruoc.cws.solver.stub.Puzzle1FakeAnswers;
import uk.co.mruoc.cws.solver.stub.StubClueExtractor;
import uk.co.mruoc.cws.solver.stub.StubWordExtractor;
import uk.co.mruoc.cws.usecase.AnswerFinder;
import uk.co.mruoc.cws.usecase.ClueExtractor;
import uk.co.mruoc.cws.usecase.WordExtractor;

@Configuration
public class StubSolverClientConfig {

  @Bean
  public WordExtractor stubWordExtractor() {
    return new StubWordExtractor();
  }

  @Bean
  public ClueExtractor stubClueExtractor() {
    return new StubClueExtractor();
  }

  @Bean
  @ConditionalOnMissingBean
  public AnswerFinder stubAnswerFinder() {
    return new FakeAnswerFinder(new Puzzle1FakeAnswers());
  }
}
