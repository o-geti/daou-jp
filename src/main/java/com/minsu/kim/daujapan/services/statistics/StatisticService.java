package com.minsu.kim.daujapan.services.statistics;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;

import com.minsu.kim.daujapan.data.response.Paging;
import com.minsu.kim.daujapan.data.statistics.StatisticRecord;

/**
 * @author minsu.kim
 * @since 1.0
 */
public interface StatisticService<T> {
  Paging<T> findStatistics(Pageable pageable);

  Paging<T> findStatisticsByDateTime(LocalDateTime from, LocalDateTime to, Pageable pageable);

  T saveStatistic(T statistic);

  T saveStatistic(StatisticRecord statistic);

  T updateStatistic(T statistic);

  void deleteStatistic(T statistic);
}
