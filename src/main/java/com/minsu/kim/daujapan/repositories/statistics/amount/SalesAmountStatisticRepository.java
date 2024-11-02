package com.minsu.kim.daujapan.repositories.statistics.amount;

import com.minsu.kim.daujapan.domain.statistics.amount.SalesAmountStatisticEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *  <p>매출금액 통계 레포지터리입니다.</p>
 *
 * @author minsu.kim
 * @since 1.0
 */
@Repository
public interface SalesAmountStatisticRepository extends JpaRepository<SalesAmountStatisticEntity, Long> {

}
