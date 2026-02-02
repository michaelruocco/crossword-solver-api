package uk.co.mruoc.cws.entity;

import java.util.Objects;
import java.util.Optional;

public record NewCell(Coordinates coordinates, boolean black, Integer id) {

    public static NewCell blackCell(Coordinates coordinates) {
        return new NewCell(coordinates, true, null);
    }

    public static NewCell whiteCell(Coordinates coordinates) {
        return new NewCell(coordinates, false, null);
    }

    public static NewCell idCell(Coordinates coordinates, Integer id) {
        return new NewCell(coordinates, false, id);
    }

    public boolean hasId() {
        return getId().isPresent();
    }

    public int forceGetId() {
        return getId().orElseThrow();
    }

    public Optional<Integer> getId() {
        return Optional.ofNullable(id);
    }
}
