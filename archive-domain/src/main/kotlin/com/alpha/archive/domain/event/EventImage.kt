package com.alpha.archive.domain.event

import com.alpha.archive.domain.base.UlidPrimaryKeyEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.SQLDelete
import java.time.LocalDateTime

@Entity
@Table(
    name = "event_images",
    indexes = [
        Index(
            name = "fk_event_image_event_id",
            columnList = "event_id"
        )
    ]
)
@SQLDelete(sql = "UPDATE event_images SET deleted_at = NOW() WHERE id = ?")
class EventImage(
    event: PublicEvent,
    url: String,
    ingestedAt: LocalDateTime,
) : UlidPrimaryKeyEntity() {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    var event: PublicEvent = event
        protected set

    @Column(name = "url", length = 400, nullable = false)
    var url: String = url
        protected set

    @Column(name = "ingested_at", nullable = false)
    var ingestedAt: LocalDateTime = ingestedAt
        protected set

    /* 소프트 삭제 시각 */
    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null
        protected set

}