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

import com.minsu.kim.daoujapan.data.request.SalesAmountRequest;
import com.minsu.kim.daoujapan.data.response.CommonResponse;
import com.minsu.kim.daoujapan.data.response.Paging;
import com.minsu.kim.daoujapan.data.statistics.amount.SalesAmountRecord;
import com.minsu.kim.daoujapan.helper.LocalDateTimeParamChecker;
import com.minsu.kim.daoujapan.services.statistics.StatisticService;

/**
 * @author minsu.kim
 * @since 1.0
 */
@RestController
@Tag(name = "매출금액 통계 컨트롤러.")
@Slf4j
@RequestMapping("/v1/statistic/sales-amount")
@RequiredArgsConstructor
public class SalesAmountStatisticController {

  private final StatisticService<SalesAmountRecord> salesAmountRecordStatisticService;
  private final LocalDateTimeParamChecker localDateTimeParamChecker;

  @GetMapping
  @Operation(summary = "매출금액 통계 목록 조회", description = "시간대별로 매출금액 통계를 확인할 수 있습니다.")
  public CommonResponse<Paging<SalesAmountRecord>> searchLeaverStatistic(
      @ParameterObject @ModelAttribute SalesAmountRecord.Filter filter) {
    var filtered =
        localDateTimeParamChecker
            .checkForBetweenFromAndTo(filter.getSearchFrom(), filter.getSearchTo())
            .isPresent();

    var pageable = PageRequest.of(filter.getPage(), filter.getSize());

    if (!filtered) {
      return CommonResponse.responseSuccess(
          salesAmountRecordStatisticService.findStatistics(pageable));
    }

    return CommonResponse.responseSuccess(
        salesAmountRecordStatisticService.findStatisticsByDateTime(
            filter.getSearchFrom(), filter.getSearchTo(), pageable));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "매출금액 통계 단건 등록", description = "매출금액의 통계 수치를 등록할 수 있습니다.")
  public CommonResponse<SalesAmountRecord> registerLeaverStatistic(
      @RequestBody @Valid SalesAmountRequest salesAmountRequest) {

    var newLeaver =
        SalesAmountRecord.builder()
            .salesAmount(salesAmountRequest.salesAmount())
            .recordTime(salesAmountRequest.recordTime())
            .build();

    return CommonResponse.responseCreated(
        salesAmountRecordStatisticService.saveStatistic(newLeaver));
  }

  @PutMapping("/{id}")
  @Operation(summary = "매출금액 통계 수정", description = "매출금액의 통계 수치를 수정할 수 있습니다.")
  public CommonResponse<SalesAmountRecord> updateLeaverStatistic(
      @Valid @NotNull @Min(1) @PathVariable Long id,
      @Valid @RequestBody SalesAmountRequest salesAmountRequest) {

    var updateLeaverStatistic =
        SalesAmountRecord.builder()
            .id(id)
            .salesAmount(salesAmountRequest.salesAmount())
            .recordTime(salesAmountRequest.recordTime())
            .build();

    return CommonResponse.responseSuccess(
        salesAmountRecordStatisticService.updateStatistic(updateLeaverStatistic));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "매출금액 통계 삭제", description = "매출금액의 통계 수치를 삭제 할 수 있습니다.")
  public Void deleteLeaverStatistic(@Valid @NotNull @Min(1) @PathVariable Long id) {
    salesAmountRecordStatisticService.deleteStatistic(id);

    return null;
  }
}
