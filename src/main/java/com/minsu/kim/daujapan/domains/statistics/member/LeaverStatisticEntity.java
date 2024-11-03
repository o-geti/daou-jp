package com.minsu.kim.daujapan.domains.statistics.member;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

/**
 * 탈퇴자 통계 엔티티입니다.
 *
 * @author minsu.kim
 * @since 1.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity(name = "LeaverStatisticEntity")
@Table(
    name = "leaver_statistics",
    indexes = @Index(name = "idx_leaver_record_time", columnList = "recordTime"))
public class LeaverStatisticEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Comment("탈퇴자통계 아이디")
  private Long id;

  @Comment("기록시간")
  private LocalDateTime recordTime;

  @Comment("탈퇴자수")
  private Integer leaverCount;
}
