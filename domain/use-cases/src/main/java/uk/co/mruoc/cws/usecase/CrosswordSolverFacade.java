package uk.co.mruoc.cws.usecase;

import lombok.Builder;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.Id;
import uk.co.mruoc.cws.entity.Puzzle;
import uk.co.mruoc.cws.usecase.attempt.AttemptService;
import uk.co.mruoc.cws.usecase.puzzle.PuzzleService;

@Builder
public class CrosswordSolverFacade {

  private final PuzzleService puzzleService;
  private final AttemptService attemptService;
  private final AnswerDeleter answerDeleter;

  public long createPuzzle(String imageUrl) {
    return puzzleService.create(imageUrl);
  }

  public Puzzle findPuzzleById(long puzzleId) {
    return puzzleService.findById(puzzleId);
  }

  public long createPuzzleAttempt(long puzzleId) {
    return attemptService.createAttempt(puzzleId);
  }

  public void updateAttemptAnswer(long attemptId, Answer answer) {
    attemptService.updateAnswer(attemptId, answer);
  }

  public Attempt findAttemptById(long attemptId) {
    return attemptService.findById(attemptId);
  }

  public void solvePuzzleAttempt(long attemptId) {
    attemptService.solveAttempt(attemptId);
  }

  public void deleteAttemptAnswer(long attemptId, Id id) {
    answerDeleter.deleteAnswer(attemptId, id);
  }
}
