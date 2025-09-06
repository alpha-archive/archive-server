package com.alpha.archive.domain.event

import com.alpha.archive.domain.base.UlidPrimaryKeyEntity
import com.alpha.archive.domain.event.embeddable.AudienceMeta
import com.alpha.archive.domain.event.embeddable.PlaceInfo
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime


@Entity
@Table(
    name = "public_event",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_public_event_source_source_event_id",
            columnNames = ["source", "source_event_id"],
        )
    ],
    indexes = [
        Index(
            name = "idx_public_event_status",
            columnList = "status"
        ),
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
@SQLDelete(sql = "UPDATE public_event SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
class PublicEvent (
    source: String,
    sourceEventId: String,
    title: String,
    description: String? = null,
    category: String? = null,
    startAt: LocalDateTime? = null,
    endAt: LocalDateTime? = null,
    place: PlaceInfo = PlaceInfo(),
    meta: AudienceMeta = AudienceMeta(),
    status: PublicEventStatus = PublicEventStatus.ACTIVE,
    rawPayload: String,
    ingestedAt: LocalDateTime,
    ) : UlidPrimaryKeyEntity() {

    @Column(name = "source", length = 50, nullable = false)
    var source: String = source
        protected set

    @Column(name = "source_event_id", length = 128, nullable = false)
    var sourceEventId: String = sourceEventId
        protected set

    @Column(name = "title", length = 300, nullable = false)
    var title: String = title
        protected set

    @Column(name = "description", columnDefinition = "text")
    var description: String? = description
        protected set

    @Column(name = "category", length = 50)
    var category: String? = category
        protected set

    @Column(name = "start_at")
    var startAt: LocalDateTime? = startAt
        protected set

    @Column(name = "end_at")
    var endAt: LocalDateTime? = endAt
        protected set

    // 장소 관련
    @Embedded
    var place: PlaceInfo = place
        protected set

    // 관람 정보 관련
    @Embedded
    var meta: AudienceMeta = meta
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    var status: PublicEventStatus = status
        protected set

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "raw_payload", columnDefinition = "jsonb", nullable = false)
    var rawPayload: String = rawPayload
        protected set

    @Column(name = "ingested_at", nullable = false)
    var ingestedAt: LocalDateTime = ingestedAt
        protected set

    /* 소프트 삭제 시각 */
    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null
        protected set

    // event_image 매핑
    @OneToMany(
        mappedBy = "event",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    val images: MutableSet<EventImage> = mutableSetOf()


    fun updateEvent(
        title: String? = null,
        description: String? = null,
        category: String? = null,
        startAt: LocalDateTime? = null,
        endAt: LocalDateTime? = null,
        place: PlaceInfo? = null,
        meta: AudienceMeta? = null,
        status: PublicEventStatus? = null,
        rawPayload: String,
        ingestedAt: LocalDateTime = LocalDateTime.now()
    ) {
        title?.let { this.title = it }
        description?.let { this.description = it }
        category?.let { this.category = it }
        startAt?.let { this.startAt = it }
        endAt?.let { this.endAt = it }
        place?.let { this.place = it }
        meta?.let { this.meta = it }
        status?.let { this.status = it }

        // 필수 갱신
        this.rawPayload = rawPayload
        this.ingestedAt = ingestedAt
    }
}