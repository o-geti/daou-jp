package com.minsu.kim.daoujapan.config;

import java.util.List;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author minsu.kim
 * @since 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.whitelist")
public class WhiteListIpConfig {

  List<String> ipList;
}
