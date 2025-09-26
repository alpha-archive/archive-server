package com.alpha.archive.activity.dto.response

import com.alpha.archive.domain.event.enums.EventCategory
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "카테고리별 통계")
data class CategoryStatisticResponse(
    @Schema(description = "활동 카테고리", example = "MUSICAL")
    val category: EventCategory,
    
    @Schema(description = "카테고리 표시명", example = "뮤지컬")
    val displayName: String,
    
    @Schema(description = "활동 수", example = "5")
    val count: Int,
    
    @Schema(description = "전체 대비 비율", example = "33.3")
    val percentage: Double
)
