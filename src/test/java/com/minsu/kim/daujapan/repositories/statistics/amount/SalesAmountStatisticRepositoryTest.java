package com.minsu.kim.daujapan.repositories.statistics.amount;

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
class SalesAmountStatisticRepositoryTest {

  @Autowired SalesAmountStatisticRepository repository;

  @Test
  @DisplayName("검색 날짜에 맞추어 매출금액 데이터가 잘 나오는지 확인한다.")
  public void findByRecordTimeBetween() {

    // h2 초기화 쿼리에 테이블당 총 24개의 데이터가 들어있음.
    var searchFrom = LocalDateTime.of(2024, 10, 28, 0, 0, 0);
    var searchTo = LocalDateTime.of(2024, 10, 28, 21, 0, 0);
    Pageable pageable = PageRequest.of(0, 5);

    var result = repository.findAllByRecordTimeBetween(searchFrom, searchTo, pageable);

    assertThat(result.getContent()).hasSize(5);
    assertThat(result.getTotalPages()).isEqualTo(5); // (24 / 5) + ((24 % 5) > 0 ? 1 : 0)
    assertThat(result.getContent().getFirst().getRecordTime()).isEqualTo(searchFrom);

    // data.sql의 sales_amount_statistics insert 문 가장 첫번째 값
    assertThat(result.getContent().getFirst().getSalesAmount()).isEqualTo(5_461_947L);
  }
}
