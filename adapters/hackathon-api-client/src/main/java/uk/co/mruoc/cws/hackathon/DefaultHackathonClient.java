package uk.co.mruoc.cws.hackathon;

import java.util.List;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.usecase.HackathonClient;

@Slf4j
@Builder
public class DefaultHackathonClient implements HackathonClient {

  private final WebClient webClient;
  private final HackathonSolveAttemptFactory attemptFactory;

  @Override
  public List<String> getPuzzleImageUrls() {
    return webClient
        .get()
        .uri("/puzzles")
        .retrieve()
        .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
        .block();
  }

  @Override
  public void recordAnswers(Attempt attempt) {
    var hackathonAttempt = attemptFactory.toHackathonAttempt(attempt);
    log.info("sending body {}", hackathonAttempt);
    webClient
        .post()
        .uri("/solve")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(hackathonAttempt)
        .exchangeToMono(
            response ->
                response
                    .bodyToMono(String.class)
                    .defaultIfEmpty("<no body>")
                    .doOnNext(
                        body -> {
                          log.info("Status: {}", response.statusCode());
                          log.info("Headers: {}", response.headers().asHttpHeaders());
                          log.info("Body: {}", body);
                        })
                    .then())
        .block();
  }
}
