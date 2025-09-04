package com.alpha.archive.domain.event

import com.alpha.archive.domain.base.UlidPrimaryKeyEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.OneToMany
import jakarta.persistence.PreRemove
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime

@Entity
@Table(
    name = "public_event",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_public_event_source_event_id",
            columnNames = ["source", "source_event_id"],
        )
    ],
    indexes = [
        Index(
            name = "idx_public_event_start_at",
            columnList = "start_at"
        ),
        Index(
            name = "idx_public_event_area",
            columnList = "place_city, place_district"
        )
    ]
)
class PublicEvent(
    source: String,
    sourceEventId: String,
    title: String,
    description: String? = null,
    category: String? = null,
    startAt: OffsetDateTime? = null,
    endAt: OffsetDateTime? = null,

    // 장소
    placeName: String? = null,
    placeAddress: String? = null,
    placeCity: String? = null,
    placeDistrict: String? = null,
    placeLatitude: Double? = null,
    placeLongitude: Double? = null,
    placePhone: String? = null,
    placeHomepage: String? = null,

    // 가격/표시
    priceText: String? = null,
    audience: String? = null,
    contact: String? = null,

    url: String? = null,
    imageUrl: String? = null,
    status: EventStatus = EventStatus.ACTIVE,

    rawPayload: MutableMap<String, Any> = mutableMapOf(),
    ingestedAt: OffsetDateTime = OffsetDateTime.now()
) : UlidPrimaryKeyEntity() {

    @Column(name = "source", nullable = false, length = 50)
    var source: String = source
        protected set

    @Column(name = "source_event_id", nullable = false, length = 128)
    var sourceEventId: String = sourceEventId
        protected set

    @Column(name = "title", nullable = false, length = 300)
    var title: String = title
        protected set

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String? = description
        protected set

    @Column(name = "category", length = 50)
    var category: String? = category
        protected set

    @Column(name = "start_at")
    var startAt: OffsetDateTime? = startAt
        protected set

    @Column(name = "end_at")
    var endAt: OffsetDateTime? = endAt
        protected set

    // 장소 관련

    @Column(name = "place_name", length = 200)
    var placeName: String? = placeName
        protected set

    @Column(name = "place_address", length = 300)
    var placeAddress: String? = placeAddress
        protected set

    @Column(name = "place_city", length = 50)
    var placeCity: String? = placeCity
        protected set

    @Column(name = "place_district", length = 50)
    var placeDistrict: String? = placeDistrict
        protected set

    @Column(name = "place_latitude")
    var placeLatitude: Double? = placeLatitude
        protected set

    @Column(name = "place_longitude")
    var placeLongitude: Double? = placeLongitude
        protected set

    @Column(name = "place_phone", length = 50)
    var placePhone: String? = placePhone
        protected set

    @Column(name = "place_homepage", length = 400)
    var placeHomepage: String? = placeHomepage
        protected set


    // 관람 정보

    @Column(name = "price_text", length = 200)
    var priceText: String? = priceText
        protected set

    @Column(name = "audience", length = 80)
    var audience: String? = audience
        protected set

    @Column(name = "contact", length = 100)
    var contact: String? = contact
        protected set

    @Column(name = "url", length = 400)
    var url: String? = url
        protected set

    @Column(name = "image_url", length = 400)
    var imageUrl: String? = imageUrl
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    var status: EventStatus = status
        protected set

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_payload", nullable = false, columnDefinition = "jsonb")
    var rawPayload: MutableMap<String, Any> = rawPayload
        protected set

    @Column(name = "ingested_at", nullable = false)
    var ingestedAt: OffsetDateTime = ingestedAt
        protected set

    @OneToMany(
        mappedBy = "event",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    private val _images: MutableList<EventImage> = mutableListOf()
    val images: List<EventImage> get() = _images


    /** 내부 헬퍼: EventImage에서 컬렉션 조작 시 리플렉션 없이 안전하게 접근 */
    internal fun __attach(child: EventImage) {
        if (!_images.contains(child)) _images.add(child)
    }
    internal fun __detach(child: EventImage) {
        _images.remove(child)
    }

    /* 편의 메서드 */
    fun addImage(image: EventImage) {
        if (image.event !== this) image.assignOwner(this) // 소유자 동기화
    }

    /* 생성과 동시에 추가하고 싶을 때 */
    fun addImage(url: String, sortNo: Int = 0, type: String? = null): EventImage {
        val img = EventImage(event = this, url = url, sortNo = sortNo, type = type)
        this.__attach(img) // assignOwner 내부에서도 attach하지만, 중복 호출은 방지됨
        return img
    }

    fun removeImage(image: EventImage) {
        _images.remove(image)
    }

    /* 전체 교체 */
    fun replaceImages(urlsInOrder: List<String>) {
        // 기존 모두 제거 (orphanRemoval로 DELETE 발생)
        _images.toList().forEach { removeImage(it) }
        // 새로 추가
        urlsInOrder.forEachIndexed { index, url -> addImage(url = url, sortNo = index) }
    }

    /* 정렬변경 */
    fun reorderImages(idToSortNo: Map<String, Int>) {
        _images.forEach { img ->
            idToSortNo[img.id]?.let { newOrder -> img.changeOrder(newOrder) }
        }
    }

    /* 부모 삭제 전에 컬렉션 로딩 + orphanRemoval 트리거 */
    @PreRemove
    private fun preRemove() {
        // 컬렉션을 강제로 초기화하고 자식들을 orphan으로 만들어 DELETE 유도
        _images.toList().forEach { _images.remove(it) }
    }

    fun refreshFromIngestion(
        title: String? = null,
        description: String? = null,
        category: String? = null,
        startAt: OffsetDateTime? = null,
        endAt: OffsetDateTime? = null,
        placeName: String? = null,
        placeAddress: String? = null,
        placeCity: String? = null,
        placeDistrict: String? = null,
        placeLatitude: Double? = null,
        placeLongitude: Double? = null,
        placePhone: String? = null,
        placeHomepage: String? = null,
        priceText: String? = null,
        audience: String? = null,
        contact: String? = null,
        url: String? = null,
        imageUrl: String? = null,
        status: EventStatus? = null,
        rawPayload: Map<String, Any>? = null,
        ingestedAt: OffsetDateTime? = null
    ) {
        title?.let { this.title = it }
        description?.let { this.description = it }
        category?.let { this.category = it }
        startAt?.let { this.startAt = it }
        endAt?.let { this.endAt = it }

        placeName?.let { this.placeName = it }
        placeAddress?.let { this.placeAddress = it }
        placeCity?.let { this.placeCity = it }
        placeDistrict?.let { this.placeDistrict = it }
        placeLatitude?.let { this.placeLatitude = it }
        placeLongitude?.let { this.placeLongitude = it }
        placePhone?.let { this.placePhone = it }
        placeHomepage?.let { this.placeHomepage = it }

        priceText?.let { this.priceText = it }
        audience?.let { this.audience = it }
        contact?.let { this.contact = it }
        url?.let { this.url = it }
        imageUrl?.let { this.imageUrl = it }
        status?.let { this.status = it }

        rawPayload?.let { this.rawPayload = it.toMutableMap() }
        ingestedAt?.let { this.ingestedAt = it }
    }
}