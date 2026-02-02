package uk.co.mruoc.cws.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;
import uk.co.mruoc.cws.usecase.CellExtractor;

@Configuration
public class TextractSolverClientConfig {

  @Bean
  public TextractClient textractClient() {
    return TextractClient.builder()
        .credentialsProvider(DefaultCredentialsProvider.builder().build())
        .region(Region.EU_WEST_1)
        .build();
  }

  @Bean
  public CellExtractor textractCellExtractor(TextractClient client) {
    return new TextractCellExtractor(client);
  }
}
