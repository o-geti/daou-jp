package com.minsu.kim.daoujapan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DaouJapanApplication {

  public static void main(String[] args) {
    SpringApplication.run(DaouJapanApplication.class, args);
  }
}
