package uk.co.mruoc.cws.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

@Builder
@Data
public class ApiClues {
  private final Collection<ApiClue> across;
  private final Collection<ApiClue> down;

  @JsonIgnore
  public int getCount() {
    return toCount(across) + toCount(down);
  }

  private static int toCount(Collection<ApiClue> clues) {
    if (CollectionUtils.isEmpty(clues)) {
      return 0;
    }
    return clues.size();
  }
}
