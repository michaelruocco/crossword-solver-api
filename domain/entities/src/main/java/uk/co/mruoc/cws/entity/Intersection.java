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

  public Id getAcrossId() {
    return across.getId();
  }

  public Id getDownId() {
    return down.getId();
  }

  public int getIndex(Direction direction) {
    if (direction == Direction.ACROSS) {
      return getAcrossIndex();
    }
    return getDownIndex();
  }

  public Word getOtherWord(Word word) {
    if (across.word.hasSameId(word)) {
      return down.word;
    }
    return across.word;
  }

  public int getIntersectingIndex(Direction direction) {
    if (direction == Direction.ACROSS) {
      return getDownIndex();
    }
    return getAcrossIndex();
  }

  public int getAcrossIndex() {
    return across.index;
  }

  public int getDownIndex() {
    return down.index;
  }

  public boolean contains(Word word) {
    return across.hasId(word) || down.hasId(word);
  }

  public boolean contains(Id id) {
    return across.hasId(id) || down.hasId(id);
  }

  public Id getId(Direction direction) {
    if (direction == Direction.ACROSS) {
      return down.getId();
    }
    return across.getId();
  }

  private record IntersectingWord(Word word, int index) {
    public Id getId() {
      return word.getId();
    }

    public boolean hasId(Word otherWord) {
      return hasId(otherWord.getId());
    }

    public boolean hasId(Id id) {
      return word.getId().equals(id);
    }
  }
}
