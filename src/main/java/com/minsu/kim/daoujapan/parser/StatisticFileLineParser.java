package com.minsu.kim.daoujapan.parser;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.minsu.kim.daoujapan.data.statistics.StatisticRecord;
import com.minsu.kim.daoujapan.data.statistics.amount.PaymentAmountRecord;
import com.minsu.kim.daoujapan.data.statistics.amount.SalesAmountRecord;
import com.minsu.kim.daoujapan.data.statistics.amount.UsageAmountRecord;
import com.minsu.kim.daoujapan.data.statistics.member.LeaverRecord;
import com.minsu.kim.daoujapan.data.statistics.member.SubscriberRecord;
import com.minsu.kim.daoujapan.enums.StatisticIndex;
import com.minsu.kim.daoujapan.helper.StackTraceUtil;

/**
 * 파일 통계 라인 1줄 데이터를 자바 데이터로 변환하는 객체입니다.
 *
 * @author minsu.kim
 * @since 1.0
 */
@Component
@Slf4j
public class StatisticFileLineParser {
  private static final DateTimeFormatter RECORD_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
  private static final DecimalFormat NUMBER_FORMATTER = new DecimalFormat("#,###");

  public StatisticRecord parseLineStringArray(String[] line) {
    try {
      var recordTime = parseRecordTime(line[StatisticIndex.RECORD_TIME_INDEX.getIndex()]);
      var subscribeRecord =
          parseSubscriberRecord(recordTime, line[StatisticIndex.SUBSCRIBER_INDEX.getIndex()]);
      var leaverRecord =
          parseLeaverRecord(recordTime, line[StatisticIndex.LEAVER_INDEX.getIndex()]);
      var paymentAmountRecord =
          parsePaymentAmountRecord(recordTime, line[StatisticIndex.PAYMENT_INDEX.getIndex()]);
      var usageAmountRecord =
          parseUsageAmountRecord(recordTime, line[StatisticIndex.USAGE_INDEX.getIndex()]);
      var salesAmountRecord =
          parseSalesAmountRecord(recordTime, line[StatisticIndex.SALES_INDEX.getIndex()]);

      return new StatisticRecord(
          subscribeRecord, leaverRecord, paymentAmountRecord, usageAmountRecord, salesAmountRecord);
    } catch (DateTimeParseException e) {
      log.info(
          "파일의 내용 중 올바르지못한 날짜 형식이 있습니다. {} ", line[StatisticIndex.RECORD_TIME_INDEX.getIndex()]);
      log.info(StackTraceUtil.filterStackTracePackage(e));

      return new StatisticRecord(
          Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }
  }

  private LocalDateTime parseRecordTime(String recordTimeString) {
    return LocalDateTime.parse(recordTimeString, RECORD_TIME_FORMATTER);
  }

  private Optional<SubscriberRecord> parseSubscriberRecord(
      LocalDateTime recordTime, String subscriberCountString) {
    return parseNumber(subscriberCountString)
        .map(
            number ->
                SubscriberRecord.builder()
                    .recordTime(recordTime)
                    .subscriberCount(number.intValue())
                    .build());
  }

  private Optional<LeaverRecord> parseLeaverRecord(
      LocalDateTime recordTime, String leavererCountString) {
    return parseNumber(leavererCountString)
        .map(
            leaverCount ->
                LeaverRecord.builder()
                    .recordTime(recordTime)
                    .leaverCount(leaverCount.intValue())
                    .build());
  }

  private Optional<PaymentAmountRecord> parsePaymentAmountRecord(
      LocalDateTime recordTime, String paymentAmountString) {
    return parseNumber(paymentAmountString)
        .map(
            paymentAmount ->
                PaymentAmountRecord.builder()
                    .recordTime(recordTime)
                    .paymentAmount(paymentAmount.longValue())
                    .build());
  }

  private Optional<UsageAmountRecord> parseUsageAmountRecord(
      LocalDateTime recordTime, String usageAmountString) {
    return parseNumber(usageAmountString)
        .map(
            usageAmount ->
                UsageAmountRecord.builder()
                    .recordTime(recordTime)
                    .usageAmount(usageAmount.longValue())
                    .build());
  }

  private Optional<SalesAmountRecord> parseSalesAmountRecord(
      LocalDateTime recordTime, String salesAmountString) {
    return parseNumber(salesAmountString)
        .map(
            salesAmount ->
                SalesAmountRecord.builder()
                    .recordTime(recordTime)
                    .salesAmount(salesAmount.longValue())
                    .build());
  }

  private Optional<Number> parseNumber(String numberString) {
    try {
      return Optional.of(NUMBER_FORMATTER.parse(numberString));
    } catch (ParseException ex) {
      log.info("파일의 내용 중 올바르지못한 숫자 형식이 있습니다. {} ", numberString);
      log.info(StackTraceUtil.filterStackTracePackage(ex));

      return Optional.empty();
    }
  }
}
