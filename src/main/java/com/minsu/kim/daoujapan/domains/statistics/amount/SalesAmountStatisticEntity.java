package com.minsu.kim.daoujapan.domains.statistics.amount;

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

import com.minsu.kim.daoujapan.domains.statistics.BaseEntity;

/**
 * 매출금액 통계 엔티티입니다.
 *
 * @author minsu.kim
 * @since 1.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
@Entity
@Table(
    name = "sales_amount_statistics",
    indexes = @Index(name = "idx_sales_record_time", columnList = "recordTime"))
public class SalesAmountStatisticEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Comment("매출금액통계 아이디")
  private Long id;

  @Comment("기록시간")
  @Column(unique = true)
  private LocalDateTime recordTime;

  @Comment("매출금액")
  private Long salesAmount;

  public void modifyEntity(LocalDateTime recordTime, Long salesAmount) {
    this.salesAmount = salesAmount;
    this.recordTime = recordTime;
  }

  public void deleteStatistic() {
    super.setDeleteDt(LocalDateTime.now());
  }
}
