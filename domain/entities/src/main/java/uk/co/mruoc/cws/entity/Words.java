package uk.co.mruoc.cws.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Stream;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@ToString
@Data
public class Words {

  private final Collection<Word> words;
  private final Collection<Intersection> intersections;

  public Stream<Word> stream() {
    return words.stream();
  }

  public Collection<Id> getIntersectingIds(Id id) {
    return getIntersectingWords(findById(id)).stream().map(Word::id).toList();
  }

  public Coordinates getCoordinates(int id) {
    return findByNumericId(id).coordinates();
  }

  public Words(Collection<Word> words) {
    this(words, buildIntersections(words));
  }

  private Word findByNumericId(int id) {
    return words.stream()
        .filter(word -> word.numericId() == id)
        .findFirst()
        .orElseThrow(() -> new WordNotFoundForNumericIdException(id));
  }

  public Word findById(Id id) {
    return words.stream()
        .filter(word -> word.hasId(id))
        .findFirst()
        .orElseThrow(() -> new WordNotFoundForIdException(id));
  }

  public Collection<Word> getIntersectingWords(Word word) {
    return getIntersections(word).stream()
        .map(intersection -> intersection.getOtherWord(word))
        .toList();
  }

  public Collection<Intersection> getIntersections(Word word) {
    return intersections.stream().filter(intersection -> intersection.contains(word)).toList();
  }

  public Collection<Intersection> getIntersections(Id id) {
    return intersections.stream().filter(intersection -> intersection.contains(id)).toList();
  }

  private static Collection<Intersection> buildIntersections(Collection<Word> words) {
    var intersections = new HashSet<Intersection>();
    for (var word : words) {
      var otherWords = toOtherWords(word, words);
      for (var otherWord : otherWords) {
        word.findIntersectionBetween(otherWord).ifPresent(intersections::add);
      }
    }
    return Collections.unmodifiableCollection(intersections);
  }

  private static Collection<Word> toOtherWords(Word word, Collection<Word> words) {
    var copy = new ArrayList<>(words);
    copy.remove(word);
    return Collections.unmodifiableCollection(copy);
  }
}
