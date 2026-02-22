package uk.co.mruoc.cws.app.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import uk.co.mruoc.cws.repository.PostgresAttemptRepository;
import uk.co.mruoc.cws.repository.PostgresCandidateRepository;
import uk.co.mruoc.cws.repository.PostgresJpaAttemptRepository;
import uk.co.mruoc.cws.repository.PostgresJpaCandidateRepository;
import uk.co.mruoc.cws.repository.PostgresJpaPuzzleRepository;
import uk.co.mruoc.cws.repository.PostgresPuzzleRepository;
import uk.co.mruoc.cws.repository.PostgresPuzzleSummaryRepository;
import uk.co.mruoc.cws.usecase.puzzle.PuzzleSummaryRepository;

@Configuration
@EnableJpaRepositories(basePackages = "uk.co.mruoc.cws.repository")
@EntityScan(basePackages = "uk.co.mruoc.cws.repository.entity")
@ConditionalOnProperty(name = "repository.type", havingValue = "postgres", matchIfMissing = true)
public class JpaRepositoryConfig {

  @Bean
  public PostgresPuzzleRepository postgresPuzzleRepository(
      PostgresJpaPuzzleRepository jpaRepository) {
    return new PostgresPuzzleRepository(jpaRepository);
  }

  @Bean
  public PostgresCandidateRepository postgresCandidateRepository(
      PostgresJpaCandidateRepository jpaRepository) {
    return new PostgresCandidateRepository(jpaRepository);
  }

  @Bean
  public PostgresAttemptRepository postgresAttemptRepository(
      PostgresJpaAttemptRepository jpaRepository) {
    return new PostgresAttemptRepository(jpaRepository);
  }

  @Bean
  public PuzzleSummaryRepository postgresPuzzleSummaryRepository(
      PostgresJpaPuzzleRepository jpaRepository) {
    return new PostgresPuzzleSummaryRepository(jpaRepository);
  }
}
