package com.minsu.kim.daoujapan.services.statistics.leaver;

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

import com.minsu.kim.daoujapan.data.statistics.member.LeaverRecord;
import com.minsu.kim.daoujapan.domains.statistics.member.LeaverStatisticEntity;
import com.minsu.kim.daoujapan.exception.NotFoundException;
import com.minsu.kim.daoujapan.mapper.statistics.member.LeaverMapperImpl;
import com.minsu.kim.daoujapan.repositories.statistics.member.LeaverStatisticRepository;

/**
 * @author minsu.kim
 * @since 1.0
 */
@SpringBootTest(classes = {LeaverStatisticServiceImpl.class, LeaverMapperImpl.class})
class LeaverStatisticServiceImplTest {
  @Autowired private LeaverStatisticServiceImpl leaverStatisticService;

  @MockBean private LeaverStatisticRepository repository;

  @Test
  @DisplayName("탈퇴자 정보를 페이징하여 반환한다.")
  void findStatistics() {

    // given
    given(repository.findAllByDeleteDtIsNull(any(Pageable.class)))
        .willReturn(TestDummy.findAllLeaverStatisticEntity());

    // when
    var pageRequest = PageRequest.of(0, 3);
    var pagingSubscriberRecords = leaverStatisticService.findStatistics(pageRequest);

    // then
    var assertDatetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    then(repository).should(times(1)).findAllByDeleteDtIsNull(pageRequest);
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
  void findStatisticsByDateTime() {
    // given
    given(
            repository.findAllByRecordTimeBetweenAndDeleteDtIsNull(
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
        .willReturn(TestDummy.findAllLeaverStatisticEntity());

    // when
    var searchFrom = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    var searchTo = searchFrom.plusDays(2L);
    var pageRequest = PageRequest.of(0, 3);
    var pagingSubscriberRecords =
        leaverStatisticService.findStatisticsByDateTime(searchFrom, searchTo, pageRequest);

    // then
    var assertDatetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
    then(repository)
        .should(times(1))
        .findAllByRecordTimeBetweenAndDeleteDtIsNull(searchFrom, searchTo, pageRequest);
    assertThat(pagingSubscriberRecords.content()).hasSize(3);
    assertThat(pagingSubscriberRecords.content().getFirst().id()).isEqualTo(1L);
    assertThat(pagingSubscriberRecords.content().getFirst().recordTime()).isEqualTo(assertDatetime);
    assertThat(pagingSubscriberRecords.content().getFirst().leaverCount()).isEqualTo(1);
    assertThat(pagingSubscriberRecords.pageNumber()).isZero();
    assertThat(pagingSubscriberRecords.hasNext()).isFalse();
    assertThat(pagingSubscriberRecords.hasPrevious()).isFalse();
  }

  @Test
  @DisplayName("탈퇴자 수에대한 데이터가 입력으로 들어오면 데이터베이스에 저장한다.")
  void saveStatistic() {
    // given
    given(repository.save(any(LeaverStatisticEntity.class)))
        .willReturn(TestDummy.findLeaverStatisticEntitySuit());
    given(repository.existsByRecordTime(any())).willReturn(false);

    // when
    var leaverDummy = TestDummy.createLeaverRecordSuit();
    var leaverRecord = leaverStatisticService.saveStatistic(leaverDummy);

    // then
    then(repository).should(times(1)).save(any(LeaverStatisticEntity.class));
    assertThat(leaverRecord.leaverCount()).isEqualTo(leaverDummy.leaverCount());
    assertThat(leaverRecord.recordTime()).isEqualTo(leaverDummy.recordTime());
  }

  @Test
  @DisplayName("탈퇴자 수에 대한 통계값을 수정합니다.")
  void updateStatistic() {
    var optionalMockUpdateTarget = TestDummy.findLeaverStatisticNotDeletedEntityMock();
    var mockUpdateTarget = optionalMockUpdateTarget.get();

    // given
    given(repository.findByDeleteDtIsNullAndId(anyLong())).willReturn(optionalMockUpdateTarget);

    // when
    var updateForDummy = TestDummy.updateForDummy();
    var updatedStatistic = leaverStatisticService.updateStatistic(TestDummy.updateForDummy());

    // then
    then(mockUpdateTarget)
        .should(times(1))
        .modifyEntity(updateForDummy.recordTime(), updateForDummy.leaverCount());
    assertThat(updatedStatistic.leaverCount()).isEqualTo(updateForDummy.leaverCount());
    assertThat(updatedStatistic.recordTime()).isEqualTo(updateForDummy.recordTime());
  }

  @Test
  @DisplayName("탈퇴자 수에 대한 통계값을 수정시 존재하지않으면 에러를 발생합니다.")
  void updateStatisticIfNotExistThenThrow() {
    // given
    given(repository.findByDeleteDtIsNullAndId(anyLong())).willReturn(Optional.empty());

    // when, then
    assertThatThrownBy(() -> leaverStatisticService.updateStatistic(TestDummy.updateForDummy()))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("탈퇴자 정보를 찾을 수 없습니다.");
  }

  @Test
  @DisplayName("탈퇴자 수에 대한 통계값을 삭제합니다.")
  void deleteStatistic() {
    var optionalMockUpdateTarget = TestDummy.findLeaverStatisticNotDeletedEntityMock();
    var mockUpdateTarget = optionalMockUpdateTarget.get();

    // given
    given(repository.findByDeleteDtIsNullAndId(anyLong())).willReturn(optionalMockUpdateTarget);

    // when
    leaverStatisticService.deleteStatistic(1L);

    // then
    then(mockUpdateTarget).should(times(1)).deleteStatistic();
    assertThat(mockUpdateTarget.getDeleteDt()).isNotNull().isInstanceOf(LocalDateTime.class);
  }

  @Test
  @DisplayName("탈퇴자 수에 데이터를 삭제시 존재하지않으면 에러를 발생합니다.")
  void deleteStatisticIfNotExistThenThrow() {
    // given
    given(repository.findByDeleteDtIsNullAndId(anyLong())).willReturn(Optional.empty());

    // when
    assertThatThrownBy(() -> leaverStatisticService.deleteStatistic(1L))
        .isInstanceOf(NotFoundException.class)
        .hasMessage("탈퇴자 정보를 찾을 수 없습니다.");
  }

  public static class TestDummy {
    public static LeaverStatisticEntity findLeaverStatisticEntitySuit() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return new LeaverStatisticEntity(1L, datetime, 10);
    }

    public static LeaverRecord createLeaverRecordSuit() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return new LeaverRecord(null, datetime, 10, null);
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

    public static Optional<LeaverStatisticEntity> findLeaverStatisticNotDeletedEntityMock() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return Optional.of(
          spy(LeaverStatisticEntity.builder().id(1L).recordTime(datetime).leaverCount(1).build()));
    }

    public static LeaverRecord updateForDummy() {
      var datetime = LocalDateTime.of(2024, 11, 4, 0, 0, 0);

      return LeaverRecord.builder().id(1L).recordTime(datetime).leaverCount(100).build();
    }
  }
}
