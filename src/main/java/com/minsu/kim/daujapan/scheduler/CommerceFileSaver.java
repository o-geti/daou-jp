package com.minsu.kim.daujapan.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 커머스 파일 대상을 저장하는 객체입니다.
 *
 * @author minsu.kim
 * @since 1.0
 */
@Component
public class CommerceFileSaver {

  @Scheduled(cron = "0 0 0 * * *") // 매일 자정 실행
  public void saveToDatabase() {}
}
