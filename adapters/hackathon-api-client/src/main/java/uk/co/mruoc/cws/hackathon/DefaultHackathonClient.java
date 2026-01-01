package uk.co.mruoc.cws.hackathon;

import lombok.Builder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.usecase.HackathonClient;

import java.util.List;

@Builder
public class DefaultHackathonClient implements HackathonClient {

    private final WebClient webClient;

    @Override
    public List<String> getPuzzleImageUrls() {
        return webClient
                .get()
                .uri("/puzzles")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })
                .block();
    }

    @Override
    public void recordAnswers(Attempt attempt) {

    }
}
