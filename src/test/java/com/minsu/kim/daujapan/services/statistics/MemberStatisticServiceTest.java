package com.minsu.kim.daujapan.services.statistics;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.minsu.kim.daujapan.data.statistics.member.SubscriberRecord;
import com.minsu.kim.daujapan.domains.statistics.member.SubscriberStatisticEntity;
import com.minsu.kim.daujapan.mapper.statistics.member.SubscriberMapper;
import com.minsu.kim.daujapan.mapper.statistics.member.SubscriberMapperImpl;
import com.minsu.kim.daujapan.repositories.statistics.member.SubscriberStatisticRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 *
 * @author minsu.kim
 * @since 1.0
 */
@SpringBootTest(classes = {MemberStatisticService.class, SubscriberMapperImpl.class})
class MemberStatisticServiceTest {

    @Autowired
    MemberStatisticService statisticService;

    @Autowired
    SubscriberMapperImpl subscriberMapper;

    @MockBean
    SubscriberStatisticRepository subscriberStatisticRepository;


    @Test
    void saveSubscriberStatistic() {
        // given
        given(subscriberStatisticRepository.save(any(SubscriberStatisticEntity.class)))
            .willReturn(TestSuite.findSubscriberStatisticEntitySuit());

        // when
        var subscriberSuit = TestSuite.findSubscriberRecordSuit();
        var savedSubscriberStatisticRecord =
            statisticService.saveSubscriberStatistic(subscriberSuit);

        //then
        then(subscriberStatisticRepository)
            .should(times(1))
            .save(any(SubscriberStatisticEntity.class));
        assertThat(savedSubscriberStatisticRecord.subscriberCount()).isEqualTo(subscriberSuit.subscriberCount());
        assertThat(savedSubscriberStatisticRecord.recordTime()).isEqualTo(subscriberSuit.recordTime());
    }

    public static class TestSuite {
        public static SubscriberStatisticEntity findSubscriberStatisticEntitySuit() {
            var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);
            return new SubscriberStatisticEntity(0L, datetime, 10);
        }

        public static SubscriberRecord findSubscriberRecordSuit() {
            var datetime = LocalDateTime.of(2024, 11, 3, 0, 0, 0);

            return new SubscriberRecord(datetime, 10);
        }
    }
}