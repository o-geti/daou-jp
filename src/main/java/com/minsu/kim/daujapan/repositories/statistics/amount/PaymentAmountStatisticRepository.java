package com.minsu.kim.daujapan.repositories.statistics.amount;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.minsu.kim.daujapan.domains.statistics.amount.PaymentAmountStatisticEntity;

/**
 * 판매금액 통계 레포지터리입니다.
 *
 * @author minsu.kim
 * @since 1.0
 */
@Repository
public interface PaymentAmountStatisticRepository
    extends JpaRepository<PaymentAmountStatisticEntity, Long> {
  Page<PaymentAmountStatisticEntity> findAllByRecordTimeBetween(
      LocalDateTime searchFrom, LocalDateTime searchTo, Pageable pageable);
}
