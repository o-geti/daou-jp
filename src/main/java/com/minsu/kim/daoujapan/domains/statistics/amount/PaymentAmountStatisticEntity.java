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
 * 결제금액 통계 엔티티입니다.
 *
 * @author minsu.kim
 * @since 1.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Getter
@Entity(name = "PaymentAmountStatisticEntity")
@Table(
    name = "payment_amount_statistics",
    indexes = @Index(name = "idx_payment_record_time", columnList = "recordTime"))
public class PaymentAmountStatisticEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Comment("결제금액통계 아이디")
  private Long id;

  @Comment("기록시간")
  @Column(unique = true)
  private LocalDateTime recordTime;

  @Comment("결제금액")
  private Long paymentAmount;

  public void modifyEntity(LocalDateTime recordTime, Long paymentAmount) {
    this.paymentAmount = paymentAmount;
    this.recordTime = recordTime;
  }

  public void deleteStatistic() {
    super.setDeleteDt(LocalDateTime.now());
  }
}
