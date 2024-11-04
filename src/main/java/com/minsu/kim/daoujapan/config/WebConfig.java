package com.minsu.kim.daoujapan.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.minsu.kim.daoujapan.interceptors.RequestProcessTimeMarkerInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
  private final RequestProcessTimeMarkerInterceptor timeMarkerInterceptor;

  @Override
  public void configurePathMatch(PathMatchConfigurer configurer) {
    configurer.setUseTrailingSlashMatch(true);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(timeMarkerInterceptor);
  }
}
