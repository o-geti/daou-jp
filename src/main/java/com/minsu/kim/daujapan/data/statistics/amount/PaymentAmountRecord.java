package com.minsu.kim.daujapan.data.statistics.amount;

import java.time.LocalDateTime;

/**
 * 결제금액 통계 데이터 레코드입니다.
 *
 * @author minsu.kim
 * @since 1.0
 */
public record PaymentAmountRecord(LocalDateTime recordTime, long paymentAmount) {}
