package com.minsu.kim.daujapan.mapper.statistics.member;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.minsu.kim.daujapan.data.statistics.member.LeaverRecord;
import com.minsu.kim.daujapan.domains.statistics.member.LeaverStatisticEntity;

/**
 * @author minsu.kim
 * @since 1.0
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LeaverMapper {
  LeaverMapper INSTANCE = Mappers.getMapper(LeaverMapper.class);

  LeaverRecord entityToDto(LeaverStatisticEntity entity);

  LeaverStatisticEntity dtoToEntity(LeaverRecord leaverRecord);
}
