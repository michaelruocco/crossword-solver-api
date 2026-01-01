package uk.co.mruoc.cws.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class WordTest {

  @Test
  void shouldFindIntersectionOfTwoWords() {
    /*
      0 1 2 3 4 5 X
    0       D
    1 A A A X A A
    2       D
    3       D
    Y
    */
    var across = across().coordinates(new Coordinates(0, 1)).length(5).build();
    var down = down().coordinates(new Coordinates(3, 0)).length(4).build();

    var intersection = across.findIntersectionBetween(down).orElseThrow();

    assertThat(intersection.acrossIndex()).isEqualTo(3);
    assertThat(intersection.downIndex()).isEqualTo(1);
  }

  @Test
  void shouldFindIntersectionOfTwoWordsThatIntersectOnLastLetter() {
    /*
      0 1 2 3 4 5 X
    0 D
    1 D
    2 D
    3 D
    4 X A A A A
    Y
    */
    var across = across().coordinates(new Coordinates(0, 4)).length(5).build();
    var down = down().coordinates(new Coordinates(0, 0)).length(5).build();

    var intersection = across.findIntersectionBetween(down).orElseThrow();

    assertThat(intersection.acrossIndex()).isEqualTo(0);
    assertThat(intersection.downIndex()).isEqualTo(4);
  }

  @Test
  void shouldFindIntersectionOfTwoWordsThatIntersectOnLastLetter1() {
    /*
      0 1 2 3 4 5 6 7 8 X
    0
    1
    2
    3
    4         D
    5         D
    6         D
    7         D
    8         X A A A A
    Y
    */
    var across = across().coordinates(new Coordinates(4, 8)).length(5).build();
    var down = down().coordinates(new Coordinates(4, 4)).length(5).build();

    var intersection = across.findIntersectionBetween(down).orElseThrow();

    assertThat(intersection.acrossIndex()).isEqualTo(0);
    assertThat(intersection.downIndex()).isEqualTo(4);
  }

  @Test
  void shouldNotReturnIntersectionIfNotPresent() {
    /*
      0 1 2 3 4 5 X
    0       D
    1 A A   D
    2       D
    3       D
    Y
    */
    var across = across().coordinates(new Coordinates(0, 1)).length(2).build();
    var down = down().coordinates(new Coordinates(3, 0)).length(4).build();

    var intersection = across.findIntersectionBetween(down);

    assertThat(intersection).isEmpty();
  }

  private static Word.WordBuilder across() {
    return Word.builder().id(Id.across(1));
  }

  private static Word.WordBuilder down() {
    return Word.builder().id(Id.down(1));
  }
}
