package uk.co.mruoc.cws.app.controller;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.mruoc.cws.api.ApiCandidateIdConverter;
import uk.co.mruoc.cws.api.ApiClueCandidateId;
import uk.co.mruoc.cws.usecase.CandidateDeleter;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Slf4j
public class CandidateController {

  private final CandidateDeleter deleter;
  private final ApiCandidateIdConverter converter;

  @Autowired
  public CandidateController(CandidateDeleter deleter) {
    this(deleter, new ApiCandidateIdConverter());
  }

  @DeleteMapping("/candidate-answers")
  public ResponseEntity<Void> deleteAllCandidateAnswers(
      @RequestBody(required = false) ApiClueCandidateId apiId) {
    Optional.ofNullable(apiId)
        .map(converter::toClueCandidateId)
        .ifPresentOrElse(deleter::delete, deleter::deleteAll);
    return ResponseEntity.noContent().build();
  }
}
