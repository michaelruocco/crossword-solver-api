package uk.co.mruoc.cws.solver.bedrock;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;
import uk.co.mruoc.cws.solver.textract.TextractCellExtractor;
import uk.co.mruoc.cws.usecase.CellExtractor;
import uk.co.mruoc.cws.usecase.StubImageDownloader;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TextractClientFactory {

  public static CellExtractor buildCellExtractor() {
    return new TextractCellExtractor(new StubImageDownloader(), buildClient());
  }

  private static TextractClient buildClient() {
    return TextractClient.builder()
        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
        .region(Region.EU_WEST_1)
        .build();
  }
}
