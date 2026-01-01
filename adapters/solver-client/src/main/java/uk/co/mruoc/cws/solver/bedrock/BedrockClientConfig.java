package uk.co.mruoc.cws.solver.bedrock;

import java.time.Duration;
import software.amazon.awssdk.regions.Region;

public interface BedrockClientConfig {

  Region region();

  Duration connectionTimeout();

  Duration socketTimeout();

  Duration apiCallAttemptTimeout();

  Duration apiCallTimeout();
}
