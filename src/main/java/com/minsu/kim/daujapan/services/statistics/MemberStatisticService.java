package com.minsu.kim.daujapan.services.statistics;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.minsu.kim.daujapan.data.response.Paging;
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

  public Paging<SubscriberRecord> findSubscribeRecords(Pageable pageable) {
    var pageSubscriber = subscriberStatisticRepository.findAll(pageable);

    return Paging.createPaging(pageSubscriber, pageable, subscriberMapper::entityToDto);
  }

  public Paging<SubscriberRecord> findSubscribeRecordsByRecordDate(
      LocalDateTime from, LocalDateTime to, Pageable pageable) {
    var pageSubscriber =
        subscriberStatisticRepository.findAllByRecordTimeBetween(from, to, pageable);

    return Paging.createPaging(pageSubscriber, pageable, subscriberMapper::entityToDto);
  }

  public Paging<LeaverRecord> findLeaverRecords(Pageable pageable) {
    var pageLeaver = leaverStatisticRepository.findAll(pageable);

    return Paging.createPaging(pageLeaver, pageable, leaverMapper::entityToDto);
  }

  public Paging<LeaverRecord> findLeaverRecordsByRecordDate(
      LocalDateTime from, LocalDateTime to, Pageable pageable) {
    var pageLeaver = leaverStatisticRepository.findAllByRecordTimeBetween(from, to, pageable);

    return Paging.createPaging(pageLeaver, pageable, leaverMapper::entityToDto);
  }

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
