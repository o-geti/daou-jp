package com.minsu.kim.daoujapan.services.statistics.payment;

import java.time.LocalDateTime;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.minsu.kim.daoujapan.data.response.Paging;
import com.minsu.kim.daoujapan.data.statistics.StatisticRecord;
import com.minsu.kim.daoujapan.data.statistics.amount.PaymentAmountRecord;
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

  @Override
  public Paging<PaymentAmountRecord> findStatistics(Pageable pageable) {
    var pageSubscriber = paymentAmountStatisticRepository.findAll(pageable);

    return Paging.createPaging(pageSubscriber, pageable, paymentAmountMapper::entityToDto);
  }

  @Override
  public Paging<PaymentAmountRecord> findStatisticsByDateTime(
      LocalDateTime from, LocalDateTime to, Pageable pageable) {
    var pageSubscriber =
        paymentAmountStatisticRepository.findAllByRecordTimeBetween(from, to, pageable);

    return Paging.createPaging(pageSubscriber, pageable, paymentAmountMapper::entityToDto);
  }

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

  @Override
  public Optional<PaymentAmountRecord> saveStatistic(StatisticRecord statistic) {
    return statistic.paymentAmountRecord().map(this::saveStatistic);
  }

  @Override
  public void deleteStatistic(PaymentAmountRecord statistic) {}
}