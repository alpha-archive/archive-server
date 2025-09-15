package com.alpha.archive.publicdata.mapper

import com.alpha.archive.domain.event.PublicEvent
import com.alpha.archive.domain.event.embeddable.AudienceMeta
import com.alpha.archive.domain.event.embeddable.PlaceInfo
import com.alpha.archive.domain.event.enums.EventCategory
import com.alpha.archive.domain.event.enums.PublicEventStatus
import com.alpha.archive.publicdata.dto.CulturalItem
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class CulturalDataMapper(
    private val objectMapper: ObjectMapper
) {
    
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    
    fun mapToPublicEvent(item: CulturalItem, source: String): PublicEvent {
        val (startDate, endDate) = parsePeriods(item.period, item.eventPeriod)
        
        return PublicEvent(
            source = source,
            sourceEventId = item.localId ?: "unknown",
            title = item.title ?: "제목 없음",
            description = buildDescription(item),
            category = mapCategory(item.genre),
            startAt = startDate?.atStartOfDay(),
            endAt = endDate?.atTime(23, 59, 59), // 종료일은 하루 끝으로 설정
            place = mapPlaceInfo(item),
            meta = mapAudienceMeta(item),
            status = PublicEventStatus.ACTIVE,
            rawPayload = objectMapper.writeValueAsString(item),
            ingestedAt = LocalDateTime.now()
        )
    }
    
    /**
     * PERIOD와 EVENT_PERIOD에서 시작일과 종료일을 추출합니다.
     * 둘 중 하나라도 유효한 데이터가 있으면 사용합니다.
     */
    private fun parsePeriods(period: String?, eventPeriod: String?): Pair<LocalDate?, LocalDate?> {
        // PERIOD 먼저 시도
        val periodResult = parseSinglePeriod(period)
        if (periodResult.first != null || periodResult.second != null) {
            return periodResult
        }
        
        // PERIOD가 null이거나 파싱 실패 시 EVENT_PERIOD 시도
        return parseSinglePeriod(eventPeriod)
    }
    
    /**
     * 단일 PERIOD 문자열을 파싱합니다.
     */
    private fun parseSinglePeriod(period: String?): Pair<LocalDate?, LocalDate?> {
        if (period.isNullOrBlank()) return Pair(null, null)
        
        return try {
            val parts = period.split("~").map { it.trim() }
            when {
                parts.size >= 2 -> {
                    val startDate = parseDate(parts[0])
                    val endDate = parseDate(parts[1])
                    Pair(startDate, endDate)
                }
                parts.size == 1 -> {
                    val date = parseDate(parts[0])
                    Pair(date, date) // 단일 날짜인 경우 시작일과 종료일 동일
                }
                else -> Pair(null, null)
            }
        } catch (e: Exception) {
            Pair(null, null)
        }
    }
    
    /**
     * 다양한 날짜 형식을 파싱합니다.
     */
    private fun parseDate(dateString: String): LocalDate? {
        val patterns = listOf(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy.MM.dd"),
            DateTimeFormatter.ofPattern("yyyyMMdd")
        )
        
        for (pattern in patterns) {
            try {
                return LocalDate.parse(dateString, pattern)
            } catch (e: Exception) {
                // 다음 패턴 시도
            }
        }
        
        return null
    }
    
    /**
     * 설명을 구성합니다 (여러 필드 조합).
     */
    private fun buildDescription(item: CulturalItem): String? {
        val descriptions = listOfNotNull(
            item.description,
            item.subDescription,
            item.eventPeriod?.let { "운영시간: $it" }
        ).filter { it.isNotBlank() }
        
        return if (descriptions.isNotEmpty()) {
            descriptions.joinToString("\n\n")
        } else null
    }
    
    /**
     * 장르를 기반으로 카테고리를 매핑합니다.
     */
    private fun mapCategory(genre: String?): EventCategory {
        return when {
            genre == null -> EventCategory.EXHIBITION // 장르가 없으면 전시로 기본 설정
            genre.contains("전시", ignoreCase = true) -> EventCategory.EXHIBITION
            genre.contains("연극", ignoreCase = true) -> EventCategory.THEATER
            genre.contains("뮤지컬", ignoreCase = true) -> EventCategory.MUSICAL
            genre.contains("콘서트", ignoreCase = true) || genre.contains("음악", ignoreCase = true) -> EventCategory.CONCERT
            genre.contains("축제", ignoreCase = true) -> EventCategory.FESTIVAL
            genre.contains("영화", ignoreCase = true) -> EventCategory.MOVIE
            genre.contains("워크샵", ignoreCase = true) -> EventCategory.WORKSHOP
            else -> EventCategory.OTHER
        }
    }
    
    /**
     * 장소 정보를 매핑합니다.
     */
    private fun mapPlaceInfo(item: CulturalItem): PlaceInfo {
        return PlaceInfo(
            placeName = item.eventSite ?: item.contactInstitutionName,
            placeAddress = item.spatialCoverage, // 공간적 범위를 주소로 사용
            placeCity = extractCityFromInstitution(item.contactInstitutionName),
            placeDistrict = null, // 제공되지 않음
            placeLatitude = null, // 제공되지 않음
            placeLongitude = null, // 제공되지 않음
            placePhone = item.contactPoint,
            placeHomepage = item.url
        )
    }
    
    /**
     * 기관명에서 도시 정보를 추출합니다.
     */
    private fun extractCityFromInstitution(institutionName: String?): String? {
        if (institutionName.isNullOrBlank()) return null
        
        return when {
            institutionName.contains("서울") -> "서울"
            institutionName.contains("부산") -> "부산"
            institutionName.contains("대구") -> "대구"
            institutionName.contains("인천") -> "인천"
            institutionName.contains("광주") -> "광주"
            institutionName.contains("대전") -> "대전"
            institutionName.contains("울산") -> "울산"
            institutionName.contains("세종") -> "세종"
            institutionName.contains("경기") -> "경기"
            institutionName.contains("강원") -> "강원"
            institutionName.contains("충북") -> "충북"
            institutionName.contains("충남") -> "충남"
            institutionName.contains("전북") -> "전북"
            institutionName.contains("전남") -> "전남"
            institutionName.contains("경북") -> "경북"
            institutionName.contains("경남") -> "경남"
            institutionName.contains("제주") -> "제주"
            else -> null
        }
    }
    
    /**
     * 관람 메타 정보를 매핑합니다.
     */
    private fun mapAudienceMeta(item: CulturalItem): AudienceMeta {
        return AudienceMeta(
            priceText = item.charge,
            audience = item.audience,
            contact = item.contactPoint,
            url = item.url,
            imageUrl = item.imageObject
        )
    }
}
