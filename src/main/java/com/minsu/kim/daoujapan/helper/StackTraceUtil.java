package com.minsu.kim.daoujapan.helper;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.core.NestedExceptionUtils;

/**
 * @author minsu.kim
 * @since 1.0
 */
public class StackTraceUtil {

  private StackTraceUtil() {}

  public static String filterStackTracePackage(Throwable throwable) {
    var throwableTarget = NestedExceptionUtils.getMostSpecificCause(throwable);

    return "\r\n"
        + throwableTarget.getMessage()
        + "\r\n"
        + Arrays.stream(throwableTarget.getStackTrace())
            .filter(
                stackTraceElement ->
                    stackTraceElement.getClassName().contains("com.minsu.kim.daoujapan"))
            .map(
                stackTraceElement ->
                    stackTraceElement.getClassName()
                        + "."
                        + stackTraceElement.getMethodName()
                        + " : "
                        + stackTraceElement.getLineNumber()
                        + " line")
            .collect(Collectors.joining("\r\n"))
        + "\r\n";
  }

  public static String printStackTrace(Throwable throwable) {
    var throwableTarget = NestedExceptionUtils.getMostSpecificCause(throwable);

    return "\r\n"
        + throwableTarget.getMessage()
        + "\r\n"
        + Arrays.stream(throwableTarget.getStackTrace())
            .map(
                stackTraceElement ->
                    stackTraceElement.getClassName()
                        + "."
                        + stackTraceElement.getMethodName()
                        + " : "
                        + stackTraceElement.getLineNumber()
                        + " line")
            .collect(Collectors.joining("\r\n"))
        + "\r\n";
  }
}
