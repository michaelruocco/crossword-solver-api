package uk.co.mruoc.cws.usecase;

import java.util.Collection;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public class HackathonFacade {

  private final HackathonClient hackathonClient;
  private final CrosswordSolverFacade solverFacade;

  public void run() {
    Collection<String> imageUrls = hackathonClient.getPuzzleImageUrls();
    imageUrls.forEach(this::run);
  }

  public void run(String imageUrl) {
    log.info("running hackathon puzzle {}", imageUrl);
    var puzzleId = solverFacade.createPuzzle(imageUrl);
    var attemptId = solverFacade.createPuzzleAttempt(puzzleId);
    log.info("created attempt {} for puzzle {}", attemptId, puzzleId);
    solverFacade.syncSolvePuzzleAttempt(attemptId);
    recordAttemptAnswers(attemptId);
  }

  public void recordAttemptAnswers(long attemptId) {
    var attempt = solverFacade.findAttemptById(attemptId);
    log.info("recording answers for attempt {}", attempt.asString());
    hackathonClient.recordAnswers(attempt);
  }
}
