package com.alpha.archive.domain.event.repository.dto

import java.time.LocalDate

/**
 * 날짜별 활동 개수 데이터 클래스
 */
data class DailyActivityCount(
    val date: LocalDate,
    val count: Long
)

