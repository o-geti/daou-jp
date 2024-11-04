package com.minsu.kim.daoujapan.services.statistics.subscriber;

import java.time.LocalDateTime;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minsu.kim.daoujapan.data.response.Paging;
import com.minsu.kim.daoujapan.data.statistics.StatisticRecord;
import com.minsu.kim.daoujapan.data.statistics.member.SubscriberRecord;
import com.minsu.kim.daoujapan.exception.StatisticIsAlreadyExist;
import com.minsu.kim.daoujapan.mapper.statistics.member.SubscriberMapper;
import com.minsu.kim.daoujapan.repositories.statistics.member.SubscriberStatisticRepository;
import com.minsu.kim.daoujapan.services.statistics.StatisticService;

/**
 * @author minsu.kim
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class SubscriberStatisticServiceImpl implements StatisticService<SubscriberRecord> {
  private final SubscriberStatisticRepository subscriberStatisticRepository;
  private final SubscriberMapper subscriberMapper;

  @Transactional(readOnly = true)
  @Override
  public Paging<SubscriberRecord> findStatistics(Pageable pageable) {
    var pageSubscriber = subscriberStatisticRepository.findAllByDeleteDtIsNull(pageable);

    return Paging.createPaging(pageSubscriber, pageable, subscriberMapper::entityToDto);
  }

  @Transactional(readOnly = true)
  @Override
  public Paging<SubscriberRecord> findStatisticsByDateTime(
      LocalDateTime from, LocalDateTime to, Pageable pageable) {
    var pageSubscriber =
        subscriberStatisticRepository.findAllByRecordTimeBetweenAndDeleteDtIsNull(
            from, to, pageable);

    return Paging.createPaging(pageSubscriber, pageable, subscriberMapper::entityToDto);
  }

  @Transactional
  @Override
  public SubscriberRecord saveStatistic(SubscriberRecord statistic) {

    if (Boolean.TRUE.equals(
        subscriberStatisticRepository.existsByRecordTime(statistic.recordTime()))) {
      throw new StatisticIsAlreadyExist();
    }

    var subscriberEntity = subscriberMapper.dtoToEntity(statistic);
    var savedEntity = subscriberStatisticRepository.save(subscriberEntity);

    return subscriberMapper.entityToDto(savedEntity);
  }

  @Transactional
  @Override
  public Optional<SubscriberRecord> saveStatistic(StatisticRecord statistic) {
    return statistic.subscriberRecord().map(this::saveStatistic);
  }

  @Override
  public SubscriberRecord updateStatistic(SubscriberRecord statistic) {
    return null;
  }

  @Transactional
  @Override
  public void deleteStatistic(Long statisticId) {}
}
