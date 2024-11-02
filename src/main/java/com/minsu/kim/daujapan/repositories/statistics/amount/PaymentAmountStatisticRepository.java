package com.minsu.kim.daujapan.repositories.statistics.amount;

import com.minsu.kim.daujapan.domain.statistics.amount.PaymentAmountStatisticEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *  <p>판매금액 통계 레포지터리입니다.</p>
 *
 * @author minsu.kim
 * @since 1.0
 */
@Repository
public interface PaymentAmountStatisticRepository extends JpaRepository<PaymentAmountStatisticEntity, Long> {

}
