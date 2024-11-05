package com.minsu.kim.daoujapan.controllers.statistic;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.minsu.kim.daoujapan.data.request.UsageAmountRequest;
import com.minsu.kim.daoujapan.data.response.CommonResponse;
import com.minsu.kim.daoujapan.data.response.Paging;
import com.minsu.kim.daoujapan.data.statistics.amount.UsageAmountRecord;
import com.minsu.kim.daoujapan.helper.LocalDateTimeParamChecker;
import com.minsu.kim.daoujapan.services.statistics.StatisticService;

/**
 * @author minsu.kim
 * @since 1.0
 */
@RestController
@Tag(name = "사용금액 통계 컨트롤러.")
@Slf4j
@RequestMapping("/v1/statistic/usage-amount")
@RequiredArgsConstructor
public class UsageAmountStatisticController {

  private final StatisticService<UsageAmountRecord> usageAmountRecordStatisticService;
  private final LocalDateTimeParamChecker localDateTimeParamChecker;

  @GetMapping
  @Operation(summary = "사용금액 통계 목록 조회", description = "시간대별로 사용금액 통계를 확인할 수 있습니다.")
  public CommonResponse<Paging<UsageAmountRecord>> searchUsageAmountStatistic(
      @ParameterObject @ModelAttribute UsageAmountRecord.Filter filter) {
    var filtered =
        localDateTimeParamChecker
            .checkForBetweenFromAndTo(filter.getSearchFrom(), filter.getSearchTo())
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

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "사용금액 통계 단건 등록", description = "사용금액의 통계 수치를 등록할 수 있습니다.")
  public CommonResponse<UsageAmountRecord> registerLeaverStatistic(
      @RequestBody @Valid UsageAmountRequest usageAmountRequest) {

    var newLeaver =
        UsageAmountRecord.builder()
            .usageAmount(usageAmountRequest.usageAmount())
            .recordTime(usageAmountRequest.recordTime())
            .build();

    return CommonResponse.responseCreated(
        usageAmountRecordStatisticService.saveStatistic(newLeaver));
  }

  @PutMapping("/{id}")
  @Operation(summary = "사용금액 통계 수정", description = "사용금액의 통계 수치를 수정할 수 있습니다.")
  public CommonResponse<UsageAmountRecord> updateLeaverStatistic(
      @Valid @NotNull @Min(1) @PathVariable Long id,
      @Valid @RequestBody UsageAmountRequest usageAmountRequest) {

    var updateLeaverStatistic =
        UsageAmountRecord.builder()
            .id(id)
            .usageAmount(usageAmountRequest.usageAmount())
            .recordTime(usageAmountRequest.recordTime())
            .build();

    return CommonResponse.responseSuccess(
        usageAmountRecordStatisticService.updateStatistic(updateLeaverStatistic));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "사용금액 통계 삭제", description = "사용금액의 통계 수치를 삭제 할 수 있습니다.")
  public Void deleteLeaverStatistic(@Valid @NotNull @Min(1) @PathVariable Long id) {
    usageAmountRecordStatisticService.deleteStatistic(id);

    return null;
  }
}
