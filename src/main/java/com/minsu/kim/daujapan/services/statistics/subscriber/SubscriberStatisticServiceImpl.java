package com.minsu.kim.daujapan.services.statistics.subscriber;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.minsu.kim.daujapan.data.response.Paging;
import com.minsu.kim.daujapan.data.statistics.StatisticRecord;
import com.minsu.kim.daujapan.data.statistics.member.SubscriberRecord;
import com.minsu.kim.daujapan.mapper.statistics.member.SubscriberMapper;
import com.minsu.kim.daujapan.repositories.statistics.member.SubscriberStatisticRepository;
import com.minsu.kim.daujapan.services.statistics.StatisticService;

/**
 * @author minsu.kim
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class SubscriberStatisticServiceImpl implements StatisticService<SubscriberRecord> {
  private final SubscriberStatisticRepository subscriberStatisticRepository;
  private final SubscriberMapper subscriberMapper;

  @Override
  public Paging<SubscriberRecord> findStatistics(Pageable pageable) {
    var pageSubscriber = subscriberStatisticRepository.findAll(pageable);

    return Paging.createPaging(pageSubscriber, pageable, subscriberMapper::entityToDto);
  }

  @Override
  public Paging<SubscriberRecord> findStatisticsByDateTime(
      LocalDateTime from, LocalDateTime to, Pageable pageable) {
    var pageSubscriber =
        subscriberStatisticRepository.findAllByRecordTimeBetween(from, to, pageable);

    return Paging.createPaging(pageSubscriber, pageable, subscriberMapper::entityToDto);
  }

  @Override
  public SubscriberRecord saveStatistic(SubscriberRecord statistic) {
    var subscriberEntity = subscriberMapper.dtoToEntity(statistic);
    var savedEntity = subscriberStatisticRepository.save(subscriberEntity);

    return subscriberMapper.entityToDto(savedEntity);
  }

  @Override
  public SubscriberRecord saveStatistic(StatisticRecord statistic) {
    return this.saveStatistic(statistic.subscriberRecord());
  }

  @Override
  public void deleteStatistic(SubscriberRecord statistic) {}
}
