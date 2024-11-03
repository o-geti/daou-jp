package com.minsu.kim.daujapan.services.statistics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.minsu.kim.daujapan.data.statistics.amount.PaymentAmountRecord;
import com.minsu.kim.daujapan.data.statistics.amount.SalesAmountRecord;
import com.minsu.kim.daujapan.data.statistics.amount.UsageAmountRecord;
import com.minsu.kim.daujapan.domains.statistics.amount.PaymentAmountStatisticEntity;
import com.minsu.kim.daujapan.domains.statistics.amount.SalesAmountStatisticEntity;
import com.minsu.kim.daujapan.domains.statistics.amount.UsageAmountStatisticEntity;
import com.minsu.kim.daujapan.mapper.statistics.amount.PaymentAmountMapperImpl;
import com.minsu.kim.daujapan.mapper.statistics.amount.SalesAmountMapperImpl;
import com.minsu.kim.daujapan.mapper.statistics.amount.UsageAmountMapperImpl;
import com.minsu.kim.daujapan.repositories.statistics.amount.PaymentAmountStatisticRepository;
import com.minsu.kim.daujapan.repositories.statistics.amount.SalesAmountStatisticRepository;
import com.minsu.kim.daujapan.repositories.statistics.amount.UsageAmountStatisticRepository;

/**
 * @author minsu.kim
 * @since 1.0
 */
@SpringBootTest(
    classes = {
      AmountStatisticService.class,
      PaymentAmountMapperImpl.class,
      UsageAmountMapperImpl.class,
      SalesAmountMapperImpl.class
    })
class AmountStatisticServiceTest {

  @Autowired AmountStatisticService amountStatisticService;

  @MockBean PaymentAmountStatisticRepository paymentAmountStatisticRepository;
  @MockBean UsageAmountStatisticRepository usageAmountStatisticRepository;
  @MockBean SalesAmountStatisticRepository salesAmountStatisticRepository;

  @Test
  @DisplayName("결제금액 데이터가 입력으로 들어오면 데이터베이스에 저장한다.")
  void savePaymentAmountStatistic() {
    // given
    given(paymentAmountStatisticRepository.save(any(PaymentAmountStatisticEntity.class)))
        .willReturn(TestDummy.findPaymentAmountStatisticEntitySuit());

    // when
    var paymentAmountRecord = TestDummy.createPaymentAmountRecordSuit();
    var savedPaymentAmountStatistic =
        amountStatisticService.savePaymentAmountStatistic(paymentAmountRecord);

    // then
    then(paymentAmountStatisticRepository)
        .should(times(1))
        .save(any(PaymentAmountStatisticEntity.class));
    assertThat(savedPaymentAmountStatistic.paymentAmount())
        .isEqualTo(paymentAmountRecord.paymentAmount());
    assertThat(savedPaymentAmountStatistic.recordTime())
        .isEqualTo(paymentAmountRecord.recordTime());
  }

  @Test
  @DisplayName("사용금액 데이터가 입력으로 들어오면 데이터베이스에 저장한다.")
  void saveUsageAmountStatistic() {
    // given
    given(usageAmountStatisticRepository.save(any(UsageAmountStatisticEntity.class)))
        .willReturn(TestDummy.findUsageAmountStatisticEntitySuit());

    // when
    var usageAmountRecord = TestDummy.createUsageAmountRecordSuit();
    var savedUsageAmountStatistic =
        amountStatisticService.saveUsageAmountStatistic(usageAmountRecord);

    // then
    then(usageAmountStatisticRepository)
        .should(times(1))
        .save(any(UsageAmountStatisticEntity.class));
    assertThat(savedUsageAmountStatistic.usageAmount()).isEqualTo(usageAmountRecord.usageAmount());
    assertThat(savedUsageAmountStatistic.recordTime()).isEqualTo(usageAmountRecord.recordTime());
  }

  @Test
  @DisplayName("매출금액 데이터가 입력으로 들어오면 데이터베이스에 저장한다.")
  void saveSalesAmountStatistic() {
    // given
    given(salesAmountStatisticRepository.save(any(SalesAmountStatisticEntity.class)))
        .willReturn(TestDummy.findSalesAmountStatisticEntitySuit());

    // when
    var salesAmountRecord = TestDummy.createSalesAmountRecordSuit();
    var saveSalesAmountStatistic =
        amountStatisticService.saveSalesAmountStatistic(salesAmountRecord);

    // then
    then(salesAmountStatisticRepository)
        .should(times(1))
        .save(any(SalesAmountStatisticEntity.class));
    assertThat(saveSalesAmountStatistic.salesAmount()).isEqualTo(salesAmountRecord.salesAmount());
    assertThat(saveSalesAmountStatistic.recordTime()).isEqualTo(salesAmountRecord.recordTime());
  }

  public static class TestDummy {
    public static PaymentAmountStatisticEntity findPaymentAmountStatisticEntitySuit() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return new PaymentAmountStatisticEntity(1L, datetime, 10_000L);
    }

    public static PaymentAmountRecord createPaymentAmountRecordSuit() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return new PaymentAmountRecord(null, datetime, 10_000L);
    }

    public static UsageAmountStatisticEntity findUsageAmountStatisticEntitySuit() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return new UsageAmountStatisticEntity(1L, datetime, 10_000L);
    }

    public static UsageAmountRecord createUsageAmountRecordSuit() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return new UsageAmountRecord(null, datetime, 10_000L);
    }

    public static SalesAmountStatisticEntity findSalesAmountStatisticEntitySuit() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return new SalesAmountStatisticEntity(1L, datetime, 10_000L);
    }

    public static SalesAmountRecord createSalesAmountRecordSuit() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return new SalesAmountRecord(null, datetime, 10_000L);
    }
  }
}
