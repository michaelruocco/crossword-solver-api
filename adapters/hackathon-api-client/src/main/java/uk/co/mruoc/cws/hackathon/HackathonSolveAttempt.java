package uk.co.mruoc.cws.hackathon;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Builder(toBuilder = true)
@Data
public class HackathonSolveAttempt {

  @JsonProperty("image_name")
  private final String imageName;

  @JsonProperty("team_name")
  private final String teamName;

  private final Map<String, String> across;
  private final Map<String, String> down;
}
