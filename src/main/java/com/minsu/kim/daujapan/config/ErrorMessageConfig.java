package com.minsu.kim.daujapan.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 에러 메시지 설정 객체입니다.
 *
 * @author minsu.kim
 * @since 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "error.message")
public class ErrorMessageConfig {
  private String timeRequired;
  private String fromIsNotAfterTo;
}
