package uk.co.mruoc.cws.entity;

import java.util.Collection;
import lombok.Builder;
import lombok.With;
import org.apache.commons.lang3.StringUtils;

@Builder
public record Clue(Id id, String text, Collection<Integer> lengths, @With String pattern) {

  private static final String UNKNOWN = "?";

  public int numericId() {
    return id.getId();
  }

  public Direction direction() {
    return id.getDirection();
  }

  @Override
  public String pattern() {
    if (StringUtils.isEmpty(pattern)) {
      return UNKNOWN.repeat(getTotalLength());
    }
    return pattern;
  }

  public int getTotalLength() {
    return lengths.stream().mapToInt(Integer::intValue).sum();
  }

  public int getPatternCharCount() {
    if (StringUtils.isEmpty(pattern)) {
      return 0;
    }
    return pattern.replace(UNKNOWN, "").length();
  }

  public boolean hasPatternChars() {
    return getPatternCharCount() > 0;
  }
}
