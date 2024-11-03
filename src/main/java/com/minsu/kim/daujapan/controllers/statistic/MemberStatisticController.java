package com.minsu.kim.daujapan.controllers.statistic;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minsu.kim.daujapan.data.response.CommonResponse;
import com.minsu.kim.daujapan.data.statistics.member.SubscriberRecord;

/**
 * @author minsu.kim
 * @since 1.0
 */
@RestController
@Tag(name = "멤버와 관련된 통계 데이터를 조회할 수 있는 컨트롤러입니다.")
@Slf4j
@RequestMapping("/v1/statistic/member")
public class MemberStatisticController {

  @GetMapping
  @Operation(summary = "가입자 통계 목록 조회", description = "시간대별로 가입자의 통계를 확인할 수 있습니다.")
  public CommonResponse<Object> searchSubscriberStatistic(
      @ParameterObject @ModelAttribute SubscriberRecord.Filter filter) {
    //    PageRequest.of()
    return CommonResponse.responseSuccess(null);
  }
}
