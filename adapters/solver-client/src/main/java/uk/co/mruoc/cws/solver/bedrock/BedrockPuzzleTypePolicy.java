package uk.co.mruoc.cws.solver.bedrock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import uk.co.mruoc.cws.entity.Puzzle;
import uk.co.mruoc.cws.solver.DeterminePuzzleTypePromptTextFactory;
import uk.co.mruoc.cws.usecase.PuzzleType;
import uk.co.mruoc.cws.usecase.PuzzleTypePolicy;

@Slf4j
@RequiredArgsConstructor
public class BedrockPuzzleTypePolicy implements PuzzleTypePolicy {

    private final PromptTextExecutor promptTextExecutor;
    private final DeterminePuzzleTypePromptTextFactory promptTextFactory;

    public BedrockPuzzleTypePolicy(BedrockRuntimeClient client) {
        this(client, new DefaultBedrockConversationConfig());
    }

    public BedrockPuzzleTypePolicy(
            BedrockRuntimeClient client, BedrockConversationConfig conversationConfig) {
        this(new PromptTextExecutor(client, conversationConfig));
    }

    public BedrockPuzzleTypePolicy(PromptTextExecutor promptTextExecutor) {
        this(promptTextExecutor, new DeterminePuzzleTypePromptTextFactory());
    }

    @Override
    public PuzzleType determinePuzzleType(Puzzle puzzle) {
        var promptText = promptTextFactory.toPromptText(puzzle.getClues());
        var result = promptTextExecutor.execute(promptText);
        log.debug("puzzle type result {} for puzzle {} with id {}", result, puzzle.getNameAndFormat(), puzzle.getId());
        return PuzzleType.valueOf(result);
    }
}
