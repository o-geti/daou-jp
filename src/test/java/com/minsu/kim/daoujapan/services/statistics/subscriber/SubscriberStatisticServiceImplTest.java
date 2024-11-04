package com.minsu.kim.daoujapan.services.statistics.subscriber;

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

import com.minsu.kim.daoujapan.data.statistics.member.SubscriberRecord;
import com.minsu.kim.daoujapan.domains.statistics.member.SubscriberStatisticEntity;
import com.minsu.kim.daoujapan.mapper.statistics.member.SubscriberMapperImpl;
import com.minsu.kim.daoujapan.repositories.statistics.member.SubscriberStatisticRepository;

/**
 * @author minsu.kim
 * @since 1.0
 */
@SpringBootTest(classes = {SubscriberStatisticServiceImpl.class, SubscriberMapperImpl.class})
class SubscriberStatisticServiceImplTest {

  @Autowired SubscriberStatisticServiceImpl service;

  @MockBean SubscriberStatisticRepository repository;

  @Test
  @DisplayName("가입자 정보를 페이징하여 반환한다.")
  void findStatistics() {
    // given
    given(repository.findAll(any(Pageable.class)))
        .willReturn(TestDummy.findAllSubscriberStatisticEntity());

    // when
    var pageRequest = PageRequest.of(0, 3);
    var pagingSubscriberRecords = service.findStatistics(pageRequest);

    // then
    var assertDatetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    then(repository).should(times(1)).findAll(any(Pageable.class));
    assertThat(pagingSubscriberRecords.content()).hasSize(3);
    assertThat(pagingSubscriberRecords.content().getFirst().id()).isEqualTo(1L);
    assertThat(pagingSubscriberRecords.content().getFirst().recordTime()).isEqualTo(assertDatetime);
    assertThat(pagingSubscriberRecords.content().getFirst().subscriberCount()).isEqualTo(1);
    assertThat(pagingSubscriberRecords.pageNumber()).isZero();
    assertThat(pagingSubscriberRecords.hasNext()).isFalse();
    assertThat(pagingSubscriberRecords.hasPrevious()).isFalse();
  }

  @Test
  @DisplayName("가입자 정보를 날짜 필터링하고, 페이징하여 반환한다.")
  void findStatisticsByDateTime() {
    // given
    given(
            repository.findAllByRecordTimeBetween(
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
        .willReturn(TestDummy.findAllSubscriberStatisticEntity());

    // when
    var searchFrom = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    var searchTo = searchFrom.plusDays(2L);
    var pageRequest = PageRequest.of(0, 3);
    var pagingSubscriberRecords =
        service.findStatisticsByDateTime(searchFrom, searchTo, pageRequest);

    // then
    var assertDatetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    then(repository).should(times(1)).findAllByRecordTimeBetween(searchFrom, searchTo, pageRequest);
    assertThat(pagingSubscriberRecords.content()).hasSize(3);
    assertThat(pagingSubscriberRecords.content().getFirst().id()).isEqualTo(1L);
    assertThat(pagingSubscriberRecords.content().getFirst().recordTime()).isEqualTo(assertDatetime);
    assertThat(pagingSubscriberRecords.content().getFirst().subscriberCount()).isEqualTo(1);
    assertThat(pagingSubscriberRecords.pageNumber()).isZero();
    assertThat(pagingSubscriberRecords.hasNext()).isFalse();
    assertThat(pagingSubscriberRecords.hasPrevious()).isFalse();
  }

  @Test
  @DisplayName("가입자 수에대한 데이터가 입력으로 들어오면 데이터베이스에 저장한다.")
  void saveStatistic() {
    // given
    given(repository.save(any(SubscriberStatisticEntity.class)))
        .willReturn(TestDummy.findSubscriberStatisticEntitySuit());

    // when
    var subscriberSuit = TestDummy.createSubscriberRecordSuit();
    var savedSubscriberStatisticRecord = service.saveStatistic(subscriberSuit);

    // then
    then(repository).should(times(1)).save(any(SubscriberStatisticEntity.class));
    assertThat(savedSubscriberStatisticRecord.subscriberCount())
        .isEqualTo(subscriberSuit.subscriberCount());
    assertThat(savedSubscriberStatisticRecord.recordTime()).isEqualTo(subscriberSuit.recordTime());
  }

  @Test
  void updateStatistic() {}

  @Test
  void deleteStatistic() {}

  public static class TestDummy {
    public static SubscriberStatisticEntity findSubscriberStatisticEntitySuit() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return new SubscriberStatisticEntity(1L, datetime, 10);
    }

    public static SubscriberRecord createSubscriberRecordSuit() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return new SubscriberRecord(null, datetime, 10);
    }

    public static Page<SubscriberStatisticEntity> findAllSubscriberStatisticEntity() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      var elem1 =
          SubscriberStatisticEntity.builder()
              .id(1L)
              .recordTime(datetime)
              .subscriberCount(1)
              .build();
      var elem2 =
          SubscriberStatisticEntity.builder()
              .id(2L)
              .recordTime(datetime.plusDays(1L))
              .subscriberCount(2)
              .build();
      var elem3 =
          SubscriberStatisticEntity.builder()
              .id(3L)
              .recordTime(datetime.plusDays(2L))
              .subscriberCount(3)
              .build();

      var pageRequest = PageRequest.of(0, 3);
      return (new PageImpl<>(List.of(elem1, elem2, elem3), pageRequest, 3));
    }
  }
}
