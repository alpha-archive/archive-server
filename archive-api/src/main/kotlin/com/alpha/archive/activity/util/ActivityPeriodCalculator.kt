package com.alpha.archive.activity.util

import java.time.LocalDateTime

/**
 * 활동 통계 기간 계산 유틸리티
 */
object ActivityPeriodCalculator {
    
    /**
     * 최근 7일 기간 계산 (7일 전 00:00 ~ 오늘 23:59)
     */
    fun calculateWeeklyPeriod(): Pair<LocalDateTime, LocalDateTime> {
        val now = LocalDateTime.now()
        val endDate = now.withHour(23).withMinute(59).withSecond(59).withNano(999999999)
        val startDate = now.minusDays(6)
            .withHour(0).withMinute(0).withSecond(0).withNano(0)
        return startDate to endDate
    }

    /**
     * 이번 달 기간 계산 (1일 00:00 ~ 말일 23:59)
     */
    fun calculateMonthlyPeriod(): Pair<LocalDateTime, LocalDateTime> {
        val now = LocalDateTime.now()
        val startOfMonth = now.withDayOfMonth(1)
            .withHour(0).withMinute(0).withSecond(0).withNano(0)
        val endOfMonth = now.withDayOfMonth(now.toLocalDate().lengthOfMonth())
            .withHour(23).withMinute(59).withSecond(59).withNano(999999999)
        return startOfMonth to endOfMonth
    }
}
