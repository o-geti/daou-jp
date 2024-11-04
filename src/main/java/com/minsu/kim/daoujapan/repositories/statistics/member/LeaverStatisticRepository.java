package com.minsu.kim.daoujapan.repositories.statistics.member;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.minsu.kim.daoujapan.domains.statistics.member.LeaverStatisticEntity;

/**
 * 탈퇴자수 통계 레포지터리입니다.
 *
 * @author minsu.kim
 * @since 1.0
 */
@Repository
public interface LeaverStatisticRepository extends JpaRepository<LeaverStatisticEntity, Long> {
  Page<LeaverStatisticEntity> findAllByRecordTimeBetween(
      LocalDateTime searchFrom, LocalDateTime searchTo, Pageable pageable);

  Boolean existsByRecordTime(LocalDateTime recordTime);
}