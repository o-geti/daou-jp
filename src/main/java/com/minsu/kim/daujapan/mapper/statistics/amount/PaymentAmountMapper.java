package com.minsu.kim.daujapan.mapper.statistics.amount;

import com.minsu.kim.daujapan.data.statistics.amount.PaymentAmountRecord;
import com.minsu.kim.daujapan.domains.statistics.amount.PaymentAmountStatisticEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 *
 * @author minsu.kim
 * @since 1.0
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentAmountMapper {

    PaymentAmountMapper INSTANCE =  Mappers.getMapper(PaymentAmountMapper.class);

    PaymentAmountRecord entityToDto(PaymentAmountStatisticEntity entity);
    PaymentAmountStatisticEntity dtoToEntity(PaymentAmountRecord paymentAmountRecord);
}
