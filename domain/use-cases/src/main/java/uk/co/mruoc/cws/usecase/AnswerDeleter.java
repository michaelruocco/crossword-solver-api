package uk.co.mruoc.cws.usecase;

import java.util.UUID;
import lombok.Builder;
import uk.co.mruoc.cws.entity.Id;
import uk.co.mruoc.cws.usecase.attempt.AttemptFinder;
import uk.co.mruoc.cws.usecase.attempt.AttemptRepository;

@Builder
public class AnswerDeleter {

  private final AttemptFinder finder;
  private final AttemptRepository repository;

  public void deleteAnswer(UUID attemptId, Id id) {
    var attempt = finder.findById(attemptId);
    repository.save(attempt.deleteAnswer(id));
  }
}
