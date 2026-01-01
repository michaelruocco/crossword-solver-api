package uk.co.mruoc.cws.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class Intersection {

  private final IntersectingWord across;
  private final IntersectingWord down;

  public Intersection(Word across, Word down, Coordinates coordinates) {
    this(
        new IntersectingWord(across, coordinates.x() - across.x()),
        new IntersectingWord(down, coordinates.y() - down.y()));
  }

  public int toIndex(Direction direction) {
    if (direction == Direction.ACROSS) {
      return acrossIndex();
    }
    return downIndex();
  }

  public Id toIntersectingId(Id id) {
    if (across.word.hasId(id)) {
      return down.word.id();
    }
    return across.word.id();
  }

  public Id toIntersectingId(Direction direction) {
    if (direction == Direction.ACROSS) {
      return down.id();
    }
    return across.id();
  }

  public Word getOtherWord(Word word) {
    if (across.word.hasSameId(word)) {
      return down.word;
    }
    return across.word;
  }

  public int toIntersectingIndex(Direction direction) {
    if (direction == Direction.ACROSS) {
      return downIndex();
    }
    return acrossIndex();
  }

  public int acrossIndex() {
    return across.index;
  }

  public int downIndex() {
    return down.index;
  }

  public boolean contains(Word word) {
    return across.hasSameId(word) || down.hasSameId(word);
  }

  public boolean contains(Id id) {
    return across.hasId(id) || down.hasId(id);
  }

  private record IntersectingWord(Word word, int index) {
    public Id id() {
      return word.id();
    }

    public boolean hasSameId(Word otherWord) {
      return hasId(otherWord.id());
    }

    public boolean hasId(Id id) {
      return word.id().equals(id);
    }
  }
}
