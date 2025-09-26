package com.alpha.archive.domain.event.repository.dto

import com.alpha.archive.domain.event.enums.EventCategory

/**
 * 카테고리별 개수 데이터 클래스
 */
data class CategoryCount(
    val category: EventCategory,
    val count: Long
)