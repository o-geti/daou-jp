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
import org.springframework.data.domain.Pageable;

import com.minsu.kim.daujapan.data.statistics.member.LeaverRecord;
import com.minsu.kim.daujapan.data.statistics.member.SubscriberRecord;
import com.minsu.kim.daujapan.domains.statistics.member.LeaverStatisticEntity;
import com.minsu.kim.daujapan.domains.statistics.member.SubscriberStatisticEntity;
import com.minsu.kim.daujapan.mapper.statistics.member.LeaverMapperImpl;
import com.minsu.kim.daujapan.mapper.statistics.member.SubscriberMapperImpl;
import com.minsu.kim.daujapan.repositories.statistics.member.LeaverStatisticRepository;
import com.minsu.kim.daujapan.repositories.statistics.member.SubscriberStatisticRepository;

/**
 * @author minsu.kim
 * @since 1.0
 */
@SpringBootTest(
    classes = {MemberStatisticService.class, SubscriberMapperImpl.class, LeaverMapperImpl.class})
class MemberStatisticServiceTest {

  @Autowired MemberStatisticService statisticService;

  @MockBean SubscriberStatisticRepository subscriberStatisticRepository;
  @MockBean LeaverStatisticRepository leaverStatisticRepository;

  @Test
  @DisplayName("가입자 정보를 페이징하여 반환한다.")
  void findSubscribeRecords() {
    // given
    given(subscriberStatisticRepository.findAll(any(Pageable.class)))
        .willReturn(TestDummy.findAllSubscriberStatisticEntity());

    // when
    var pageRequest = PageRequest.of(0, 3);
    var pagingSubscriberRecords = statisticService.findSubscribeRecords(pageRequest);

    // then
    var assertDatetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    then(subscriberStatisticRepository).should(times(1)).findAll(any(Pageable.class));
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
  void findSubscribeRecordsByRecordDate() {
    // given
    given(
            subscriberStatisticRepository.findAllByRecordTimeBetween(
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
        .willReturn(TestDummy.findAllSubscriberStatisticEntity());

    // when
    var searchFrom = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    var searchTo = searchFrom.plusDays(2L);
    var pageRequest = PageRequest.of(0, 3);
    var pagingSubscriberRecords =
        statisticService.findSubscribeRecordsByRecordDate(searchFrom, searchTo, pageRequest);

    // then
    var assertDatetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    then(subscriberStatisticRepository)
        .should(times(1))
        .findAllByRecordTimeBetween(searchFrom, searchTo, pageRequest);
    assertThat(pagingSubscriberRecords.content()).hasSize(3);
    assertThat(pagingSubscriberRecords.content().getFirst().id()).isEqualTo(1L);
    assertThat(pagingSubscriberRecords.content().getFirst().recordTime()).isEqualTo(assertDatetime);
    assertThat(pagingSubscriberRecords.content().getFirst().subscriberCount()).isEqualTo(1);
    assertThat(pagingSubscriberRecords.pageNumber()).isZero();
    assertThat(pagingSubscriberRecords.hasNext()).isFalse();
    assertThat(pagingSubscriberRecords.hasPrevious()).isFalse();
  }

  @Test
  @DisplayName("탈퇴자 정보를 페이징하여 반환한다.")
  void findLeaverRecords() {
    // given
    given(leaverStatisticRepository.findAll(any(Pageable.class)))
        .willReturn(TestDummy.findAllLeaverStatisticEntity());

    // when
    var pageRequest = PageRequest.of(0, 3);
    var pagingSubscriberRecords = statisticService.findLeaverRecords(pageRequest);

    // then
    var assertDatetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    then(leaverStatisticRepository).should(times(1)).findAll(pageRequest);
    assertThat(pagingSubscriberRecords.content()).hasSize(3);
    assertThat(pagingSubscriberRecords.content().getFirst().id()).isEqualTo(1L);
    assertThat(pagingSubscriberRecords.content().getFirst().recordTime()).isEqualTo(assertDatetime);
    assertThat(pagingSubscriberRecords.content().getFirst().leaverCount()).isEqualTo(1);
    assertThat(pagingSubscriberRecords.pageNumber()).isZero();
    assertThat(pagingSubscriberRecords.hasNext()).isFalse();
    assertThat(pagingSubscriberRecords.hasPrevious()).isFalse();
  }

  @Test
  @DisplayName("탈퇴자 정보를 날짜로 필터링하고 페이징하여 반환한다.")
  void findLeaverRecordsByRecordDate() {
    // given
    given(
            leaverStatisticRepository.findAllByRecordTimeBetween(
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
        .willReturn(TestDummy.findAllLeaverStatisticEntity());

    // when
    var searchFrom = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    var searchTo = searchFrom.plusDays(2L);
    var pageRequest = PageRequest.of(0, 3);
    var pagingSubscriberRecords =
        statisticService.findLeaverRecordsByRecordDate(searchFrom, searchTo, pageRequest);

    // then
    var assertDatetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    then(leaverStatisticRepository)
        .should(times(1))
        .findAllByRecordTimeBetween(searchFrom, searchTo, pageRequest);
    assertThat(pagingSubscriberRecords.content()).hasSize(3);
    assertThat(pagingSubscriberRecords.content().getFirst().id()).isEqualTo(1L);
    assertThat(pagingSubscriberRecords.content().getFirst().recordTime()).isEqualTo(assertDatetime);
    assertThat(pagingSubscriberRecords.content().getFirst().leaverCount()).isEqualTo(1);
    assertThat(pagingSubscriberRecords.pageNumber()).isZero();
    assertThat(pagingSubscriberRecords.hasNext()).isFalse();
    assertThat(pagingSubscriberRecords.hasPrevious()).isFalse();
  }

  @Test
  @DisplayName("가입자 수에대한 데이터가 입력으로 들어오면 데이터베이스에 저장한다.")
  void saveSubscriberStatistic() {
    // given
    given(subscriberStatisticRepository.save(any(SubscriberStatisticEntity.class)))
        .willReturn(TestDummy.findSubscriberStatisticEntitySuit());

    // when
    var subscriberSuit = TestDummy.createSubscriberRecordSuit();
    var savedSubscriberStatisticRecord = statisticService.saveSubscriberStatistic(subscriberSuit);

    // then
    then(subscriberStatisticRepository).should(times(1)).save(any(SubscriberStatisticEntity.class));
    assertThat(savedSubscriberStatisticRecord.subscriberCount())
        .isEqualTo(subscriberSuit.subscriberCount());
    assertThat(savedSubscriberStatisticRecord.recordTime()).isEqualTo(subscriberSuit.recordTime());
  }

  @Test
  @DisplayName("탈퇴자 수에대한 데이터가 입력으로 들어오면 데이터베이스에 저장한다.")
  void saveLeaverStatistic() {
    // given
    given(leaverStatisticRepository.save(any(LeaverStatisticEntity.class)))
        .willReturn(TestDummy.findLeaverStatisticEntitySuit());

    // when
    var leaverDummy = TestDummy.createLeaverRecordSuit();
    var leaverRecord = statisticService.saveLeaverStatistic(leaverDummy);

    // then
    then(leaverStatisticRepository).should(times(1)).save(any(LeaverStatisticEntity.class));
    assertThat(leaverRecord.leaverCount()).isEqualTo(leaverDummy.leaverCount());
    assertThat(leaverRecord.recordTime()).isEqualTo(leaverDummy.recordTime());
  }

  public static class TestDummy {
    public static SubscriberStatisticEntity findSubscriberStatisticEntitySuit() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return new SubscriberStatisticEntity(1L, datetime, 10);
    }

    public static SubscriberRecord createSubscriberRecordSuit() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return new SubscriberRecord(null, datetime, 10);
    }

    public static LeaverStatisticEntity findLeaverStatisticEntitySuit() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return new LeaverStatisticEntity(1L, datetime, 10);
    }

    public static LeaverRecord createLeaverRecordSuit() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return new LeaverRecord(null, datetime, 10);
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

    public static Page<LeaverStatisticEntity> findAllLeaverStatisticEntity() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      var elem1 =
          LeaverStatisticEntity.builder().id(1L).recordTime(datetime).leaverCount(1).build();
      var elem2 =
          LeaverStatisticEntity.builder()
              .id(2L)
              .recordTime(datetime.plusDays(1L))
              .leaverCount(2)
              .build();
      var elem3 =
          LeaverStatisticEntity.builder()
              .id(3L)
              .recordTime(datetime.plusDays(2L))
              .leaverCount(3)
              .build();

      var pageRequest = PageRequest.of(0, 3);

      return (new PageImpl<>(List.of(elem1, elem2, elem3), pageRequest, 3));
    }
  }
}
