package com.minsu.kim.daujapan.data.statistics;

import com.minsu.kim.daujapan.data.statistics.amount.PaymentAmountRecord;
import com.minsu.kim.daujapan.data.statistics.amount.SalesAmountRecord;
import com.minsu.kim.daujapan.data.statistics.amount.UsageAmountRecord;
import com.minsu.kim.daujapan.data.statistics.member.LeaverRecord;
import com.minsu.kim.daujapan.data.statistics.member.SubscriverRecord;

/**
 * 자세한 기능을 적어주세요
 *
 * @author minsu.kim
 * @since 1.0
 */
public record StatisticRecord(
    SubscriverRecord subscriverRecord,
    LeaverRecord leaverRecord,
    PaymentAmountRecord paymentAmountRecord,
    UsageAmountRecord usageAmountRecord,
    SalesAmountRecord salesAmountRecord) {}
