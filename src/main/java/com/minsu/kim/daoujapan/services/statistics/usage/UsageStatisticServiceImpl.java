package com.minsu.kim.daoujapan.services.statistics.usage;

import java.time.LocalDateTime;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minsu.kim.daoujapan.data.response.Paging;
import com.minsu.kim.daoujapan.data.statistics.StatisticRecord;
import com.minsu.kim.daoujapan.data.statistics.amount.UsageAmountRecord;
import com.minsu.kim.daoujapan.exception.StatisticIsAlreadyExist;
import com.minsu.kim.daoujapan.mapper.statistics.amount.UsageAmountMapper;
import com.minsu.kim.daoujapan.repositories.statistics.amount.UsageAmountStatisticRepository;
import com.minsu.kim.daoujapan.services.statistics.StatisticService;

/**
 * @author minsu.kim
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class UsageStatisticServiceImpl implements StatisticService<UsageAmountRecord> {

  private final UsageAmountStatisticRepository usageAmountStatisticRepository;
  private final UsageAmountMapper usageAmountMapper;

  @Transactional(readOnly = true)
  @Override
  public Paging<UsageAmountRecord> findStatistics(Pageable pageable) {
    var pageLeaver = usageAmountStatisticRepository.findAll(pageable);

    return Paging.createPaging(pageLeaver, pageable, usageAmountMapper::entityToDto);
  }

  @Transactional(readOnly = true)
  @Override
  public Paging<UsageAmountRecord> findStatisticsByDateTime(
      LocalDateTime from, LocalDateTime to, Pageable pageable) {
    var pageLeaver = usageAmountStatisticRepository.findAllByRecordTimeBetween(from, to, pageable);

    return Paging.createPaging(pageLeaver, pageable, usageAmountMapper::entityToDto);
  }

  @Transactional
  @Override
  public UsageAmountRecord saveStatistic(UsageAmountRecord statistic) {
    if (Boolean.TRUE.equals(
        usageAmountStatisticRepository.existsByRecordTime(statistic.recordTime()))) {
      throw new StatisticIsAlreadyExist();
    }

    var usageAmountStatisticEntity = usageAmountMapper.dtoToEntity(statistic);
    var savedEntity = usageAmountStatisticRepository.save(usageAmountStatisticEntity);

    return usageAmountMapper.entityToDto(savedEntity);
  }

  @Transactional
  @Override
  public Optional<UsageAmountRecord> saveStatistic(StatisticRecord statistic) {
    return statistic.usageAmountRecord().map(this::saveStatistic);
  }

  @Override
  public UsageAmountRecord updateStatistic(UsageAmountRecord statistic) {
    return null;
  }

  @Transactional
  @Override
  public void deleteStatistic(UsageAmountRecord statistic) {}
}
