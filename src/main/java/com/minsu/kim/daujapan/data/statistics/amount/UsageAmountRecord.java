package com.minsu.kim.daujapan.data.statistics.amount;

import java.time.LocalDateTime;

import lombok.Builder;

/**
 * 사용금액 통계 데이터 레코드입니다.
 *
 * @author minsu.kim
 * @since 1.0
 */
@Builder
public record UsageAmountRecord(Long id, LocalDateTime recordTime, long usageAmount) {}
