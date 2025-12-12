package uk.co.mruoc.cws.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.mruoc.cws.repository.StubAttemptRepository;
import uk.co.mruoc.cws.usecase.attempt.AttemptRepository;

@Configuration
public class StubRepositoryConfig {

  /*@Bean
  public PuzzleRepository stubPuzzleRepository() {
    return new StubPuzzleRepository();
  }*/

  @Bean
  public AttemptRepository stubAttemptRepository() {
    return new StubAttemptRepository();
  }
}
