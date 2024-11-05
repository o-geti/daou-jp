package com.minsu.kim.daoujapan.services.statistics.subscriber;

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

import com.minsu.kim.daoujapan.data.statistics.member.SubscriberRecord;
import com.minsu.kim.daoujapan.domains.statistics.member.SubscriberStatisticEntity;
import com.minsu.kim.daoujapan.exception.NotFoundException;
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
    given(repository.findAllByDeleteDtIsNull(any(Pageable.class)))
        .willReturn(TestDummy.findAllSubscriberStatisticEntity());

    // when
    var pageRequest = PageRequest.of(0, 3);
    var pagingSubscriberRecords = service.findStatistics(pageRequest);

    // then
    var assertDatetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    then(repository).should(times(1)).findAllByDeleteDtIsNull(any(Pageable.class));
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
            repository.findAllByRecordTimeBetweenAndDeleteDtIsNull(
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
    then(repository)
        .should(times(1))
        .findAllByRecordTimeBetweenAndDeleteDtIsNull(searchFrom, searchTo, pageRequest);
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
    given(repository.existsByRecordTime(any())).willReturn(false);

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
  @DisplayName("가입자 수에 대한 통계값을 수정합니다.")
  void updateStatistic() {
    var optionalMockUpdateTarget = TestDummy.findSubscriberStatisticNotDeletedEntityMock();
    var mockUpdateTarget = optionalMockUpdateTarget.get();

    // given
    given(repository.findByDeleteDtIsNullAndId(anyLong())).willReturn(optionalMockUpdateTarget);

    // when
    var updateForDummy = TestDummy.updateForDummy();
    var updatedStatistic = service.updateStatistic(TestDummy.updateForDummy());

    // then
    then(mockUpdateTarget)
        .should(times(1))
        .modifyEntity(updateForDummy.recordTime(), updateForDummy.subscriberCount());
    assertThat(updatedStatistic.subscriberCount()).isEqualTo(updateForDummy.subscriberCount());
    assertThat(updatedStatistic.recordTime()).isEqualTo(updateForDummy.recordTime());
  }

  @Test
  @DisplayName("가입자 수에 대한 통계값을 수정시 존재하지않으면 에러를 발생합니다.")
  void updateStatisticIfNotExistThenThrow() {
    // given
    given(repository.findByDeleteDtIsNullAndId(anyLong())).willReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> service.updateStatistic(TestDummy.updateForDummy()))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("가입자 정보를 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("가입자 수에 대한 통계값을 삭제합니다.")
  void deleteStatistic() {
    var optionalMockUpdateTarget = TestDummy.findSubscriberStatisticNotDeletedEntityMock();
    var mockUpdateTarget = optionalMockUpdateTarget.get();

    // given
    given(repository.findByDeleteDtIsNullAndId(anyLong())).willReturn(optionalMockUpdateTarget);

    // when
    service.deleteStatistic(1L);

    // then
    then(mockUpdateTarget).should(times(1)).deleteStatistic();
    assertThat(mockUpdateTarget.getDeleteDt()).isNotNull().isInstanceOf(LocalDateTime.class);
  }

  @Test
  @DisplayName("가입자 데이터를 삭제시 존재하지않으면 에러를 발생합니다.")
  void deleteStatisticIfNotExistThenThrow() {
    // given
    given(repository.findByDeleteDtIsNullAndId(anyLong())).willReturn(Optional.empty());

    // when
    assertThatThrownBy(() -> service.deleteStatistic(1L))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("가입자 정보를 찾을 수 없습니다.");
  }

  public static class TestDummy {
    public static SubscriberStatisticEntity findSubscriberStatisticEntitySuit() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return new SubscriberStatisticEntity(1L, datetime, 10);
    }

    public static SubscriberRecord createSubscriberRecordSuit() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return new SubscriberRecord(null, datetime, 10, null);
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

    public static Optional<SubscriberStatisticEntity>
        findSubscriberStatisticNotDeletedEntityMock() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return Optional.of(
          spy(
              SubscriberStatisticEntity.builder()
                  .id(1L)
                  .recordTime(datetime)
                  .subscriberCount(10)
                  .build()));
    }

    public static SubscriberRecord updateForDummy() {
      var datetime = LocalDateTime.of(2024, 11, 4, 0, 0, 0);

      return SubscriberRecord.builder().id(1L).recordTime(datetime).subscriberCount(10).build();
    }
  }
}
