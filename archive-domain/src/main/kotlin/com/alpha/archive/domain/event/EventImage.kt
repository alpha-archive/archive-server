package com.alpha.archive.domain.event

import com.alpha.archive.domain.base.UlidPrimaryKeyEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(
    name = "event_image",
    indexes = [
        Index(
            name = "idx_event_image_event_id",
            columnList = "event_id"
        ),
        Index(
            name = "idx_event_image_event_id_sort",
            columnList = "event_id, sort")
    ]
)
class EventImage(
    event: PublicEvent,
    url: String,
    sort: Int = 0,
    ingestedAt: LocalDateTime,
) : UlidPrimaryKeyEntity() {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    var event: PublicEvent = event
        protected set

    @Column(name = "url", length = 400, nullable = false)
    var url: String = url
        protected set

    @Column(name = "sort", nullable = false)
    var sort: Int = sort
        protected set

    @Column(name = "ingested_at", nullable = false)
    var ingestedAt: LocalDateTime = ingestedAt
        protected set

    /* 소프트 삭제 시각 */
    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null
        protected set


    fun updateImageInfo(
        url: String? = null,
        sort: Int? = null,
        ingestedAt: LocalDateTime = LocalDateTime.now()
    ) {
        url?.let { this.url = it }
        sort?.let { this.sort = it }
        this.ingestedAt = ingestedAt
    }
}