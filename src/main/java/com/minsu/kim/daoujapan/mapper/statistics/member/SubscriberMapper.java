package com.minsu.kim.daoujapan.mapper.statistics.member;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.minsu.kim.daoujapan.data.statistics.member.SubscriberRecord;
import com.minsu.kim.daoujapan.domains.statistics.member.SubscriberStatisticEntity;

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
