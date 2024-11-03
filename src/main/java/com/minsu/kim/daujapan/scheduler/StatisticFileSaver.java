package com.minsu.kim.daujapan.scheduler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.minsu.kim.daujapan.config.FileScheduleConfig;
import com.minsu.kim.daujapan.parser.StatisticFileLineParser;
import com.minsu.kim.daujapan.services.statistics.AmountStatisticService;
import com.minsu.kim.daujapan.services.statistics.MemberStatisticService;
import com.minsu.kim.daujapan.util.StackTraceUtil;

/**
 * 커머스 파일 대상을 저장하는 객체입니다.
 *
 * @author minsu.kim
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StatisticFileSaver {

  private final FileScheduleConfig fileScheduleConfig;
  private final StatisticFileLineParser statisticFileLineParser;

  private final AmountStatisticService amountStatisticService;
  private final MemberStatisticService memberStatisticService;

  // 매일 자정 실행, KST가 없어 같은 시간대 JST 사용
  @Scheduled(cron = "${schedule-file.job.file-saver-run}", zone = "Asia/Tokyo")
  public void commerceFileSaveToDatabase() {

    var directory = System.getProperty("user.dir");
    var yesterDay =
        LocalDateTime.now().minusDays(1L).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    var fileName = "commerce-file-" + yesterDay;

    fileScheduleConfig
        .getExtensions()
        .forEach(
            extension -> {
              Path filePath = Paths.get(directory + "/files/" + fileName + "." + extension);
              readFileStreamMode(filePath);
            });
  }

  public void readFileStreamMode(Path filePath) {
    try (FileReader fr = new FileReader(filePath.toFile());
        BufferedReader br = new BufferedReader(fr)) {
      br.lines()
          .map(line -> line.split("\\|"))
          .map(statisticFileLineParser::parseLineStringArray)
          .forEach(
              statisticRecord -> {
                memberStatisticService.saveSubscriberStatistic(statisticRecord.subscriberRecord());
                memberStatisticService.saveLeaverStatistic(statisticRecord.leaverRecord());

                amountStatisticService.savePaymentAmountStatistic(
                    statisticRecord.paymentAmountRecord());
                amountStatisticService.saveUsageAmountStatistic(
                    statisticRecord.usageAmountRecord());
                amountStatisticService.saveSalesAmountStatistic(
                    statisticRecord.salesAmountRecord());
              });
    } catch (FileNotFoundException e) {
      log.warn("파일을 찾지 못하였습니다. : {}", StackTraceUtil.filterStackTracePackage(e));
    } catch (IOException e) {
      log.error("IO읽기 작업 도중 에러가 발생하였습니다. 에러 스택트레이스를 확인하여주세요.");
      log.error("ERROR STACKTRACE : {}", StackTraceUtil.filterStackTracePackage(e));
    }
  }
}
