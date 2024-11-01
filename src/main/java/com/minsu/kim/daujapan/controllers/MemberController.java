package com.minsu.kim.daujapan.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minsu.kim.daujapan.domain.member.MemberWithView;
import com.minsu.kim.daujapan.records.response.CommonResponse;

/**
 * TODO: 예시입니다.
 *
 * @author minsu.kim
 * @since 1.0
 */
@RestController
@RequestMapping("/v1/member")
@Tag(name = "멤버를 조회할 수 있는 컨트롤러입니다.")
@Slf4j
public class MemberController {

  @GetMapping("")
  @Operation(summary = "사용자 목록 조회", description = "모든 사용자의 목록을 반환합니다.")
  public CommonResponse<Object> getMemberInfos(
      @ParameterObject @ModelAttribute MemberWithView.Filter filter) {
    log.info("filter : {}", filter.toString());
    return CommonResponse.responseSuccess(null);
  }
}
