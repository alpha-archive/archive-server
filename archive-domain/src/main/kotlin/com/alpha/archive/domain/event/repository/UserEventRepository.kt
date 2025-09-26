package com.alpha.archive.domain.event.repository

import com.alpha.archive.domain.event.UserEvent
import com.alpha.archive.domain.event.enums.EventCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface UserEventRepository : JpaRepository<UserEvent, String>, UserEventCustomRepository {
}