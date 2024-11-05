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
import com.minsu.kim.daoujapan.exception.NotFoundException;
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
    var pageLeaver = usageAmountStatisticRepository.findAllByDeleteDtIsNull(pageable);

    return Paging.createPaging(pageLeaver, pageable, usageAmountMapper::entityToDto);
  }

  @Transactional(readOnly = true)
  @Override
  public Paging<UsageAmountRecord> findStatisticsByDateTime(
      LocalDateTime from, LocalDateTime to, Pageable pageable) {
    var pageLeaver =
        usageAmountStatisticRepository.findAllByRecordTimeBetweenAndDeleteDtIsNull(
            from, to, pageable);

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

  @Transactional
  @Override
  public UsageAmountRecord updateStatistic(UsageAmountRecord statistic) {
    var usageAmountStatisticEntity =
        usageAmountStatisticRepository
            .findByDeleteDtIsNullAndId(statistic.id())
            .orElseThrow(() -> new NotFoundException("사용금액 정보를 찾을 수 없습니다."));

    usageAmountStatisticEntity.modifyEntity(statistic.recordTime(), statistic.usageAmount());

    return usageAmountMapper.entityToDto(usageAmountStatisticEntity);
  }

  @Transactional
  @Override
  public void deleteStatistic(Long statisticId) {
    usageAmountStatisticRepository
        .findByDeleteDtIsNullAndId(statisticId)
        .orElseThrow(() -> new NotFoundException("사용금액 정보를 찾을 수 없습니다."))
        .deleteStatistic();
  }
}
