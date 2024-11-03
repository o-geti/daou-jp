package com.minsu.kim.daujapan.data.statistics.amount;

import java.time.LocalDateTime;

/**
 * 매출금액 통계 데이터 레코드입니다.
 *
 * @author minsu.kim
 * @since 1.0
 */
public record SalesAmountRecord(LocalDateTime recordTime, long salesAmount) {}
