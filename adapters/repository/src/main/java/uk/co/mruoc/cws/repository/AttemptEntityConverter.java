package uk.co.mruoc.cws.repository;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Answers;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.Id;
import uk.co.mruoc.cws.repository.entity.AttemptAnswerEntity;
import uk.co.mruoc.cws.repository.entity.AttemptEntity;

@RequiredArgsConstructor
public class AttemptEntityConverter {

  private final PuzzleEntityConverter puzzleConverter;

  public AttemptEntityConverter() {
    this(new PuzzleEntityConverter());
  }

  public Attempt toAttempt(AttemptEntity entity) {
    return Attempt.builder()
        .id(entity.getId())
        .puzzle(puzzleConverter.toPuzzle(entity.getPuzzle()))
        .answers(toAnswers(entity.getAnswers()))
        .build();
  }

  public AttemptEntity toEntity(Attempt attempt) {
    var entity = new AttemptEntity();
    entity.setId(attempt.id());
    entity.setPuzzle(puzzleConverter.toEntity(attempt.puzzle()));
    entity.setAnswers(toAnswerEntities(attempt));
    return entity;
  }

  private Answers toAnswers(Collection<AttemptAnswerEntity> entities) {
    return new Answers(entities.stream().map(this::toAnswer).toList());
  }

  private Answer toAnswer(AttemptAnswerEntity entity) {
    return Answer.builder()
        .id(new Id(entity.getClueId()))
        .value(entity.getValue())
        .confidenceScore(entity.getScore())
        .confirmed(entity.isConfirmed())
        .build();
  }

  private Collection<AttemptAnswerEntity> toAnswerEntities(Attempt attempt) {
    var answers = attempt.answers();
    return answers.stream().map(this::toAnswerEntity).toList();
  }

  private AttemptAnswerEntity toAnswerEntity(Answer answer) {
    var entity = new AttemptAnswerEntity();
    entity.setClueId(answer.idAsString());
    entity.setValue(answer.value());
    entity.setScore(answer.confidenceScore());
    entity.setConfirmed(answer.confirmed());
    return entity;
  }
}
