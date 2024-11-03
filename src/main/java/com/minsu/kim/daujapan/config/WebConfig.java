package com.minsu.kim.daujapan.config;

import com.minsu.kim.daujapan.interceptors.RequestProcessTimeMarkerInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
