package com.minsu.kim.daujapan.services.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.minsu.kim.daujapan.data.statistics.member.SubscriberRecord;
import com.minsu.kim.daujapan.mapper.statistics.member.SubscriberMapper;
import com.minsu.kim.daujapan.repositories.statistics.member.SubscriberStatisticRepository;

/**
 * @author minsu.kim
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class MemberStatisticService {
  private final SubscriberStatisticRepository subscriberStatisticRepository;
  private final SubscriberMapper subscriberMapper;

  public SubscriberRecord saveSubscriberStatistic(SubscriberRecord subscriberStatistic) {
    var subscriberEntity = subscriberMapper.dtoToEntity(subscriberStatistic);
    var savedEntity = subscriberStatisticRepository.save(subscriberEntity);

    return subscriberMapper.entityToDto(savedEntity);
  }
}
