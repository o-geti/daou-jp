package com.minsu.kim.daoujapan.repositories.statistics.member;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * @author minsu.kim
 * @since 1.0
 */
@SpringBootTest
class LeaverStatisticRepositoryTest {
  @Autowired private LeaverStatisticRepository repository;

  @Test
  @DisplayName("검색 날짜에 맞추어 탈퇴자 수 데이터가 잘 나오는지 확인한다.")
  void findByRecordTimeBetween() {

    // h2 초기화 쿼리에 테이블당 총 24개의 데이터가 들어있음.
    var searchFrom = LocalDateTime.of(2024, 10, 28, 0, 0, 0);
    var searchTo = LocalDateTime.of(2024, 10, 28, 21, 0, 0);
    Pageable pageable = PageRequest.of(0, 5);

    var result =
        repository.findAllByRecordTimeBetweenAndDeleteDtIsNull(searchFrom, searchTo, pageable);

    assertThat(result.getContent()).hasSize(5);
    assertThat(result.getTotalPages()).isEqualTo(5); // (24 / 5) + ((24 % 5) > 0 ? 1 : 0)
    assertThat(result.getContent().getFirst().getRecordTime()).isEqualTo(searchFrom);

    // data.sql의 leaver_statistics insert 문 가장 첫번째 값
    assertThat(result.getContent().getFirst().getLeaverCount()).isEqualTo(43);
  }

  @Test
  @DisplayName("삭제 시간이 null인 데이터만 뽑아와지는지 확인한다. (전체 데이터 25건중 1건만 삭제상태)")
  void findAllByDeleteDtIsNull() {
    // h2 초기화 쿼리에 테이블당 총 25개의 데이터가 들어있음.
    // 이 중 1건은 삭제된 건
    Pageable pageable = PageRequest.of(0, 30);

    var result = repository.findAllByDeleteDtIsNull(pageable);

    assertThat(result.getContent()).hasSize(24);
    assertThat(result.getTotalPages()).isEqualTo(1);
  }

  @Test
  @DisplayName("검색 날짜에 맞추어 존재하면 True, 없다면 false를 반환한다.")
  void existsByRecordTime() {
    var availableCase = LocalDateTime.of(2024, 10, 28, 0, 0, 0);
    var notAvailableCase = LocalDateTime.of(2025, 10, 28, 21, 0, 0);

    var isAvailable1 = repository.existsByRecordTime(availableCase);
    var isAvailable2 = repository.existsByRecordTime(notAvailableCase);

    assertThat(isAvailable1).isTrue();
    assertThat(isAvailable2).isFalse();
  }

  @Test
  @DisplayName("삭제 된 데이터를 제외한 ID만을 검색한다.")
  void findByDeleteDtIsNullAndId() {
    //    아래 주석이 첫번째 ID를 가진 탈퇴자 행
    //    INSERT INTO leaver_statistics (record_time, leaver_count, delete_dt) VALUES ('2024-10-28
    // 00:00:00', 43, null)
    var optionalLeaver = repository.findByDeleteDtIsNullAndId(1L);
    assertThat(optionalLeaver).isNotEmpty();
    assertThat(optionalLeaver.get().getId()).isEqualTo(1L);
    assertThat(optionalLeaver.get().getRecordTime())
        .isEqualTo(LocalDateTime.of(2024, 10, 28, 0, 0, 0));
    assertThat(optionalLeaver.get().getLeaverCount()).isEqualTo(43);
  }

  @Test
  @DisplayName("삭제 된 데이터를 검색할 경우 Optional.empty()가 나온다.")
  void findByDeleteDtIsNullAndIdDeletedSearchCase() {
    //    아래 주석이 25번째 ID를 가진 탈퇴자 행
    //    INSERT INTO leaver_statistics (record_time, leaver_count, delete_dt) VALUES ('2024-10-29
    // 23:00:00', 701,'2024-10-30 23:50:00')
    var optionalLeaver = repository.findByDeleteDtIsNullAndId(25L);
    assertThat(optionalLeaver).isEmpty();
  }
}
