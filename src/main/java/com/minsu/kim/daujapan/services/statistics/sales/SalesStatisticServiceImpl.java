package com.minsu.kim.daujapan.services.statistics.sales;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.minsu.kim.daujapan.data.response.Paging;
import com.minsu.kim.daujapan.data.statistics.StatisticRecord;
import com.minsu.kim.daujapan.data.statistics.amount.SalesAmountRecord;
import com.minsu.kim.daujapan.mapper.statistics.amount.SalesAmountMapper;
import com.minsu.kim.daujapan.repositories.statistics.amount.SalesAmountStatisticRepository;
import com.minsu.kim.daujapan.services.statistics.StatisticService;

/**
 * @author minsu.kim
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class SalesStatisticServiceImpl implements StatisticService<SalesAmountRecord> {
  private final SalesAmountStatisticRepository salesAmountStatisticRepository;
  private final SalesAmountMapper salesAmountMapper;

  @Override
  public Paging<SalesAmountRecord> findStatistics(Pageable pageable) {

    var pageLeaver = salesAmountStatisticRepository.findAll(pageable);

    return Paging.createPaging(pageLeaver, pageable, salesAmountMapper::entityToDto);
  }

  @Override
  public Paging<SalesAmountRecord> findStatisticsByDateTime(
      LocalDateTime from, LocalDateTime to, Pageable pageable) {
    var pageLeaver = salesAmountStatisticRepository.findAllByRecordTimeBetween(from, to, pageable);

    return Paging.createPaging(pageLeaver, pageable, salesAmountMapper::entityToDto);
  }

  @Override
  public SalesAmountRecord saveStatistic(SalesAmountRecord statistic) {
    var salesAmountStatisticEntity = salesAmountMapper.dtoToEntity(statistic);
    var savedEntity = salesAmountStatisticRepository.save(salesAmountStatisticEntity);

    return salesAmountMapper.entityToDto(savedEntity);
  }

  @Override
  public SalesAmountRecord saveStatistic(StatisticRecord statistic) {
    return this.saveStatistic(statistic.salesAmountRecord());
  }

  @Override
  public SalesAmountRecord updateStatistic(SalesAmountRecord statistic) {
    return null;
  }

  @Override
  public void deleteStatistic(SalesAmountRecord statistic) {}
}
