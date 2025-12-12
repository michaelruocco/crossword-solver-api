package uk.co.mruoc.cws.app.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import uk.co.mruoc.cws.repository.PostgresJpaPuzzleRepository;
import uk.co.mruoc.cws.repository.PostgresPuzzleRepository;

@Configuration
@EnableJpaRepositories(basePackages = "uk.co.mruoc.cws.repository")
@EntityScan(basePackages = "uk.co.mruoc.cws.repository.entity")
public class JpaRepositoryConfig {

  @Bean
  public PostgresPuzzleRepository postgresPuzzleRepository(
      PostgresJpaPuzzleRepository jpaRepository) {
    return new PostgresPuzzleRepository(jpaRepository);
  }
}
