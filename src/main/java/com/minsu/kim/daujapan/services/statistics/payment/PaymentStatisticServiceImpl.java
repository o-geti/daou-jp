package com.minsu.kim.daujapan.services.statistics.payment;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.minsu.kim.daujapan.data.response.Paging;
import com.minsu.kim.daujapan.data.statistics.StatisticRecord;
import com.minsu.kim.daujapan.data.statistics.amount.PaymentAmountRecord;
import com.minsu.kim.daujapan.mapper.statistics.amount.PaymentAmountMapper;
import com.minsu.kim.daujapan.repositories.statistics.amount.PaymentAmountStatisticRepository;
import com.minsu.kim.daujapan.services.statistics.StatisticService;

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
    var paymentAmountStatisticEntity = paymentAmountMapper.dtoToEntity(statistic);
    var savedEntity = paymentAmountStatisticRepository.save(paymentAmountStatisticEntity);

    return paymentAmountMapper.entityToDto(savedEntity);
  }

  @Override
  public PaymentAmountRecord saveStatistic(StatisticRecord statistic) {
    return this.saveStatistic(statistic.paymentAmountRecord());
  }

  @Override
  public PaymentAmountRecord updateStatistic(PaymentAmountRecord statistic) {
    return null;
  }

  @Override
  public void deleteStatistic(PaymentAmountRecord statistic) {}
}
