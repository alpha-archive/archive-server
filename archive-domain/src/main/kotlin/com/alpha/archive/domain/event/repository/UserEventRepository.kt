package com.alpha.archive.domain.event.repository

import com.alpha.archive.domain.event.UserEvent
import com.alpha.archive.domain.event.enums.EventCategory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface UserEventRepository : JpaRepository<UserEvent, String>, UserEventCustomRepository {
    @Query("""
        SELECT ue FROM UserEvent ue 
        WHERE ue.user.id = :userId 
        AND ue.activityDate >= :startDate 
        AND ue.activityDate <= :endDate 
        AND ue.deletedAt IS NULL
        ORDER BY ue.activityDate DESC
    """)
    fun findByUserIdAndActivityDateBetween(
        @Param("userId") userId: String,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<UserEvent>

    fun findByUserIdAndDeletedAtIsNull(userId: String): List<UserEvent>

    fun findByIdAndUserIdAndDeletedAtIsNull(activityId: String, userId: String): UserEvent?
}