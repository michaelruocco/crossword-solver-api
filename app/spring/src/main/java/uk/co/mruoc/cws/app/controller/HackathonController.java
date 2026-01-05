package uk.co.mruoc.cws.app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.mruoc.cws.api.ApiConverter;
import uk.co.mruoc.cws.api.ApiResult;
import uk.co.mruoc.cws.usecase.HackathonFacade;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/hackathon-attempts")
public class HackathonController {

  private final HackathonFacade facade;
  private final ApiConverter converter;

  @Autowired
  public HackathonController(HackathonFacade facade) {
    this(facade, new ApiConverter());
  }

  @PostMapping
  public void run() {
    facade.run();
  }

  @PostMapping("/{attemptId}")
  public ApiResult recordAnswers(@PathVariable long attemptId) {
    var result = facade.recordAnswers(attemptId);
    return converter.toApiResult(result);
  }
}
