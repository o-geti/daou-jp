package com.minsu.kim.daujapan.parser;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.minsu.kim.daujapan.data.statistics.StatisticRecord;
import com.minsu.kim.daujapan.data.statistics.amount.PaymentAmountRecord;
import com.minsu.kim.daujapan.data.statistics.amount.SalesAmountRecord;
import com.minsu.kim.daujapan.data.statistics.amount.UsageAmountRecord;
import com.minsu.kim.daujapan.data.statistics.member.LeaverRecord;
import com.minsu.kim.daujapan.data.statistics.member.SubscriberRecord;
import com.minsu.kim.daujapan.enums.StatisticIndex;

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
    var recordTime = parseRecordTime(line[StatisticIndex.RECORD_TIME_INDEX.getIndex()]);
    var subscribeRecord =
        parseSubscriberRecord(recordTime, line[StatisticIndex.SUBSCRIBER_INDEX.getIndex()]);
    var leaverRecord = parseLeaverRecord(recordTime, line[StatisticIndex.LEAVER_INDEX.getIndex()]);
    var paymentAmountRecord =
        parsePaymentAmountRecord(recordTime, line[StatisticIndex.PAYMENT_INDEX.getIndex()]);
    var usageAmountRecord =
        parseUsageAmountRecord(recordTime, line[StatisticIndex.USAGE_INDEX.getIndex()]);
    var salesAmountRecord =
        parseSalesAmountRecord(recordTime, line[StatisticIndex.SALES_INDEX.getIndex()]);

    return new StatisticRecord(
        subscribeRecord, leaverRecord, paymentAmountRecord, usageAmountRecord, salesAmountRecord);
  }

  private LocalDateTime parseRecordTime(String recordTimeString) {
    return LocalDateTime.parse(recordTimeString, RECORD_TIME_FORMATTER);
  }

  private SubscriberRecord parseSubscriberRecord(
      LocalDateTime recordTime, String subscriberCountString) {
    var subscriberCount = parseNumber(subscriberCountString, () -> 0).intValue();

    return SubscriberRecord.builder()
        .recordTime(recordTime)
        .subscriberCount(subscriberCount)
        .build();
  }

  private LeaverRecord parseLeaverRecord(LocalDateTime recordTime, String subscriberCountString) {
    var leaverCount = parseNumber(subscriberCountString, () -> 0).intValue();

    return LeaverRecord.builder().recordTime(recordTime).leaverCount(leaverCount).build();
  }

  private PaymentAmountRecord parsePaymentAmountRecord(
      LocalDateTime recordTime, String paymentAmountString) {
    var paymentAmount = parseNumber(paymentAmountString, () -> 0).longValue();

    return PaymentAmountRecord.builder()
        .recordTime(recordTime)
        .paymentAmount(paymentAmount)
        .build();
  }

  private UsageAmountRecord parseUsageAmountRecord(
      LocalDateTime recordTime, String usageAmountString) {
    var usageAmount = parseNumber(usageAmountString, () -> 0).longValue();

    return UsageAmountRecord.builder().recordTime(recordTime).usageAmount(usageAmount).build();
  }

  private SalesAmountRecord parseSalesAmountRecord(
      LocalDateTime recordTime, String salesAmountString) {
    var salesAmount = parseNumber(salesAmountString, () -> 0).longValue();

    return SalesAmountRecord.builder().recordTime(recordTime).salesAmount(salesAmount).build();
  }

  private Number parseNumber(String numberString, Supplier<Number> parseFailThen) {
    try {
      return NUMBER_FORMATTER.parse(numberString);
    } catch (ParseException ex) {
      return parseFailThen.get();
    }
  }
}
