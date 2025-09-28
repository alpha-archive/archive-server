package com.alpha.archive.activity.dto.response

import com.alpha.archive.domain.event.UserEvent
import com.alpha.archive.domain.event.enums.EventCategory
import com.alpha.archive.util.ImageUtils
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "활동 응답")
data class ActivityResponse(
    @Schema(description = "활동 ID", example = "01HZ8X9GYR7J8Q3N5V2BPMKWD1")
    val id: String,
    
    @Schema(description = "활동 제목", example = "해리포터 뮤지컬 관람")
    val title: String,
    
    @Schema(description = "활동 카테고리", example = "MUSICAL")
    val category: EventCategory,
    
    @Schema(description = "카테고리 표시명", example = "뮤지컬")
    val categoryDisplayName: String,
    
    @Schema(description = "활동 장소", example = "충무아트센터 대극장")
    val location: String?,
    
    @Schema(description = "활동 날짜", example = "2024-01-15T19:30:00")
    val activityDate: LocalDateTime,
    
    @Schema(description = "활동 평점 (1-5점)", example = "5")
    val rating: Int?,
    
    @Schema(description = "대표 이미지 URL", example = "https://kr.object.ncloudstorage.com/archive-image-storage/images/sample.jpg")
    val thumbnailImageUrl: String?,
    
    @Schema(description = "공공 이벤트 여부", example = "false")
    val isPublicEvent: Boolean,
    
    @Schema(description = "이미지 개수", example = "3")
    val imageCount: Int
) {
    companion object {
        fun from(userEvent: UserEvent): ActivityResponse {
            val displayTitle = userEvent.publicEvent?.title ?: userEvent.activityInfo.customTitle ?: "제목 없음"
            val displayLocation = userEvent.publicEvent?.place?.placeName ?: userEvent.activityInfo.customLocation
            
            return ActivityResponse(
                id = userEvent.getId(),
                title = displayTitle,
                category = userEvent.activityInfo.customCategory,
                categoryDisplayName = userEvent.activityInfo.customCategory.displayName,
                location = displayLocation,
                activityDate = userEvent.activityDate,
                rating = userEvent.activityInfo.rating,
                thumbnailImageUrl = userEvent.images.firstOrNull()?.url,
                isPublicEvent = userEvent.publicEvent != null,
                imageCount = userEvent.images.size
            )
        }
    }
}