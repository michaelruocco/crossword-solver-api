package uk.co.mruoc.cws.api;

import uk.co.mruoc.cws.entity.ClueCandidateId;

public class ApiCandidateIdConverter {

  public ClueCandidateId toClueCandidateId(ApiClueCandidateId apiId) {
    return ClueCandidateId.builder().text(apiId.getText()).pattern(apiId.getPattern()).build();
  }
}
