package uk.co.mruoc.cws.solver.textract;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.opencv.core.Mat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class MatLogger {

  private static final IMatLogger logger;

  static {
    logger = build();
    logger.init();
  }

  public static void debug(Mat mat, String name) {
    logger.debug(mat, name);
  }

  public static void deleteAll() {
    logger.deleteAll();
  }

  private static IMatLogger build() {
    if (Boolean.parseBoolean(System.getenv("MAT_DEBUG_ENABLED"))) {
      log.info("mat logger is enabled");
      return new DefaultMatLogger(getDebugFolderPath());
    }
    log.info("mat logger is disabled");
    return new NoopMatLogger();
  }

  private static String getDebugFolderPath() {
    var path = System.getenv("MAT_DEBUG_FOLDER_PATH");
    if (StringUtils.isEmpty(path)) {
      return "integration-test-files/debug-images";
    }
    return path;
  }
}
