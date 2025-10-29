package uk.co.mruoc.cws.api;

import lombok.Builder;

@Builder
public record ApiAttemptClueAnswer(String value, int confidenceScore, boolean confirmed) {}
