package com.alpha.archive.domain.event.repository

import com.alpha.archive.domain.event.PublicEvent
import org.springframework.data.jpa.repository.JpaRepository

interface PublicEventRepository : JpaRepository<PublicEvent, String>, PublicEventRepositoryCustom {
    fun findByIdAndDeletedAtIsNull(id: String): PublicEvent?
}
