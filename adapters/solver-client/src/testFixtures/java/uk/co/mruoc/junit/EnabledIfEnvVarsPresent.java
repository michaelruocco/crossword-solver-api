package uk.co.mruoc.junit;

import java.util.Arrays;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class EnabledIfEnvVarsPresent implements ExecutionCondition {

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    var annotation = findAnnotation(context);
    if (annotation.isEmpty()) {
      return ConditionEvaluationResult.enabled("EnvVarPresent annotation not applied");
    }
    return annotation
        .map(EnvVarsPresent::values)
        .map(this::toResult)
        .orElseGet(
            () ->
                ConditionEvaluationResult.enabled(
                    "EnvVarPresent annotation applied but variable name(s) not specified"));
  }

  private Optional<EnvVarsPresent> findAnnotation(ExtensionContext context) {
    return context
        .getElement()
        .flatMap(e -> Optional.ofNullable(e.getAnnotation(EnvVarsPresent.class)));
  }

  private ConditionEvaluationResult toResult(String[] variableNames) {
    for (var variableName : variableNames) {
      var result = toResult(variableName);
      if (result.isDisabled()) {
        return result;
      }
    }
    return ConditionEvaluationResult.enabled(
        String.format("Env vars %s all contain values", Arrays.toString(variableNames)));
  }

  private ConditionEvaluationResult toResult(String variableName) {
    if (isVariableValuePresent(variableName)) {
      return ConditionEvaluationResult.enabled(
          String.format("Env var %s contains value", variableName));
    }
    return ConditionEvaluationResult.disabled(
        String.format("Env var %s does not contain value", variableName));
  }

  private boolean isVariableValuePresent(String name) {
    var value = System.getenv(name);
    return StringUtils.isNotEmpty(value);
  }
}
