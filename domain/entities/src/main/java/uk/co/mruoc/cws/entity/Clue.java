package uk.co.mruoc.cws.entity;

import java.util.Collection;
import lombok.Builder;
import lombok.With;
import org.apache.commons.lang3.StringUtils;

@Builder(toBuilder = true)
public record Clue(
    @With Id id,
    @With String text,
    Collection<Integer> lengths,
    @With ClueType type,
    @With String pattern,
    @With Candidates candidates) {

  private static final String UNKNOWN = "?";

  public int numericId() {
    return id.getNumber();
  }

  public Direction direction() {
    return id.getDirection();
  }

  public Clue withPatternLetter(int index, char letter) {
    char[] chars = pattern().toCharArray();
    chars[index] = letter;
    return withPattern(new String(chars));
  }

  public Clue normalizeHyphens() {
    return withText(HyphenNormalizer.normalizeHyphens(text));
  }

  public int totalLength() {
    return lengths.stream().mapToInt(Integer::intValue).sum();
  }

  public boolean isConstrainedByAtLeastNChars(int n) {
    return knownLetterCount() >= n;
  }

  public int knownLetterCount() {
    return pattern().replace(UNKNOWN, "").length();
  }

  public String asString() {
    if (knownLetterCount() > 0) {
      return String.format("%s %s %s", id, text, pattern);
    }
    return String.format("%s %s", id, text);
  }

  public String pattern() {
    if (StringUtils.isEmpty(pattern)) {
      return StringUtils.repeat(UNKNOWN, totalLength());
    }
    return pattern;
  }
}
