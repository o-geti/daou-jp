package com.minsu.kim.daoujapan.services.statistics.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
import com.minsu.kim.daoujapan.exception.NotFoundException;
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
  @DisplayName("결제금액에 대한 통계값을 수정합니다.")
  void updateStatistic() {
    var optionalMockUpdateTarget = TestDummy.findPaymentAmountStatisticNotDeletedEntityMock();
    var mockUpdateTarget = optionalMockUpdateTarget.get();

    // given
    given(paymentAmountStatisticRepository.findByDeleteDtIsNullAndId(anyLong()))
        .willReturn(optionalMockUpdateTarget);

    // when
    var updateForDummy = TestDummy.updateForDummy();
    var updatedStatistic = service.updateStatistic(TestDummy.updateForDummy());

    // then
    then(mockUpdateTarget)
        .should(times(1))
        .modifyEntity(updateForDummy.recordTime(), updateForDummy.paymentAmount());
    assertThat(updatedStatistic.paymentAmount()).isEqualTo(updateForDummy.paymentAmount());
    assertThat(updatedStatistic.recordTime()).isEqualTo(updateForDummy.recordTime());
  }

  @Test
  @DisplayName("결제금액에 대한 통계값을 수정시 존재하지않으면 에러를 발생합니다.")
  void updateStatisticIfNotExistThenThrow() {
    // given
    given(paymentAmountStatisticRepository.findByDeleteDtIsNullAndId(anyLong()))
        .willReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> service.updateStatistic(TestDummy.updateForDummy()))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("결제금액 정보를 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("결제금액에 대한 통계값을 삭제합니다.")
  void deleteStatistic() {
    var optionalMockUpdateTarget = TestDummy.findPaymentAmountStatisticNotDeletedEntityMock();
    var mockUpdateTarget = optionalMockUpdateTarget.get();

    // given
    given(paymentAmountStatisticRepository.findByDeleteDtIsNullAndId(anyLong()))
        .willReturn(optionalMockUpdateTarget);

    // when
    service.deleteStatistic(1L);

    // then
    then(mockUpdateTarget).should(times(1)).deleteStatistic();
    assertThat(mockUpdateTarget.getDeleteDt()).isNotNull().isInstanceOf(LocalDateTime.class);
  }

  @Test
  @DisplayName("결제금액 데이터를 삭제시 존재하지않으면 에러를 발생합니다.")
  void deleteStatisticIfNotExistThenThrow() {
    // given
    given(paymentAmountStatisticRepository.findByDeleteDtIsNullAndId(anyLong()))
        .willReturn(Optional.empty());

    // when
    assertThatThrownBy(() -> service.deleteStatistic(1L))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("결제금액 정보를 찾을 수 없습니다.");
  }

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

    public static Optional<PaymentAmountStatisticEntity>
        findPaymentAmountStatisticNotDeletedEntityMock() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return Optional.of(
          spy(
              PaymentAmountStatisticEntity.builder()
                  .id(1L)
                  .recordTime(datetime)
                  .paymentAmount(10_000L)
                  .build()));
    }

    public static PaymentAmountRecord updateForDummy() {
      var datetime = LocalDateTime.of(2024, 11, 4, 0, 0, 0);

      return PaymentAmountRecord.builder()
          .id(1L)
          .recordTime(datetime)
          .paymentAmount(10_000L)
          .build();
    }
  }
}
