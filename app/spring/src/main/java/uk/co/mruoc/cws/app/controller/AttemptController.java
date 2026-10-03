package uk.co.mruoc.cws.app.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.mruoc.cws.usecase.CrosswordSolverFacade;

@RestController
@RequestMapping("/v1")
@Slf4j
public class AttemptController {

  private final CrosswordSolverFacade facade;

  @Autowired
  public AttemptController(CrosswordSolverFacade facade) {
    this.facade = facade;
  }

  @DeleteMapping("/attempts")
  public ResponseEntity<Void> deleteAllAttempts() {
    facade.deleteAllAttempts();
    return ResponseEntity.noContent().build();
  }
}
