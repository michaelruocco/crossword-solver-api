package uk.co.mruoc.cws.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.mruoc.cws.usecase.HackathonFacade;

@RestController
@RequestMapping("/v1/hackathon-attempts")
public class HackathonController {

  private final HackathonFacade facade;

  @Autowired
  public HackathonController(HackathonFacade facade) {
    this.facade = facade;
  }

  @PostMapping
  public void run() {
    facade.run();
  }

  @PostMapping("/{attemptId}")
  public void recordAttemptAnswers(@PathVariable long attemptId) {
    facade.recordAttemptAnswers(attemptId);
  }
}
