package com.minsu.kim.daoujapan.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.minsu.kim.daoujapan.config.ErrorMessageConfig;
import com.minsu.kim.daoujapan.exception.ValidateCheckError;

/**
 * @author minsu.kim
 * @since 1.0
 */
@SpringBootTest(classes = {LocalDateTimeParamChecker.class, ErrorMessageConfig.class})
class LocalDateTimeParamCheckerTest {
  @Autowired LocalDateTimeParamChecker checker;
  @Autowired ErrorMessageConfig errorMessageConfig;

  @Test
  @DisplayName("from, to가 정상적으로 LocalDateTime으로 입력이 들어오면 true를 리턴한다.")
  void checkForBetweenFromAndTo() {
    var now = LocalDateTime.now();
    var nowPlusOneDay = now.plusDays(1);

    assertThat(checker.checkForBetweenFromAndTo(now, nowPlusOneDay)).isNotEmpty().hasValue(true);
  }

  @Test
  @DisplayName("from, to 둘중 하나라도 null이라면 ValidateCheckError를 반환한다.")
  void checkForBetweenFromAndToErrorCase() {
    var errorCase1 = LocalDateTime.now().plusDays(1);
    var errorCase2 = LocalDateTime.now();

    assertThatThrownBy(() -> checker.checkForBetweenFromAndTo(null, errorCase1))
        .isInstanceOf(ValidateCheckError.class)
        .hasMessage(errorMessageConfig.getTimeRequired());

    assertThatThrownBy(() -> checker.checkForBetweenFromAndTo(errorCase2, null))
        .isInstanceOf(ValidateCheckError.class)
        .hasMessage(errorMessageConfig.getTimeRequired());
  }

  @Test
  @DisplayName("from 날짜가 to 날짜보다 크면 ValidateCheckError를 반환한다.")
  void checkForBetweenFromAndToErrorCase2() {
    var errorCase1 = LocalDateTime.now().plusDays(1);
    var errorCase2 = LocalDateTime.now();

    assertThatThrownBy(() -> checker.checkForBetweenFromAndTo(errorCase1, errorCase2))
        .isInstanceOf(ValidateCheckError.class)
        .hasMessage(errorMessageConfig.getFromIsNotAfterTo());
  }

  @Test
  @DisplayName("from, to가 둘 다 null이라면 Optional.empty()를반환한다.")
  void checkForBetweenFromAndToReturnEmptyOptional() {
    assertThat(checker.checkForBetweenFromAndTo(null, null)).isEmpty();
  }
}
