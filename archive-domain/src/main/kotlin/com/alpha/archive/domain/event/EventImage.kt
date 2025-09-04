package com.alpha.archive.domain.event

import com.alpha.archive.domain.base.UlidPrimaryKeyEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.OffsetDateTime


@Entity
@Table(
    name = "event_image",
    indexes = [
        Index(name = "idx_event_image_event_sort", columnList = "event_id, sort_no")
    ]
)
class EventImage(
    event: PublicEvent,
    url: String,
    sortNo: Int = 0,
    type: String? = null,
) : UlidPrimaryKeyEntity() {

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = [CascadeType.REMOVE])
    @JoinColumn(name = "event_id", nullable = false, foreignKey = ForeignKey(name = "fk_event_image_id"))
    var event: PublicEvent = event
        protected set

    @Column(name = "url", nullable = false, length = 400)
    var url: String = url
        protected set

    @Column(name = "sort_no", nullable = false)
    var sortNo: Int = sortNo
        protected set

    @Column(name = "type", length = 30)
    var type: String? = type
        protected set

    @CreationTimestamp
    @Column(name = "image_created_at", nullable = false, updatable = false)
    var imageCreatedAt: OffsetDateTime? = null
        protected set

    @Column(name = "deleted_at", nullable = true)
    var deletedAt: OffsetDateTime? = null
        protected set

    init {
        this.event.__attach(this)
    }

    // 도메인 메서드
    fun changeUrl(newUrl: String) {
        this.url = newUrl
    }

    fun changeOrder(newSortNo: Int) {
        this.sortNo = newSortNo
    }

    fun updateMeta(type: String?) {
        this.type = type
    }

    /** 양방향 일관성 유지 — 리플렉션 제거, 부모 내부 헬퍼 사용 */
    fun assignOwner(owner: PublicEvent?) {
        if (this.event === owner) return
        this.event.__detach(this)
        if (owner != null) {
            this.event = owner
        }
        owner?.__attach(this)
    }
}