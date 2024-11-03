package com.minsu.kim.daujapan.config;

import java.util.List;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 파일 스케줄러 설정입니다.
 *
 * <p>extensions : 요구사항의 확장자 변동에 대응하기 위해 확장자를 yml로부터 지정받아 추가 확장자도 적용 가능합니다.
 *
 * @author minsu.kim
 * @since 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "schedule-file")
public class FileScheduleConfig {
  private List<String> extensions;
}
