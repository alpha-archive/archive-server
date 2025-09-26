package com.alpha.archive.activity.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "주간 활동 통계 응답")
data class WeeklyActivityStatisticsResponse(
    @Schema(description = "총 활동 수", example = "15")
    val totalActivities: Int,
    
    @Schema(description = "활동 유형별 비율")
    val categoryStats: List<CategoryStatisticResponse>,
    
    @Schema(description = "요일별 활동 수")
    val dailyStats: List<DayStatisticResponse>,
    
    @Schema(description = "만족도 평균", example = "4.2")
    val averageRating: Double?
)
