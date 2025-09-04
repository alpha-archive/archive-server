package com.alpha.archive.domain.event

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.OffsetDateTime

interface PublicEventRepository : JpaRepository<PublicEvent, String> {
    // 업서트 키 조회에 필요
    fun findBySourceAndSourceEventId(source: String, sourceEventId: String): PublicEvent?
    fun existsBySourceAndSourceEventId(source: String, sourceEventId: String): Boolean

    // 기간 조회
    fun findAllByStartAtBetween(start: OffsetDateTime, end: OffsetDateTime): List<PublicEvent>

    // 카테고리 조회
    fun findAllByCategory(category: String): List<PublicEvent>

    // 지역 조회
    fun findAllByPlaceCityAndPlaceDistrict(placeCity: String, placeDistrict: String): List<PublicEvent>

    @Query("select e from PublicEvent e left join fetch e._images where e.id = :id")
    fun findWithImagesById(@Param("id") id: String): PublicEvent?
}