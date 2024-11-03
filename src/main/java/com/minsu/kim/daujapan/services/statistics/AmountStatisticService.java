package com.minsu.kim.daujapan.services.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.minsu.kim.daujapan.data.statistics.amount.PaymentAmountRecord;
import com.minsu.kim.daujapan.data.statistics.amount.SalesAmountRecord;
import com.minsu.kim.daujapan.data.statistics.amount.UsageAmountRecord;
import com.minsu.kim.daujapan.mapper.statistics.amount.PaymentAmountMapper;
import com.minsu.kim.daujapan.mapper.statistics.amount.SalesAmountMapper;
import com.minsu.kim.daujapan.mapper.statistics.amount.UsageAmountMapper;
import com.minsu.kim.daujapan.repositories.statistics.amount.PaymentAmountStatisticRepository;
import com.minsu.kim.daujapan.repositories.statistics.amount.SalesAmountStatisticRepository;
import com.minsu.kim.daujapan.repositories.statistics.amount.UsageAmountStatisticRepository;

/**
 * 자세한 기능을 적어주세요
 *
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class AmountStatisticService {
  private final PaymentAmountStatisticRepository paymentAmountStatisticRepository;
  private final UsageAmountStatisticRepository usageAmountStatisticRepository;
  private final SalesAmountStatisticRepository salesAmountStatisticRepository;

  private final PaymentAmountMapper paymentAmountMapper;
  private final UsageAmountMapper usageAmountMapper;
  private final SalesAmountMapper salesAmountMapper;

  public PaymentAmountRecord savePaymentAmountStatistic(PaymentAmountRecord paymentAmountRecord) {
    var paymentAmountStatisticEntity = paymentAmountMapper.dtoToEntity(paymentAmountRecord);
    var savedEntity = paymentAmountStatisticRepository.save(paymentAmountStatisticEntity);

    return paymentAmountMapper.entityToDto(savedEntity);
  }

  public UsageAmountRecord saveUsageAmountStatistic(UsageAmountRecord usageAmountRecord) {
    var usageAmountStatisticEntity = usageAmountMapper.dtoToEntity(usageAmountRecord);
    var savedEntity = usageAmountStatisticRepository.save(usageAmountStatisticEntity);

    return usageAmountMapper.entityToDto(savedEntity);
  }

  public SalesAmountRecord saveSalesAmountStatistic(SalesAmountRecord salesAmountRecord) {
    var salesAmountStatisticEntity = salesAmountMapper.dtoToEntity(salesAmountRecord);
    var savedEntity = salesAmountStatisticRepository.save(salesAmountStatisticEntity);

    return salesAmountMapper.entityToDto(savedEntity);
  }
}
