package uk.co.mruoc.junit;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

@Slf4j
public class EnabledIfTesseractInstalled implements ExecutionCondition {

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    var installed = isTesseractInstalled();
    log(installed);
    if (installed) {
      return ConditionEvaluationResult.enabled("Tesseract is installed");
    }
    return ConditionEvaluationResult.disabled("Tesseract is not installed");
  }

  private boolean isTesseractInstalled() {
    try {
      Process p = new ProcessBuilder("tesseract", "--version").start();
      p.waitFor();
      return true;
    } catch (IOException | InterruptedException e) {
      return false;
    }
  }

  private void log(boolean installed) {
    if (installed) {
      log.info("tesseract is installed");
    } else {
      log.warn("tesseract is not installed");
    }
  }
}
