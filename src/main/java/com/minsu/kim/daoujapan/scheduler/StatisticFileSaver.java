package com.minsu.kim.daoujapan.scheduler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.minsu.kim.daoujapan.config.FileScheduleConfig;
import com.minsu.kim.daoujapan.data.statistics.StatisticRecord;
import com.minsu.kim.daoujapan.exception.StatisticIsAlreadyExist;
import com.minsu.kim.daoujapan.helper.StackTraceUtil;
import com.minsu.kim.daoujapan.parser.StatisticFileLineParser;
import com.minsu.kim.daoujapan.services.statistics.StatisticService;

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

  private final List<StatisticService<?>> statisticServices;

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
              statisticRecord ->
                  statisticServices.forEach(service -> saveStatistic(statisticRecord, service)));

    } catch (FileNotFoundException e) {
      log.warn("파일을 찾지 못하였습니다. : {}", StackTraceUtil.filterStackTracePackage(e));
    } catch (IOException e) {
      log.error("IO읽기 작업 도중 에러가 발생하였습니다. 에러 스택트레이스를 확인하여주세요.");
      log.error("ERROR STACKTRACE : {}", StackTraceUtil.filterStackTracePackage(e));
    }
  }

  private static void saveStatistic(StatisticRecord statisticRecord, StatisticService<?> service) {
    try {
      service.saveStatistic(statisticRecord);
    } catch (StatisticIsAlreadyExist e) {
      log.info("파일 저장중 충돌된 데이터가있습니다. 아래의 시간대를 확인 후 파일의 데이터 정합성을 확인해주세요.");
      log.info("저장 대상 시간 : {}", statisticRecord.leaverRecord().recordTime());
    }
  }
}
