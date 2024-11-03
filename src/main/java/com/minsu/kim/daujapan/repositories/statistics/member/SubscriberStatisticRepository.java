package com.minsu.kim.daujapan.repositories.statistics.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.minsu.kim.daujapan.domains.statistics.member.SubscriberStatisticEntity;

/**
 * 가입자수 통계 레포지터리입니다.
 *
 * @author minsu.kim
 * @since 1.0
 */
@Repository
public interface SubscriberStatisticRepository
    extends JpaRepository<SubscriberStatisticEntity, Long> {}
