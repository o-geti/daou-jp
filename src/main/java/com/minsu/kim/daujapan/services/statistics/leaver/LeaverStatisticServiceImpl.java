package com.minsu.kim.daujapan.services.statistics.leaver;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.minsu.kim.daujapan.data.response.Paging;
import com.minsu.kim.daujapan.data.statistics.StatisticRecord;
import com.minsu.kim.daujapan.data.statistics.member.LeaverRecord;
import com.minsu.kim.daujapan.mapper.statistics.member.LeaverMapper;
import com.minsu.kim.daujapan.repositories.statistics.member.LeaverStatisticRepository;
import com.minsu.kim.daujapan.services.statistics.StatisticService;

/**
 * @author minsu.kim
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class LeaverStatisticServiceImpl implements StatisticService<LeaverRecord> {
  private final LeaverStatisticRepository leaverStatisticRepository;
  private final LeaverMapper leaverMapper;

  @Override
  public Paging<LeaverRecord> findStatistics(Pageable pageable) {
    var pageLeaver = leaverStatisticRepository.findAll(pageable);

    return Paging.createPaging(pageLeaver, pageable, leaverMapper::entityToDto);
  }

  @Override
  public Paging<LeaverRecord> findStatisticsByDateTime(
      LocalDateTime from, LocalDateTime to, Pageable pageable) {
    var pageLeaver = leaverStatisticRepository.findAllByRecordTimeBetween(from, to, pageable);

    return Paging.createPaging(pageLeaver, pageable, leaverMapper::entityToDto);
  }

  @Override
  public LeaverRecord saveStatistic(LeaverRecord statistic) {
    var leaverStatisticEntity = leaverMapper.dtoToEntity(statistic);
    var savedEntity = leaverStatisticRepository.save(leaverStatisticEntity);

    return leaverMapper.entityToDto(savedEntity);
  }

  @Override
  public LeaverRecord saveStatistic(StatisticRecord statistic) {
    return this.saveStatistic(statistic.leaverRecord());
  }

  @Override
  public LeaverRecord updateStatistic(LeaverRecord statistic) {
    return null;
  }

  @Override
  public void deleteStatistic(LeaverRecord statistic) {}
}
