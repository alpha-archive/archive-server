package com.alpha.archive.activity.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "요일별 통계")
data class DayStatisticResponse(
    @Schema(description = "요일", example = "월")
    val dayOfWeek: String,
    
    @Schema(description = "활동 수", example = "3")
    val count: Int
)
