package uk.co.mruoc.cws.usecase;

import uk.co.mruoc.cws.entity.Clues;

public interface ClueRanker {

  Clues rankByEase(Clues clues);
}
