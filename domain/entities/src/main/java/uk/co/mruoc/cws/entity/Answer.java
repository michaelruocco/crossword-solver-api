package uk.co.mruoc.cws.entity;

import java.util.Optional;
import lombok.Builder;
import lombok.With;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public record Answer(@With Id id, @With String value, int confidenceScore, boolean confirmed) {

  public Answer(String id, String value, int confidenceScore) {
    this(new Id(id), value, confidenceScore, false);
  }

  public static Answer noMatch(Clue clue) {
    return noMatchBuilder().id(clue.id()).build();
  }

  public static Answer.AnswerBuilder noMatchBuilder() {
    return Answer.builder().value("NO_MATCH").confidenceScore(0).confirmed(false);
  }

  public boolean hasSameValue(Answer otherAnswer) {
    var same = value.equals(otherAnswer.value);
    log.debug(
        "answer {} {} same value as {} {} {}", id, value, otherAnswer.id, otherAnswer.value, same);
    return same;
  }

  public boolean conflictsWith(Answer other, Intersection intersection) {
    int thisIndex = intersection.toIndex(id.getDirection());
    int otherIndex = intersection.toIntersectingIndex(id.getDirection());
    var conflicts =
        letterAt(thisIndex)
            .flatMap(
                thisLetter ->
                    other.letterAt(otherIndex).map(otherLetter -> thisLetter != otherLetter))
            .orElse(false);
    if (conflicts) {
      log.debug(
          "answer {} {} letter {} conflicts with {} {} letter {}",
          id,
          value,
          letterAt(thisIndex),
          other.id,
          other.value,
          other.letterAt(otherIndex));
    }
    return conflicts;
  }

  public boolean hasDirection(Direction direction) {
    return id.getDirection() == direction;
  }

  public int numericId() {
    return id.getId();
  }

  public Answer confirm() {
    return new Answer(id, value, confidenceScore, true);
  }

  public Answer unconfirm() {
    log.info("unconfirming answer {} {}", id, value);
    return new Answer(id, value, confidenceScore, false);
  }

  public Optional<Character> letterAt(int index) {
    if (index < value.length()) {
      return Optional.of(value.charAt(index));
    }
    return Optional.empty();
  }

  public boolean matches(String pattern) {
    for (var i = 0; i < pattern.length(); i++) {
      var c = pattern.charAt(i);
      if (Character.isAlphabetic(c)) {
        if (value.charAt(i) != c) {
          return false;
        }
      }
    }
    return true;
  }

  public String idAsString() {
    return id.toString();
  }

  public String asString() {
    return String.format("%s %s %d %s", id, value, confidenceScore, confirmed);
  }
}
