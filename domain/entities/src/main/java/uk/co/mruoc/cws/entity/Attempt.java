package uk.co.mruoc.cws.entity;

import java.util.Optional;
import lombok.Builder;
import lombok.With;

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
}
