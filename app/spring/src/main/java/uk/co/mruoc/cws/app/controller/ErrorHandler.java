package uk.co.mruoc.cws.app.controller;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import uk.co.mruoc.cws.usecase.puzzle.PuzzleNotFoundByIdException;

@ControllerAdvice
@Slf4j
public class ErrorHandler {

  @ExceptionHandler(PuzzleNotFoundByIdException.class)
  public ResponseEntity<ProblemDetail> handle(PuzzleNotFoundByIdException e) {
    log.warn(e.getMessage(), e);
    return toResponse(NOT_FOUND, e.getMessage());
  }

  private static ResponseEntity<ProblemDetail> toResponse(HttpStatus status, String detail) {
    return ResponseEntity.of(ProblemDetail.forStatusAndDetail(status, detail)).build();
  }
}
