package com.minsu.kim.daujapan.mapper.statistics.member;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.minsu.kim.daujapan.data.statistics.member.SubscriberRecord;
import com.minsu.kim.daujapan.domains.statistics.member.SubscriberStatisticEntity;

/**
 * @author minsu.kim
 * @since 1.0
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubscriberMapper {
  SubscriberMapper INSTANCE = Mappers.getMapper(SubscriberMapper.class);

  SubscriberRecord entityToDto(SubscriberStatisticEntity entity);

  SubscriberStatisticEntity dtoToEntity(SubscriberRecord subscriberRecord);
}
