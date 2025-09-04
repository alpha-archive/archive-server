package com.alpha.archive.domain.event

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface EventImageRepository : JpaRepository<EventImage, String> {

    @Query("select i from EventImage i where i.event.id = :eventId order by i.sortNo asc")
    fun findAllByEventIdOrderBySortNo(@Param("eventId") eventId: String): List<EventImage>

    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from EventImage i where i.event.id = :eventId")
    fun deleteByEventId(@Param("eventId") eventId: String)

    @Query("select count(i) from EventImage i where i.event.id = :eventId")
    fun countByEventId(@Param("eventId") eventId: String): Long
}
