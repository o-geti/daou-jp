package com.minsu.kim.daoujapan.exception;

/**
 * @author minsu.kim
 * @since 1.0
 */
public class ValidateCheckError extends RuntimeException {

  public ValidateCheckError(String timeRequired) {
    super(timeRequired);
  }
}
