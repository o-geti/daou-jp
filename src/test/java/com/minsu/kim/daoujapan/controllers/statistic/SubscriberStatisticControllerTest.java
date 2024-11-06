package com.minsu.kim.daoujapan.controllers.statistic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.minsu.kim.daoujapan.data.request.SubscriberRequest;
import com.minsu.kim.daoujapan.data.response.Paging;
import com.minsu.kim.daoujapan.data.statistics.member.SubscriberRecord;
import com.minsu.kim.daoujapan.exception.NotFoundException;
import com.minsu.kim.daoujapan.exception.ValidateCheckError;
import com.minsu.kim.daoujapan.helper.LocalDateTimeParamChecker;
import com.minsu.kim.daoujapan.services.statistics.StatisticService;

/**
 * @author minsu.kim
 * @since 1.0
 */
@WebMvcTest(SubscriberStatisticController.class)
@Import(ConcurrentHashMap.class)
class SubscriberStatisticControllerTest {

  @Autowired MockMvc mvc;

  @Autowired ObjectMapper objectMapper;
  @Autowired ConcurrentHashMap<String, LocalDateTime> fakeRedis;

  @MockBean StatisticService<SubscriberRecord> subscriberRecordStatisticService;
  @MockBean LocalDateTimeParamChecker checker;

  String exampleToken = "test_token";

  @BeforeEach
  void setUp() {
    fakeRedis.put(exampleToken, LocalDateTime.now().plusHours(3L));
  }

  @Test
  @WithMockUser
  @DisplayName("필터없이 페이징 조회 요청하기")
  void testSearchSubscriberStatisticWithoutFilter() throws Exception {
    given(checker.checkForBetweenFromAndTo(null, null)).willReturn(Optional.empty());

    given(subscriberRecordStatisticService.findStatistics(any()))
        .willReturn(TestDummy.findAllSubScribeRecords());

    mvc.perform(
            get("/v1/statistic/subscriber")
                .param("page", "0")
                .param("size", "10")
                .header(HttpHeaders.AUTHORIZATION, exampleToken)
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data.content.size()").value(3))
        .andDo(print());

    then(subscriberRecordStatisticService).should(times(1)).findStatistics(any(Pageable.class));
  }

  @Test
  @WithMockUser
  @DisplayName("날짜 필터추가 후 기본값 페이징 조회 요청하기")
  void testSearchSubscriberStatisticWithFilter() throws Exception {
    LocalDateTime from = LocalDateTime.now().minusDays(1);
    LocalDateTime to = LocalDateTime.now();

    var fromString = from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    var toString = to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

    given(checker.checkForBetweenFromAndTo(any(LocalDateTime.class), any(LocalDateTime.class)))
        .willReturn(Optional.of(true));

    given(subscriberRecordStatisticService.findStatisticsByDateTime(any(), any(), any()))
        .willReturn(TestDummy.findAllSubScribeRecords());

    mvc.perform(
            get("/v1/statistic/subscriber")
                .header(HttpHeaders.AUTHORIZATION, exampleToken)
                .with(csrf())
                .param("searchFrom", fromString)
                .param("searchTo", toString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data.content.size()").value(3))
        .andExpect(jsonPath("$.data.pageSize").value(10))
        .andExpect(jsonPath("$.data.pageNumber").value(0))
        .andDo(print());

    then(subscriberRecordStatisticService)
        .should(times(1))
        .findStatisticsByDateTime(any(), any(), any());
  }

  @Test
  @WithMockUser
  @DisplayName("닐짜 필터 공통 에러 핸들러 응답 확인")
  void testSearchSubscriberStatisticWithFilterError() throws Exception {
    LocalDateTime from = LocalDateTime.now();
    LocalDateTime to = LocalDateTime.now().minusDays(1);

    var fromString = from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    var toString = to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    var errorString = "to 보다 from이 큽니다.";

    given(checker.checkForBetweenFromAndTo(any(LocalDateTime.class), any(LocalDateTime.class)))
        .willThrow(new ValidateCheckError(errorString));

    given(subscriberRecordStatisticService.findStatisticsByDateTime(any(), any(), any()))
        .willReturn(TestDummy.findAllSubScribeRecords());

    mvc.perform(
            get("/v1/statistic/subscriber")
                .header(HttpHeaders.AUTHORIZATION, exampleToken)
                .with(csrf())
                .param("searchFrom", fromString)
                .param("searchTo", toString))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data").value(errorString))
        .andDo(print());

    then(subscriberRecordStatisticService)
        .should(times(0))
        .findStatisticsByDateTime(any(), any(), any());
  }

  @Test
  @WithMockUser
  @DisplayName("데이터 생성")
  void testCreateSearchLeaverStatistic() throws Exception {
    var data = TestDummy.createSubscribeRecord();

    given(subscriberRecordStatisticService.saveStatistic(data))
        .willReturn(TestDummy.findSubscriberRecord());

    var datetime =
        LocalDateTime.of(2024, 11, 3, 0, 0, 0)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

    mvc.perform(
            post("/v1/statistic/subscriber")
                .header(HttpHeaders.AUTHORIZATION, exampleToken)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsBytes(
                        new SubscriberRequest(data.recordTime(), data.subscriberCount()))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value(201))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.recordTime").value(datetime))
        .andExpect(jsonPath("$.data.subscriberCount").value(1))
        .andDo(print());

    then(subscriberRecordStatisticService).should(times(1)).saveStatistic(data);
  }

  @Test
  @WithMockUser
  @DisplayName("데이터 생성 밸리데이션 에러")
  void testCreateSearchLeaverStatisticValuidError() throws Exception {
    var data = TestDummy.createSubscribeRecord();

    given(subscriberRecordStatisticService.saveStatistic(data))
        .willReturn(TestDummy.findSubscriberRecord());

    mvc.perform(
            post("/v1/statistic/subscriber")
                .header(HttpHeaders.AUTHORIZATION, exampleToken)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsBytes(
                        new SubscriberRequest(null, data.subscriberCount()))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.data").exists())
        .andDo(print());

    mvc.perform(
            post("/v1/statistic/subscriber")
                .header(HttpHeaders.AUTHORIZATION, exampleToken)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsBytes(new SubscriberRequest(data.recordTime(), -1))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.data").exists())
        .andDo(print());

    mvc.perform(
            post("/v1/statistic/subscriber")
                .header(HttpHeaders.AUTHORIZATION, exampleToken)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new SubscriberRequest(null, null))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.data").exists())
        .andDo(print());

    then(subscriberRecordStatisticService).should(times(0)).saveStatistic(data);
  }

  @Test
  @WithMockUser
  @DisplayName("가입자 데이터 업데이트 케이스")
  void testUpdateSearchLeaverStatistic() throws Exception {
    var data = TestDummy.findSubscriberRecord();

    given(subscriberRecordStatisticService.updateStatistic(data)).willReturn(data);

    // case1 벨리데이션 에러
    mvc.perform(
            put("/v1/statistic/subscriber/" + data.id())
                .header(HttpHeaders.AUTHORIZATION, exampleToken)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new SubscriberRequest(null, -1))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.size()").value(2))
        .andDo(print());

    // case2 정상 케이스
    mvc.perform(
            put("/v1/statistic/subscriber/" + data.id())
                .header(HttpHeaders.AUTHORIZATION, exampleToken)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(data)))
        .andExpect(status().is2xxSuccessful())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.data").exists())
        .andDo(print());

    // case3 미존재 케이스
    var errorMsg = "가입자 정보가 없습니다.";
    given(subscriberRecordStatisticService.updateStatistic(data))
        .willThrow(new NotFoundException(errorMsg));
    mvc.perform(
            put("/v1/statistic/subscriber/" + data.id())
                .header(HttpHeaders.AUTHORIZATION, exampleToken)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(data)))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data").value(errorMsg))
        .andDo(print());

    then(subscriberRecordStatisticService).should(times(2)).updateStatistic(data);
  }

  @Test
  @WithMockUser
  @DisplayName("가입자 데이터 삭제 요청 케이스")
  void testDeleteSearchLeaverStatistic() throws Exception {
    var data = TestDummy.findSubscriberRecord();

    willDoNothing().given(subscriberRecordStatisticService).deleteStatistic(data.id());

    // case1 벨리데이션 에러
    mvc.perform(
            delete("/v1/statistic/subscriber/" + 0)
                .header(HttpHeaders.AUTHORIZATION, exampleToken)
                .with(csrf()))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.size()").value(1))
        .andDo(print());

    // case2 정상 케이스
    mvc.perform(
            delete("/v1/statistic/subscriber/" + data.id())
                .header(HttpHeaders.AUTHORIZATION, exampleToken)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(data)))
        .andExpect(status().is2xxSuccessful())
        .andExpect(result -> assertThat(result.getResponse().getContentLength()).isZero())
        .andDo(print());

    // case3 미존재 케이스
    var errorMsg = "가입자 정보가 없습니다.";
    willThrow(new NotFoundException(errorMsg))
        .given(subscriberRecordStatisticService)
        .deleteStatistic(data.id());

    mvc.perform(
            delete("/v1/statistic/subscriber/" + data.id())
                .header(HttpHeaders.AUTHORIZATION, exampleToken)
                .with(csrf()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data").value(errorMsg))
        .andDo(print());

    then(subscriberRecordStatisticService).should(times(2)).deleteStatistic(data.id());
  }

  public static class TestDummy {
    public static SubscriberRecord createSubscribeRecord() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return SubscriberRecord.builder().recordTime(datetime).subscriberCount(1).build();
    }

    public static SubscriberRecord findSubscriberRecord() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return SubscriberRecord.builder().id(1L).recordTime(datetime).subscriberCount(1).build();
    }

    public static Paging<SubscriberRecord> findAllSubScribeRecords() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      var elem1 = SubscriberRecord.builder().id(1L).recordTime(datetime).subscriberCount(1).build();
      var elem2 =
          SubscriberRecord.builder()
              .id(2L)
              .recordTime(datetime.plusDays(1L))
              .subscriberCount(2)
              .build();
      var elem3 =
          SubscriberRecord.builder()
              .id(3L)
              .recordTime(datetime.plusDays(2L))
              .subscriberCount(3)
              .build();

      return new Paging<>(List.of(elem1, elem2, elem3), 0, 10, 1, false, false);
    }
  }
}
