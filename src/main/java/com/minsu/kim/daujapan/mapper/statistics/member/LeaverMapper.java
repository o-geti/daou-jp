package com.minsu.kim.daujapan.mapper.statistics.member;

import com.minsu.kim.daujapan.data.statistics.member.LeaverRecord;
import com.minsu.kim.daujapan.domains.statistics.member.LeaverStatisticEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 *
 * @author minsu.kim
 * @since 1.0
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LeaverMapper {
    LeaverMapper INSTANCE =  Mappers.getMapper(LeaverMapper.class);

    LeaverRecord entityToDto(LeaverStatisticEntity entity);
    LeaverStatisticEntity dtoToEntity(LeaverRecord leaverRecord);
}
