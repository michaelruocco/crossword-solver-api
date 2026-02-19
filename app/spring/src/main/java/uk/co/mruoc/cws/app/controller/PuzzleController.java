package uk.co.mruoc.cws.app.controller;

import java.util.Collection;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.co.mruoc.cws.api.ApiAnswer;
import uk.co.mruoc.cws.api.ApiAttempt;
import uk.co.mruoc.cws.api.ApiConverter;
import uk.co.mruoc.cws.api.ApiCreatePuzzleRequest;
import uk.co.mruoc.cws.api.ApiPuzzle;
import uk.co.mruoc.cws.api.ApiPuzzleSummary;
import uk.co.mruoc.cws.entity.Id;
import uk.co.mruoc.cws.usecase.CrosswordSolverFacade;

@RestController
@RequestMapping("/v1/puzzles")
@RequiredArgsConstructor
@Slf4j
public class PuzzleController {

  private final CrosswordSolverFacade facade;
  private final ApiConverter apiConverter;
  private final MultipartFileConverter multipartFileConverter;
  private final ImageResponseFactory imageResponseFactory;

  @Autowired
  public PuzzleController(CrosswordSolverFacade facade) {
    this(facade, new ApiConverter(), new MultipartFileConverter(), new ImageResponseFactory());
  }

  @GetMapping("/puzzle-summaries")
  public Collection<ApiPuzzleSummary> getPuzzleSummaries() {
    var summaries = facade.findPuzzleSummaries();
    return apiConverter.toApiSummaries(summaries);
  }

  @PostMapping("/puzzles")
  public ApiPuzzle createPuzzle(@RequestBody ApiCreatePuzzleRequest request) {
    var puzzleId = facade.createPuzzle(request.getImageUrl());
    var puzzle = facade.findPuzzleById(puzzleId);
    return apiConverter.toApiPuzzle(puzzle);
  }

  @PostMapping(path = "/puzzles/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiPuzzle createPuzzle(@RequestParam("file") MultipartFile file) {
    var image = multipartFileConverter.toImage(file);
    var puzzleId = facade.createPuzzle(image);
    var puzzle = facade.findPuzzleById(puzzleId);
    return apiConverter.toApiPuzzle(puzzle);
  }

  @GetMapping("/puzzles/{puzzleId}")
  public ApiPuzzle getPuzzle(@PathVariable UUID puzzleId) {
    var puzzle = facade.findPuzzleById(puzzleId);
    return apiConverter.toApiPuzzle(puzzle);
  }

  @GetMapping("/puzzles/{puzzleId}/grid-images")
  public ResponseEntity<byte[]> getPuzzleGridImage(@PathVariable UUID puzzleId) {
    log.info("getting grid image of puzzle {}", puzzleId);
    var gridImage = facade.findPuzzleGridImage(puzzleId);
    return imageResponseFactory.toResponse(gridImage);
  }

  @PostMapping("/puzzles/{puzzleId}/attempts")
  public ApiAttempt createAttempt(@PathVariable UUID puzzleId) {
    var attemptId = facade.createPuzzleAttempt(puzzleId);
    return getAttempt(puzzleId, attemptId);
  }

  @PostMapping("/puzzles/{puzzleId}/attempts/{attemptId}/answers")
  public ApiAttempt updateAttemptAnswer(
      @PathVariable UUID puzzleId, @PathVariable UUID attemptId, @RequestBody ApiAnswer apiAnswer) {
    log.info(
        "posting answer {} for attempt {} on puzzle {}", apiAnswer.getValue(), attemptId, puzzleId);
    var answer = apiConverter.toAnswer(apiAnswer);
    facade.updateAttemptAnswer(attemptId, answer);
    return getAttempt(puzzleId, attemptId);
  }

  @DeleteMapping("/puzzles/{puzzleId}/attempts/{attemptId}/answers/{answerId}")
  public ApiAttempt deleteAttemptAnswer(
      @PathVariable UUID puzzleId, @PathVariable UUID attemptId, @PathVariable String answerId) {
    log.info("deleting answer for attempt {} on puzzle {}", attemptId, puzzleId);
    facade.deleteAttemptAnswer(attemptId, new Id(answerId));
    return getAttempt(puzzleId, attemptId);
  }

  @PostMapping("/puzzles/{puzzleId}/attempts/{attemptId}/automatic-answers")
  public ApiAttempt updateAttemptAutomaticAnswers(
      @PathVariable UUID puzzleId, @PathVariable UUID attemptId) {
    log.info("auto solving attempt {} on puzzle {}", attemptId, puzzleId);
    facade.asyncSolvePuzzleAttempt(attemptId);
    return getAttempt(puzzleId, attemptId);
  }

  @GetMapping("/puzzles/{puzzleId}/attempts/{attemptId}")
  public ApiAttempt getAttempt(@PathVariable UUID puzzleId, @PathVariable UUID attemptId) {
    log.info("getting attempt {} for puzzle {}", attemptId, puzzleId);
    var attempt = facade.findAttemptById(attemptId);
    return apiConverter.toApiAttempt(attempt);
  }

  @GetMapping("/puzzles/{puzzleId}/attempts/{attemptId}/grid-images")
  public ResponseEntity<byte[]> getAttemptGridImage(
      @PathVariable UUID puzzleId, @PathVariable UUID attemptId) {
    log.info("getting grid image of attempt {} for puzzle {}", attemptId, puzzleId);
    var gridImage = facade.findAttemptGridImage(attemptId);
    return imageResponseFactory.toResponse(gridImage);
  }
}
