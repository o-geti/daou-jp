package com.minsu.kim.daoujapan.data.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author minsu.kim
 * @since 1.0
 */
public record LeaverRequest(
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @NotNull(message = "탈퇴자 통계 등록 시간을 입력하세요.")
        LocalDateTime recordTime,
        @NotNull(message = "탈퇴자 수는 필수값입니다.")
        @Min(value = 0, message = "탈퇴자 수는 음수가 될 수 없습니다.")
        Integer leaverCount) {}