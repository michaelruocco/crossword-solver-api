package uk.co.mruoc.cws.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.ClueCandidateId;

@Slf4j
@RequiredArgsConstructor
public class CandidateDeleter {

  private final CandidateRepository repository;

  public void deleteAll() {
    log.info("deleting all candidate answers");
    repository.deleteAll();
  }

  public void delete(ClueCandidateId id) {
    log.info("deleting candidate answers for id {}", id.asString());
    repository.delete(id);
  }
}
