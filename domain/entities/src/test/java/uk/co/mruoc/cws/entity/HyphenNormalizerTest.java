package uk.co.mruoc.cws.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.mruoc.cws.entity.HyphenNormalizer.normalizeHyphens;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class HyphenNormalizerTest {

  @ParameterizedTest
  @ValueSource(
      strings = {
        "---Waller-Bridge",
        "- - -Waller-Bridge",
        "-  -   -Waller-Bridge",
        "‒ − - Waller-Bridge",
        " – — ⁃ Waller-Bridge"
      })
  void shouldNormalizeHyphensFollowedByText(String input) {
    var normalized = normalizeHyphens(input);

    assertThat(normalized).isEqualTo("- - - Waller-Bridge");
  }

  @ParameterizedTest
  @ValueSource(strings = {"Carla ---", "Carla- - -", "Carla- -   -", "Carla ‒ − -", "Carla – — ⁃ "})
  void shouldNormalizeHyphensWithLeadingText(String input) {
    var normalized = normalizeHyphens(input);

    assertThat(normalized).isEqualTo("Carla - - -");
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "Carla --- Francis",
        "Carla- - -Francis",
        "Carla- -   -  Francis",
        "Carla  - -   -  Francis",
        "Carla ‒ − - Francis",
        "Carla – — ⁃ Francis",
      })
  void shouldNormalizeHyphensWithBothLeadingAndTrailingText(String input) {
    var normalized = normalizeHyphens(input);

    assertThat(normalized).isEqualTo("Carla - - - Francis");
  }

  @Test
  void shouldNormalizeHyphensFollowedByComma() {
    var input = "Carla - - - , model turned musician";

    var normalized = normalizeHyphens(input);

    assertThat(normalized).isEqualTo("Carla - - -, model turned musician");
  }
}
