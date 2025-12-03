package uk.co.mruoc.cws.app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.mruoc.cws.api.ApiAnswer;
import uk.co.mruoc.cws.api.ApiAttempt;
import uk.co.mruoc.cws.api.ApiClue;
import uk.co.mruoc.cws.api.ApiConverter;
import uk.co.mruoc.cws.api.ApiCreatePuzzleRequest;
import uk.co.mruoc.cws.api.ApiPuzzle;
import uk.co.mruoc.cws.entity.Id;
import uk.co.mruoc.cws.usecase.CrosswordSolverFacade;

@RestController
@RequestMapping("/v1/puzzles")
@RequiredArgsConstructor
public class PuzzleController {

  private final CrosswordSolverFacade facade;
  private final ApiConverter converter;

  @Autowired
  public PuzzleController(CrosswordSolverFacade facade) {
    this(facade, new ApiConverter());
  }

  @PostMapping
  public ApiPuzzle<ApiClue> createPuzzle(@RequestBody ApiCreatePuzzleRequest request) {
    System.out.println("request " + request);
    var puzzleId = facade.createPuzzle(request.getImageUrl());
    var puzzle = facade.findPuzzleById(puzzleId);
    return converter.toApiPuzzle(puzzle);
  }

  @GetMapping("/{puzzleId}")
  public ApiPuzzle<ApiClue> getPuzzle(@PathVariable long puzzleId) {
    var puzzle = facade.findPuzzleById(puzzleId);
    return converter.toApiPuzzle(puzzle);
  }

  @PostMapping("/{puzzleId}/attempts")
  public ApiAttempt createAttempt(@PathVariable long puzzleId) {
    var attemptId = facade.createPuzzleAttempt(puzzleId);
    return getAttempt(attemptId);
  }

  @PostMapping("/{puzzleId}/attempts/{attemptId}/answers")
  public ApiAttempt updateAttemptAnswer(
      @PathVariable long attemptId, @RequestBody ApiAnswer apiAnswer) {
    var answer = converter.toAnswer(apiAnswer);
    facade.updateAttemptAnswer(attemptId, answer);
    return getAttempt(attemptId);
  }

  @DeleteMapping("/{puzzleId}/attempts/{attemptId}/answers/{answerId}")
  public ApiAttempt deleteAttemptAnswer(
      @PathVariable long attemptId, @PathVariable String answerId) {
    facade.deleteAttemptAnswer(attemptId, new Id(answerId));
    return getAttempt(attemptId);
  }

  @PostMapping("/{puzzleId}/attempts/{attemptId}/automatic-answers")
  public ApiAttempt updateAttemptAutomaticAnswers(@PathVariable long attemptId) {
    facade.solvePuzzleAttempt(attemptId);
    return getAttempt(attemptId);
  }

  @GetMapping("/{puzzleId}/attempts/{attemptId}")
  public ApiAttempt getAttempt(@PathVariable long attemptId) {
    var attempt = facade.findAttemptById(attemptId);
    return converter.toApiAttempt(attempt);
  }
}
