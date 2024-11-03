package com.minsu.kim.daujapan.data.statistics.member;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 탈퇴자 통계 데이터 레코드입니다.
 *
 * @author minsu.kim
 * @since 1.0
 */
@Builder
public record LeaverRecord(Long id, LocalDateTime recordTime, int leaverCount) {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        LocalDateTime searchFrom;
        LocalDateTime searchTo;
    }
}
