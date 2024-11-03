package com.minsu.kim.daujapan.services.statistics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.minsu.kim.daujapan.data.statistics.member.LeaverRecord;
import com.minsu.kim.daujapan.data.statistics.member.SubscriberRecord;
import com.minsu.kim.daujapan.domains.statistics.member.LeaverStatisticEntity;
import com.minsu.kim.daujapan.domains.statistics.member.SubscriberStatisticEntity;
import com.minsu.kim.daujapan.mapper.statistics.member.LeaverMapperImpl;
import com.minsu.kim.daujapan.mapper.statistics.member.SubscriberMapperImpl;
import com.minsu.kim.daujapan.repositories.statistics.member.LeaverStatisticRepository;
import com.minsu.kim.daujapan.repositories.statistics.member.SubscriberStatisticRepository;

/**
 * @author minsu.kim
 * @since 1.0
 */
@SpringBootTest(
    classes = {MemberStatisticService.class, SubscriberMapperImpl.class, LeaverMapperImpl.class})
class MemberStatisticServiceTest {

  @Autowired MemberStatisticService statisticService;

  @MockBean SubscriberStatisticRepository subscriberStatisticRepository;
  @MockBean LeaverStatisticRepository leaverStatisticRepository;

  @Test
  @DisplayName("가입자 수에대한 데이터가 입력으로 들어오면 데이터베이스에 저장한다.")
  void saveSubscriberStatistic() {
    // given
    given(subscriberStatisticRepository.save(any(SubscriberStatisticEntity.class)))
        .willReturn(TestDummy.findSubscriberStatisticEntitySuit());

    // when
    var subscriberSuit = TestDummy.createSubscriberRecordSuit();
    var savedSubscriberStatisticRecord = statisticService.saveSubscriberStatistic(subscriberSuit);

    // then
    then(subscriberStatisticRepository).should(times(1)).save(any(SubscriberStatisticEntity.class));
    assertThat(savedSubscriberStatisticRecord.subscriberCount())
        .isEqualTo(subscriberSuit.subscriberCount());
    assertThat(savedSubscriberStatisticRecord.recordTime()).isEqualTo(subscriberSuit.recordTime());
  }

  @Test
  @DisplayName("탈퇴자 수에대한 데이터가 입력으로 들어오면 데이터베이스에 저장한다.")
  void saveLeaverStatistic() {
    // given
    given(leaverStatisticRepository.save(any(LeaverStatisticEntity.class)))
        .willReturn(TestDummy.findLeaverStatisticEntitySuit());

    // when
    var leaverDummy = TestDummy.createLeaverRecordSuit();
    var leaverRecord = statisticService.saveLeaverStatistic(leaverDummy);

    // then
    then(leaverStatisticRepository).should(times(1)).save(any(LeaverStatisticEntity.class));
    assertThat(leaverRecord.leaverCount()).isEqualTo(leaverDummy.leaverCount());
    assertThat(leaverRecord.recordTime()).isEqualTo(leaverDummy.recordTime());
  }

  public static class TestDummy {
    public static SubscriberStatisticEntity findSubscriberStatisticEntitySuit() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return new SubscriberStatisticEntity(1L, datetime, 10);
    }

    public static SubscriberRecord createSubscriberRecordSuit() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return new SubscriberRecord(null, datetime, 10);
    }

    public static LeaverStatisticEntity findLeaverStatisticEntitySuit() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return new LeaverStatisticEntity(1L, datetime, 10);
    }

    public static LeaverRecord createLeaverRecordSuit() {
      var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

      return new LeaverRecord(null, datetime, 10);
    }
  }
}
