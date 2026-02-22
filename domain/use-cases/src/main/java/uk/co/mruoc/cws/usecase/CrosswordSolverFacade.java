package uk.co.mruoc.cws.usecase;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.UUID;
import lombok.Builder;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.Id;
import uk.co.mruoc.cws.entity.Puzzle;
import uk.co.mruoc.cws.entity.PuzzleSummary;
import uk.co.mruoc.cws.usecase.attempt.AttemptService;
import uk.co.mruoc.cws.usecase.puzzle.PuzzleService;

@Builder
public class CrosswordSolverFacade {

  private final PuzzleService puzzleService;
  private final AttemptService attemptService;
  private final AnswerDeleter answerDeleter;
  private final GridImageFactory gridImageFactory;

  public Collection<PuzzleSummary> findPuzzleSummaries() {
    return puzzleService.findAllSummaries();
  }

  public UUID createPuzzle(String imageUrl) {
    return puzzleService.create(imageUrl);
  }

  public UUID createPuzzle(Image image) {
    return puzzleService.create(image);
  }

  public Puzzle findPuzzleById(UUID puzzleId) {
    return puzzleService.findById(puzzleId);
  }

  public BufferedImage findPuzzleGridImage(UUID puzzleId) {
    var puzzle = findPuzzleById(puzzleId);
    return gridImageFactory.toImage(puzzle.getGrid());
  }

  public UUID createPuzzleAttempt(UUID puzzleId) {
    return attemptService.createAttempt(puzzleId);
  }

  public void updateAttemptAnswer(UUID attemptId, Answer answer) {
    attemptService.updateAnswer(attemptId, answer);
  }

  public BufferedImage findAttemptGridImage(UUID attemptId) {
    var attempt = findAttemptById(attemptId);
    return gridImageFactory.toImage(attempt.getGrid());
  }

  public Attempt findAttemptById(UUID attemptId) {
    return attemptService.findById(attemptId);
  }

  public void syncSolvePuzzleAttempt(UUID attemptId) {
    attemptService.syncSolveAttempt(attemptId);
  }

  public void asyncSolvePuzzleAttempt(UUID attemptId) {
    attemptService.asyncSolveAttempt(attemptId);
  }

  public void deleteAttemptAnswer(UUID attemptId, Id id) {
    answerDeleter.deleteAnswer(attemptId, id);
  }
}
