package com.alpha.archive.activity.controller

import com.alpha.archive.activity.dto.request.UpdateActivityRequest
import com.alpha.archive.activity.dto.request.UserActivityRequest
import com.alpha.archive.activity.dto.response.ActivityDetailResponse
import com.alpha.archive.activity.dto.response.ActivityResponse
import com.alpha.archive.activity.dto.response.WeeklyActivityStatisticsResponse
import com.alpha.archive.activity.dto.response.MonthlyActivityStatisticsResponse
import com.alpha.archive.activity.service.ActivityService
import com.alpha.archive.auth.security.service.ArchiveUserDetails
import com.alpha.archive.common.annotations.ArchiveDeleteMapping
import com.alpha.archive.common.annotations.ArchiveGetMapping
import com.alpha.archive.common.annotations.ArchivePostMapping
import com.alpha.archive.common.annotations.ArchivePutMapping
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
import org.springframework.web.bind.annotation.PathVariable
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

    @ArchiveGetMapping("", authenticated = true)
    @SwaggerApiResponse(responseCode = "200", description = "활동 리스트 조회 성공")
    @Operation(
        summary = "사용자 활동 리스트 조회 API", 
        description = "사용자의 활동 리스트를 페이징하여 조회합니다. 최신 활동 순으로 정렬됩니다."
    )
    @CustomFailResponseAnnotation(ErrorTitle.NotFoundUser)
    fun getUserActivities(
        @AuthenticationPrincipal userDetails: ArchiveUserDetails,
    ): ResponseEntity<ApiResponse.Success<List<ActivityResponse>>> {
        val activities = activityService.getUserActivities(userDetails.getUserId())
        return ResponseUtil.success("활동 리스트를 성공적으로 조회했습니다.", activities)
    }

    @ArchiveGetMapping("/{activityId}", authenticated = true)
    @SwaggerApiResponse(responseCode = "200", description = "활동 상세 조회 성공")
    @Operation(
        summary = "사용자 활동 상세 조회 API", 
        description = "특정 활동의 상세 정보를 조회합니다. 연결된 이미지들과 공공 이벤트 정보도 함께 제공됩니다."
    )
    @CustomFailResponseAnnotation(ErrorTitle.NotFoundUser)
    @CustomFailResponseAnnotation(ErrorTitle.NotFoundUserEvent)
    fun getUserActivityDetail(
        @AuthenticationPrincipal userDetails: ArchiveUserDetails,
        @PathVariable activityId: String
    ): ResponseEntity<ApiResponse.Success<ActivityDetailResponse>> {
        val activity = activityService.getUserActivityDetail(userDetails.getUserId(), activityId)
        return ResponseUtil.success("활동 상세 정보를 성공적으로 조회했습니다.", activity)
    }

    @ArchivePutMapping("/{activityId}", authenticated = true)
    @SwaggerApiResponse(responseCode = "200", description = "활동 수정 성공")
    @Operation(
        summary = "사용자 활동 수정 API", 
        description = "기존 활동의 정보를 수정합니다. 이미지 추가/삭제도 가능합니다."
    )
    @CustomFailResponseAnnotation(ErrorTitle.BadRequest)
    @CustomFailResponseAnnotation(ErrorTitle.NotFoundUser)
    @CustomFailResponseAnnotation(ErrorTitle.NotFoundUserEvent)
    @CustomFailResponseAnnotation(ErrorTitle.NotFoundUserEventImage)
    fun updateUserActivity(
        @AuthenticationPrincipal userDetails: ArchiveUserDetails,
        @PathVariable activityId: String,
        @RequestBody @Valid request: UpdateActivityRequest
    ): ResponseEntity<ApiResponse.Success<ActivityDetailResponse>> {
        val updatedActivity = activityService.updateUserActivity(userDetails.getUserId(), activityId, request)
        return ResponseUtil.success("활동이 성공적으로 수정되었습니다.", updatedActivity)
    }

    @ArchiveDeleteMapping("/{activityId}", authenticated = true)
    @SwaggerApiResponse(responseCode = "200", description = "활동 삭제 성공")
    @Operation(
        summary = "사용자 활동 삭제 API", 
        description = "기존 활동을 삭제합니다. 소프트 삭제로 처리되며, 연결된 이미지들은 TEMP 상태로 변경됩니다."
    )
    @CustomFailResponseAnnotation(ErrorTitle.NotFoundUser)
    @CustomFailResponseAnnotation(ErrorTitle.NotFoundUserEvent)
    fun deleteUserActivity(
        @AuthenticationPrincipal userDetails: ArchiveUserDetails,
        @PathVariable activityId: String
    ): ResponseEntity<ApiResponse.Success<Unit>> {
        activityService.deleteUserActivity(userDetails.getUserId(), activityId)
        return ResponseUtil.success("활동이 성공적으로 삭제되었습니다.")
    }
}
