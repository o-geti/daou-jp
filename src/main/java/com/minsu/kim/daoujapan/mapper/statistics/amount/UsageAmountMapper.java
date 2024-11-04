package com.minsu.kim.daoujapan.mapper.statistics.amount;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.minsu.kim.daoujapan.data.statistics.amount.UsageAmountRecord;
import com.minsu.kim.daoujapan.domains.statistics.amount.UsageAmountStatisticEntity;

/**
 * @author minsu.kim
 * @since 1.0
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsageAmountMapper {

  UsageAmountMapper INSTANCE = Mappers.getMapper(UsageAmountMapper.class);

  UsageAmountRecord entityToDto(UsageAmountStatisticEntity entity);

  UsageAmountStatisticEntity dtoToEntity(UsageAmountRecord usageAmountRecord);
}
