package uk.co.mruoc.cws.app.config;

import org.springframework.context.annotation.Bean;
import uk.co.mruoc.cws.repository.StubAttemptRepository;
import uk.co.mruoc.cws.repository.StubPuzzleRepository;
import uk.co.mruoc.cws.usecase.attempt.AttemptRepository;
import uk.co.mruoc.cws.usecase.puzzle.PuzzleRepository;

// @Configuration
public class StubRepositoryConfig {

  @Bean
  public PuzzleRepository stubPuzzleRepository() {
    return new StubPuzzleRepository();
  }

  @Bean
  public AttemptRepository stubAttemptRepository() {
    return new StubAttemptRepository();
  }
}
