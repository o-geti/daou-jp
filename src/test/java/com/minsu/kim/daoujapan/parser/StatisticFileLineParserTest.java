package com.minsu.kim.daoujapan.parser;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.minsu.kim.daoujapan.data.statistics.amount.PaymentAmountRecord;
import com.minsu.kim.daoujapan.data.statistics.amount.SalesAmountRecord;
import com.minsu.kim.daoujapan.data.statistics.amount.UsageAmountRecord;
import com.minsu.kim.daoujapan.data.statistics.member.LeaverRecord;
import com.minsu.kim.daoujapan.data.statistics.member.SubscriberRecord;

/**
 * @author minsu.kim
 * @since 1.0
 */
class StatisticFileLineParserTest {
  StatisticFileLineParser parser = new StatisticFileLineParser();

  @Test
  @DisplayName("통계 파일 내용을 | 단위로 쪼개어 낸 값을 LocalDateTime과 각 데이터 타입으로 변환합니다.")
  void parseLineStringArray() {
    var testData = "2024-11-02 23|999|30|2,500,000|4,750,000|72,258,158";
    var parserResult = parser.parseLineStringArray(testData.split("\\|"));

    var recordDatetime = LocalDateTime.of(2024, 11, 2, 23, 0, 0);

    assertThat(parserResult).isNotNull();
    assertThat(parserResult.subscriberRecord())
        .isNotEmpty()
        .hasValue(new SubscriberRecord(null, recordDatetime, 999));
    assertThat(parserResult.leaverRecord())
        .isNotEmpty()
        .hasValue(new LeaverRecord(null, recordDatetime, 30));
    assertThat(parserResult.paymentAmountRecord())
        .isNotEmpty()
        .hasValue(new PaymentAmountRecord(null, recordDatetime, 2_500_000L));
    assertThat(parserResult.usageAmountRecord())
        .isNotEmpty()
        .hasValue(new UsageAmountRecord(null, recordDatetime, 4_750_000L));
    assertThat(parserResult.salesAmountRecord())
        .isNotEmpty()
        .hasValue(new SalesAmountRecord(null, recordDatetime, 72_258_158L));
  }

  @Test
  @DisplayName("통계 파일 내용 중 숫자 데이터가 깨진 것이 존재하면 해당 값은 Optional.empty()를 반환합니다.")
  void parseLineStringArrayExceptNumberException() {
    var parser = new StatisticFileLineParser();
    var testData = "2024-11-02 23|a999|b30|2,500,000|d4,750,000|72,258,158";
    var parserResult = parser.parseLineStringArray(testData.split("\\|"));

    var recordDatetime = LocalDateTime.of(2024, 11, 2, 23, 0, 0);

    assertThat(parserResult).isNotNull();
    assertThat(parserResult.subscriberRecord()).isEmpty();
    assertThat(parserResult.leaverRecord()).isEmpty();
    ;
    assertThat(parserResult.paymentAmountRecord())
        .isNotEmpty()
        .hasValue(new PaymentAmountRecord(null, recordDatetime, 2_500_000L));
    assertThat(parserResult.usageAmountRecord()).isEmpty();
    assertThat(parserResult.salesAmountRecord())
        .isNotEmpty()
        .hasValue(new SalesAmountRecord(null, recordDatetime, 72_258_158L));
  }

  @Test
  @DisplayName("통계 파일 내용 중 날짜 데이터 변환중 오류가 발생하면 해당 행 전체를 Optional.empty()로 반환합니다.")
  void parseLineStringArrayExceptDateTimeParseException() {
    var parser = new StatisticFileLineParser();
    var testData = "qwer2024-11-02 23|999|30|2,500,000|4,750,000|72,258,158";
    var parserResult = parser.parseLineStringArray(testData.split("\\|"));

    var recordDatetime = LocalDateTime.of(2024, 11, 2, 23, 0, 0);

    assertThat(parserResult).isNotNull();
    assertThat(parserResult.subscriberRecord()).isEmpty();
    assertThat(parserResult.leaverRecord()).isEmpty();
    assertThat(parserResult.paymentAmountRecord()).isEmpty();
    assertThat(parserResult.usageAmountRecord()).isEmpty();
    assertThat(parserResult.salesAmountRecord()).isEmpty();
  }
}
