package com.alpha.archive.domain.event

import com.alpha.archive.domain.base.UlidPrimaryKeyEntity
import com.alpha.archive.domain.event.embeddable.ActivityInfo
import com.alpha.archive.domain.user.User
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDateTime

@Entity
@Table(
    name = "user_event",
    indexes = [
        Index(
            name = "idx_user_event_user_id",
            columnList = "user_id"
        ),
        Index(
            name = "idx_user_event_public_event_id",
            columnList = "public_event_id"
        )
    ]
)
@SQLDelete(sql = "UPDATE user_event SET deleted_at = NOW() WHERE id = ?")
class UserEvent(
    user: User,
    publicEvent: PublicEvent? = null,
    activityDate: LocalDateTime,
    activityInfo: ActivityInfo
) : UlidPrimaryKeyEntity() {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User = user
        protected set

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "public_event_id", nullable = true)
    var publicEvent: PublicEvent? = publicEvent
        protected set

    @Column(name = "activity_date", nullable = false)
    var activityDate: LocalDateTime = activityDate
        protected set

    @Embedded
    var activityInfo: ActivityInfo = activityInfo
        protected set

    /* 소프트 삭제 시각 */
    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null
        protected set

    // user_event_image 매핑
    @OneToMany(mappedBy = "userEvent", fetch = FetchType.LAZY)
    protected val mutableImages: MutableSet<UserEventImage> = mutableSetOf()
    val images: Set<UserEventImage> get() = mutableImages.toSet()

}
