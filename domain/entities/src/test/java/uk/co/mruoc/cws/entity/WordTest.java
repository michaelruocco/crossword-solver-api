package uk.co.mruoc.cws.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class WordTest {

  @Test
  void shouldFindIntersectionOfTwoWords() {
    /*
    Y
    4
    3       D
    2 A A A X A A
    1       D
    0       D
      0 1 2 3 4 5 X
    */
    var across = across().coordinates(new Coordinates(0, 2)).length(5).build();
    var down = down().coordinates(new Coordinates(3, 3)).length(4).build();

    var intersection = across.findIntersectionBetween(down).orElseThrow();

    assertThat(intersection.acrossIndex()).isEqualTo(3);
    assertThat(intersection.downIndex()).isEqualTo(1);
  }

  @Test
  void shouldFindIntersectionOfTwoWordsThatIntersectOnLastLetter() {
    /*
    Y
    4 D
    3 D
    2 D
    1 D
    0 X A A A A
      0 1 2 3 4 5 X
    */
    var across = across().coordinates(new Coordinates(0, 0)).length(5).build();
    var down = down().coordinates(new Coordinates(0, 4)).length(5).build();

    var intersection = across.findIntersectionBetween(down).orElseThrow();

    assertThat(intersection.acrossIndex()).isEqualTo(0);
    assertThat(intersection.downIndex()).isEqualTo(4);
  }

  @Test
  void shouldFindIntersectionOfTwoWordsThatIntersectOnLastLetter1() {
    /*
    Y
    5         D
    4         D
    3         D
    2         D
    1         X A A A A
      0 1 2 3 4 5 6 7 8 X
    */
    var across = across().coordinates(new Coordinates(4, 1)).length(5).build();
    var down = down().coordinates(new Coordinates(4, 5)).length(5).build();

    var intersection = across.findIntersectionBetween(down).orElseThrow();

    assertThat(intersection.acrossIndex()).isEqualTo(0);
    assertThat(intersection.downIndex()).isEqualTo(4);
  }

  @Test
  void shouldNotReturnIntersectionIfNotPresent() {
    /*
    Y
    3       D
    2 A A   D
    1       D
    0       D
      0 1 2 3 4 5 X
    */
    var across = across().coordinates(new Coordinates(0, 2)).length(2).build();
    var down = down().coordinates(new Coordinates(3, 3)).length(4).build();

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
