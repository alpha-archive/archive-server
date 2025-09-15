package com.alpha.archive.publicdata.mapper

import com.alpha.archive.domain.event.PublicEvent
import com.alpha.archive.domain.event.embeddable.AudienceMeta
import com.alpha.archive.domain.event.embeddable.PlaceInfo
import com.alpha.archive.domain.event.enums.EventCategory
import com.alpha.archive.domain.event.enums.PublicEventStatus
import com.alpha.archive.publicdata.dto.CultureItem
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class CultureDataMapper(
    private val objectMapper: ObjectMapper
) {
    
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    
    fun mapToPublicEvent(item: CultureItem, source: String): PublicEvent {
        return PublicEvent(
            source = source,
            sourceEventId = item.seq ?: "unknown",
            title = item.title ?: "제목 없음",
            description = null, // 문화 데이터에는 상세 설명이 없음
            category = mapCategory(item.realmName),
            startAt = parseDate(item.startDate),
            endAt = parseDate(item.endDate),
            place = mapPlaceInfo(item),
            meta = mapAudienceMeta(item),
            status = PublicEventStatus.ACTIVE,
            rawPayload = objectMapper.writeValueAsString(item),
            ingestedAt = LocalDateTime.now()
        )
    }
    
    private fun mapCategory(realmName: String?): EventCategory {
        return when (realmName) {
            "연극" -> EventCategory.THEATER
            "뮤지컬" -> EventCategory.MUSICAL
            "전시" -> EventCategory.EXHIBITION
            "콘서트", "음악" -> EventCategory.CONCERT
            "축제" -> EventCategory.FESTIVAL
            "영화" -> EventCategory.MOVIE
            else -> EventCategory.OTHER
        }
    }
    
    private fun parseDate(dateString: String?): LocalDateTime? {
        if (dateString.isNullOrBlank()) return null
        
        val patterns = listOf(
            DateTimeFormatter.ofPattern("yyyyMMdd"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy.MM.dd")
        )
        
        for (pattern in patterns) {
            try {
                val localDate = LocalDate.parse(dateString, pattern)
                return localDate.atStartOfDay()
            } catch (e: Exception) {
                // 다음 패턴 시도
            }
        }
        
        return null
    }
    
    private fun mapPlaceInfo(item: CultureItem): PlaceInfo {
        return PlaceInfo(
            placeName = item.place,
            placeAddress = null, // API에서 제공하지 않음
            placeCity = item.area,
            placeDistrict = item.sigungu,
            placeLatitude = item.gpsY?.toDoubleOrNull(),
            placeLongitude = item.gpsX?.toDoubleOrNull(),
            placePhone = null, // API에서 제공하지 않음
            placeHomepage = null // API에서 제공하지 않음
        )
    }
    
    private fun mapAudienceMeta(item: CultureItem): AudienceMeta {
        return AudienceMeta(
            priceText = null, // API에서 제공하지 않음
            audience = null, // API에서 제공하지 않음
            contact = null, // API에서 제공하지 않음
            url = null, // API에서 제공하지 않음
            imageUrl = item.thumbnail
        )
    }
}
