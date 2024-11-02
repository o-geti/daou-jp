package com.minsu.kim.daujapan.repositories.statistics.member;

import com.minsu.kim.daujapan.domain.statistics.member.LeaverStatisticEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <p>탈퇴자수 통계 레포지터리입니다.</p>
 *
 * @author minsu.kim
 * @since 1.0
 */
@Repository
public interface LeaverStatisticRepository extends JpaRepository<LeaverStatisticEntity, Long> {

}
