package uk.co.mruoc.cws.usecase;

import lombok.Builder;
import uk.co.mruoc.cws.entity.Clues;

@Builder
public class CluesFactory {

    private final ClueExtractor clueExtractor;
    private final ClueTypePolicy clueTypePolicy;

    public Clues build(Image image) {
        var clues = clueExtractor.extractClues(image);
        var type = clueTypePolicy.determineClueType(clues);
        return clues.withType(type);
    }
}
