package com.minsu.kim.daujapan.parser;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.minsu.kim.daujapan.data.statistics.amount.PaymentAmountRecord;
import com.minsu.kim.daujapan.data.statistics.amount.SalesAmountRecord;
import com.minsu.kim.daujapan.data.statistics.amount.UsageAmountRecord;
import com.minsu.kim.daujapan.data.statistics.member.LeaverRecord;
import com.minsu.kim.daujapan.data.statistics.member.SubscriberRecord;

/**
 *
 * @author minsu.kim
 * @since 1.0
 */
class StatisticFileLineParserTest {

  @Test
  @DisplayName("통계 파일 내용을 | 단위로 쪼개어 낸 값을 LocalDateTime과 각 데이터 타입으로 변환합니다.")
  void parseLineStringArray() {
    var parser = new StatisticFileLineParser();
    var testData = "2024-11-02 23|999|30|2,500,000|4,750,000|72,258,158";
    var parserResult = parser.parseLineStringArray(testData.split("\\|"));

    var recordDatetime = LocalDateTime.of(2024, 11, 2, 23, 0, 0);

    assertThat(parserResult).isNotNull();
    assertThat(parserResult.subscriberRecord())
        .isEqualTo(new SubscriberRecord(null, recordDatetime, 999));
    assertThat(parserResult.leaverRecord()).isEqualTo(new LeaverRecord(null, recordDatetime, 30));
    assertThat(parserResult.paymentAmountRecord())
        .isEqualTo(new PaymentAmountRecord(null, recordDatetime, 2_500_000L));
    assertThat(parserResult.usageAmountRecord())
        .isEqualTo(new UsageAmountRecord(null, recordDatetime, 4_750_000L));
    assertThat(parserResult.salesAmountRecord())
        .isEqualTo(new SalesAmountRecord(null, recordDatetime, 72_258_158L));
  }
}
