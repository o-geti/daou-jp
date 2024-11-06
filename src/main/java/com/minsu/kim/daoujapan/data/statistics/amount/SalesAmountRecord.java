package com.minsu.kim.daoujapan.data.statistics.amount;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 매출금액 통계 데이터 레코드입니다.
 *
 * @author minsu.kim
 * @since 1.0
 */
@Builder
public record SalesAmountRecord(
    Long id, LocalDateTime recordTime, long salesAmount, LocalDateTime deleteDt) {
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  public static class Filter {
    @Parameter(description = "조회 시작 날짜 및 시간", example = "2024-11-01T14:30:00")
    private LocalDateTime searchFrom;

    @Parameter(description = "조회 마지막 날짜 및 시간", example = "2024-11-01T14:40:00")
    private LocalDateTime searchTo;

    @Parameter(description = "페이지 크기", example = "10")
    @Min(1)
    private int size = 10;

    @Parameter(description = "페이지 번호", example = "0")
    @Min(0)
    private int page = 0;
  }
}
