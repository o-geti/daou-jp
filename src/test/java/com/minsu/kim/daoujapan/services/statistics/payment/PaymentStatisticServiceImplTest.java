package com.minsu.kim.daoujapan.services.statistics.payment;

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
import org.springframework.data.domain.Pageable;

import com.minsu.kim.daoujapan.data.statistics.amount.PaymentAmountRecord;
import com.minsu.kim.daoujapan.domains.statistics.amount.PaymentAmountStatisticEntity;
import com.minsu.kim.daoujapan.mapper.statistics.amount.PaymentAmountMapperImpl;
import com.minsu.kim.daoujapan.repositories.statistics.amount.PaymentAmountStatisticRepository;

/**
 * @author minsu.kim
 * @since 1.0
 */
@SpringBootTest(
    classes = {
      PaymentStatisticServiceImpl.class,
      PaymentAmountMapperImpl.class,
    })
class PaymentStatisticServiceImplTest {
  @Autowired private PaymentStatisticServiceImpl service;

  @MockBean PaymentAmountStatisticRepository paymentAmountStatisticRepository;

  @Test
  @DisplayName("결제금액 정보를 페이징하여 반환한다.")
  void findStatistics() {
    // given
    given(paymentAmountStatisticRepository.findAllByDeleteDtIsNull(any(Pageable.class)))
        .willReturn(TestDummy.findAllPaymentAmountStatisticEntity());

    // when
    var pageRequest = PageRequest.of(0, 3);
    var pagingSubscriberRecords = service.findStatistics(pageRequest);

    // then
    var assertDatetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    then(paymentAmountStatisticRepository)
        .should(times(1))
        .findAllByDeleteDtIsNull(any(Pageable.class));
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
  void findStatisticsByDateTime() {
    // given
    given(
            paymentAmountStatisticRepository.findAllByRecordTimeBetweenAndDeleteDtIsNull(
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
        .willReturn(TestDummy.findAllPaymentAmountStatisticEntity());

    // when
    var searchFrom = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    var searchTo = searchFrom.plusDays(2L);
    var pageRequest = PageRequest.of(0, 3);
    var pagingSubscriberRecords =
        service.findStatisticsByDateTime(searchFrom, searchTo, pageRequest);

    // then
    var assertDatetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    then(paymentAmountStatisticRepository)
        .should(times(1))
        .findAllByRecordTimeBetweenAndDeleteDtIsNull(searchFrom, searchTo, pageRequest);
    assertThat(pagingSubscriberRecords.content()).hasSize(3);
    assertThat(pagingSubscriberRecords.content().getFirst().id()).isEqualTo(1L);
    assertThat(pagingSubscriberRecords.content().getFirst().recordTime()).isEqualTo(assertDatetime);
    assertThat(pagingSubscriberRecords.content().getFirst().paymentAmount()).isEqualTo(1_000_000);
    assertThat(pagingSubscriberRecords.pageNumber()).isZero();
    assertThat(pagingSubscriberRecords.hasNext()).isFalse();
    assertThat(pagingSubscriberRecords.hasPrevious()).isFalse();
  }

  @Test
  @DisplayName("결제금액 데이터가 입력으로 들어오면 데이터베이스에 저장한다.")
  void saveStatistic() {
    // given
    given(paymentAmountStatisticRepository.save(any(PaymentAmountStatisticEntity.class)))
        .willReturn(TestDummy.findPaymentAmountStatisticEntitySuit());
    given(paymentAmountStatisticRepository.existsByRecordTime(any())).willReturn(false);

    // when
    var paymentAmountRecord = TestDummy.createPaymentAmountRecordSuit();
    var savedPaymentAmountStatistic = service.saveStatistic(paymentAmountRecord);

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
  void updateStatistic() {}

  @Test
  void deleteStatistic() {}

  public static class TestDummy {
    public static PaymentAmountStatisticEntity findPaymentAmountStatisticEntitySuit() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return new PaymentAmountStatisticEntity(1L, datetime, 10_000L);
    }

    public static PaymentAmountRecord createPaymentAmountRecordSuit() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return new PaymentAmountRecord(null, datetime, 10_000L, null);
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
  }
}
