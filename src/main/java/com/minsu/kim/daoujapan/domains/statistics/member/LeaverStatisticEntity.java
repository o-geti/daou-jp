package com.minsu.kim.daoujapan.domains.statistics.member;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Comment;

/**
 * 탈퇴자 통계 엔티티입니다.
 *
 * @author minsu.kim
 * @since 1.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
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
  @Column(unique = true)
  private LocalDateTime recordTime;

  @Comment("탈퇴자수")
  private Integer leaverCount;

  public void modifyEntity(LocalDateTime recordTime, Integer leaverCount) {
    this.leaverCount = leaverCount;
    this.recordTime = recordTime;
  }
}
