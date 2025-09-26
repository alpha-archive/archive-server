package com.alpha.archive.activity.service

import com.alpha.archive.activity.dto.request.UserActivityRequest
import com.alpha.archive.activity.dto.response.CategoryStatisticResponse
import com.alpha.archive.activity.dto.response.DayStatisticResponse
import com.alpha.archive.activity.dto.response.WeekStatisticResponse
import com.alpha.archive.activity.dto.response.WeeklyActivityStatisticsResponse
import com.alpha.archive.activity.dto.response.MonthlyActivityStatisticsResponse
import com.alpha.archive.activity.util.ActivityPeriodCalculator
import com.alpha.archive.domain.event.UserEvent
import com.alpha.archive.domain.event.embeddable.ActivityInfo
import com.alpha.archive.domain.event.enums.UserEventImageStatus
import com.alpha.archive.domain.event.repository.PublicEventRepository
import com.alpha.archive.domain.event.repository.UserEventImageRepository
import com.alpha.archive.domain.event.repository.UserEventRepository
import com.alpha.archive.domain.event.repository.dto.CategoryCount
import com.alpha.archive.exception.ApiException
import com.alpha.archive.exception.ErrorTitle
import com.alpha.archive.user.service.UserService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import kotlin.math.roundToInt

interface ActivityService {
    fun createUserActivity(userId: String, request: UserActivityRequest)
    fun getWeeklyActivityStatistics(userId: String): WeeklyActivityStatisticsResponse
    fun getMonthlyActivityStatistics(userId: String): MonthlyActivityStatisticsResponse
}

@Service
class ActivityServiceImpl(
    private val userService: UserService,
    private val userEventRepository: UserEventRepository,
    private val userEventImageRepository: UserEventImageRepository,
    private val publicEventRepository: PublicEventRepository
) : ActivityService {

    /**
     * 사용자 활동 기록 생성
     * 1. 개인 활동 필드 검증 (publicEventId가 없는 경우)
     * 2. PublicEvent 조회 (publicEventId가 있는 경우)
     * 3. ActivityInfo 생성 (공공/개인 데이터 활용)
     * 4. UserEvent 생성 및 저장
     * 5. 이미지 연결 (imageIds가 있는 경우)
     */
    @Transactional
    override fun createUserActivity(userId: String, request: UserActivityRequest) {
        // 1. 요청 검증
        request.validateForPersonalActivity()

        // 2. 사용자 조회
        val user = userService.getUserEntityById(userId)

        // 3. PublicEvent 조회 (있는 경우)
        val publicEvent = request.publicEventId?.let { publicEventId ->
            publicEventRepository.findByIdAndDeletedAtIsNull(publicEventId)
                ?: throw ApiException(ErrorTitle.NotFoundPublicEvent)
        }

        // 4. ActivityInfo 생성 (공공/개인 데이터 결합)
        val activityInfo = ActivityInfo(
            customTitle = request.title,
            customCategory = request.category,
            customLocation = request.location,
            rating = request.rating,
            memo = request.memo
        )

        // 5. UserEvent 생성 및 저장
        val userEvent = UserEvent(
            user = user,
            publicEvent = publicEvent,
            activityDate = request.activityDate,
            activityInfo = activityInfo
        )
        val savedUserEvent = userEventRepository.save(userEvent)

        // 6. 이미지 연결 (imageIds가 있는 경우)
        request.imageIds?.takeIf { it.isNotEmpty() }?.let { imageIds ->
            linkImagesToUserEvent(userId, imageIds, savedUserEvent)
        }
    }


    /**
     * TEMP 상태의 이미지들을 UserEvent와 연결
     */
    private fun linkImagesToUserEvent(
        userId: String,
        imageIds: List<String>,
        userEvent: UserEvent
    ) {
        // TEMP 상태의 이미지들 조회
        val tempImages = userEventImageRepository.findByIdInAndUserIdAndStatusAndDeletedAtIsNull(
            ids = imageIds,
            userId = userId,
            status = UserEventImageStatus.TEMP
        )

        // 요청된 imageIds와 실제 조회된 이미지 개수가 다르면 예외 발생
        if (tempImages.size != imageIds.size) {
            val foundIds = tempImages.map { it.getId() }.toSet()
            val notFoundIds = imageIds.filterNot { it in foundIds }
            throw ApiException(ErrorTitle.NotFoundUserEventImage, "존재하지 않거나 이미 연결된 이미지가 있습니다: $notFoundIds")
        }

        // 각 이미지를 UserEvent와 연결하고 상태 변경
        tempImages.forEach { image ->
            image.linkToUserEvent(userEvent)
        }

        // 변경사항 저장
        userEventImageRepository.saveAll(tempImages)
    }


    /**
     * 주간 활동 통계 조회
     */
    override fun getWeeklyActivityStatistics(userId: String): WeeklyActivityStatisticsResponse {
        userService.getUserEntityById(userId)
        
        val (startDate, endDate) = ActivityPeriodCalculator.calculateWeeklyPeriod()
        
        val basicStats = userEventRepository.findUserActivityStatistics(userId, startDate, endDate)
        val totalCount = basicStats.totalCount.toInt()
        
        val categoryStats = userEventRepository.findCategoryStatistics(userId, startDate, endDate)
            .buildCategoryStatistics(totalCount)
        
        val dailyStats = userEventRepository.findDailyActivityCounts(userId, startDate, endDate)
            .buildDailyStatistics()
        
        return WeeklyActivityStatisticsResponse(
            totalActivities = totalCount,
            categoryStats = categoryStats,
            dailyStats = dailyStats,
            averageRating = basicStats.averageRating
        )
    }

    /**
     * 월간 활동 통계 조회
     */
    override fun getMonthlyActivityStatistics(userId: String): MonthlyActivityStatisticsResponse {
        userService.getUserEntityById(userId)
        
        val (startDate, endDate) = ActivityPeriodCalculator.calculateMonthlyPeriod()
        
        val basicStats = userEventRepository.findUserActivityStatistics(userId, startDate, endDate)
        val totalCount = basicStats.totalCount.toInt()
        
        val categoryStats = userEventRepository.findCategoryStatistics(userId, startDate, endDate)
            .buildCategoryStatistics(totalCount)
        
        val weeklyStats = userEventRepository.findByUserIdAndActivityDateBetween(userId, startDate, endDate)
            .buildWeeklyStatistics()
        
        return MonthlyActivityStatisticsResponse(
            totalActivities = totalCount,
            categoryStats = categoryStats,
            weeklyStats = weeklyStats,
            averageRating = basicStats.averageRating
        )
    }


    private fun List<CategoryCount>.buildCategoryStatistics(totalCount: Int): List<CategoryStatisticResponse> =
        map { categoryCount ->
            val count = categoryCount.count.toInt()
            val percentage = if (totalCount > 0) (count.toDouble() / totalCount) * 100 else 0.0
            
            CategoryStatisticResponse(
                category = categoryCount.category,
                displayName = categoryCount.category.displayName,
                count = count,
                percentage = (percentage * 10).roundToInt() / 10.0
            )
        }

    private fun Map<Int, Long>.buildDailyStatistics(): List<DayStatisticResponse> {
        val dayNames = listOf("월", "화", "수", "목", "금", "토", "일")
        return (1..7).map { dayOfWeek ->
            DayStatisticResponse(
                dayOfWeek = dayNames[dayOfWeek - 1],
                count = this[dayOfWeek]?.toInt() ?: 0
            )
        }
    }

    private fun List<UserEvent>.buildWeeklyStatistics(): List<WeekStatisticResponse> =
        groupBy { activity ->
            val dayOfMonth = activity.activityDate.dayOfMonth
            ((dayOfMonth - 1) / 7) + 1
        }
        .mapValues { it.value.size }
        .let { activityCountByWeek ->
            (1..5).map { weekOfMonth ->
                WeekStatisticResponse(
                    weekOfMonth = weekOfMonth,
                    count = activityCountByWeek[weekOfMonth] ?: 0
                )
            }.filter { it.count > 0 || it.weekOfMonth <= 4 }
        }
}