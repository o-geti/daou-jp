package com.minsu.kim.daujapan.services.statistics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
import org.springframework.data.domain.Pageable;

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
  @DisplayName("결제금액 정보를 페이징하여 반환한다.")
  void findSubscribeRecords() {
    // given
    given(paymentAmountStatisticRepository.findAll(any(Pageable.class)))
        .willReturn(TestDummy.findAllPaymentAmountStatisticEntity());

    // when
    var pageRequest = PageRequest.of(0, 3);
    var pagingSubscriberRecords = amountStatisticService.findPaymentAmountRecords(pageRequest);

    // then
    var assertDatetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    then(paymentAmountStatisticRepository).should(times(1)).findAll(any(Pageable.class));
    assertThat(pagingSubscriberRecords.content()).hasSize(3);
    assertThat(pagingSubscriberRecords.content().getFirst().id()).isEqualTo(1L);
    assertThat(pagingSubscriberRecords.content().getFirst().recordTime()).isEqualTo(assertDatetime);
    assertThat(pagingSubscriberRecords.content().getFirst().paymentAmount()).isEqualTo(1_000_000L);
    assertThat(pagingSubscriberRecords.pageNumber()).isZero();
    assertThat(pagingSubscriberRecords.hasNext()).isFalse();
    assertThat(pagingSubscriberRecords.hasPrevious()).isFalse();
  }

  @Test
  @DisplayName("결제금액 정보를 날짜 필터링하고, 페이징하여 반환한다.")
  void findSubscribeRecordsByRecordDate() {
    // given
    given(
        paymentAmountStatisticRepository.findAllByRecordTimeBetween(
            any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
        .willReturn(TestDummy.findAllPaymentAmountStatisticEntity());

    // when
    var searchFrom = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    var searchTo = searchFrom.plusDays(2L);
    var pageRequest = PageRequest.of(0, 3);
    var pagingSubscriberRecords =
        amountStatisticService.findPaymentAmountRecordsByRecordDate(searchFrom, searchTo, pageRequest);

    // then
    var assertDatetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    then(paymentAmountStatisticRepository)
        .should(times(1))
        .findAllByRecordTimeBetween(searchFrom, searchTo, pageRequest);
    assertThat(pagingSubscriberRecords.content()).hasSize(3);
    assertThat(pagingSubscriberRecords.content().getFirst().id()).isEqualTo(1L);
    assertThat(pagingSubscriberRecords.content().getFirst().recordTime()).isEqualTo(assertDatetime);
    assertThat(pagingSubscriberRecords.content().getFirst().paymentAmount()).isEqualTo(1_000_000);
    assertThat(pagingSubscriberRecords.pageNumber()).isZero();
    assertThat(pagingSubscriberRecords.hasNext()).isFalse();
    assertThat(pagingSubscriberRecords.hasPrevious()).isFalse();
  }

  @Test
  @DisplayName("사용금액 정보를 페이징하여 반환한다.")
  void findUsageAmountRecords() {
    // given
    var dummy = TestDummy.findAllUsageAmountStatisticEntity();
    given(usageAmountStatisticRepository.findAll(any(Pageable.class)))
        .willReturn(dummy);

    // when
    var pageRequest = PageRequest.of(0, 3);
    var pagingSubscriberRecords = amountStatisticService.findUsageAmountRecords(pageRequest);

    // then
    var assertDatetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    then(usageAmountStatisticRepository).should(times(1)).findAll(pageRequest);
    assertThat(pagingSubscriberRecords.content()).hasSize(3);
    assertThat(pagingSubscriberRecords.content().getFirst().id()).isEqualTo(1L);
    assertThat(pagingSubscriberRecords.content().getFirst().recordTime()).isEqualTo(assertDatetime);
    assertThat(pagingSubscriberRecords.content().getFirst().usageAmount()).isEqualTo(10_000);
    assertThat(pagingSubscriberRecords.pageNumber()).isZero();
    assertThat(pagingSubscriberRecords.hasNext()).isFalse();
    assertThat(pagingSubscriberRecords.hasPrevious()).isFalse();
  }

  @Test
  @DisplayName("사용금액 정보를 날짜로 필터링하고 페이징하여 반환한다.")
  void findUsageAmountRecordsByRecordDate() {
    // given
    given(
        usageAmountStatisticRepository.findAllByRecordTimeBetween(
            any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
        .willReturn(TestDummy.findAllUsageAmountStatisticEntity());

    // when
    var searchFrom = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    var searchTo = searchFrom.plusDays(2L);
    var pageRequest = PageRequest.of(0, 3);
    var pagingSubscriberRecords =
        amountStatisticService.findUsageAmountRecordsByRecordDate(searchFrom, searchTo, pageRequest);

    // then
    var assertDatetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    then(usageAmountStatisticRepository)
        .should(times(1))
        .findAllByRecordTimeBetween(searchFrom, searchTo, pageRequest);
    assertThat(pagingSubscriberRecords.content()).hasSize(3);
    assertThat(pagingSubscriberRecords.content().getFirst().id()).isEqualTo(1L);
    assertThat(pagingSubscriberRecords.content().getFirst().recordTime()).isEqualTo(assertDatetime);
    assertThat(pagingSubscriberRecords.content().getFirst().usageAmount()).isEqualTo(10_000);
    assertThat(pagingSubscriberRecords.pageNumber()).isZero();
    assertThat(pagingSubscriberRecords.hasNext()).isFalse();
    assertThat(pagingSubscriberRecords.hasPrevious()).isFalse();
  }

  @Test
  @DisplayName("매출금액 정보를 페이징하여 반환한다.")
  void findSalesAmountRecords() {
    // given
    given(salesAmountStatisticRepository.findAll(any(Pageable.class)))
        .willReturn(TestDummy.findAllSalesAmountStatisticEntity());

    // when
    var pageRequest = PageRequest.of(0, 3);
    var pagingSubscriberRecords = amountStatisticService.findSalesAmountRecords(pageRequest);

    // then
    var assertDatetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    then(salesAmountStatisticRepository).should(times(1)).findAll(pageRequest);
    assertThat(pagingSubscriberRecords.content()).hasSize(3);
    assertThat(pagingSubscriberRecords.content().getFirst().id()).isEqualTo(1L);
    assertThat(pagingSubscriberRecords.content().getFirst().recordTime()).isEqualTo(assertDatetime);
    assertThat(pagingSubscriberRecords.content().getFirst().salesAmount()).isEqualTo(1_000_000L);
    assertThat(pagingSubscriberRecords.pageNumber()).isZero();
    assertThat(pagingSubscriberRecords.hasNext()).isFalse();
    assertThat(pagingSubscriberRecords.hasPrevious()).isFalse();
  }

  @Test
  @DisplayName("매출금액 정보를 날짜로 필터링하고 페이징하여 반환한다.")
  void findSalesAmountRecordsByRecordDate() {
    // given
    given(
        salesAmountStatisticRepository.findAllByRecordTimeBetween(
            any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
        .willReturn(TestDummy.findAllSalesAmountStatisticEntity());

    // when
    var searchFrom = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    var searchTo = searchFrom.plusDays(2L);
    var pageRequest = PageRequest.of(0, 3);
    var pagingSubscriberRecords =
        amountStatisticService.findSalesAmountRecordsByRecordDate(searchFrom, searchTo, pageRequest);

    // then
    var assertDatetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    then(salesAmountStatisticRepository)
        .should(times(1))
        .findAllByRecordTimeBetween(searchFrom, searchTo, pageRequest);
    assertThat(pagingSubscriberRecords.content()).hasSize(3);
    assertThat(pagingSubscriberRecords.content().getFirst().id()).isEqualTo(1L);
    assertThat(pagingSubscriberRecords.content().getFirst().recordTime()).isEqualTo(assertDatetime);
    assertThat(pagingSubscriberRecords.content().getFirst().salesAmount()).isEqualTo(1_000_000L);
    assertThat(pagingSubscriberRecords.pageNumber()).isZero();
    assertThat(pagingSubscriberRecords.hasNext()).isFalse();
    assertThat(pagingSubscriberRecords.hasPrevious()).isFalse();
  }

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

    public static Page<PaymentAmountStatisticEntity> findAllPaymentAmountStatisticEntity() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      var elem1 =
          PaymentAmountStatisticEntity.builder()
              .id(1L)
              .recordTime(datetime)
              .paymentAmount(1_000_000L)
              .build();
      var elem2 =
          PaymentAmountStatisticEntity.builder()
              .id(2L)
              .recordTime(datetime.plusDays(1L))
              .paymentAmount(1_100_000L)
              .build();
      var elem3 =
          PaymentAmountStatisticEntity.builder()
              .id(3L)
              .recordTime(datetime.plusDays(2L))
              .paymentAmount(1_200_000L)
              .build();

      var pageRequest = PageRequest.of(0, 3);
      return (new PageImpl<>(List.of(elem1, elem2, elem3), pageRequest, 3));
    }

    public static Page<UsageAmountStatisticEntity> findAllUsageAmountStatisticEntity() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      var elem1 =
          UsageAmountStatisticEntity.builder()
              .id(1L)
              .recordTime(datetime)
              .usageAmount(10_000L)
              .build();
      var elem2 =
          UsageAmountStatisticEntity.builder()
              .id(2L)
              .recordTime(datetime.plusDays(1L))
              .usageAmount(11_000L)
              .build();
      var elem3 =
          UsageAmountStatisticEntity.builder()
              .id(3L)
              .recordTime(datetime.plusDays(2L))
              .usageAmount(12_000L)
              .build();

      var pageRequest = PageRequest.of(0, 3);

      return (new PageImpl<>(List.of(elem1, elem2, elem3), pageRequest, 3));
    }

    public static Page<SalesAmountStatisticEntity> findAllSalesAmountStatisticEntity() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      var elem1 =
          SalesAmountStatisticEntity.builder()
              .id(1L)
              .recordTime(datetime)
              .salesAmount(1_000_000L)
              .build();
      var elem2 =
          SalesAmountStatisticEntity.builder()
              .id(2L)
              .recordTime(datetime.plusDays(1L))
              .salesAmount(2_100_000L)
              .build();
      var elem3 =
          SalesAmountStatisticEntity.builder()
              .id(3L)
              .recordTime(datetime.plusDays(2L))
              .salesAmount(3_300_000L)
              .build();

      var pageRequest = PageRequest.of(0, 3);
      return (new PageImpl<>(List.of(elem1, elem2, elem3), pageRequest, 3));
    }
  }
}
