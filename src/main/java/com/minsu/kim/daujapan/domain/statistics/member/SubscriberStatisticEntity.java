package com.minsu.kim.daujapan.domain.statistics.member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

/**
 * <p> 가입자 통계 엔티티입니다.
 *
 * @author minsu.kim
 * @since 1.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity(name = "SubscriberStatisticEntity")
@Table(name = "subscriber_statistics", indexes = @Index(name = "idx_subscriber_record_time", columnList = "recordTime"))
public class SubscriberStatisticEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("가입자통계 아이디")
    private Long id;

    @Comment("기록시간")
    private LocalDateTime recordTime;

    @Comment("가입자수")
    private Integer subscriberCount;
}
