package com.alpha.archive.activity.controller

import com.alpha.archive.activity.dto.request.UserActivityRequest
import com.alpha.archive.activity.dto.response.WeeklyActivityStatisticsResponse
import com.alpha.archive.activity.dto.response.MonthlyActivityStatisticsResponse
import com.alpha.archive.activity.service.ActivityService
import com.alpha.archive.auth.security.service.ArchiveUserDetails
import com.alpha.archive.common.annotations.ArchiveGetMapping
import com.alpha.archive.common.annotations.ArchivePostMapping
import com.alpha.archive.common.dto.ApiResponse
import com.alpha.archive.exception.ErrorTitle
import com.alpha.archive.exception.annotation.CustomFailResponseAnnotation
import com.alpha.archive.util.ResponseUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/activities")
@Tag(name = "활동 관련 API", description = "활동 관련 API 입니다.")
@SecurityRequirement(name = "bearerAuth")
class ActivityController(
    private val activityService: ActivityService
) {

    @ArchivePostMapping("", authenticated = true)
    @SwaggerApiResponse(responseCode = "201", description = "활동 기록 생성 성공")
    @Operation(summary = "사용자 활동을 기록하는 API", description = "사용자의 활동을 기록하고, 업로드된 이미지들을 연결합니다.")
    @CustomFailResponseAnnotation(ErrorTitle.BadRequest)
    @CustomFailResponseAnnotation(ErrorTitle.NotFoundUser)
    @CustomFailResponseAnnotation(ErrorTitle.NotFoundUserEventImage)
    @CustomFailResponseAnnotation(ErrorTitle.NotFoundPublicEvent)
    fun createActivity(
        @AuthenticationPrincipal userDetails: ArchiveUserDetails,
        @RequestBody @Valid request: UserActivityRequest
    ): ResponseEntity<ApiResponse.Success<Unit>> {
        activityService.createUserActivity(userDetails.getUserId(), request)
        return ResponseUtil.success("활동 기록이 성공적으로 생성되었습니다.")
    }

    @ArchiveGetMapping("/statistics/weekly", authenticated = true)
    @SwaggerApiResponse(responseCode = "200", description = "주간 활동 통계 조회 성공")
    @Operation(
        summary = "주간 활동 통계 조회 API", 
        description = "사용자의 이번 주 활동 통계를 조회합니다. 카테고리별 분포, 요일별 활동량, 평균 평점 등을 제공합니다."
    )
    @CustomFailResponseAnnotation(ErrorTitle.NotFoundUser)
    fun getWeeklyActivityStatistics(
        @AuthenticationPrincipal userDetails: ArchiveUserDetails
    ): ResponseEntity<ApiResponse.Success<WeeklyActivityStatisticsResponse>> {
        val statistics = activityService.getWeeklyActivityStatistics(userDetails.getUserId())
        return ResponseUtil.success("주간 활동 통계를 성공적으로 조회했습니다.", statistics)
    }

    @ArchiveGetMapping("/statistics/monthly", authenticated = true)
    @SwaggerApiResponse(responseCode = "200", description = "월간 활동 통계 조회 성공")
    @Operation(
        summary = "월간 활동 통계 조회 API", 
        description = "사용자의 이번 달 활동 통계를 조회합니다. 카테고리별 분포, 주별 활동량, 평균 평점 등을 제공합니다."
    )
    @CustomFailResponseAnnotation(ErrorTitle.NotFoundUser)
    fun getMonthlyActivityStatistics(
        @AuthenticationPrincipal userDetails: ArchiveUserDetails
    ): ResponseEntity<ApiResponse.Success<MonthlyActivityStatisticsResponse>> {
        val statistics = activityService.getMonthlyActivityStatistics(userDetails.getUserId())
        return ResponseUtil.success("월간 활동 통계를 성공적으로 조회했습니다.", statistics)
    }
}
