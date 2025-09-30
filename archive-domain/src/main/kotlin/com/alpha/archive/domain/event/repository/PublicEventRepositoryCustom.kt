package com.alpha.archive.domain.event.repository

import com.alpha.archive.domain.event.PublicEvent
import com.alpha.archive.domain.event.enums.EventCategory

interface PublicEventRepositoryCustom {
    fun findRecommendedActivitiesWithCursor(
        cursor: String?,
        size: Int,
        locationFilter: String?,
        titleFilter: String?,
        categoryFilter: EventCategory?
    ): List<PublicEvent>
    
    fun countRecommendedActivities(
        locationFilter: String?,
        titleFilter: String?,
        categoryFilter: EventCategory?
    ): Long
}
