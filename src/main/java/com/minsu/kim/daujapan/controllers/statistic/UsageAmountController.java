package com.minsu.kim.daujapan.controllers.statistic;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minsu.kim.daujapan.data.response.CommonResponse;
import com.minsu.kim.daujapan.data.statistics.amount.UsageAmountRecord;
import com.minsu.kim.daujapan.helper.LocalDateTimeParamChecker;
import com.minsu.kim.daujapan.services.statistics.StatisticService;

/**
 * @author minsu.kim
 * @since 1.0
 */
@RestController
@Tag(name = "사용금액 통계 컨트롤러.")
@Slf4j
@RequestMapping("/v1/statistic/usage-amount")
@RequiredArgsConstructor
public class UsageAmountController {

  private final StatisticService<UsageAmountRecord> usageAmountRecordStatisticService;
  private final LocalDateTimeParamChecker localDateTimeParamChecker;

  @GetMapping
  @Operation(summary = "사용금액 통계 목록 조회", description = "시간대별로 사용금액 통계를 확인할 수 있습니다.")
  public CommonResponse<Object> searchLeaverStatistic(
      @ParameterObject @ModelAttribute UsageAmountRecord.Filter filter) {
    var filtered =
        localDateTimeParamChecker.checkForBetweenFromAndTo(
                filter.getSearchFrom(), filter.getSearchTo())
            .isPresent();

    var pageable = PageRequest.of(filter.getPage(), filter.getSize());

    if (!filtered) {
      return CommonResponse.responseSuccess(
          usageAmountRecordStatisticService.findStatistics(pageable));
    }

    return CommonResponse.responseSuccess(
        usageAmountRecordStatisticService.findStatisticsByDateTime(
            filter.getSearchFrom(), filter.getSearchTo(), pageable));
  }
}
