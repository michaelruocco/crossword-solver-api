package uk.co.mruoc.cws.usecase.attempt;

import java.util.UUID;
import lombok.Builder;
import uk.co.mruoc.cws.entity.Answer;

@Builder
public class AttemptUpdater {

  private final AttemptFinder finder;
  private final AttemptRepository repository;

  public void saveAnswer(UUID attemptId, Answer answer) {
    var attempt = finder.findById(attemptId);
    var updatedAttempt = attempt.saveAnswer(answer);
    repository.save(updatedAttempt);
  }
}
