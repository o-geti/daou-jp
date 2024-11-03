package com.minsu.kim.daujapan.services.statistics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.minsu.kim.daujapan.data.statistics.member.LeaverRecord;
import com.minsu.kim.daujapan.data.statistics.member.SubscriberRecord;
import com.minsu.kim.daujapan.mapper.statistics.member.LeaverMapper;
import com.minsu.kim.daujapan.mapper.statistics.member.SubscriberMapper;
import com.minsu.kim.daujapan.repositories.statistics.member.LeaverStatisticRepository;
import com.minsu.kim.daujapan.repositories.statistics.member.SubscriberStatisticRepository;

/**
 * @author minsu.kim
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberStatisticService {
  private final SubscriberStatisticRepository subscriberStatisticRepository;
  private final LeaverStatisticRepository leaverStatisticRepository;

  private final SubscriberMapper subscriberMapper;
  private final LeaverMapper leaverMapper;

  public SubscriberRecord saveSubscriberStatistic(SubscriberRecord subscriberStatistic) {
    var subscriberEntity = subscriberMapper.dtoToEntity(subscriberStatistic);
    var savedEntity = subscriberStatisticRepository.save(subscriberEntity);

    return subscriberMapper.entityToDto(savedEntity);
  }

  public LeaverRecord saveLeaverStatistic(LeaverRecord leaverStatistic) {
    var leaverStatisticEntity = leaverMapper.dtoToEntity(leaverStatistic);
    var savedEntity = leaverStatisticRepository.save(leaverStatisticEntity);

    return leaverMapper.entityToDto(savedEntity);
  }
}
