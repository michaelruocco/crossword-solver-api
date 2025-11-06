package uk.co.mruoc.cws.entity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.With;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public record Attempt(long id, @With Puzzle puzzle, @With Answers answers) {

  public long puzzleId() {
    return puzzle.getId();
  }

  public Attempt saveAnswers(Answers otherAnswers) {
    otherAnswers.forEach(this::validateClueExistsForAnswer);
    return withAnswers(answers.save(otherAnswers));
  }

  public Attempt saveAnswer(Answer answer) {
    validateClueExistsForAnswer(answer);
    return withAnswers(answers.save(answer));
  }

  public Clues getClues() {
    return puzzle.getClues();
  }

  public Optional<Clue> getClue(Id id) {
    return puzzle.getClue(id);
  }

  public Clues getClues(Id... ids) {
    return getClues(List.of(ids));
  }

  public Clues getClues(Collection<Id> ids) {
    return new Clues(ids.stream().map(this::getClue).flatMap(Optional::stream).toList());
  }

  public Clues getCluesWithUnconfirmedAnswer() {
    return new Clues(getClues().stream().filter(clue -> !answers.isConfirmed(clue.id())).toList());
  }

  public Answers getConfirmedAnswers() {
    return answers.getConfirmedAnswers();
  }

  public Words getWords() {
    return puzzle.getWords();
  }

  public Optional<Answer> getAnswer(Id id) {
    return answers.findById(id);
  }

  public boolean isComplete() {
    return puzzle.getClues().stream().map(Clue::id).allMatch(answers::isConfirmed);
  }

  private void validateClueExistsForAnswer(Answer answer) {
    var id = answer.id();
    if (!puzzle.hasClue(id)) {
      throw new ClueNotFoundForIdException(id, this);
    }
  }

  public Attempt deleteAnswer(Id id) {
    return withAnswers(answers.delete(id));
  }

  public Collection<Id> getIntersectingIds(Id id) {
    return puzzle.getIntersectingIds(id);
  }

  public Attempt unconfirmAnswers(Collection<Id> ids) {
    return withAnswers(answers.unconfirmAnswers(ids));
  }

  public Attempt unconfirmAnswer(Id id) {
    return withAnswers(answers.unconfirmAnswer(id));
  }

  public Attempt unconfirmIntersectingAnswers(Collection<Id> ids) {
    var updatedAttempt = this;
    for (var id : ids) {
      updatedAttempt = updatedAttempt.unconfirmIntersectingAnswers(id);
    }
    return updatedAttempt;
  }

  public Attempt unconfirmIntersectingAnswers(Id id) {
    var intersectingIds = puzzle.getIntersectingIds(id);
    log.info("got intersecting ids {} for {}", intersectingIds, id);
    return withAnswers(answers.unconfirmAnswers(intersectingIds));
  }

  public Collection<Intersection> getIntersections(Id id) {
    return puzzle.getIntersections(id);
  }
}
