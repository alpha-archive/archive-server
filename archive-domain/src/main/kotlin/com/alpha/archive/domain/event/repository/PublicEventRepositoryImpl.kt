package com.alpha.archive.domain.event.repository

import com.alpha.archive.domain.event.PublicEvent
import com.alpha.archive.domain.event.QPublicEvent
import com.alpha.archive.domain.event.enums.EventCategory
import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class PublicEventRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : PublicEventRepositoryCustom {

    companion object {
        private val publicEvent = QPublicEvent.publicEvent
    }

    override fun findRecommendedActivitiesWithCursor(
        cursor: String?,
        size: Int,
        locationFilter: String?,
        titleFilter: String?,
        categoryFilter: EventCategory?
    ): List<PublicEvent> {
        val whereCondition = buildBaseCondition(locationFilter, titleFilter, categoryFilter)

        cursor?.let {
            whereCondition.and(publicEvent.id.lt(it))
        }

        return queryFactory
            .selectFrom(publicEvent)
            .where(whereCondition)
            .orderBy(publicEvent.id.desc())
            .limit(size.toLong())
            .fetch()
    }

    override fun countRecommendedActivities(
        locationFilter: String?,
        titleFilter: String?,
        categoryFilter: EventCategory?
    ): Long {
        val whereCondition = buildBaseCondition(locationFilter, titleFilter, categoryFilter)

        val query = queryFactory
            .select(publicEvent.countDistinct())
            .from(publicEvent)
            .where(whereCondition)
            .fetchOne() ?: 0L

        println("Generated SQL: ${query}")

        return query
    }

    /**
     * 기본 조건(소프트 삭제, 지역 필터, 제목 필터, 카테고리 필터)을 빌드합니다.
     */
    private fun buildBaseCondition(
        locationFilter: String?,
        titleFilter: String?,
        categoryFilter: EventCategory?
    ): BooleanBuilder {
        return BooleanBuilder().apply {
            // 소프트 삭제되지 않은 것만
            and(publicEvent.deletedAt.isNull)

            // 지역 필터 적용
            locationFilter?.let { location ->
                and(buildLocationCondition(location))
            }

            // 제목 필터 적용
            titleFilter?.let { title ->
                and(publicEvent.title.containsIgnoreCase(title))
            }

            // 카테고리 필터 적용
            categoryFilter?.let { category ->
                and(publicEvent.category.eq(category))
            }
        }
    }

    /**
     * 지역 관련 필터 조건을 빌드합니다.
     * 주소, 도시, 구/군에서 검색합니다. (장소명은 제외하여 정확한 지역 검색 보장)
     */
    private fun buildLocationCondition(location: String): BooleanBuilder {
        return BooleanBuilder().apply {
            or(publicEvent.place.placeAddress.containsIgnoreCase(location))
            or(publicEvent.place.placeCity.containsIgnoreCase(location))
            or(publicEvent.place.placeDistrict.containsIgnoreCase(location))
        }
    }
}