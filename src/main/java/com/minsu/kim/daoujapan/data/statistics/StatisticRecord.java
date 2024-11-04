package com.minsu.kim.daoujapan.data.statistics;

import java.util.Optional;

import com.minsu.kim.daoujapan.data.statistics.amount.PaymentAmountRecord;
import com.minsu.kim.daoujapan.data.statistics.amount.SalesAmountRecord;
import com.minsu.kim.daoujapan.data.statistics.amount.UsageAmountRecord;
import com.minsu.kim.daoujapan.data.statistics.member.LeaverRecord;
import com.minsu.kim.daoujapan.data.statistics.member.SubscriberRecord;

/**
 * 자세한 기능을 적어주세요
 *
 * @author minsu.kim
 * @since 1.0
 */
public record StatisticRecord(
    Optional<SubscriberRecord> subscriberRecord,
    Optional<LeaverRecord> leaverRecord,
    Optional<PaymentAmountRecord> paymentAmountRecord,
    Optional<UsageAmountRecord> usageAmountRecord,
    Optional<SalesAmountRecord> salesAmountRecord) {}
