package com.alpha.archive.activity.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "월간 활동 통계 응답 (GitHub 잔디 스타일)")
data class MonthlyActivityStatisticsResponse(
    @Schema(description = "조회 년월", example = "2025-10")
    val yearMonth: String,
    
    @Schema(description = "일별 활동 건수")
    val dailyActivities: List<DailyActivityCount>
)

@Schema(description = "일별 활동 건수")
data class DailyActivityCount(
    @Schema(description = "날짜", example = "2025-10-01")
    val day: LocalDate,

    @Schema(description = "활동 건수", example = "3")
    val count: Int
)
