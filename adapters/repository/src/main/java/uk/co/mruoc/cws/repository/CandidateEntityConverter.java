package uk.co.mruoc.cws.repository;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Candidates;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.repository.entity.CandidateAnswerEntity;
import uk.co.mruoc.cws.repository.entity.CandidateClueEntity;

@RequiredArgsConstructor
public class CandidateEntityConverter {

  private final CandidateClueEntityIdFactory idFactory;

  public CandidateEntityConverter() {
    this(new CandidateClueEntityIdFactory());
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
    entity.setPattern(clue.pattern());
    entity.setLengths(clue.lengths());
    entity.setAnswers(toAnswerEntities(id, candidates));
    return entity;
  }

  private Clue toClue(CandidateClueEntity entity) {
    return Clue.builder()
        .text(entity.getText())
        .pattern(entity.getPattern())
        .lengths(entity.getLengths().stream().toList())
        .build();
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
    entity.setValue(answer.value());
    entity.setScore(answer.confidenceScore());
    entity.setClueId(clueId);
    return entity;
  }
}
