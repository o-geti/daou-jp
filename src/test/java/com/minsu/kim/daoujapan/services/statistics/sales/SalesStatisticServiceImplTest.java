package com.minsu.kim.daoujapan.services.statistics.sales;

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

import com.minsu.kim.daoujapan.data.statistics.amount.SalesAmountRecord;
import com.minsu.kim.daoujapan.domains.statistics.amount.SalesAmountStatisticEntity;
import com.minsu.kim.daoujapan.exception.NotFoundException;
import com.minsu.kim.daoujapan.mapper.statistics.amount.SalesAmountMapperImpl;
import com.minsu.kim.daoujapan.repositories.statistics.amount.SalesAmountStatisticRepository;

/**
 * @author minsu.kim
 * @since 1.0
 */
@SpringBootTest(
    classes = {
      SalesStatisticServiceImpl.class,
      SalesAmountMapperImpl.class,
    })
class SalesStatisticServiceImplTest {
  @Autowired private SalesStatisticServiceImpl salesStatisticService;

  @MockBean SalesAmountStatisticRepository salesAmountStatisticRepository;

  @Test
  @DisplayName("매출금액 정보를 페이징하여 반환한다.")
  void findStatistics() {
    // given
    given(salesAmountStatisticRepository.findAllByDeleteDtIsNull(any(Pageable.class)))
        .willReturn(TestDummy.findAllSalesAmountStatisticEntity());

    // when
    var pageRequest = PageRequest.of(0, 3);
    var pagingSubscriberRecords = salesStatisticService.findStatistics(pageRequest);

    // then
    var assertDatetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    then(salesAmountStatisticRepository).should(times(1)).findAllByDeleteDtIsNull(pageRequest);
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
  void findStatisticsByDateTime() {
    // given
    given(
            salesAmountStatisticRepository.findAllByRecordTimeBetweenAndDeleteDtIsNull(
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
        .willReturn(TestDummy.findAllSalesAmountStatisticEntity());

    // when
    var searchFrom = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    var searchTo = searchFrom.plusDays(2L);
    var pageRequest = PageRequest.of(0, 3);
    var pagingSubscriberRecords =
        salesStatisticService.findStatisticsByDateTime(searchFrom, searchTo, pageRequest);

    // then
    var assertDatetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    then(salesAmountStatisticRepository)
        .should(times(1))
        .findAllByRecordTimeBetweenAndDeleteDtIsNull(searchFrom, searchTo, pageRequest);
    assertThat(pagingSubscriberRecords.content()).hasSize(3);
    assertThat(pagingSubscriberRecords.content().getFirst().id()).isEqualTo(1L);
    assertThat(pagingSubscriberRecords.content().getFirst().recordTime()).isEqualTo(assertDatetime);
    assertThat(pagingSubscriberRecords.content().getFirst().salesAmount()).isEqualTo(1_000_000L);
    assertThat(pagingSubscriberRecords.pageNumber()).isZero();
    assertThat(pagingSubscriberRecords.hasNext()).isFalse();
    assertThat(pagingSubscriberRecords.hasPrevious()).isFalse();
  }

  @Test
  @DisplayName("매출금액 데이터가 입력으로 들어오면 데이터베이스에 저장한다.")
  void saveStatistic() {
    // given
    given(salesAmountStatisticRepository.save(any(SalesAmountStatisticEntity.class)))
        .willReturn(TestDummy.findSalesAmountStatisticEntitySuit());
    given(salesAmountStatisticRepository.existsByRecordTime(any())).willReturn(false);
    // when
    var salesAmountRecord = TestDummy.createSalesAmountRecordSuit();
    var saveSalesAmountStatistic = salesStatisticService.saveStatistic(salesAmountRecord);

    // then
    then(salesAmountStatisticRepository)
        .should(times(1))
        .save(any(SalesAmountStatisticEntity.class));
    assertThat(saveSalesAmountStatistic.salesAmount()).isEqualTo(salesAmountRecord.salesAmount());
    assertThat(saveSalesAmountStatistic.recordTime()).isEqualTo(salesAmountRecord.recordTime());
  }

  @Test
  @DisplayName("매출금액에 대한 통계값을 수정합니다.")
  void updateStatistic() {
    var optionalMockUpdateTarget = TestDummy.findSalesAmountStatisticNotDeletedEntityMock();
    var mockUpdateTarget = optionalMockUpdateTarget.get();

    // given
    given(salesAmountStatisticRepository.findByDeleteDtIsNullAndId(anyLong()))
        .willReturn(optionalMockUpdateTarget);

    // when
    var updateForDummy = TestDummy.updateForDummy();
    var updatedStatistic = salesStatisticService.updateStatistic(TestDummy.updateForDummy());

    // then
    then(mockUpdateTarget)
        .should(times(1))
        .modifyEntity(updateForDummy.recordTime(), updateForDummy.salesAmount());
    assertThat(updatedStatistic.salesAmount()).isEqualTo(updateForDummy.salesAmount());
    assertThat(updatedStatistic.recordTime()).isEqualTo(updateForDummy.recordTime());
  }

  @Test
  @DisplayName("매출금액에 대한 통계값을 수정시 존재하지않으면 에러를 발생합니다.")
  void updateStatisticIfNotExistThenThrow() {
    // given
    given(salesAmountStatisticRepository.findByDeleteDtIsNullAndId(anyLong()))
        .willReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> salesStatisticService.updateStatistic(TestDummy.updateForDummy()))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("매출금액 정보를 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("매출금액에 대한 통계값을 삭제합니다.")
  void deleteStatistic() {
    var optionalMockUpdateTarget = TestDummy.findSalesAmountStatisticNotDeletedEntityMock();
    var mockUpdateTarget = optionalMockUpdateTarget.get();

    // given
    given(salesAmountStatisticRepository.findByDeleteDtIsNullAndId(anyLong()))
        .willReturn(optionalMockUpdateTarget);

    // when
    salesStatisticService.deleteStatistic(1L);

    // then
    then(mockUpdateTarget).should(times(1)).deleteStatistic();
    assertThat(mockUpdateTarget.getDeleteDt()).isNotNull().isInstanceOf(LocalDateTime.class);
  }

  @Test
  @DisplayName("매출금액 데이터를 삭제시 존재하지않으면 에러를 발생합니다.")
  void deleteStatisticIfNotExistThenThrow() {
    // given
    given(salesAmountStatisticRepository.findByDeleteDtIsNullAndId(anyLong()))
        .willReturn(Optional.empty());

    // when
    assertThatThrownBy(() -> salesStatisticService.deleteStatistic(1L))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("매출금액 정보를 찾을 수 없습니다.");
  }

  public static class TestDummy {

    public static SalesAmountStatisticEntity findSalesAmountStatisticEntitySuit() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return new SalesAmountStatisticEntity(1L, datetime, 10_000L);
    }

    public static SalesAmountRecord createSalesAmountRecordSuit() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return new SalesAmountRecord(null, datetime, 10_000L, null);
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

    public static Optional<SalesAmountStatisticEntity>
        findSalesAmountStatisticNotDeletedEntityMock() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return Optional.of(
          spy(
              SalesAmountStatisticEntity.builder()
                  .id(1L)
                  .recordTime(datetime)
                  .salesAmount(1_000_000L)
                  .build()));
    }

    public static SalesAmountRecord updateForDummy() {
      var datetime = LocalDateTime.of(2024, 11, 4, 0, 0, 0);

      return SalesAmountRecord.builder()
          .id(1L)
          .recordTime(datetime)
          .salesAmount(1_000_000L)
          .build();
    }
  }
}
