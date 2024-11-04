package com.minsu.kim.daoujapan.repositories.statistics.member;

import java.time.LocalDateTime;
import java.util.Optional;

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
  Page<LeaverStatisticEntity> findAllByDeleteDtIsNull(Pageable pageable);

  Page<LeaverStatisticEntity> findAllByRecordTimeBetweenAndDeleteDtIsNull(
      LocalDateTime searchFrom, LocalDateTime searchTo, Pageable pageable);

  Optional<LeaverStatisticEntity> findByDeleteDtIsNullAndId(Long id);

  Boolean existsByRecordTime(LocalDateTime recordTime);
}
