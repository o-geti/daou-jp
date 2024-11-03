package com.minsu.kim.daujapan.util;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.core.NestedExceptionUtils;

/**
 * @author minsu.kim
 * @since 1.0
 */
public class StackTraceUtil {
  public static String filterStackTracePackage(Throwable throwable) {
    var throwableTarget = NestedExceptionUtils.getMostSpecificCause(throwable);

    return "\r\n"
        + throwableTarget.getMessage()
        + "\r\n"
        + Arrays.stream(throwableTarget.getStackTrace())
            .filter(
                stackTraceElement ->
                    stackTraceElement.getClassName().contains("com.minsu.kim.daujapan"))
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
