package uk.co.mruoc.cws.entity;

import lombok.Builder;

@Builder
public record ClueCandidateId(String text, String pattern) {

  public String asString() {
    return String.format("%s %s", text, pattern);
  }
}
