package com.minsu.kim.daoujapan.controllers.statistic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.web.servlet.MockMvc;

import com.minsu.kim.daoujapan.data.response.Paging;
import com.minsu.kim.daoujapan.data.statistics.amount.PaymentAmountRecord;
import com.minsu.kim.daoujapan.exception.ValidateCheckError;
import com.minsu.kim.daoujapan.helper.LocalDateTimeParamChecker;
import com.minsu.kim.daoujapan.services.statistics.StatisticService;

/**
 * @author minsu.kim
 * @since 1.0
 */
@WebMvcTest(PaymentAmountController.class)
class PaymentAmountControllerTest {

  @Autowired MockMvc mvc;

  @Autowired ObjectMapper objectMapper;

  @MockBean StatisticService<PaymentAmountRecord> paymentAmountStatisticService;
  @MockBean LocalDateTimeParamChecker checker;

  @Test
  @DisplayName("필터없이 페이징 조회 요청하기")
  void testSearchSubscriberStatisticWithoutFilter() throws Exception {
    given(checker.checkForBetweenFromAndTo(null, null)).willReturn(Optional.empty());

    given(paymentAmountStatisticService.findStatistics(any()))
        .willReturn(TestDummy.findAllPaymentAmountRecords());

    mvc.perform(get("/v1/statistic/payment-amount").param("page", "0").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data.content.size()").value(3))
        .andDo(print());

    then(paymentAmountStatisticService).should(times(1)).findStatistics(any(Pageable.class));
  }

  @Test
  @DisplayName("닐찌필터추가 후 기본값 페이징 조회 요청하기")
  void testSearchSubscriberStatisticWithFilter() throws Exception {
    LocalDateTime from = LocalDateTime.now().minusDays(1);
    LocalDateTime to = LocalDateTime.now();

    var fromString = from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    var toString = to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));

    given(checker.checkForBetweenFromAndTo(any(LocalDateTime.class), any(LocalDateTime.class)))
        .willReturn(Optional.of(true));

    given(paymentAmountStatisticService.findStatisticsByDateTime(any(), any(), any()))
        .willReturn(TestDummy.findAllPaymentAmountRecords());

    mvc.perform(
            get("/v1/statistic/payment-amount")
                .param("searchFrom", fromString)
                .param("searchTo", toString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data.content.size()").value(3))
        .andExpect(jsonPath("$.data.pageSize").value(10))
        .andExpect(jsonPath("$.data.pageNumber").value(0))
        .andDo(print());

    then(paymentAmountStatisticService)
        .should(times(1))
        .findStatisticsByDateTime(any(), any(), any());
  }

  @Test
  @DisplayName("닐찌 필터 공통 에러 핸들러 응답 확인")
  void testSearchUsageAmountStatisticWithFilterError() throws Exception {
    LocalDateTime from = LocalDateTime.now();
    LocalDateTime to = LocalDateTime.now().minusDays(1);

    var fromString = from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    var toString = to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    var errorString = "to 보다 from이 큽니다.";

    given(checker.checkForBetweenFromAndTo(any(LocalDateTime.class), any(LocalDateTime.class)))
        .willThrow(new ValidateCheckError(errorString));

    given(paymentAmountStatisticService.findStatisticsByDateTime(any(), any(), any()))
        .willReturn(TestDummy.findAllPaymentAmountRecords());

    mvc.perform(
            get("/v1/statistic/payment-amount")
                .param("searchFrom", fromString)
                .param("searchTo", toString))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.data").exists())
        .andExpect(jsonPath("$.data").value(errorString))
        .andDo(print());

    then(paymentAmountStatisticService)
        .should(times(0))
        .findStatisticsByDateTime(any(), any(), any());
  }

  public static class TestDummy {

    public static Paging<PaymentAmountRecord> findAllPaymentAmountRecords() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      var elem1 =
          PaymentAmountRecord.builder().id(1L).recordTime(datetime).paymentAmount(100_000L).build();
      var elem2 =
          PaymentAmountRecord.builder()
              .id(2L)
              .recordTime(datetime.plusDays(1L))
              .paymentAmount(100_000L)
              .build();
      var elem3 =
          PaymentAmountRecord.builder()
              .id(3L)
              .recordTime(datetime.plusDays(2L))
              .paymentAmount(100_000L)
              .build();

      return new Paging<>(List.of(elem1, elem2, elem3), 0, 10, 1, false, false);
    }
  }
}
