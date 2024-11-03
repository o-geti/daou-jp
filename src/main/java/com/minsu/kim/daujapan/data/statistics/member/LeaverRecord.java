package com.minsu.kim.daujapan.data.statistics.member;

import java.time.LocalDateTime;

/**
 * 탈퇴자 통계 데이터 레코드입니다.
 *
 * @author minsu.kim
 * @since 1.0
 */
public record LeaverRecord(LocalDateTime recordTime, int leaverCount) {}
