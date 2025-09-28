package com.alpha.archive.domain.event.repository

import com.alpha.archive.domain.event.UserEvent
import com.alpha.archive.domain.event.UserEventImage
import com.alpha.archive.domain.event.enums.UserEventImageStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface UserEventImageRepository : JpaRepository<UserEventImage, String> {
    fun findAllByUserIdAndDeletedAtIsNull(userId: String): List<UserEventImage>

    fun findByIdAndDeletedAtIsNull(id: String): UserEventImage?

    /**
     * 특정 사용자의 TEMP 상태 이미지들을 ID 목록으로 조회
     */
    fun findByIdInAndUserIdAndStatusAndDeletedAtIsNull(
        ids: List<String>,
        userId: String,
        status: UserEventImageStatus
    ): List<UserEventImage>

    /**
     * 특정 사용자의 특정 UserEvent에 연결된 이미지들을 ID 목록으로 조회
     */
    fun findByIdInAndUserIdAndUserEventAndDeletedAtIsNull(
        ids: List<String>,
        userId: String,
        userEvent: UserEvent
    ): List<UserEventImage>
}
