package uk.co.mruoc.junit;

import java.io.IOException;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class EnabledIfTesseractInstalled implements ExecutionCondition {

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    if (isTesseractInstalled()) {
      return ConditionEvaluationResult.enabled("Tesseract is installed");
    }
    return ConditionEvaluationResult.enabled("Tesseract is not installed");
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
}
