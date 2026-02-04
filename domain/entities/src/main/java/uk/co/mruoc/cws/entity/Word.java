package uk.co.mruoc.cws.entity;

import static uk.co.mruoc.cws.entity.Direction.ACROSS;
import static uk.co.mruoc.cws.entity.Direction.DOWN;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import lombok.Builder;
import org.apache.commons.collections4.CollectionUtils;

@Builder(toBuilder = true)
public record Word(Id id, int length, Coordinates coordinates) {
  public int numericId() {
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
    return hasId(otherWord.id());
  }

  public boolean hasId(Id otherId) {
    return id.equals(otherId);
  }

  public Direction direction() {
    return id.getDirection();
  }

  private Collection<Coordinates> allCoordinates() {
    if (direction() == (ACROSS)) {
      return allAcrossCoordinates();
    }
    return allDownCoordinates();
  }

  private Collection<Coordinates> allAcrossCoordinates() {
    var all = new ArrayList<Coordinates>();
    var y = coordinates.y();
    var startX = coordinates.x();
    for (int x = 0; x < length; x++) {
      all.add(new Coordinates(startX + x, y));
    }
    return Collections.unmodifiableCollection(all);
  }

  private Collection<Coordinates> allDownCoordinates() {
    var all = new ArrayList<Coordinates>();
    var x = coordinates.x();
    var startY = coordinates.y();
    for (int y = 0; y < length; y++) {
      all.add(new Coordinates(x, startY - y));
    }
    return Collections.unmodifiableCollection(all);
  }

  private Optional<Coordinates> findIntersectingCoordinates(Word other) {
    if (this.direction() == other.direction()) {
      return Optional.empty();
    }
    var intersectingCoordinates =
        CollectionUtils.intersection(this.allCoordinates(), other.allCoordinates());
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
    if (a.direction() == directionToFind) {
      return a;
    }
    return b;
  }
}
