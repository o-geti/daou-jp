package com.minsu.kim.daoujapan.enums;

import lombok.Getter;

/**
 * 통계 파일 내부의 인덱스 정보를 의미합니다.
 *
 * @author minsu.kim
 * @since 1.0
 */
@Getter
public enum StatisticIndex {
  RECORD_TIME_INDEX(0),
  SUBSCRIBER_INDEX(1),
  LEAVER_INDEX(2),
  PAYMENT_INDEX(3),
  USAGE_INDEX(4),
  SALES_INDEX(5);

  private final Integer index;

  StatisticIndex(Integer index) {
    this.index = index;
  }
}
