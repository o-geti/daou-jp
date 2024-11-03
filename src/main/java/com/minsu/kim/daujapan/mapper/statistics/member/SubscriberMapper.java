package com.minsu.kim.daujapan.mapper.statistics.member;

import com.minsu.kim.daujapan.data.statistics.member.SubscriverRecord;
import com.minsu.kim.daujapan.domains.statistics.member.SubscriberStatisticEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 *
 * @author minsu.kim
 * @since 1.0
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubscriberMapper {
    SubscriberMapper INSTANCE =  Mappers.getMapper(SubscriberMapper.class);

    SubscriverRecord entityToDto(SubscriberStatisticEntity entity);
    SubscriberStatisticEntity dtoToEntity(SubscriverRecord subscriverRecord);
}
