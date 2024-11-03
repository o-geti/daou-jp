package com.minsu.kim.daujapan.data.statistics.member;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 가입자 통계 데이터 레코드입니다.
 *
 * @author minsu.kim
 * @since 1.0
 */
@Builder
public record SubscriberRecord(Long id, LocalDateTime recordTime, int subscriberCount) {

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  public static class Filter {
    @Parameter(description = "조회 시작 날짜 및 시간", example = "2024-11-01T14:30:00")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime searchFrom;

    @Parameter(description = "조회 마지막 날짜 및 시간", example = "2024-11-01T14:40:00")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime searchTo;

    @Parameter(description = "페이지 크기", example = "10")
    @Min(1)
    private int size = 10;

    @Parameter(description = "페이지 번호", example = "0")
    @Min(0)
    private int page = 0;
  }
}
