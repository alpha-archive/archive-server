package com.alpha.archive.domain.event.repository

import com.alpha.archive.domain.event.PublicEvent

interface PublicEventRepositoryCustom {
    fun findRecommendedActivitiesWithCursor(
        cursor: String?,
        size: Int,
        locationFilter: String?,
        titleFilter: String?
    ): List<PublicEvent>
    
    fun countRecommendedActivities(
        locationFilter: String?,
        titleFilter: String?
    ): Long
}
