package com.alpha.archive.recommendation.controller

import com.alpha.archive.common.annotations.ArchiveGetMapping
import com.alpha.archive.common.dto.ApiResponse
import com.alpha.archive.common.dto.CursorPaginatedWithTotalCountResponse
import com.alpha.archive.common.dto.CursorRequest
import com.alpha.archive.domain.event.enums.EventCategory
import com.alpha.archive.exception.ErrorTitle
import com.alpha.archive.exception.annotation.CustomFailResponseAnnotation
import com.alpha.archive.recommendation.dto.response.RecommendedActivityResponse
import com.alpha.archive.recommendation.service.RecommendationService
import com.alpha.archive.util.ResponseUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/recommendations")
@Tag(name = "추천 활동 API", description = "공공 데이터 기반 추천 활동 API입니다.")
class RecommendationController(
    private val recommendationService: RecommendationService
) {

    @ArchiveGetMapping("/activities")
    @SwaggerApiResponse(responseCode = "200", description = "추천 활동 조회 성공")
    @Operation(
        summary = "추천 활동 목록 조회", 
        description = "공공 데이터 기반으로 추천 활동 목록을 커서 페이지네이션으로 조회합니다. 지역명, 활동명, 카테고리로 필터링이 가능합니다."
    )
    @CustomFailResponseAnnotation(ErrorTitle.BadRequest)
    @CustomFailResponseAnnotation(ErrorTitle.NotFoundUser)
    fun getRecommendedActivities(
        @Parameter(description = "커서 값 (첫 페이지인 경우 null)", required = false)
        @RequestParam(required = false) cursor: String?,

        @Parameter(description = "페이지 사이즈", required = false)
        @RequestParam(defaultValue = "10") size: Int,

        @Parameter(description = "지역명 필터 (장소명, 주소, 도시, 구/군에서 부분 검색)", required = false)
        @RequestParam(required = false) location: String?,

        @Parameter(description = "활동명 필터 (제목에서 부분 검색)", required = false)
        @RequestParam(required = false) title: String?,

        @Parameter(description = "카테고리 필터 (EventCategory enum 값)", required = false)
        @RequestParam(required = false) category: EventCategory?
    ): ResponseEntity<ApiResponse.Success<CursorPaginatedWithTotalCountResponse<RecommendedActivityResponse>>> {
        
        val cursorRequest = CursorRequest(cursor = cursor, size = size)
        
        val result = recommendationService.getRecommendedActivities(
            cursorRequest = cursorRequest,
            locationFilter = location,
            titleFilter = title,
            categoryFilter = category
        )
        
        return ResponseUtil.success("추천 활동 목록을 성공적으로 조회했습니다.", result)
    }
}
