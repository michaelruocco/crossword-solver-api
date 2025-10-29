package uk.co.mruoc.cws.usecase;

import java.util.Collection;
import java.util.Optional;
import java.util.OptionalInt;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Id;

public interface AnswerRepository {
  void save(Id id, Answer answer);

  Optional<Character> getLetter(Id id, int index);

  Collection<Answer> getUnconfirmedAnswersByConfidenceScoreDescending();

  Collection<Answer> getAnswersByConfidenceScoreDescending();

  Answer forceFind(Id id);

  Optional<Answer> find(Id id);

  OptionalInt getHighestUnconfirmedConfidenceScore();

  void confirmAnswersWithScore(int requiredScore);
}
