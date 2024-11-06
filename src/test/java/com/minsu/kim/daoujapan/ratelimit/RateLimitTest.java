package com.minsu.kim.daoujapan.ratelimit;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import com.minsu.kim.daoujapan.services.statistics.leaver.LeaverStatisticServiceImpl;

/**
 * @author minsu.kim
 * @since 1.0
 */
@SpringBootTest
public class RateLimitTest {

  @Autowired private LeaverStatisticServiceImpl service;

  @Test
  public void testRateLimiter() {
    int successfulCalls = 0;
    int rejectedCalls = 0;

    for (int i = 0; i < 10; i++) {
      try {
        PageRequest.of(0, 30);
        service.findStatistics(PageRequest.of(0, 30));
        successfulCalls++;
      } catch (RequestNotPermitted e) {
        rejectedCalls++;
        System.out.println("Request not permitted: " + e.getMessage());
      }
    }

    // 테스트에서 설정은 1초당 최대 5건의 요청을 수용할 수 있음.
    // 대기시간은 따로 없음.
    assertThat(successfulCalls).isEqualTo(5);
    assertThat(rejectedCalls).isEqualTo(5);
  }
}
