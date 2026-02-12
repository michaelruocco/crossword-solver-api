package uk.co.mruoc.cws.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.With;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public record Attempt(UUID id, @With Puzzle puzzle, @With Answers answers) {

  public UUID puzzleId() {
    return puzzle.getId();
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
    return puzzle.clue(id);
  }

  public Clues getCluesWithUnconfirmedAnswer() {
    return new Clues(getClues().stream().filter(clue -> !answers.isConfirmed(clue.id())).toList());
  }

  public Answers getConfirmedValidAnswers() {
    return answers.confirmedAnswers().validAnswers(puzzle.getClues());
  }

  public Answers getConfirmedAnswers() {
    return answers.confirmedAnswers();
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

  public Attempt deleteAnswer(Id id) {
    return withAnswers(answers.delete(id));
  }

  public Collection<Id> getIntersectingIds(Id id) {
    return puzzle.intersectingIds(id);
  }

  public Attempt unconfirmAnswer(Id id) {
    return withAnswers(answers.unconfirmAnswer(id));
  }

  public Collection<Intersection> getIntersections(Id id) {
    return puzzle.intersections(id);
  }

  public Attempt updateClue(Clue clue) {
    return withPuzzle(puzzle.withClue(clue));
  }

  public Answer forceGetConfirmedAnswer(Id id) {
    return getAnswer(id).filter(Answer::confirmed).orElseThrow();
  }

  public Attempt removeInconsistentAnswers() {
    var updatedAttempt = this;
    for (Answer answer : answers) {
      var clue = getClue(answer.id());
      if (!answer.matches(clue.pattern())) {
        updatedAttempt = deleteAnswer(answer.id());
        log.info("removed inconsistent answer {} for clue {}", answer.asString(), clue.asString());
      }
    }
    return updatedAttempt;
  }

  public boolean accepts(Answer answer) {
    var confirmedAnswers = getConfirmedAnswers();
    if (confirmedAnswers.isEmpty()) {
      return true;
    }
    var intersections = puzzle.intersections(answer.id());
    log.debug(
        "found intersections {} for answer {}",
        intersections.stream().map(i -> i.toIntersectingId(answer.id())).toList(),
        answer.asString());

    for (var intersection : intersections) {
      var intersectingId = intersection.toIntersectingId(answer.id());
      var intersectingAnswer = confirmedAnswers.findById(intersectingId);
      if (intersectingAnswer.isPresent()
          && answer.conflictsWith(intersectingAnswer.get(), intersection)) {
        return false;
      }
    }
    return true;
  }

  public String asString() {
    var lines = new ArrayList<String>();
    lines.add(String.format("attempt %s for puzzle %s", id, puzzleId()));
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

  public Attempt withClues(Clues updatedClues) {
    return withPuzzle(puzzle.withClues(updatedClues));
  }

  public int getNumberOfClues() {
    return puzzle.numberOfClues();
  }

  public Attempt removeUnconfirmedAnswers() {
    return withAnswers(answers.removeUnconfirmed());
  }

  public Grid getGrid() {
    var grid = puzzle.getGrid();
    return grid.withCells(populateCells());
  }

  private Cells populateCells() {
    var confirmedAnswers = answers.confirmedAnswers();
    if (confirmedAnswers.isEmpty()) {
      return puzzle.cells();
    }
    var populatedCells =
        confirmedAnswers.stream()
            .map(answer -> toWordCells(answer.id()).stream().toList())
            .flatMap(Collection::stream)
            .collect(Collectors.toMap(Cell::coordinates, Function.identity(), (_, c) -> c));
    for (var cell : puzzle.cells()) {
      var populatedCell = Optional.ofNullable(populatedCells.get(cell.coordinates()));
      if (populatedCell.isEmpty()) {
        populatedCells.put(cell.coordinates(), cell);
      }
    }
    return new Cells(populatedCells.values()).sort();
  }

  public String getGridAnswerValue(Id id) {
    return toWordCells(id).stream()
        .map(Cell::letter)
        .filter(Objects::nonNull)
        .map(String::valueOf)
        .collect(Collectors.joining());
  }

  private Cells toWordCells(Id id) {
    var answer = answers.confirmedAnswers().findById(id);
    var wordCells = puzzle.getWordCells(id);
    if (answer.isPresent()) {
      return wordCells.populateLetters(answer.get());
    }
    return wordCells;
  }

  private void validateClueExistsForAnswer(Answer answer) {
    var id = answer.id();
    if (!puzzle.hasClue(id)) {
      throw new ClueNotFoundForIdException(id, this);
    }
  }
}
