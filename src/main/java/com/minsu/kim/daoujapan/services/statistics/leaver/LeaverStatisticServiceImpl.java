package com.minsu.kim.daoujapan.services.statistics.leaver;

import java.time.LocalDateTime;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minsu.kim.daoujapan.data.response.Paging;
import com.minsu.kim.daoujapan.data.statistics.StatisticRecord;
import com.minsu.kim.daoujapan.data.statistics.member.LeaverRecord;
import com.minsu.kim.daoujapan.exception.NotFoundException;
import com.minsu.kim.daoujapan.exception.StatisticIsAlreadyExist;
import com.minsu.kim.daoujapan.mapper.statistics.member.LeaverMapper;
import com.minsu.kim.daoujapan.repositories.statistics.member.LeaverStatisticRepository;
import com.minsu.kim.daoujapan.services.statistics.StatisticService;

/**
 * @author minsu.kim
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LeaverStatisticServiceImpl implements StatisticService<LeaverRecord> {
  private final LeaverStatisticRepository leaverStatisticRepository;
  private final LeaverMapper leaverMapper;

  @Transactional(readOnly = true)
  @Override
  public Paging<LeaverRecord> findStatistics(Pageable pageable) {
    var pageLeaver = leaverStatisticRepository.findAllByDeleteDtIsNull(pageable);

    return Paging.createPaging(pageLeaver, pageable, leaverMapper::entityToDto);
  }

  @Transactional(readOnly = true)
  @Override
  public Paging<LeaverRecord> findStatisticsByDateTime(
      LocalDateTime from, LocalDateTime to, Pageable pageable) {
    var pageLeaver =
        leaverStatisticRepository.findAllByRecordTimeBetweenAndDeleteDtIsNull(from, to, pageable);

    return Paging.createPaging(pageLeaver, pageable, leaverMapper::entityToDto);
  }

  @Transactional
  @Override
  public LeaverRecord saveStatistic(LeaverRecord statistic) {
    if (Boolean.TRUE.equals(leaverStatisticRepository.existsByRecordTime(statistic.recordTime()))) {
      throw new StatisticIsAlreadyExist();
    }

    var leaverStatisticEntity = leaverMapper.dtoToEntity(statistic);
    var savedEntity = leaverStatisticRepository.save(leaverStatisticEntity);

    return leaverMapper.entityToDto(savedEntity);
  }

  @Transactional
  @Override
  public Optional<LeaverRecord> saveStatistic(StatisticRecord statistic) {
    return statistic.leaverRecord().map(this::saveStatistic);
  }

  @Transactional
  @Override
  public LeaverRecord updateStatistic(LeaverRecord statistic) {
    var leaverEntity =
        leaverStatisticRepository
            .findByDeleteDtIsNullAndId(statistic.id())
            .orElseThrow(() -> new NotFoundException("탈퇴자 정보를 찾을 수 없습니다."));

    leaverEntity.modifyEntity(statistic.recordTime(), statistic.leaverCount());

    return leaverMapper.entityToDto(leaverEntity);
  }

  @Transactional
  @Override
  public void deleteStatistic(Long statisticId) {
    leaverStatisticRepository
        .findByDeleteDtIsNullAndId(statisticId)
        .orElseThrow(() -> new NotFoundException("탈퇴자 정보를 찾을 수 없습니다."))
        .deleteStatistic();
  }
}
