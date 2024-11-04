package com.minsu.kim.daoujapan.services.statistics.sales;

import java.time.LocalDateTime;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minsu.kim.daoujapan.data.response.Paging;
import com.minsu.kim.daoujapan.data.statistics.StatisticRecord;
import com.minsu.kim.daoujapan.data.statistics.amount.SalesAmountRecord;
import com.minsu.kim.daoujapan.exception.StatisticIsAlreadyExist;
import com.minsu.kim.daoujapan.mapper.statistics.amount.SalesAmountMapper;
import com.minsu.kim.daoujapan.repositories.statistics.amount.SalesAmountStatisticRepository;
import com.minsu.kim.daoujapan.services.statistics.StatisticService;

/**
 * @author minsu.kim
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class SalesStatisticServiceImpl implements StatisticService<SalesAmountRecord> {
  private final SalesAmountStatisticRepository salesAmountStatisticRepository;
  private final SalesAmountMapper salesAmountMapper;

  @Transactional(readOnly = true)
  @Override
  public Paging<SalesAmountRecord> findStatistics(Pageable pageable) {

    var pageLeaver = salesAmountStatisticRepository.findAll(pageable);

    return Paging.createPaging(pageLeaver, pageable, salesAmountMapper::entityToDto);
  }

  @Transactional(readOnly = true)
  @Override
  public Paging<SalesAmountRecord> findStatisticsByDateTime(
      LocalDateTime from, LocalDateTime to, Pageable pageable) {
    var pageLeaver = salesAmountStatisticRepository.findAllByRecordTimeBetween(from, to, pageable);

    return Paging.createPaging(pageLeaver, pageable, salesAmountMapper::entityToDto);
  }

  @Transactional
  @Override
  public SalesAmountRecord saveStatistic(SalesAmountRecord statistic) {
    if (Boolean.TRUE.equals(
        salesAmountStatisticRepository.existsByRecordTime(statistic.recordTime()))) {
      throw new StatisticIsAlreadyExist();
    }

    var salesAmountStatisticEntity = salesAmountMapper.dtoToEntity(statistic);
    var savedEntity = salesAmountStatisticRepository.save(salesAmountStatisticEntity);

    return salesAmountMapper.entityToDto(savedEntity);
  }

  @Transactional
  @Override
  public Optional<SalesAmountRecord> saveStatistic(StatisticRecord statistic) {
    return statistic.salesAmountRecord().map(this::saveStatistic);
  }

  @Override
  public SalesAmountRecord updateStatistic(SalesAmountRecord statistic) {
    return null;
  }

  @Transactional
  @Override
  public void deleteStatistic(SalesAmountRecord statistic) {}
}
