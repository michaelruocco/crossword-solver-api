package uk.co.mruoc.cws.entity;

import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HyphenNormalizer {

  private static final Pattern TRIPLE_HYPHEN = Pattern.compile("-\\s*-\\s*-\\s*");

  public static String normalizeHyphens(String input) {
    var normalized =
        StringUtils.replaceEach(
            input,
            new String[] {"–", "—", "‐", "-", "−", "⁃", "‒"},
            new String[] {"-", "-", "-", "-", "-", "-", "-"});
    normalized = TRIPLE_HYPHEN.matcher(normalized).replaceAll("- - -");
    // Ensure exactly one space before triple-hyphen unless at start
    normalized = normalized.replaceAll("(?<=\\S)\\s*- - -", " - - -");
    // Ensure exactly one space after triple-hyphen when followed by text,
    // but not before punctuation such as a comma.
    normalized = normalized.replaceAll("- - -(?!\\s|$|,)", "- - - ");
    return normalized.trim();
  }
}
