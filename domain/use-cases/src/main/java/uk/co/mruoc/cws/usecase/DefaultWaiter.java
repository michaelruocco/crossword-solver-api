package uk.co.mruoc.cws.usecase;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import uk.co.mruoc.cws.usecase.attempt.Waiter;

@Slf4j
public class DefaultWaiter implements Waiter {

  @Override
  public void wait(Duration duration) {
    log.info("starting wait for {}", duration);
    Awaitility.await().pollDelay(duration).until(() -> true);
    log.info("wait complete");
  }
}
