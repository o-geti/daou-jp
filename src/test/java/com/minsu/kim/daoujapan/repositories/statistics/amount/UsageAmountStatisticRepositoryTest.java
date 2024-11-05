package com.minsu.kim.daoujapan.repositories.statistics.amount;

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
class UsageAmountStatisticRepositoryTest {
  @Autowired private UsageAmountStatisticRepository repository;

  @Test
  @DisplayName("검색 날짜에 맞추어 사용금액 데이터가 잘 나오는지 확인한다.")
  public void findByRecordTimeBetween() {

    // h2 초기화 쿼리에 테이블당 총 24개의 데이터가 들어있음.
    var searchFrom = LocalDateTime.of(2024, 10, 28, 0, 0, 0);
    var searchTo = LocalDateTime.of(2024, 10, 28, 21, 0, 0);
    Pageable pageable = PageRequest.of(0, 5);

    var result =
        repository.findAllByRecordTimeBetweenAndDeleteDtIsNull(searchFrom, searchTo, pageable);

    assertThat(result.getContent()).hasSize(5);
    assertThat(result.getTotalPages()).isEqualTo(5); // (24 / 5) + ((24 % 5) > 0 ? 1 : 0)
    assertThat(result.getContent().getFirst().getRecordTime()).isEqualTo(searchFrom);

    // data.sql의 usage_amount_statistics insert 문 가장 첫번째 값
    assertThat(result.getContent().getFirst().getUsageAmount()).isEqualTo(9_315_981L);
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
    //    INSERT INTO usage_amount_statistics (record_time, usage_amount, delete_dt) VALUES
    // ('2024-10-28 00:00:00', 9315981, null);
    var optionalUsageAmount = repository.findByDeleteDtIsNullAndId(1L);

    assertThat(optionalUsageAmount).isNotEmpty();
    assertThat(optionalUsageAmount.get().getId()).isEqualTo(1L);
    assertThat(optionalUsageAmount.get().getRecordTime())
        .isEqualTo(LocalDateTime.of(2024, 10, 28, 0, 0, 0));
    assertThat(optionalUsageAmount.get().getUsageAmount()).isEqualTo(9_315_981);
  }

  @Test
  @DisplayName("삭제 된 데이터를 검색할 경우 Optional.empty()가 나온다.")
  void findByDeleteDtIsNullAndIdDeletedSearchCase() {
    //    아래 주석이 25번째 ID를 가진 탈퇴자 행
    //    INSERT INTO usage_amount_statistics (record_time, usage_amount, delete_dt) VALUES
    // ('2024-10-29 23:00:00', 9018106,'2024-10-29 23:30:00')
    var optionalUsageAmount = repository.findByDeleteDtIsNullAndId(25L);
    assertThat(optionalUsageAmount).isEmpty();
  }
}
