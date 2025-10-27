package com.alpha.archive.domain.event.repository

import com.alpha.archive.domain.event.repository.dto.CategoryCount
import com.alpha.archive.domain.event.repository.dto.DailyActivityCount
import com.alpha.archive.domain.event.repository.dto.UserActivityStatistics
import java.time.LocalDateTime

interface UserEventCustomRepository {
    
    /**
     * 기간 내 사용자 활동 통계 조회 (Querydsl 활용)
     */
    fun findUserActivityStatistics(userId: String, startDate: LocalDateTime, endDate: LocalDateTime): UserActivityStatistics
    
    /**
     * 기간 내 사용자의 카테고리별 활동 수 조회 (Querydsl 활용)
     */
    fun findCategoryStatistics(userId: String, startDate: LocalDateTime, endDate: LocalDateTime): List<CategoryCount>
    
    /**
     * 기간 내 사용자의 일별 활동 수 조회 (요일별 - 1:월요일, 7:일요일)
     */
    fun findDailyActivityCounts(userId: String, startDate: LocalDateTime, endDate: LocalDateTime): Map<Int, Long>
    
    /**
     * 기간 내 사용자의 날짜별 활동 수 조회 (실제 날짜별)
     */
    fun findActivityCountsByDate(userId: String, startDate: LocalDateTime, endDate: LocalDateTime): Map<java.time.LocalDate, Long>
    
    /**
     * 사용자의 전체 활동 수 조회
     */
    fun countAllUserActivities(userId: String): Long
    
    /**
     * 사용자의 전체 기간 카테고리별 통계 조회
     */
    fun findAllCategoryStatistics(userId: String): List<CategoryCount>
}