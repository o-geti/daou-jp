package com.minsu.kim.daujapan.mapper.statistics.amount;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.minsu.kim.daujapan.data.statistics.amount.SalesAmountRecord;
import com.minsu.kim.daujapan.domains.statistics.amount.SalesAmountStatisticEntity;

/**
 * @author minsu.kim
 * @since 1.0
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SalesAmountMapper {

  SalesAmountMapper INSTANCE = Mappers.getMapper(SalesAmountMapper.class);

  SalesAmountRecord entityToDto(SalesAmountStatisticEntity entity);

  SalesAmountStatisticEntity dtoToEntity(SalesAmountRecord salesAmountRecord);
}
