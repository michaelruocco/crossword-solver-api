package uk.co.mruoc.cws.usecase;

import uk.co.mruoc.cws.entity.ClueType;
import uk.co.mruoc.cws.entity.Clues;

public interface ClueTypePolicy {

  ClueType determineClueType(Clues clues);
}
