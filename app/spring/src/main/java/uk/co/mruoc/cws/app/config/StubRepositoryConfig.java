package uk.co.mruoc.cws.app.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.mruoc.cws.repository.StubAttemptRepository;
import uk.co.mruoc.cws.repository.StubCandidateRepository;
import uk.co.mruoc.cws.repository.StubPuzzleRepository;
import uk.co.mruoc.cws.repository.StubPuzzleSummaryRepository;
import uk.co.mruoc.cws.usecase.CandidateRepository;
import uk.co.mruoc.cws.usecase.attempt.AttemptRepository;
import uk.co.mruoc.cws.usecase.puzzle.PuzzleRepository;
import uk.co.mruoc.cws.usecase.puzzle.PuzzleSummaryRepository;

@Configuration
@EnableAutoConfiguration(
    exclude = {
      DataSourceAutoConfiguration.class,
      DataSourceTransactionManagerAutoConfiguration.class
    })
@ConditionalOnProperty(name = "repository.type", havingValue = "stub")
@Slf4j
public class StubRepositoryConfig {

  @Bean
  public PuzzleRepository stubPuzzleRepository() {
    log.warn("creating stub puzzle repository");
    return new StubPuzzleRepository();
  }

  @Bean
  public CandidateRepository stubCandidateRepository() {
    log.warn("creating stub candidate repository");
    return new StubCandidateRepository();
  }

  @Bean
  public AttemptRepository stubAttemptRepository() {
    log.warn("creating stub attempt repository");
    return new StubAttemptRepository();
  }

  @Bean
  public PuzzleSummaryRepository stubPuzzleSummaryRepository(
      PuzzleRepository puzzleRepository, AttemptRepository attemptRepository) {
    log.warn("creating stub puzzle summary repository");
    return StubPuzzleSummaryRepository.builder()
        .puzzleRepository(puzzleRepository)
        .attemptRepository(attemptRepository)
        .build();
  }
}
