package com.alpha.archive.domain.event

import com.alpha.archive.domain.base.UlidPrimaryKeyEntity
import com.alpha.archive.domain.event.enums.UserEventImageStatus
import com.alpha.archive.domain.user.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.SQLDelete
import java.time.LocalDateTime

@Entity
@Table(
    name = "user_event_images",
    indexes = [
        Index(
            name = "fk_user_event_image_user_event_id",
            columnList = "user_event_id"
        ),
        Index(
            name = "fk_user_event_image_user_id",
            columnList = "user_id"
        )
    ]
)
@SQLDelete(sql = "UPDATE user_event_images SET deleted_at = NOW() WHERE id = ?")
class UserEventImage(
    user: User,
    userEvent: UserEvent?,
    url: String,
    fileName: String? = null,
    contentType: String? = null,
    status: UserEventImageStatus
) : UlidPrimaryKeyEntity() {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User = user
        protected set


    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_event_id", nullable = true)
    var userEvent: UserEvent? = userEvent
        protected set

    @Column(name = "url", length = 400, nullable = false)
    var url: String = url
        protected set

    @Column(name = "file_name", length = 255)
    var fileName: String? = fileName
        protected set

    @Column(name = "content_type", length = 100)
    var contentType: String? = contentType
        protected set

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: UserEventImageStatus = status
        protected set

    /* 소프트 삭제 시각 */
    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null
        protected set

}
