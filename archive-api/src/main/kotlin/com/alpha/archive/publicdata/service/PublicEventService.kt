package com.alpha.archive.publicdata.service

import com.alpha.archive.domain.event.PublicEvent
import com.alpha.archive.domain.event.repository.PublicEventRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PublicEventService(
    private val publicEventRepository: PublicEventRepository
) {
    
    private val logger = LoggerFactory.getLogger(this::class.java)
    
    /**
     * PublicEvent 목록을 저장합니다.
     * 중복 처리: source + sourceEventId 조합으로 중복 확인
     */
    @Transactional
    fun savePublicEvents(events: List<PublicEvent>): Int {
        logger.info("Saving ${events.size} public events")
        
        var savedCount = 0
        
        events.forEach { event ->
            try {
                // 중복 확인 (source + sourceEventId 조합)
                val existing = findExistingEvent(event.source, event.sourceEventId)
                
                if (existing == null) {
                    publicEventRepository.save(event)
                    savedCount++
                    logger.debug("Saved new public event: ${event.source}:${event.sourceEventId} - ${event.title}")
                } else {
                    // 기존 이벤트가 있는 경우 업데이트할지 결정
                    if (shouldUpdateEvent(existing, event)) {
                        val updatedEvent = updateExistingEvent(existing, event)
                        publicEventRepository.save(updatedEvent)
                        savedCount++
                        logger.debug("Updated existing public event: ${event.source}:${event.sourceEventId} - ${event.title}")
                    } else {
                        logger.debug("Skipped duplicate public event: ${event.source}:${event.sourceEventId} - ${event.title}")
                    }
                }
            } catch (e: Exception) {
                logger.error("Failed to save public event: ${event.source}:${event.sourceEventId} - ${event.title}", e)
            }
        }
        
        logger.info("Successfully saved $savedCount out of ${events.size} public events")
        return savedCount
    }
    
    /**
     * 기존 이벤트를 찾습니다.
     */
    private fun findExistingEvent(source: String, sourceEventId: String): PublicEvent? {
        // 향후 성능 개선을 위해 custom query method 추가 권장
        // 현재는 간단하게 전체 조회 후 필터링
        return publicEventRepository.findAll()
            .firstOrNull { it.source == source && it.sourceEventId == sourceEventId && it.deletedAt == null }
    }
    
    /**
     * 기존 이벤트를 업데이트할지 결정합니다.
     * 기본적으로 새로운 데이터로 덮어씁니다.
     */
    private fun shouldUpdateEvent(existing: PublicEvent, new: PublicEvent): Boolean {
        // 제목이나 날짜가 변경된 경우 업데이트
        return existing.title != new.title ||
                existing.startAt != new.startAt ||
                existing.endAt != new.endAt ||
                existing.place.placeName != new.place.placeName
    }
    
    /**
     * 기존 이벤트를 새로운 데이터로 업데이트합니다.
     * PublicEvent의 필드들이 protected set이므로 새로운 객체로 대체합니다.
     */
    private fun updateExistingEvent(existing: PublicEvent, new: PublicEvent): PublicEvent {
        // PublicEvent의 필드들이 protected set이므로 새로운 객체를 생성하여 ID만 유지
        return PublicEvent(
            source = new.source,
            sourceEventId = new.sourceEventId,
            title = new.title,
            description = new.description,
            category = new.category,
            startAt = new.startAt,
            endAt = new.endAt,
            place = new.place,
            meta = new.meta,
            status = new.status,
            rawPayload = new.rawPayload,
            ingestedAt = new.ingestedAt
        )
    }
}
