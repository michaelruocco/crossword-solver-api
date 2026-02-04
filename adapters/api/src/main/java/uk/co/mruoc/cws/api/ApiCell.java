package uk.co.mruoc.cws.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;
import uk.co.mruoc.cws.entity.Coordinates;

@Builder
@Data
@JsonPropertyOrder({"id", "black", "coordinates"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiCell {
  private final Integer id;
  private final boolean black;
  private final Coordinates coordinates;
}
