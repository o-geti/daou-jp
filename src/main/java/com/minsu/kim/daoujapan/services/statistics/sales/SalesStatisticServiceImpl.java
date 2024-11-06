package com.minsu.kim.daoujapan.services.statistics.sales;

import java.time.LocalDateTime;
import java.util.Optional;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minsu.kim.daoujapan.data.response.Paging;
import com.minsu.kim.daoujapan.data.statistics.StatisticRecord;
import com.minsu.kim.daoujapan.data.statistics.amount.SalesAmountRecord;
import com.minsu.kim.daoujapan.exception.NotFoundException;
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

  @RateLimiter(name = "STATISTIC_SERVICE")
  @Transactional(readOnly = true)
  @Override
  public Paging<SalesAmountRecord> findStatistics(Pageable pageable) {

    var pageLeaver = salesAmountStatisticRepository.findAllByDeleteDtIsNull(pageable);

    return Paging.createPaging(pageLeaver, pageable, salesAmountMapper::entityToDto);
  }

  @RateLimiter(name = "STATISTIC_SERVICE")
  @Transactional(readOnly = true)
  @Override
  public Paging<SalesAmountRecord> findStatisticsByDateTime(
      LocalDateTime from, LocalDateTime to, Pageable pageable) {
    var pageLeaver =
        salesAmountStatisticRepository.findAllByRecordTimeBetweenAndDeleteDtIsNull(
            from, to, pageable);

    return Paging.createPaging(pageLeaver, pageable, salesAmountMapper::entityToDto);
  }

  @RateLimiter(name = "STATISTIC_SERVICE")
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

  @RateLimiter(name = "STATISTIC_SERVICE")
  @Transactional
  @Override
  public SalesAmountRecord updateStatistic(SalesAmountRecord statistic) {
    var salesAmountStatisticEntity =
        salesAmountStatisticRepository
            .findByDeleteDtIsNullAndId(statistic.id())
            .orElseThrow(() -> new NotFoundException("매출금액 정보를 찾을 수 없습니다."));

    salesAmountStatisticEntity.modifyEntity(statistic.recordTime(), statistic.salesAmount());

    return salesAmountMapper.entityToDto(salesAmountStatisticEntity);
  }

  @RateLimiter(name = "STATISTIC_SERVICE")
  @Transactional
  @Override
  public void deleteStatistic(Long statisticId) {
    salesAmountStatisticRepository
        .findByDeleteDtIsNullAndId(statisticId)
        .orElseThrow(() -> new NotFoundException("매출금액 정보를 찾을 수 없습니다."))
        .deleteStatistic();
  }
}
