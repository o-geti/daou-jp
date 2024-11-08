package com.minsu.kim.daoujapan.services.statistics;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.minsu.kim.daoujapan.data.response.Paging;
import com.minsu.kim.daoujapan.data.statistics.StatisticRecord;

/**
 * @author minsu.kim
 * @since 1.0
 */
public interface StatisticService<T> {
  Paging<T> findStatistics(Pageable pageable);

  Paging<T> findStatisticsByDateTime(LocalDateTime from, LocalDateTime to, Pageable pageable);

  T saveStatistic(T statistic);

  Optional<T> saveStatistic(StatisticRecord statistic);

  T updateStatistic(T statistic);

  void deleteStatistic(Long id);
}
