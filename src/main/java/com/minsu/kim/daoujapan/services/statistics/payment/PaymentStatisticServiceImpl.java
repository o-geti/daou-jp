package com.minsu.kim.daoujapan.services.statistics.payment;

import java.time.LocalDateTime;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.minsu.kim.daoujapan.data.response.Paging;
import com.minsu.kim.daoujapan.data.statistics.StatisticRecord;
import com.minsu.kim.daoujapan.data.statistics.amount.PaymentAmountRecord;
import com.minsu.kim.daoujapan.exception.NotFoundException;
import com.minsu.kim.daoujapan.exception.StatisticIsAlreadyExist;
import com.minsu.kim.daoujapan.mapper.statistics.amount.PaymentAmountMapper;
import com.minsu.kim.daoujapan.repositories.statistics.amount.PaymentAmountStatisticRepository;
import com.minsu.kim.daoujapan.services.statistics.StatisticService;

/**
 * @author minsu.kim
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class PaymentStatisticServiceImpl implements StatisticService<PaymentAmountRecord> {
  private final PaymentAmountStatisticRepository paymentAmountStatisticRepository;
  private final PaymentAmountMapper paymentAmountMapper;

  @Transactional(readOnly = true)
  @Override
  public Paging<PaymentAmountRecord> findStatistics(Pageable pageable) {
    var pageSubscriber = paymentAmountStatisticRepository.findAllByDeleteDtIsNull(pageable);

    return Paging.createPaging(pageSubscriber, pageable, paymentAmountMapper::entityToDto);
  }

  @Transactional(readOnly = true)
  @Override
  public Paging<PaymentAmountRecord> findStatisticsByDateTime(
      LocalDateTime from, LocalDateTime to, Pageable pageable) {
    var pageSubscriber =
        paymentAmountStatisticRepository.findAllByRecordTimeBetweenAndDeleteDtIsNull(
            from, to, pageable);

    return Paging.createPaging(pageSubscriber, pageable, paymentAmountMapper::entityToDto);
  }

  @Transactional
  @Override
  public PaymentAmountRecord saveStatistic(PaymentAmountRecord statistic) {
    if (Boolean.TRUE.equals(
        paymentAmountStatisticRepository.existsByRecordTime(statistic.recordTime()))) {
      throw new StatisticIsAlreadyExist();
    }

    var paymentAmountStatisticEntity = paymentAmountMapper.dtoToEntity(statistic);
    var savedEntity = paymentAmountStatisticRepository.save(paymentAmountStatisticEntity);

    return paymentAmountMapper.entityToDto(savedEntity);
  }

  @Transactional
  @Override
  public Optional<PaymentAmountRecord> saveStatistic(StatisticRecord statistic) {
    return statistic.paymentAmountRecord().map(this::saveStatistic);
  }

  @Transactional
  @Override
  public PaymentAmountRecord updateStatistic(PaymentAmountRecord statistic) {
    var paymentAmountStatisticEntity =
        paymentAmountStatisticRepository
            .findByDeleteDtIsNullAndId(statistic.id())
            .orElseThrow(() -> new NotFoundException("결제금액 정보를 찾을 수 없습니다."));

    paymentAmountStatisticEntity.modifyEntity(statistic.recordTime(), statistic.paymentAmount());

    return paymentAmountMapper.entityToDto(paymentAmountStatisticEntity);
  }

  @Transactional
  @Override
  public void deleteStatistic(Long statisticId) {
    paymentAmountStatisticRepository
        .findByDeleteDtIsNullAndId(statisticId)
        .orElseThrow(() -> new NotFoundException("결제금액 정보를 찾을 수 없습니다."))
        .deleteStatistic();
  }
}
