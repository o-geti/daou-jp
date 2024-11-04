package com.minsu.kim.daoujapan.controllers.statistic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.minsu.kim.daoujapan.data.request.LeaverRequest;
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
import com.minsu.kim.daoujapan.data.statistics.member.LeaverRecord;
import com.minsu.kim.daoujapan.exception.ValidateCheckError;
import com.minsu.kim.daoujapan.helper.LocalDateTimeParamChecker;
import com.minsu.kim.daoujapan.services.statistics.StatisticService;

/**
 * @author minsu.kim
 * @since 1.0
 */
@WebMvcTest(LeaverStatisticController.class)
class LeaverStatisticControllerTest {

  @Autowired MockMvc mvc;

  @Autowired ObjectMapper objectMapper;

  @MockBean StatisticService<LeaverRecord> leaverRecordStatisticService;

  @MockBean LocalDateTimeParamChecker checker;

  @Test
  @DisplayName("필터없이 페이징 조회 요청하기")
  void testSearchLeaverStatisticWithoutFilter() throws Exception {
    given(checker.checkForBetweenFromAndTo(null, null)).willReturn(Optional.empty());

    given(leaverRecordStatisticService.findStatistics(any()))
        .willReturn(TestDummy.findAllLeaverRecords());

    mvc.perform(get("/v1/statistic/leaver").param("page", "0").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data.content.size()").value(3))
        .andDo(print());

    then(leaverRecordStatisticService).should(times(1)).findStatistics(any(Pageable.class));
  }

  @Test
  @DisplayName("닐찌필터추가 후 기본값 페이징 조회 요청하기")
  void testSearchLeaverStatisticWithFilter() throws Exception {
    LocalDateTime from = LocalDateTime.now().minusDays(1);
    LocalDateTime to = LocalDateTime.now();

    var fromString = from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    var toString = to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

    given(checker.checkForBetweenFromAndTo(any(LocalDateTime.class), any(LocalDateTime.class)))
        .willReturn(Optional.of(true));

    given(leaverRecordStatisticService.findStatisticsByDateTime(any(), any(), any()))
        .willReturn(TestDummy.findAllLeaverRecords());

    mvc.perform(
            get("/v1/statistic/leaver").param("searchFrom", fromString).param("searchTo", toString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data.content.size()").value(3))
        .andExpect(jsonPath("$.data.pageSize").value(10))
        .andExpect(jsonPath("$.data.pageNumber").value(0))
        .andDo(print());

    then(leaverRecordStatisticService)
        .should(times(1))
        .findStatisticsByDateTime(any(), any(), any());
  }

  @Test
  @DisplayName("닐찌 필터 공통 에러 핸들러 응답 확인")
  void testSearchLeaverStatisticWithFilterError() throws Exception {
    LocalDateTime from = LocalDateTime.now();
    LocalDateTime to = LocalDateTime.now().minusDays(1);

    var fromString = from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    var toString = to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    var errorString = "to 보다 from이 큽니다.";

    given(checker.checkForBetweenFromAndTo(any(LocalDateTime.class), any(LocalDateTime.class)))
        .willThrow(new ValidateCheckError(errorString));

    given(leaverRecordStatisticService.findStatisticsByDateTime(any(), any(), any()))
        .willReturn(TestDummy.findAllLeaverRecords());

    mvc.perform(
            get("/v1/statistic/leaver").param("searchFrom", fromString).param("searchTo", toString))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data").value(errorString))
        .andDo(print());

    then(leaverRecordStatisticService)
        .should(times(0))
        .findStatisticsByDateTime(any(), any(), any());
  }

  @Test
  @DisplayName("데이터 생성")
  void testCreateSearchLeaverStatistic() throws Exception {
    var data = TestDummy.createLeaverRequestRecord();

    given(leaverRecordStatisticService.saveStatistic(data))
        .willReturn(TestDummy.findLeaverRequestRecord());


    var datetime =
        LocalDateTime.of(2024, 11, 3, 0, 0, 0)
                     .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

    mvc.perform(
            post("/v1/statistic/leaver")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new LeaverRequest(data.recordTime(), data.leaverCount()))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value(201))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.recordTime").value(datetime))
        .andExpect(jsonPath("$.data.leaverCount").value(1))
        .andDo(print());

    then(leaverRecordStatisticService).should(times(1)).saveStatistic(data);
  }

  @Test
  @DisplayName("데이터 생성 밸리데이션 에러")
  void testCreateSearchLeaverStatisticValuidError() throws Exception {
    var data = TestDummy.createLeaverRequestRecord();

    given(leaverRecordStatisticService.saveStatistic(data))
        .willReturn(TestDummy.findLeaverRequestRecord());

    mvc.perform(
           post("/v1/statistic/leaver")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsBytes(new LeaverRequest(null, data.leaverCount()))))
       .andExpect(status().isBadRequest())
       .andExpect(jsonPath("$.status").value(400))
       .andExpect(jsonPath("$.data").exists())
       .andDo(print());

    mvc.perform(
           post("/v1/statistic/leaver")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsBytes(new LeaverRequest(data.recordTime(), -1))))
       .andExpect(status().isBadRequest())
       .andExpect(jsonPath("$.status").value(400))
       .andExpect(jsonPath("$.data").exists())
       .andDo(print());


    mvc.perform(
           post("/v1/statistic/leaver")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsBytes(new LeaverRequest(null, null))))
       .andExpect(status().isBadRequest())
       .andExpect(jsonPath("$.status").value(400))
       .andExpect(jsonPath("$.data").exists())
       .andDo(print());


    then(leaverRecordStatisticService).should(times(0)).saveStatistic(data);
  }

  public static class TestDummy {
    public static LeaverRecord createLeaverRequestRecord() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return LeaverRecord.builder().recordTime(datetime).leaverCount(1).build();
    }

    public static LeaverRecord findLeaverRequestRecord() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return LeaverRecord.builder().id(1L).recordTime(datetime).leaverCount(1).build();
    }

    public static Paging<LeaverRecord> findAllLeaverRecords() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      var elem1 = LeaverRecord.builder().id(1L).recordTime(datetime).leaverCount(1).build();
      var elem2 =
          LeaverRecord.builder().id(2L).recordTime(datetime.plusDays(1L)).leaverCount(2).build();
      var elem3 =
          LeaverRecord.builder().id(3L).recordTime(datetime.plusDays(2L)).leaverCount(3).build();

      return new Paging<>(List.of(elem1, elem2, elem3), 0, 10, 1, false, false);
    }
  }
}