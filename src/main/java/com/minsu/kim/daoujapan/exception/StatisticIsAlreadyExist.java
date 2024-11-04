package com.minsu.kim.daoujapan.exception;

/**
 * @author minsu.kim
 * @since 1.0
 */
public class StatisticIsAlreadyExist extends RuntimeException {

  public StatisticIsAlreadyExist() {
    super("이미 존재하는 시간대의 통계 데이터입니다. 데이터를 수정해주세요.");
  }
}
