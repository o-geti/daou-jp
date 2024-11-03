package com.minsu.kim.daujapan.parser;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.minsu.kim.daujapan.data.statistics.amount.PaymentAmountRecord;
import com.minsu.kim.daujapan.data.statistics.amount.SalesAmountRecord;
import com.minsu.kim.daujapan.data.statistics.amount.UsageAmountRecord;
import com.minsu.kim.daujapan.data.statistics.member.LeaverRecord;
import com.minsu.kim.daujapan.data.statistics.member.SubscriberRecord;

/**
 * 자세한 기능을 적어주세요
 *
 * <p><b>Example:</b> 사용법을 간단히 적어주세요
 *
 * <pre class="code">
 *  Demo demo = new Demo();
 *  demo.print("Hello Nhn");
 * </pre>
 *
 * @author minsu.kim
 * @since 1.0
 */
class StatisticFileLineParserTest {

  @Test
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
