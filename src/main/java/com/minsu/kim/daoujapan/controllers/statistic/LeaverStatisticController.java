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

import com.minsu.kim.daoujapan.data.request.LeaverRequest;
import com.minsu.kim.daoujapan.data.response.CommonResponse;
import com.minsu.kim.daoujapan.data.response.Paging;
import com.minsu.kim.daoujapan.data.statistics.member.LeaverRecord;
import com.minsu.kim.daoujapan.helper.LocalDateTimeParamChecker;
import com.minsu.kim.daoujapan.services.statistics.StatisticService;

/**
 * @author minsu.kim
 * @since 1.0
 */
@RestController
@Tag(name = "탈퇴자 통계 컨트롤러.")
@Slf4j
@RequestMapping("/v1/statistic/leaver")
@RequiredArgsConstructor
public class LeaverStatisticController {

  private final StatisticService<LeaverRecord> leaverRecordStatisticService;
  private final LocalDateTimeParamChecker localDateTimeParamChecker;

  @GetMapping
  @Operation(summary = "탈퇴자 통계 목록 조회", description = "시간대별로 탈퇴자의 통계를 확인할 수 있습니다.")
  public CommonResponse<Paging<LeaverRecord>> searchLeaverStatistic(
      @ParameterObject @ModelAttribute LeaverRecord.Filter filter) {
    var filtered =
        localDateTimeParamChecker
            .checkForBetweenFromAndTo(filter.getSearchFrom(), filter.getSearchTo())
            .isPresent();

    var pageable = PageRequest.of(filter.getPage(), filter.getSize());

    if (!filtered) {
      return CommonResponse.responseSuccess(leaverRecordStatisticService.findStatistics(pageable));
    }

    return CommonResponse.responseSuccess(
        leaverRecordStatisticService.findStatisticsByDateTime(
            filter.getSearchFrom(), filter.getSearchTo(), pageable));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(summary = "탈퇴자 통계 단건 등록", description = "탈퇴자의 통계 수치를 등록할 수 있습니다.")
  public CommonResponse<LeaverRecord> registerLeaverStatistic(
      @RequestBody @Valid LeaverRequest leaverRecord) {

    var newLeaver =
        LeaverRecord.builder()
            .leaverCount(leaverRecord.leaverCount())
            .recordTime(leaverRecord.recordTime())
            .build();

    return CommonResponse.responseCreated(leaverRecordStatisticService.saveStatistic(newLeaver));
  }

  @PutMapping("/{id}")
  @Operation(summary = "탈퇴자 통계 수정", description = "탈퇴자의 통계 수치를 수정할 수 있습니다.")
  public CommonResponse<LeaverRecord> updateLeaverStatistic(
      @Valid @NotNull @Min(1) @PathVariable Long id,
      @Valid @RequestBody LeaverRequest leaverRecord) {

    var updateLeaverStatistic =
        LeaverRecord.builder()
            .id(id)
            .leaverCount(leaverRecord.leaverCount())
            .recordTime(leaverRecord.recordTime())
            .build();

    return CommonResponse.responseCreated(
        leaverRecordStatisticService.updateStatistic(updateLeaverStatistic));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "탈퇴자 통계 삭제", description = "탈퇴자의 통계 수치를 삭제 할 수 있습니다.")
  public Void deleteLeaverStatistic(@Valid @NotNull @Min(1) @PathVariable Long id) {
    leaverRecordStatisticService.deleteStatistic(id);

    return null;
  }
}
