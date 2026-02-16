package uk.co.mruoc.cws.usecase;

import uk.co.mruoc.cws.entity.Puzzle;

public interface PuzzleTypePolicy {

    PuzzleType determinePuzzleType(Puzzle puzzle);
}
