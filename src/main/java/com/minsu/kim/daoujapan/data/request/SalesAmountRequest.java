package com.minsu.kim.daoujapan.data.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * @author minsu.kim
 * @since 1.0
 */
public record SalesAmountRequest(
    @NotNull(message = "매출금액 통계 등록 시간을 입력하세요.") LocalDateTime recordTime,
    @NotNull(message = "매출금액 수는 필수값입니다.") @Min(value = 0, message = "매출금액 수는 음수가 될 수 없습니다.")
        Long salesAmount) {}
