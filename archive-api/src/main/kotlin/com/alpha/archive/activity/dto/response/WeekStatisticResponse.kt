package com.alpha.archive.activity.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "주별 통계")
data class WeekStatisticResponse(
    @Schema(description = "월의 몇 번째 주", example = "1")
    val weekOfMonth: Int,
    
    @Schema(description = "활동 수", example = "8")
    val count: Int
)
