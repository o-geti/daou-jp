package com.minsu.kim.daoujapan.controllers.statistic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.minsu.kim.daoujapan.data.request.UsageAmountRequest;
import com.minsu.kim.daoujapan.exception.NotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.minsu.kim.daoujapan.data.response.Paging;
import com.minsu.kim.daoujapan.data.statistics.amount.UsageAmountRecord;
import com.minsu.kim.daoujapan.exception.ValidateCheckError;
import com.minsu.kim.daoujapan.helper.LocalDateTimeParamChecker;
import com.minsu.kim.daoujapan.services.statistics.StatisticService;

/**
 * @author minsu.kim
 * @since 1.0
 */
@WebMvcTest(UsageAmountStatisticController.class)
class UsageAmountStatisticControllerTest {
  @Autowired MockMvc mvc;

  @Autowired ObjectMapper objectMapper;

  @MockBean StatisticService<UsageAmountRecord> usageAmountRecordStatisticService;
  @MockBean LocalDateTimeParamChecker checker;

  @Test
  @DisplayName("필터없이 페이징 조회 요청하기")
  void testSearchUsageAmountStatisticWithoutFilter() throws Exception {
    given(checker.checkForBetweenFromAndTo(null, null)).willReturn(Optional.empty());

    given(usageAmountRecordStatisticService.findStatistics(any()))
        .willReturn(TestDummy.findAllUsageAmountStatisticRecord());

    mvc.perform(get("/v1/statistic/usage-amount").param("page", "0").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.data").exists())
        .andDo(print());

    then(usageAmountRecordStatisticService).should(times(1)).findStatistics(any(Pageable.class));
  }

  @Test
  @DisplayName("날짜 필터추가 후 기본값 페이징 조회 요청하기")
  void testSearchUsageAmountStatisticWithFilter() throws Exception {
    LocalDateTime from = LocalDateTime.now().minusDays(1);
    LocalDateTime to = LocalDateTime.now();

    var fromString = from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    var toString = to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

    given(checker.checkForBetweenFromAndTo(any(LocalDateTime.class), any(LocalDateTime.class)))
        .willReturn(Optional.of(true));

    given(usageAmountRecordStatisticService.findStatisticsByDateTime(any(), any(), any()))
        .willReturn(TestDummy.findAllUsageAmountStatisticRecord());

    mvc.perform(
            get("/v1/statistic/usage-amount")
                .param("searchFrom", fromString)
                .param("searchTo", toString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data.pageSize").value(10))
        .andExpect(jsonPath("$.data.pageNumber").value(0))
        .andDo(print());

    then(usageAmountRecordStatisticService)
        .should(times(1))
        .findStatisticsByDateTime(any(), any(), any());
  }

  @Test
  @DisplayName("닐짜 필터 공통 에러 핸들러 응답 확인")
  void testSearchUsageAmountStatisticWithFilterError() throws Exception {
    LocalDateTime from = LocalDateTime.now();
    LocalDateTime to = LocalDateTime.now().minusDays(1);

    var fromString = from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    var toString = to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    var errorString = "to 보다 from이 큽니다.";

    given(checker.checkForBetweenFromAndTo(any(LocalDateTime.class), any(LocalDateTime.class)))
        .willThrow(new ValidateCheckError(errorString));

    given(usageAmountRecordStatisticService.findStatisticsByDateTime(any(), any(), any()))
        .willReturn(TestDummy.findAllUsageAmountStatisticRecord());

    mvc.perform(
            get("/v1/statistic/usage-amount")
                .param("searchFrom", fromString)
                .param("searchTo", toString))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data").value(errorString))
        .andDo(print());

    then(usageAmountRecordStatisticService)
        .should(times(0))
        .findStatisticsByDateTime(any(), any(), any());
  }

  @Test
  @DisplayName("사용금액 데이터 생성 요청")
  void testCreateSearchUsageAmountStatistic() throws Exception {
    var data = TestDummy.createUsageAmountRecord();

    given(usageAmountRecordStatisticService.saveStatistic(data))
        .willReturn(TestDummy.findUsageAmountRecord());

    var datetime =
        LocalDateTime.of(2024, 11, 3, 0, 0, 0)
                     .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

    mvc.perform(
           post("/v1/statistic/usage-amount")
               .contentType(MediaType.APPLICATION_JSON)
               .content(
                   objectMapper.writeValueAsBytes(
                       new UsageAmountRequest(data.recordTime(), data.usageAmount()))))
       .andExpect(status().isCreated())
       .andExpect(jsonPath("$.status").value(201))
       .andExpect(jsonPath("$.data").exists())
       .andExpect(jsonPath("$.data.id").value(1))
       .andExpect(jsonPath("$.data.recordTime").value(datetime))
       .andExpect(jsonPath("$.data.usageAmount").value(10_000))
       .andDo(print());

    then(usageAmountRecordStatisticService).should(times(1)).saveStatistic(data);
  }

  @Test
  @DisplayName("데이터 생성 밸리데이션 에러")
  void testCreateSearchUsageAmountStatisticValidError() throws Exception {
    var data = TestDummy.createUsageAmountRecord();

    given(usageAmountRecordStatisticService.saveStatistic(data))
        .willReturn(TestDummy.findUsageAmountRecord());

    mvc.perform(
           post("/v1/statistic/usage-amount")
               .contentType(MediaType.APPLICATION_JSON)
               .content(
                   objectMapper.writeValueAsBytes(new UsageAmountRequest(null, data.usageAmount()))))
       .andExpect(status().isBadRequest())
       .andExpect(jsonPath("$.status").value(400))
       .andExpect(jsonPath("$.data").exists())
       .andDo(print());

    mvc.perform(
           post("/v1/statistic/usage-amount")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsBytes(new UsageAmountRequest(data.recordTime(), -1L))))
       .andExpect(status().isBadRequest())
       .andExpect(jsonPath("$.status").value(400))
       .andExpect(jsonPath("$.data").exists())
       .andDo(print());

    mvc.perform(
           post("/v1/statistic/usage-amount")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsBytes(new UsageAmountRequest(null, null))))
       .andExpect(status().isBadRequest())
       .andExpect(jsonPath("$.status").value(400))
       .andExpect(jsonPath("$.data").exists())
       .andDo(print());

    then(usageAmountRecordStatisticService).should(times(0)).saveStatistic(data);
  }

  @Test
  @DisplayName("사용금액 데이터 업데이트 케이스")
  void testUpdateSearchUsageAmountStatistic() throws Exception {
    var data = TestDummy.findUsageAmountRecord();

    given(usageAmountRecordStatisticService.updateStatistic(data)).willReturn(data);

    // case1 벨리데이션 에러
    mvc.perform(
           put("/v1/statistic/usage-amount/" + data.id())
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsBytes(new UsageAmountRequest(null, -1L))))
       .andExpect(status().isBadRequest())
       .andExpect(jsonPath("$.status").value(400))
       .andExpect(jsonPath("$.data").isArray())
       .andExpect(jsonPath("$.data.size()").value(2))
       .andDo(print());

    // case2 정상 케이스
    mvc.perform(
           put("/v1/statistic/usage-amount/" + data.id())
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsBytes(data)))
       .andExpect(status().is2xxSuccessful())
       .andExpect(jsonPath("$.status").value(200))
       .andExpect(jsonPath("$.data").exists())
       .andDo(print());

    // case3 미존재 케이스
    var errorMsg = "사용금액 정보가 없습니다.";
    given(usageAmountRecordStatisticService.updateStatistic(data))
        .willThrow(new NotFoundException(errorMsg));
    mvc.perform(
           put("/v1/statistic/usage-amount/" + data.id())
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsBytes(data)))
       .andExpect(status().isNotFound())
       .andExpect(jsonPath("$.status").value(404))
       .andExpect(jsonPath("$.data").exists())
       .andExpect(jsonPath("$.data").value(errorMsg))
       .andDo(print());

    then(usageAmountRecordStatisticService).should(times(2)).updateStatistic(data);
  }

  @Test
  @DisplayName("사용금액 데이터 삭제 요청 케이스")
  void testDeleteSearchUsageAmountStatistic() throws Exception {
    var data = TestDummy.findUsageAmountRecord();

    willDoNothing().given(usageAmountRecordStatisticService).deleteStatistic(data.id());

    // case1 벨리데이션 에러
    mvc.perform(delete("/v1/statistic/usage-amount/" + 0))
       .andExpect(status().isBadRequest())
       .andExpect(jsonPath("$.status").value(400))
       .andExpect(jsonPath("$.data").isArray())
       .andExpect(jsonPath("$.data.size()").value(1))
       .andDo(print());

    // case2 정상 케이스
    mvc.perform(
           delete("/v1/statistic/usage-amount/" + data.id())
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsBytes(data)))
       .andExpect(status().is2xxSuccessful())
       .andExpect(result -> assertThat(result.getResponse().getContentLength()).isZero())
       .andDo(print());

    // case3 미존재 케이스
    var errorMsg = "사용금액 정보가 없습니다.";
    willThrow(new NotFoundException(errorMsg))
        .given(usageAmountRecordStatisticService)
        .deleteStatistic(data.id());

    mvc.perform(delete("/v1/statistic/usage-amount/" + data.id()))
       .andExpect(status().isNotFound())
       .andExpect(jsonPath("$.status").value(404))
       .andExpect(jsonPath("$.data").exists())
       .andExpect(jsonPath("$.data").value(errorMsg))
       .andDo(print());

    then(usageAmountRecordStatisticService).should(times(2)).deleteStatistic(data.id());
  }

  public static class TestDummy {
    public static UsageAmountRecord createUsageAmountRecord() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return UsageAmountRecord.builder().recordTime(datetime).usageAmount(10_000L).build();
    }

    public static UsageAmountRecord findUsageAmountRecord() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return UsageAmountRecord.builder().id(1L).recordTime(datetime).usageAmount(10_000L).build();
    }

    public static Paging<UsageAmountRecord> findAllUsageAmountStatisticRecord() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      var elem1 =
          UsageAmountRecord.builder().id(1L).recordTime(datetime).usageAmount(10_000L).build();
      var elem2 =
          UsageAmountRecord.builder()
              .id(2L)
              .recordTime(datetime.plusDays(1L))
              .usageAmount(11_000L)
              .build();
      var elem3 =
          UsageAmountRecord.builder()
              .id(3L)
              .recordTime(datetime.plusDays(2L))
              .usageAmount(12_000L)
              .build();

      return new Paging<>(List.of(elem1, elem2, elem3), 0, 10, 1, false, false);
    }
  }
}
