package com.alpha.archive

import com.alpha.archive.domain.event.EventImage
import com.alpha.archive.domain.event.EventImageRepository
import com.alpha.archive.domain.event.EventStatus
import com.alpha.archive.domain.event.PublicEvent
import com.alpha.archive.domain.event.PublicEventRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class ArchiveApplicationTests {

    @Autowired lateinit var publicEventRepository: PublicEventRepository
    @Autowired lateinit var eventImageRepository: EventImageRepository

    @AfterEach
    fun tearDown() {
        // 👉 DB에 실제로 남겨서 pgAdmin에서 보고 싶다면 이 메서드를 주석 처리하세요.
        publicEventRepository.deleteAll()
    }

    @Test
    fun contextLoads() {
        // Simple test to verify that the application context loads successfully
    }

    @Test
    fun `PublicEvent 저장 및 고유키 조회`() {
        val event = PublicEvent(
            source = "culture.go.kr",
            sourceEventId = "TEST-" + UUID.randomUUID().toString(),
            title = "테스트 공연",
            category = "PERFORMANCE",
            startAt = OffsetDateTime.now(),
            endAt = OffsetDateTime.now().plusHours(2),
            placeName = "국립극장",
            placeCity = "서울",
            placeDistrict = "중구",
            status = EventStatus.ACTIVE,
            rawPayload = mutableMapOf("raw" to "ok")
        )
        val saved = publicEventRepository.save(event)
        val found = publicEventRepository.findBySourceAndSourceEventId(saved.source, saved.sourceEventId)
        assertNotNull(found)
        assertEquals("테스트 공연", found!!.title)
        assertEquals("PERFORMANCE", found.category)
        assertEquals("국립극장", found.placeName)
    }

    @Test
    fun `PublicEvent 업서트 시 기존 레코드 갱신`() {
        val key = "TEST-" + UUID.randomUUID().toString()
        val first = PublicEvent(
            source = "culture.go.kr",
            sourceEventId = key,
            title = "초기 제목",
            category = "EXHIBITION",
            startAt = OffsetDateTime.now(),
            endAt = OffsetDateTime.now().plusDays(1),
            placeName = "서울시립미술관",
            placeCity = "서울",
            placeDistrict = "중구",
            status = EventStatus.ACTIVE,
            rawPayload = mutableMapOf("version" to 1)
        )
        val saved = publicEventRepository.save(first)

        val existing = publicEventRepository.findBySourceAndSourceEventId("culture.go.kr", key)!!
        existing.refreshFromIngestion(
            title = "업데이트된 제목",
            category = "PERFORMANCE",
            placeName = "세종문화회관",
            imageUrl = "https://example.com/poster.jpg",
            status = EventStatus.ACTIVE,
            rawPayload = mapOf("version" to 2),
        )
        val updated = publicEventRepository.save(existing)

        assertEquals(saved.id, updated.id)
        assertEquals("업데이트된 제목", updated.title)
        assertEquals("PERFORMANCE", updated.category)
        assertEquals("세종문화회관", updated.placeName)
        assertEquals("https://example.com/poster.jpg", updated.imageUrl)
        assertEquals(EventStatus.ACTIVE, updated.status)
        assertEquals(2, (updated.rawPayload["version"] as Number).toInt())
    }

    @Test
    fun `기간 검색 - start_at 사이에 있는 이벤트 조회`() {
        val now = OffsetDateTime.now()
        val k1 = "TEST-" + UUID.randomUUID().toString()
        val k2 = "TEST-" + UUID.randomUUID().toString()

        publicEventRepository.save(
            PublicEvent(
                source = "culture.go.kr",
                sourceEventId = k1,
                title = "오늘 공연",
                category = "PERFORMANCE",
                startAt = now.plusHours(1),
                endAt = now.plusHours(3),
                placeName = "A홀",
                status = EventStatus.ACTIVE,
                rawPayload = mutableMapOf()
            )
        )
        publicEventRepository.save(
            PublicEvent(
                source = "culture.go.kr",
                sourceEventId = k2,
                title = "다음주 전시",
                category = "EXHIBITION",
                startAt = now.plusDays(7),
                endAt = now.plusDays(14),
                placeName = "B갤러리",
                status = EventStatus.ACTIVE,
                rawPayload = mutableMapOf()
            )
        )

        val results = publicEventRepository.findAllByStartAtBetween(now, now.plusDays(1))
        assertTrue(results.any { it.title == "오늘 공연" })
        assertFalse(results.any { it.title == "다음주 전시" })
    }

    /**
     * ✅ 이벤트 + 이미지 저장/정렬/메타변경/삭제 검증
     * DB에 남겨서 pgAdmin으로 보고 싶으면, 클래스 위의 @AfterEach 주석 처리 + 이 테스트에 @Rollback(false) 추가
     */
    @Test
    @Transactional
    @Rollback(false) // <- DB에 남기려면 유지, 남기기 싫으면 지워도 됨
    fun `EventImage 저장-정렬-업데이트-삭제`() {
        // 1) 이벤트 저장
        val ev = PublicEvent(
            source = "culture.go.kr",
            sourceEventId = "TEST-" + UUID.randomUUID().toString(),
            title = "이미지 테스트 이벤트",
            category = "PERFORMANCE",
            startAt = OffsetDateTime.now(),
            endAt = OffsetDateTime.now().plusHours(1),
            placeName = "테스트홀",
            placeCity = "서울",
            placeDistrict = "중구",
            status = EventStatus.ACTIVE,
            rawPayload = mutableMapOf("k" to "v")
        )
        val savedEvent = publicEventRepository.save(ev)

        // 2) 이미지 2장 저장 (sort 0,1)
        val im1 = eventImageRepository.save(EventImage(event = savedEvent, url = "https://example.com/img1.jpg", sortNo = 0, type = "poster"))
        val im2 = eventImageRepository.save(EventImage(event = savedEvent, url = "https://example.com/img2.jpg", sortNo = 1, type = "detail"))
        assertNotNull(im1.id)
        assertNotNull(im2.id)
        assertEquals(2, eventImageRepository.countByEventId(savedEvent.id))

        // 3) 정렬 조회 (asc)
        val listAsc = eventImageRepository.findAllByEventIdOrderBySortNo(savedEvent.id)
        assertEquals(2, listAsc.size)
        assertEquals("https://example.com/img1.jpg", listAsc[0].url)
        assertEquals("https://example.com/img2.jpg", listAsc[1].url)

        // 4) 순서 바꾸기 (img2를 0번으로)
        listAsc[0].changeOrder(1)
        listAsc[1].changeOrder(0)
        eventImageRepository.saveAll(listAsc)

        val resorted = eventImageRepository.findAllByEventIdOrderBySortNo(savedEvent.id)
        assertEquals("https://example.com/img2.jpg", resorted[0].url)
        assertEquals("https://example.com/img1.jpg", resorted[1].url)

        // 5) 메타 업데이트 (type 변경)
        resorted[0].updateMeta("thumb")
        eventImageRepository.save(resorted[0])
        val updated = eventImageRepository.findAllByEventIdOrderBySortNo(savedEvent.id)
        assertEquals("thumb", updated[0].type)

        // 6) 이벤트 별 이미지 삭제
        eventImageRepository.deleteByEventId(savedEvent.id)
        assertEquals(0, eventImageRepository.countByEventId(savedEvent.id))
    }

    /**
     * ✅ 부모 삭제 시 자식 이미지 CASCADE 삭제 확인
     * (이 테스트는 롤백 true로, 실제 DB는 건드리지 않음)
     */
    @Test
    @Transactional
    @Rollback(true)
    fun `PublicEvent 삭제시 EventImage도 함께 삭제`() {
        val ev = PublicEvent(
            source = "culture.go.kr",
            sourceEventId = "TEST-" + UUID.randomUUID().toString(),
            title = "CASCADE 테스트",
            status = EventStatus.ACTIVE,
            rawPayload = mutableMapOf("a" to 1)
        )
        val savedEvent = publicEventRepository.save(ev)
        eventImageRepository.save(EventImage(event = savedEvent, url = "https://example.com/a.jpg", sortNo = 0))
        eventImageRepository.save(EventImage(event = savedEvent, url = "https://example.com/b.jpg", sortNo = 1))
        assertEquals(2, eventImageRepository.countByEventId(savedEvent.id))

        // 부모 삭제 → 자식들도 삭제 (엔티티에 @OnDelete(CASCADE) 설정이 되어 있어야 함)
        publicEventRepository.delete(savedEvent)
        publicEventRepository.flush()
        assertEquals(0, eventImageRepository.countByEventId(savedEvent.id))
    }
}