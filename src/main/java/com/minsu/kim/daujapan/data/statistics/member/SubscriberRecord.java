package com.minsu.kim.daujapan.data.statistics.member;

import java.time.LocalDateTime;

/**
 * 가입자 통계 데이터 레코드입니다.
 *
 * @author minsu.kim
 * @since 1.0
 */
public record SubscriberRecord(LocalDateTime recordTime, int subscriberCount) {}
