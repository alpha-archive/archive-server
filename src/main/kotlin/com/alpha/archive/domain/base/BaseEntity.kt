package com.alpha.archive.domain.base

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.OffsetDateTime
import java.time.ZoneOffset

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity : BasicBaseEntity() {

    @LastModifiedDate
    @Column(name = "\"UpdatedAt\"", nullable = true)
    var updatedAt: OffsetDateTime? = null
        protected set

    @Column(name = "\"DeletedAt\"", nullable = true)
    var deletedAt: OffsetDateTime? = null
        protected set

    fun delete() {
        deletedAt = OffsetDateTime.now(ZoneOffset.UTC)
    }

    fun update() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC)
    }
}
