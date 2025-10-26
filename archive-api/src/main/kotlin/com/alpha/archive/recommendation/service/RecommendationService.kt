package com.alpha.archive.recommendation.service

import com.alpha.archive.common.dto.CursorPaginatedWithTotalCountResponse
import com.alpha.archive.common.dto.CursorRequest
import com.alpha.archive.domain.event.PublicEvent
import com.alpha.archive.domain.event.enums.EventCategory
import com.alpha.archive.domain.event.repository.PublicEventRepository
import com.alpha.archive.recommendation.dto.response.RecommendedActivityResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class RecommendationService(
    private val publicEventRepository: PublicEventRepository
) {
    
    fun getRecommendedActivities(
        cursorRequest: CursorRequest,
        locationFilter: String?,
        titleFilter: String?,
        categoryFilter: EventCategory?
    ): CursorPaginatedWithTotalCountResponse<RecommendedActivityResponse> {
        // 전체 개수 조회
        val totalCount = publicEventRepository.countRecommendedActivities(
            locationFilter = locationFilter,
            titleFilter = titleFilter,
            categoryFilter = categoryFilter
        )
        
        // 데이터 조회
        val events = publicEventRepository.findRecommendedActivitiesWithCursor(
            cursor = cursorRequest.cursor,
            size = cursorRequest.size,
            locationFilter = locationFilter,
            titleFilter = titleFilter,
            categoryFilter = categoryFilter
        )
        
        // 다음 페이지 존재 여부 확인
        val hasNext = events.size >= cursorRequest.size
        
        // 마지막 커서 계산 (마지막 아이템의 ID)
        val lastCursor = if (events.isNotEmpty()) events.last().id else null
        
        // 응답 DTO 변환
        val responseData = events.map { it.toRecommendedActivityResponse() }
        
        return CursorPaginatedWithTotalCountResponse.toResponseDto(
            lastCursor = lastCursor,
            hasNext = hasNext,
            totalCount = totalCount.toInt(),
            content = responseData
        )
    }
    
    fun getRecommendedActivityDetail(activityId: String): RecommendedActivityResponse {
        val event = publicEventRepository.findByIdAndDeletedAtIsNull(activityId)
            ?: throw IllegalArgumentException("추천 활동을 찾을 수 없습니다. ID: $activityId")
        
        return event.toRecommendedActivityResponse()
    }
    
    private fun PublicEvent.toRecommendedActivityResponse(): RecommendedActivityResponse {
        return RecommendedActivityResponse(
            id = this.id,
            title = this.title,
            description = this.description,
            category = this.category,
            thumbnailImageUrl = this.meta.imageUrl,
            startAt = this.startAt,
            endAt = this.endAt,
            placeName = this.place.placeName,
            placeAddress = this.place.placeAddress,
            placeCity = this.place.placeCity,
            placeDistrict = this.place.placeDistrict,
            placeLatitude = this.place.placeLatitude,
            placeLongitude = this.place.placeLongitude,
            placePhone = this.place.placePhone,
            placeHomepage = this.place.placeHomepage,
            ingestedAt = this.ingestedAt
        )
    }
}
