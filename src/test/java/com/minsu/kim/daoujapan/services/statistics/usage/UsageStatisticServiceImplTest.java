package com.minsu.kim.daoujapan.services.statistics.usage;

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

import com.minsu.kim.daoujapan.data.statistics.amount.UsageAmountRecord;
import com.minsu.kim.daoujapan.domains.statistics.amount.UsageAmountStatisticEntity;
import com.minsu.kim.daoujapan.mapper.statistics.amount.UsageAmountMapperImpl;
import com.minsu.kim.daoujapan.repositories.statistics.amount.UsageAmountStatisticRepository;

/**
 * @author minsu.kim
 * @since 1.0
 */
@SpringBootTest(
    classes = {
      UsageStatisticServiceImpl.class,
      UsageAmountMapperImpl.class,
    })
class UsageStatisticServiceImplTest {
  @Autowired private UsageStatisticServiceImpl usageStatisticService;

  @MockBean private UsageAmountStatisticRepository usageAmountStatisticRepository;

  @Test
  @DisplayName("사용금액 정보를 페이징하여 반환한다.")
  void findStatistics() {
    // given
    var dummy = TestDummy.findAllUsageAmountStatisticEntity();
    given(usageAmountStatisticRepository.findAll(any(Pageable.class))).willReturn(dummy);

    // when
    var pageRequest = PageRequest.of(0, 3);
    var pagingSubscriberRecords = usageStatisticService.findStatistics(pageRequest);

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
  void findStatisticsByDateTime() {
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
        usageStatisticService.findStatisticsByDateTime(searchFrom, searchTo, pageRequest);

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
  @DisplayName("사용금액 데이터가 입력으로 들어오면 데이터베이스에 저장한다.")
  void saveStatistic() {
    // given
    given(usageAmountStatisticRepository.save(any(UsageAmountStatisticEntity.class)))
        .willReturn(TestDummy.findUsageAmountStatisticEntitySuit());

    // when
    var usageAmountRecord = TestDummy.createUsageAmountRecordSuit();
    var savedUsageAmountStatistic = usageStatisticService.saveStatistic(usageAmountRecord);

    // then
    then(usageAmountStatisticRepository)
        .should(times(1))
        .save(any(UsageAmountStatisticEntity.class));
    assertThat(savedUsageAmountStatistic.usageAmount()).isEqualTo(usageAmountRecord.usageAmount());
    assertThat(savedUsageAmountStatistic.recordTime()).isEqualTo(usageAmountRecord.recordTime());
  }

  @Test
  void updateStatistic() {}

  @Test
  void deleteStatistic() {}

  public static class TestDummy {

    public static UsageAmountStatisticEntity findUsageAmountStatisticEntitySuit() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return new UsageAmountStatisticEntity(1L, datetime, 10_000L);
    }

    public static UsageAmountRecord createUsageAmountRecordSuit() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return new UsageAmountRecord(null, datetime, 10_000L);
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
  }
}
