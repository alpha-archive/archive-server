package com.alpha.archive.domain.event

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface EventImageService {
    fun list(eventId: String): List<EventImage>
    fun add(event: PublicEvent, url: String, sortNo: Int = 0, type: String? = null): EventImage
    fun replaceAll(event: PublicEvent, urlsInOrder: List<String>): List<EventImage>
    fun reorder(eventId: String, idToSortNo: Map<String, Int>)
    fun removeAllByEventId(eventId: String)
}

@Service
@Transactional
class EventImageServiceImpl(
    private val imageRepo: EventImageRepository,
    private val eventRepo: PublicEventRepository

) : EventImageService {

    @Transactional(readOnly = true)
    override fun list(eventId: String): List<EventImage> =
        imageRepo.findAllByEventIdOrderBySortNo(eventId)

    override fun add(
        event: PublicEvent,
        url: String,
        sortNo: Int,
        type: String?,
    ): EventImage {
        // ✅ 항상 부모 편의 메서드로 연관관계 관리
        val img = EventImage(event = event, url = url, sortNo = sortNo, type = type)
        event.addImage(img)
        // 부모만 저장하면 cascade로 자식도 저장
        return eventRepo.save(event).images.first { it.id == img.id }
    }

    /* 기존 이미지를 모두 삭제하고, 전달된 URL 순서대로 재생성 */
    override fun replaceAll(event: PublicEvent, urlsInOrder: List<String>): List<EventImage> {
        // ✅ 부모 도메인 메서드 사용
        // (이미지 갯수가 많지 않다는 가정 하에 간단/명확)
        val managed = eventRepo.findWithImagesById(event.id) ?: event
        managed.replaceImages(urlsInOrder)
        return eventRepo.save(managed).images.sortedBy { it.sortNo }
    }

    /* 특정 이미지들에 대해 정렬 번호만 일괄 갱신 */
    override fun reorder(eventId: String, idToSortNo: Map<String, Int>) {
        val managed = eventRepo.findWithImagesById(eventId)
            ?: throw IllegalArgumentException("PublicEvent not found: $eventId")
        managed.reorderImages(idToSortNo)
        eventRepo.save(managed)
    }

    override fun removeAllByEventId(eventId: String) {
        val managed = eventRepo.findWithImagesById(eventId)
            ?: return
        // ✅ 부모 컬렉션에서 제거 → orphanRemoval로 DELETE
        managed.images.toList().forEach { managed.removeImage(it) }
        eventRepo.save(managed)
    }
}
