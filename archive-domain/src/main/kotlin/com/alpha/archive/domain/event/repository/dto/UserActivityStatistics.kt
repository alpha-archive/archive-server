package com.alpha.archive.domain.event.repository.dto

/**
 * 사용자 활동 통계 데이터 클래스
 */
data class UserActivityStatistics(
    val totalCount: Long,
    val averageRating: Double?
)