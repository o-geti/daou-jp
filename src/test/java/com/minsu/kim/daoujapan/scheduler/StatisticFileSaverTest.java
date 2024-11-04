package com.minsu.kim.daoujapan.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.minsu.kim.daoujapan.repositories.statistics.amount.PaymentAmountStatisticRepository;
import com.minsu.kim.daoujapan.repositories.statistics.amount.SalesAmountStatisticRepository;
import com.minsu.kim.daoujapan.repositories.statistics.amount.UsageAmountStatisticRepository;
import com.minsu.kim.daoujapan.repositories.statistics.member.LeaverStatisticRepository;
import com.minsu.kim.daoujapan.repositories.statistics.member.SubscriberStatisticRepository;

/**
 * @author minsu.kim
 * @since 1.0
 */
@SpringBootTest(properties = {"schedule-file.job.file-saver-run=0/2 * * * * ?"})
class StatisticFileSaverTest {

  private static final DateTimeFormatter RECORD_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH");

  @Autowired private SubscriberStatisticRepository subscriberStatisticRepository;
  @Autowired private LeaverStatisticRepository leaverStatisticRepository;
  @Autowired private PaymentAmountStatisticRepository paymentAmountStatisticRepository;
  @Autowired private UsageAmountStatisticRepository usageAmountStatisticRepository;
  @Autowired private SalesAmountStatisticRepository salesAmountStatisticRepository;

  @Test
  @DisplayName("0번째 인덱스 yyyy-MM-dd HH 패턴의 데이터를 자바 LocalDateTime 데이터로 변환한다.")
  void recordTimeToLocalDateTime() {
    var localDateTime = LocalDateTime.parse("2024-11-03 08", RECORD_TIME_FORMATTER);

    assertThat(localDateTime).isInstanceOf(LocalDateTime.class);
    assertThat(localDateTime.getYear()).isEqualTo(2024);
    assertThat(localDateTime.getMonthValue()).isEqualTo(11);
    assertThat(localDateTime.getDayOfMonth()).isEqualTo(3);
    assertThat(localDateTime.getHour()).isEqualTo(8);
  }

  @Test
  @DisplayName("1,2,3,4,5번째 인덱스 가입자 데이터 문자열을 숫자로 변환 테스트를 진행한다. - 성공")
  void parseIntCase1() throws ParseException {
    var numberFormat = new DecimalFormat("#,###");
    var numberCase1 = numberFormat.parse("12");
    var numberCase2 = numberFormat.parse("12,000");

    assertThat(numberCase1.intValue()).isEqualTo(12);
    assertThat(numberCase2.intValue()).isEqualTo(12000);
  }

  @Test
  @DisplayName("1,2,3,4,5번째 인덱스 가입자 데이터 문자열을 숫자로 변환 테스트를 진행한다. - 실패")
  void parseIntCase2() {
    var numberFormat = new DecimalFormat("#,###");

    assertThatThrownBy(() -> numberFormat.parse("₩12,000")).isInstanceOf(ParseException.class);
  }

  @Test
  @DisplayName("스케줄러 서비스 동작 확인, 임의의 파일을 읽어들여 테스트 결과를 확인한다.")
  void schedulerCronRunTest() {
    Awaitility.await()
        .atMost(3, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              assertThat(subscriberStatisticRepository.count()).isEqualTo(46);
              assertThat(leaverStatisticRepository.count()).isEqualTo(47);
              assertThat(paymentAmountStatisticRepository.count()).isEqualTo(46);
              assertThat(usageAmountStatisticRepository.count()).isEqualTo(47);
              assertThat(salesAmountStatisticRepository.count()).isEqualTo(47);
            });
  }
}
