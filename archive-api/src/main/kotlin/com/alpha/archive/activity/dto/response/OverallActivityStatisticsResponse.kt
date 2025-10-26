package com.alpha.archive.activity.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "전체 활동 통계 응답")
data class OverallActivityStatisticsResponse(
    @Schema(description = "총 활동 수", example = "66")
    val totalActivities: Int,
    
    @Schema(description = "활동 유형별 비율")
    val categoryStats: List<CategoryStatisticResponse>
)

