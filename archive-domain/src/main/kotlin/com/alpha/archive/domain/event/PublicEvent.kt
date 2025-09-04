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

    rawPayload: String = "",
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
    var rawPayload: String = rawPayload
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

}