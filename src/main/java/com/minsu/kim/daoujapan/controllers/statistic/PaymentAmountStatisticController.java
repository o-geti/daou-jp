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

import com.minsu.kim.daoujapan.data.request.PaymentAmountRequest;
import com.minsu.kim.daoujapan.data.response.CommonResponse;
import com.minsu.kim.daoujapan.data.response.Paging;
import com.minsu.kim.daoujapan.data.statistics.amount.PaymentAmountRecord;
import com.minsu.kim.daoujapan.helper.LocalDateTimeParamChecker;
import com.minsu.kim.daoujapan.services.statistics.StatisticService;

/**
 * @author minsu.kim
 * @since 1.0
 */
@RestController
@Tag(name = "결제금액 통계 컨트롤러.")
@Slf4j
@RequestMapping("/v1/statistic/payment-amount")
@RequiredArgsConstructor
public class PaymentAmountStatisticController {

  private final StatisticService<PaymentAmountRecord> paymentAmountRecordStatisticService;
  private final LocalDateTimeParamChecker localDateTimeParamChecker;

  @GetMapping
  @Operation(summary = "결제금액 통계 목록 조회", description = "시간대별로 결제금액 통계를 확인할 수 있습니다.")
  public CommonResponse<Paging<PaymentAmountRecord>> searchLeaverStatistic(
      @ParameterObject @ModelAttribute PaymentAmountRecord.Filter filter) {
    var filtered =
        localDateTimeParamChecker
            .checkForBetweenFromAndTo(filter.getSearchFrom(), filter.getSearchTo())
            .isPresent();

    var pageable = PageRequest.of(filter.getPage(), filter.getSize());

    if (!filtered) {
      return CommonResponse.responseSuccess(
          paymentAmountRecordStatisticService.findStatistics(pageable));
    }

    return CommonResponse.responseSuccess(
        paymentAmountRecordStatisticService.findStatisticsByDateTime(
            filter.getSearchFrom(), filter.getSearchTo(), pageable));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "결제금액 통계 단건 등록", description = "결제금액의 통계 수치를 등록할 수 있습니다.")
  public CommonResponse<PaymentAmountRecord> registerLeaverStatistic(
      @RequestBody @Valid PaymentAmountRequest leaverRecord) {

    var newLeaver =
        PaymentAmountRecord.builder()
            .paymentAmount(leaverRecord.paymentAmount())
            .recordTime(leaverRecord.recordTime())
            .build();

    return CommonResponse.responseCreated(
        paymentAmountRecordStatisticService.saveStatistic(newLeaver));
  }

  @PutMapping("/{id}")
  @Operation(summary = "결제금액 통계 수정", description = "결제금액의 통계 수치를 수정할 수 있습니다.")
  public CommonResponse<PaymentAmountRecord> updateLeaverStatistic(
      @Valid @NotNull @Min(1) @PathVariable Long id,
      @Valid @RequestBody PaymentAmountRequest leaverRecord) {

    var updateLeaverStatistic =
        PaymentAmountRecord.builder()
            .id(id)
            .paymentAmount(leaverRecord.paymentAmount())
            .recordTime(leaverRecord.recordTime())
            .build();

    return CommonResponse.responseSuccess(
        paymentAmountRecordStatisticService.updateStatistic(updateLeaverStatistic));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "결제금액 통계 삭제", description = "결제금액의 통계 수치를 삭제 할 수 있습니다.")
  public Void deleteLeaverStatistic(@Valid @NotNull @Min(1) @PathVariable Long id) {
    paymentAmountRecordStatisticService.deleteStatistic(id);

    return null;
  }
}
