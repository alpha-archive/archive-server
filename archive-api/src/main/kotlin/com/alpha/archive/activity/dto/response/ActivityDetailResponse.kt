package com.alpha.archive.activity.dto.response

import com.alpha.archive.domain.event.UserEvent
import com.alpha.archive.domain.event.enums.EventCategory
import com.alpha.archive.util.ImageUtils
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "활동 상세 응답")
data class ActivityDetailResponse(
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
    
    @Schema(description = "활동 메모", example = "정말 감동적인 공연이었다...")
    val memo: String?,
    
    @Schema(description = "공공 이벤트 여부", example = "false")
    val isPublicEvent: Boolean,
    
    @Schema(description = "연결된 공공 이벤트 정보")
    val publicEventInfo: PublicEventInfoResponse?,
    
    @Schema(description = "활동 이미지 목록")
    val images: List<ActivityImageResponse>,
    
    @Schema(description = "생성일시", example = "2024-01-15T20:00:00")
    val createdAt: LocalDateTime,
    
    @Schema(description = "수정일시", example = "2024-01-15T20:30:00")
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(userEvent: UserEvent): ActivityDetailResponse {
            val displayTitle = userEvent.publicEvent?.title ?: userEvent.activityInfo.customTitle ?: "제목 없음"
            val displayLocation = userEvent.publicEvent?.place?.placeName ?: userEvent.activityInfo.customLocation
            
            val publicEventInfo = userEvent.publicEvent?.let { event ->
                PublicEventInfoResponse.from(event)
            }
            
            val activityImages = userEvent.images.map { image ->
                ActivityImageResponse.from(image)
            }
            
            return ActivityDetailResponse(
                id = userEvent.getId(),
                title = displayTitle,
                category = userEvent.activityInfo.customCategory,
                categoryDisplayName = userEvent.activityInfo.customCategory.displayName,
                location = displayLocation,
                activityDate = userEvent.activityDate,
                rating = userEvent.activityInfo.rating,
                memo = userEvent.activityInfo.memo,
                isPublicEvent = userEvent.publicEvent != null,
                publicEventInfo = publicEventInfo,
                images = activityImages,
                createdAt = userEvent.createdAt,
                updatedAt = LocalDateTime.now()
            )
        }
    }
}

@Schema(description = "공공 이벤트 정보")
data class PublicEventInfoResponse(
    @Schema(description = "공공 이벤트 ID", example = "01HZ8X9GYR7J8Q3N5V2BPMKWD1")
    val id: String,
    
    @Schema(description = "공공 이벤트 제목", example = "해리포터와 저주받은 아이")
    val title: String,
    
    @Schema(description = "공공 이벤트 설명", example = "마법 세계로의 특별한 여행...")
    val description: String?,
    
    @Schema(description = "공공 이벤트 시작일시", example = "2024-01-15T19:30:00")
    val startAt: LocalDateTime?,
    
    @Schema(description = "공공 이벤트 종료일시", example = "2024-03-15T21:30:00")
    val endAt: LocalDateTime?
) {
    companion object {
        fun from(publicEvent: com.alpha.archive.domain.event.PublicEvent): PublicEventInfoResponse {
            return PublicEventInfoResponse(
                id = publicEvent.getId(),
                title = publicEvent.title,
                description = publicEvent.description,
                startAt = publicEvent.startAt,
                endAt = publicEvent.endAt
            )
        }
    }
}

@Schema(description = "활동 이미지 정보")
data class ActivityImageResponse(
    @Schema(description = "이미지 ID", example = "01HZ8X9GYR7J8Q3N5V2BPMKWD1")
    val id: String,
    
    @Schema(description = "이미지 URL", example = "https://kr.object.ncloudstorage.com/archive-image-storage/images/sample.jpg")
    val imageUrl: String,
    
    @Schema(description = "원본 파일명", example = "musical_photo.jpg")
    val fileName: String?,
    
    @Schema(description = "컨텐츠 타입", example = "image/jpeg")
    val contentType: String?
) {
    companion object {
        fun from(userEventImage: com.alpha.archive.domain.event.UserEventImage): ActivityImageResponse {
            return ActivityImageResponse(
                id = userEventImage.getId(),
                imageUrl = userEventImage.url,
                fileName = ImageUtils.getSafeFilename(userEventImage.fileName),
                contentType = ImageUtils.getSafeContentType(userEventImage.contentType)
            )
        }
    }
}
