package uk.co.mruoc.cws.hackathon;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Answers;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.Result;
import uk.co.mruoc.cws.usecase.HackathonClient;
import uk.co.mruoc.cws.usecase.HackathonClientException;

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
  public Result recordAnswers(Attempt attempt) {
    var initialResult = doRecordAnswers(attempt);
    var finalResult = Result.builder()
            .totalCount(initialResult.getTotal())
            .correctCount(initialResult.getCorrect())
            .incorrectAnswers(toIncorrectAnswers(attempt, initialResult))
            .build();
    if (finalResult.hasIncorrectAnswers()) {
      doRecordAnswers(attempt);
    }
    return finalResult;
  }

  private HackathonResult doRecordAnswers(Attempt attempt) {
    var hackathonAttempt = attemptFactory.toHackathonAttempt(attempt);
    log.debug("sending body {}", hackathonAttempt);
    var hackathonResult = webClient
            .post()
            .uri("/solve")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(hackathonAttempt)
            .retrieve()
            .bodyToMono(HackathonResult.class)
            .switchIfEmpty(Mono.error(new HackathonClientException("empty response")))
            .block(Duration.ofSeconds(10));
    return Optional.ofNullable(hackathonResult).orElseThrow(() -> new HackathonClientException(attempt.id()));
  }

  private Answers toIncorrectAnswers(Attempt attempt, HackathonResult initialResult) {
    if (initialResult.allCorrect()) {
      return new Answers();
    }
    return new Answers(attempt.answers().stream()
            .filter(answer -> isIncorrect(answer, attempt, initialResult))
            .toList());
  }

  private boolean isIncorrect(Answer answer, Attempt attempt, HackathonResult initialResult) {
    var updatedAttempt = attempt.deleteAnswer(answer.id());
    var answerResult = doRecordAnswers(updatedAttempt);
    log.debug("got answer result {} after removing answer {}", answerResult, answer.asString());
    return answerResult.getCorrect() >= initialResult.getCorrect();
  }
}
