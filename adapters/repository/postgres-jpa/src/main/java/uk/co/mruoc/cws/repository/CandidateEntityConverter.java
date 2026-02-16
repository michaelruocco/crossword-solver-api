package uk.co.mruoc.cws.repository;

import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Candidates;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.repository.entity.CandidateAnswerEntity;
import uk.co.mruoc.cws.repository.entity.CandidateClueEntity;
import uk.co.mruoc.cws.usecase.CandidateClueHashFactory;
import uk.co.mruoc.cws.usecase.UUIDSupplier;

@RequiredArgsConstructor
public class CandidateEntityConverter {

  private final CandidateClueHashFactory idFactory;
  private final Supplier<UUID> idSupplier;

  public CandidateEntityConverter() {
    this(new CandidateClueHashFactory(), new UUIDSupplier());
  }

  public Candidates toCandidates(CandidateClueEntity entity) {
    var clue = toClue(entity);
    var answers = toAnswers(entity.getAnswers());
    return new Candidates(clue, answers);
  }

  public CandidateClueEntity toEntity(Candidates candidates) {
    var clue = candidates.clue();
    var id = idFactory.toId(clue);
    var entity = new CandidateClueEntity();
    entity.setClueId(id);
    entity.setText(clue.text());
    entity.setType(clue.type());
    entity.setPattern(clue.pattern());
    entity.setAnswers(toAnswerEntities(id, candidates));
    return entity;
  }

  private Clue toClue(CandidateClueEntity entity) {
    return Clue.builder().text(entity.getText()).type(entity.getType()).pattern(entity.getPattern()).build();
  }

  private Collection<Answer> toAnswers(Collection<CandidateAnswerEntity> entities) {
    return entities.stream().map(this::toAnswer).toList();
  }

  private Answer toAnswer(CandidateAnswerEntity entity) {
    return Answer.builder()
        .value(entity.getValue())
        .confidenceScore(entity.getScore())
        .confirmed(false)
        .build();
  }

  private Collection<CandidateAnswerEntity> toAnswerEntities(String clueId, Candidates candidates) {
    return candidates.stream().map(answer -> toAnswerEntity(clueId, answer)).toList();
  }

  private CandidateAnswerEntity toAnswerEntity(String clueId, Answer answer) {
    var entity = new CandidateAnswerEntity();
    entity.setId(idSupplier.get());
    entity.setValue(answer.value());
    entity.setScore(answer.confidenceScore());
    entity.setClueId(clueId);
    return entity;
  }
}
