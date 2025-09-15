 package com.alpha.archive.publicdata.source

import com.alpha.archive.publicdata.client.CulturalDataClient
import com.alpha.archive.publicdata.dto.CulturalItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class CulturalDataSource(
    private val culturalDataClient: CulturalDataClient,
    @Value("\${public-data.cultural.service-key}") private val serviceKey: String
) : PublicDataSource<CulturalItem> {
    
    private val logger = LoggerFactory.getLogger(this::class.java)
    override val sourceName: String = "CULTURAL_DATA_PORTAL"
    
    override suspend fun fetchData(params: Map<String, Any>): List<CulturalItem> {
        return withContext(Dispatchers.IO) {
            try {
                val response = culturalDataClient.getCulturalEvents(
                    serviceKey = serviceKey,
                    pageNo = params["pageNo"] as? Int ?: 1,
                    numOfRows = params["numOfRows"] as? Int ?: 9129
                )
                
                if (response.response.header.resultCode == "0000") {
                    val allItems = response.response.body.items.items
                    val filteredItems = filterValidEvents(allItems)
                    
                    logger.info("Fetched ${allItems.size} cultural items, filtered to ${filteredItems.size} valid events")
                    filteredItems
                } else {
                    throw RuntimeException("API Error: ${response.response.header.resultMsg}")
                }
            } catch (e: Exception) {
                throw RuntimeException("Failed to fetch cultural data: ${e.message}", e)
            }
        }
    }
    
    /**
     * 2025년 이후까지 진행되는 유효한 이벤트만 필터링합니다.
     * PERIOD와 EVENT_PERIOD 모두 확인합니다.
     */
    private fun filterValidEvents(items: List<CulturalItem>): List<CulturalItem> {
        val cutoffDate = LocalDate.of(2025, 12, 31)
        
        return items.filter { item ->
            // PERIOD와 EVENT_PERIOD 모두에서 종료 날짜 추출
            val periodEndDate = extractEndDate(item.period)
            val eventPeriodEndDate = extractEndDate(item.eventPeriod)
            
            // 둘 중 하나라도 유효한 종료 날짜가 있고, 2025년 이후까지 진행되면 포함
            val isValidFromPeriod = periodEndDate?.isAfter(cutoffDate) == true
            val isValidFromEventPeriod = eventPeriodEndDate?.isAfter(cutoffDate) == true
            
            when {
                isValidFromPeriod || isValidFromEventPeriod -> {
                    val validEndDate = if (isValidFromPeriod) periodEndDate else eventPeriodEndDate
                    logger.debug("Valid event: ${item.localId} - ${item.title} (ends: $validEndDate)")
                    true
                }
                periodEndDate == null && eventPeriodEndDate == null -> {
                    logger.debug("No valid end date found for item: ${item.localId} - ${item.title}")
                    false // 둘 다 날짜가 없으면 제외
                }
                else -> {
                    logger.debug("Filtered out expired event: ${item.localId} - ${item.title} (PERIOD ends: $periodEndDate, EVENT_PERIOD ends: $eventPeriodEndDate)")
                    false // 2025년 내 종료되는 행사는 제외
                }
            }
        }
    }
    
    /**
     * PERIOD 또는 EVENT_PERIOD 문자열에서 종료 날짜를 추출합니다.
     * 예: 
     * - "2025-09-15 ~ 2026-03-15" -> LocalDate(2026, 3, 15)
     * - "2025-09-16~2026-02-22" -> LocalDate(2026, 2, 22)
     * - "2025-09-15" -> LocalDate(2025, 9, 15)
     */
    private fun extractEndDate(period: String?): LocalDate? {
        if (period.isNullOrBlank()) return null
        
        return try {
            // "~" 구분자로 나누기 (공백 있거나 없거나 모두 처리)
            val parts = period.split("~").map { it.trim() }
            
            if (parts.size >= 2) {
                // 범위 날짜인 경우 종료 날짜 추출
                val endDateString = parts[1].trim()
                parseDate(endDateString)
            } else {
                // 단일 날짜인 경우
                parseDate(period.trim())
            }
        } catch (e: Exception) {
            logger.warn("Failed to parse period: '$period' for date extraction", e)
            null
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
        
        logger.warn("Failed to parse date string: '$dateString'")
        return null
    }
}
