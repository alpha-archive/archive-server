package com.alpha.archive.domain.event.repository

import com.alpha.archive.domain.event.UserEvent
import org.springframework.data.jpa.repository.JpaRepository

interface UserEventRepository : JpaRepository<UserEvent, String> {
}