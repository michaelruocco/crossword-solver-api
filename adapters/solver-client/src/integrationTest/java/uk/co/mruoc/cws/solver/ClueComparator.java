package uk.co.mruoc.cws.solver;

import java.util.Comparator;
import org.apache.commons.lang3.StringUtils;
import uk.co.mruoc.cws.entity.Clue;

public class ClueComparator implements Comparator<Clue> {

  private static final Comparator<Clue> COMPARATOR = build();

  @Override
  public int compare(Clue c1, Clue c2) {
    return COMPARATOR.compare(c1, c2);
  }

  private static Comparator<Clue> build() {
    return Comparator.<Clue, String>comparing(c -> c.id().toString())
        .thenComparing(clue -> normalizeText(clue.text()))
        .thenComparing(clue -> clue.lengths().toString())
        .thenComparing(Clue::pattern);
  }

  private static String normalizeText(String text) {
    if (StringUtils.isEmpty(text)) {
      return "";
    }
    return text.replace(",", "").trim();
  }
}
