package com.minsu.kim.daoujapan.repositories.statistics.member;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.minsu.kim.daoujapan.domains.statistics.member.SubscriberStatisticEntity;

/**
 * 가입자수 통계 레포지터리입니다.
 *
 * @author minsu.kim
 * @since 1.0
 */
@Repository
public interface SubscriberStatisticRepository
    extends JpaRepository<SubscriberStatisticEntity, Long> {
  Page<SubscriberStatisticEntity> findAllByDeleteDtIsNull(Pageable pageable);

  Page<SubscriberStatisticEntity> findAllByRecordTimeBetweenAndDeleteDtIsNull(
      LocalDateTime searchFrom, LocalDateTime searchTo, Pageable pageable);

  Boolean existsByRecordTime(LocalDateTime recordTime);

  Optional<SubscriberStatisticEntity> findByDeleteDtIsNullAndId(Long id);
}
