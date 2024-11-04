package com.minsu.kim.daoujapan.mapper.statistics.amount;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.minsu.kim.daoujapan.data.statistics.amount.PaymentAmountRecord;
import com.minsu.kim.daoujapan.domains.statistics.amount.PaymentAmountStatisticEntity;

/**
 * @author minsu.kim
 * @since 1.0
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentAmountMapper {

  PaymentAmountMapper INSTANCE = Mappers.getMapper(PaymentAmountMapper.class);

  PaymentAmountRecord entityToDto(PaymentAmountStatisticEntity entity);

  PaymentAmountStatisticEntity dtoToEntity(PaymentAmountRecord paymentAmountRecord);
}
