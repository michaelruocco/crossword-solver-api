package uk.co.mruoc.cws.app.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.json.JsonMapper;
import uk.co.mruoc.cws.hackathon.DefaultHackathonClient;
import uk.co.mruoc.cws.hackathon.HackathonSolveAttemptFactory;
import uk.co.mruoc.cws.usecase.CrosswordSolverFacade;
import uk.co.mruoc.cws.usecase.HackathonClient;
import uk.co.mruoc.cws.usecase.HackathonFacade;

@Slf4j
@Configuration
public class HackathonConfig {

  @Bean
  public HackathonFacade hackathonFacade(
      HackathonClient hackathonClient, CrosswordSolverFacade solverFacade) {
    return HackathonFacade.builder()
        .hackathonClient(hackathonClient)
        .solverFacade(solverFacade)
        .build();
  }

  @Bean
  public HackathonClient hackathonApiClient(
      @Value("${hackathon.base-url}") String url, JsonMapper jsonMapper) {
    log.info("configuring hackathon client with base url {}", url);
    return DefaultHackathonClient.builder()
        .webClient(webClient(url, jacksonJsonDecoder(jsonMapper)))
        .attemptFactory(new HackathonSolveAttemptFactory(jsonMapper))
        .build();
  }

  private WebClient webClient(String url, JacksonJsonDecoder decoder) {
    return WebClient.builder()
        .baseUrl(url)
        .codecs(configurer -> configurer.defaultCodecs().jacksonJsonDecoder(decoder))
        .build();
  }

  private JacksonJsonDecoder jacksonJsonDecoder(JsonMapper mapper) {
    return new JacksonJsonDecoder(mapper, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN);
  }
}
