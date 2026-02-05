package uk.co.mruoc.cws.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Collection;
import lombok.Builder;
import lombok.Data;
import lombok.With;
import uk.co.mruoc.cws.entity.Direction;

@Builder
@Data
@JsonPropertyOrder({"id", "text", "lengths", "answer"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiClue {
  private final int id;
  @JsonIgnore private final Direction direction;
  private final String text;
  private final Collection<Integer> lengths;
  @With private final String answer;
}
