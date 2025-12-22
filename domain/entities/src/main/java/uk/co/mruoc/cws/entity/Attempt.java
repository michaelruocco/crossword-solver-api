package uk.co.mruoc.cws.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.With;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public record Attempt(long id, @With Puzzle puzzle, @With Answers answers) {

  public long puzzleId() {
    return puzzle.getId();
  }

  public int confirmedAnswerCount() {
    return getConfirmedAnswers().size();
  }

  public boolean hasConfirmedAnswers() {
    return !getConfirmedAnswers().isEmpty();
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

  public Clue getClue(Id id) {
    return puzzle.getClue(id);
  }

  public Clues getCluesWithUnconfirmedAnswer() {
    return new Clues(getClues().stream().filter(clue -> !answers.isConfirmed(clue.id())).toList());
  }

  public Answers getConfirmedValidAnswers() {
    return answers.getConfirmedAnswers().getValidAnswers(puzzle.getClues());
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

  public Attempt unconfirmAnswer(Id id) {
    return withAnswers(answers.unconfirmAnswer(id));
  }

  public Collection<Intersection> getIntersections(Id id) {
    return puzzle.getIntersections(id);
  }

  public Attempt updateClue(Clue clue) {
    return withPuzzle(puzzle.updateClue(clue));
  }

  public Answer forceGetConfirmedAnswer(Id id) {
    return getAnswer(id).filter(Answer::confirmed).orElseThrow();
  }

  public boolean accepts(Answer answer) {
    var confirmedAnswers = getConfirmedAnswers();
    if (confirmedAnswers.isEmpty()) {
      return true;
    }
    var intersections = puzzle.getIntersections(answer.id());
    log.debug(
        "found intersections {} for answer {}",
        intersections.stream().map(i -> i.getIntersectingId(answer.id())).toList(),
        answer.asString());

    for (var intersection : intersections) {
      var intersectingId = intersection.getIntersectingId(answer.id());
      var intersectingAnswer = confirmedAnswers.findById(intersectingId);
      if (intersectingAnswer.isPresent()) {
        if (answer.conflictsWith(intersectingAnswer.get(), intersection)) {
          return false;
        }
      }
    }
    return true;
  }

  public boolean hasConfirmedAnswer(Id id) {
    return getConfirmedAnswers().contains(id);
  }

  public String asString() {
    var lines = new ArrayList<String>();
    lines.add(String.format("attempt %d for puzzle %d", id, puzzleId()));
    lines.add(String.format("%d clues", getClues().size()));
    lines.add(String.format("%d confirmed answers", getConfirmedAnswers().size()));
    getClues().stream().map(this::toLine).forEach(lines::add);
    return lines.stream().collect(Collectors.joining(System.lineSeparator()));
  }

  private String toLine(Clue clue) {
    var answer = getAnswer(clue.id());
    var answerString = answer.map(Answer::asString).orElse("not answered");
    return String.format("%s -> %s", clue.asString(), answerString);
  }
}
