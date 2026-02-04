package uk.co.mruoc.cws.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiAttemptCell {
  private final ApiCell cell;
  private final Character letter;
}
