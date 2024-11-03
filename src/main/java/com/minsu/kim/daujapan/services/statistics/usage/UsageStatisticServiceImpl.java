/*
 * Copyright 2024 NHN (https://nhn.com) and others.
 * © NHN Corp. All rights reserved.
 */

package com.minsu.kim.daujapan.services.statistics.usage;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.minsu.kim.daujapan.data.response.Paging;
import com.minsu.kim.daujapan.data.statistics.StatisticRecord;
import com.minsu.kim.daujapan.data.statistics.amount.UsageAmountRecord;
import com.minsu.kim.daujapan.mapper.statistics.amount.UsageAmountMapper;
import com.minsu.kim.daujapan.repositories.statistics.amount.UsageAmountStatisticRepository;
import com.minsu.kim.daujapan.services.statistics.StatisticService;

/**
 * @author minsu.kim
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class UsageStatisticServiceImpl implements StatisticService<UsageAmountRecord> {

  private final UsageAmountStatisticRepository usageAmountStatisticRepository;
  private final UsageAmountMapper usageAmountMapper;

  @Override
  public Paging<UsageAmountRecord> findStatistics(Pageable pageable) {
    var pageLeaver = usageAmountStatisticRepository.findAll(pageable);

    return Paging.createPaging(pageLeaver, pageable, usageAmountMapper::entityToDto);
  }

  @Override
  public Paging<UsageAmountRecord> findStatisticsByDateTime(
      LocalDateTime from, LocalDateTime to, Pageable pageable) {
    var pageLeaver = usageAmountStatisticRepository.findAllByRecordTimeBetween(from, to, pageable);

    return Paging.createPaging(pageLeaver, pageable, usageAmountMapper::entityToDto);
  }

  @Override
  public UsageAmountRecord saveStatistic(UsageAmountRecord statistic) {
    var usageAmountStatisticEntity = usageAmountMapper.dtoToEntity(statistic);
    var savedEntity = usageAmountStatisticRepository.save(usageAmountStatisticEntity);

    return usageAmountMapper.entityToDto(savedEntity);
  }

  @Override
  public UsageAmountRecord saveStatistic(StatisticRecord statistic) {
    return this.saveStatistic(statistic.usageAmountRecord());
  }

  @Override
  public UsageAmountRecord updateStatistic(UsageAmountRecord statistic) {
    return null;
  }

  @Override
  public void deleteStatistic(UsageAmountRecord statistic) {}
}
