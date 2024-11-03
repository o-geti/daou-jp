/*
 * Copyright 2024 NHN (https://nhn.com) and others.
 * © NHN Corp. All rights reserved.
 */

package com.minsu.kim.daujapan.helper;

import com.minsu.kim.daujapan.config.ErrorMessageConfig;
import com.minsu.kim.daujapan.exception.ValidateCheckError;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author minsu.kim
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class LocalDateTimeParamChecker {
  private final ErrorMessageConfig errorMessageConfig;

  public Optional<Boolean> checkForBetweenFromAndTo(LocalDateTime from, LocalDateTime to)
      throws ValidateCheckError {
    if (Objects.isNull(from) && Objects.isNull(to)) {
      return Optional.empty();
    }

    if (Objects.isNull(to) || Objects.isNull(from)) {
      throw new ValidateCheckError(errorMessageConfig.getTimeRequired());
    }

    if (from.isAfter(to)) {
      throw new ValidateCheckError(errorMessageConfig.getFromIsNotAfterTo());
    }

    return Optional.of(true);
  }
}
