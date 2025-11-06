package uk.co.mruoc.cws.app.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.mruoc.cws.solver.stub.FakeAnswerFinder;
import uk.co.mruoc.cws.solver.stub.Puzzle1FakeAnswers;
import uk.co.mruoc.cws.solver.stub.Puzzle1StubCrosswordSolver;
import uk.co.mruoc.cws.usecase.AnswerFinder;
import uk.co.mruoc.cws.usecase.CrosswordSolver;

@Configuration
public class StubSolverClientConfig {

  private static final CrosswordSolver SOLVER = new Puzzle1StubCrosswordSolver();

  @Bean
  public CrosswordSolver stubSolver() {
    return SOLVER;
  }

  @Bean
  @ConditionalOnMissingBean
  public AnswerFinder stubAnswerFinder() {
    return new FakeAnswerFinder(new Puzzle1FakeAnswers());
  }
}
