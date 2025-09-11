package com.alpha.archive.domain.event

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserEventImageRepository : JpaRepository<UserEventImage, String> {
    fun findAllByUserIdAndDeletedAtIsNull(userId: String): List<UserEventImage>

    fun findByIdAndDeletedAtIsNull(id: String): UserEventImage?
}
