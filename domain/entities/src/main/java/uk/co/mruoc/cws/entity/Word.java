package uk.co.mruoc.cws.entity;

import static uk.co.mruoc.cws.entity.Direction.ACROSS;
import static uk.co.mruoc.cws.entity.Direction.DOWN;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

@RequiredArgsConstructor
@Builder(toBuilder = true)
@Data
public class Word {
  private final Id id;
  private final int length;
  private final Coordinates coordinates;

  public int getNumericId() {
    return id.getId();
  }

  public int x() {
    return coordinates.x();
  }

  public int y() {
    return coordinates.y();
  }

  public Optional<Intersection> findIntersectionBetween(Word other) {
    return findIntersectingCoordinates(other)
        .map(intersectingCoordinates -> toIntersection(other, intersectingCoordinates));
  }

  public boolean hasSameId(Word otherWord) {
    return hasId(otherWord.getId());
  }

  public boolean hasId(Id otherId) {
    return id.equals(otherId);
  }

  public Direction getDirection() {
    return id.getDirection();
  }

  private Collection<Coordinates> getAllCoordinates() {
    if (getDirection() == (ACROSS)) {
      return getAllAcrossCoordinates();
    }
    return getAllDownCoordinates();
  }

  private Collection<Coordinates> getAllAcrossCoordinates() {
    var all = new ArrayList<Coordinates>();
    var y = coordinates.y();
    var startX = coordinates.x();
    for (int x = 0; x < length; x++) {
      all.add(new Coordinates(startX + x, y));
    }
    return Collections.unmodifiableCollection(all);
  }

  private Collection<Coordinates> getAllDownCoordinates() {
    var all = new ArrayList<Coordinates>();
    var x = coordinates.x();
    var startY = coordinates.y();
    for (int y = 0; y < length; y++) {
      all.add(new Coordinates(x, startY + y));
    }
    return Collections.unmodifiableCollection(all);
  }

  private Optional<Coordinates> findIntersectingCoordinates(Word other) {
    if (this.getDirection() == other.getDirection()) {
      return Optional.empty();
    }
    var intersectingCoordinates =
        CollectionUtils.intersection(this.getAllCoordinates(), other.getAllCoordinates());
    if (intersectingCoordinates.isEmpty()) {
      return Optional.empty();
    }
    if (intersectingCoordinates.size() > 1) {
      throw new IllegalStateException("should not have more than one intersection point");
    }
    return intersectingCoordinates.stream().findFirst();
  }

  private Intersection toIntersection(Word other, Coordinates coordinates) {
    var across = findDirection(this, other, ACROSS);
    var down = findDirection(this, other, DOWN);
    return new Intersection(across, down, coordinates);
  }

  private static Word findDirection(Word a, Word b, Direction directionToFind) {
    if (a.getDirection() == directionToFind) {
      return a;
    }
    return b;
  }
}
