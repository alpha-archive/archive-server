package com.alpha.archive.domain.event.repository

import com.alpha.archive.domain.event.QUserEvent
import com.alpha.archive.domain.event.enums.EventCategory
import com.alpha.archive.domain.event.repository.dto.CategoryCount
import com.alpha.archive.domain.event.repository.dto.UserActivityStatistics
import com.querydsl.core.types.Projections
import com.querydsl.core.types.dsl.Expressions.numberTemplate
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class UserEventCustomRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : UserEventCustomRepository {

    private val userEvent = QUserEvent.userEvent

    override fun findUserActivityStatistics(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): UserActivityStatistics {
        val result = queryFactory
            .select(
                userEvent.count(),
                userEvent.activityInfo.rating.avg()
            )
            .from(userEvent)
            .where(
                userEvent.user.id.eq(userId)
                    .and(userEvent.activityDate.between(startDate, endDate))
                    .and(userEvent.deletedAt.isNull)
            )
            .fetchOne()

        return UserActivityStatistics(
            totalCount = result?.get(0, Long::class.java) ?: 0L,
            averageRating = result?.get(1, Double::class.java)
        )
    }

    override fun findCategoryStatistics(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<CategoryCount> {
        return queryFactory
            .select(
                Projections.constructor(
                    CategoryCount::class.java,
                    userEvent.activityInfo.customCategory,
                    userEvent.count()
                )
            )
            .from(userEvent)
            .where(
                userEvent.user.id.eq(userId)
                    .and(userEvent.activityDate.between(startDate, endDate))
                    .and(userEvent.deletedAt.isNull)
            )
            .groupBy(userEvent.activityInfo.customCategory)
            .orderBy(userEvent.count().desc())
            .fetch()
    }

    override fun findDailyActivityCounts(
        userId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): Map<Int, Long> {
        // 데이터베이스 독립적인 방법: 활동 목록을 조회하고 애플리케이션에서 그룹핑
        val activities = queryFactory
            .select(userEvent.activityDate)
            .from(userEvent)
            .where(
                userEvent.user.id.eq(userId)
                    .and(userEvent.activityDate.between(startDate, endDate))
                    .and(userEvent.deletedAt.isNull)
            )
            .fetch()

        return activities
            .groupBy { it.dayOfWeek.value } // Java DayOfWeek: 1=월요일, 7=일요일
            .mapValues { it.value.size.toLong() }
    }
    
    override fun countAllUserActivities(userId: String): Long {
        return queryFactory
            .select(userEvent.count())
            .from(userEvent)
            .where(
                userEvent.user.id.eq(userId)
                    .and(userEvent.deletedAt.isNull)
            )
            .fetchOne() ?: 0L
    }
    
    override fun findAllCategoryStatistics(userId: String): List<CategoryCount> {
        return queryFactory
            .select(
                Projections.constructor(
                    CategoryCount::class.java,
                    userEvent.activityInfo.customCategory,
                    userEvent.count()
                )
            )
            .from(userEvent)
            .where(
                userEvent.user.id.eq(userId)
                    .and(userEvent.deletedAt.isNull)
            )
            .groupBy(userEvent.activityInfo.customCategory)
            .orderBy(userEvent.count().desc())
            .fetch()
    }
}
