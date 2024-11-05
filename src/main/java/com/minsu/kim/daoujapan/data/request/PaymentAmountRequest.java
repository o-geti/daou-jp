package com.minsu.kim.daoujapan.data.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author minsu.kim
 * @since 1.0
 */
public record PaymentAmountRequest(
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @NotNull(message = "결제금액 통계 등록 시간을 입력하세요.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime recordTime,
    @NotNull(message = "결제금액 수는 필수값입니다.") @Min(value = 0, message = "결제금액 수는 음수가 될 수 없습니다.")
        Long paymentAmount) {}
