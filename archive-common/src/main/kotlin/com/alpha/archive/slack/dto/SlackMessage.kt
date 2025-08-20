package com.alpha.archive.slack.dto

/**
 * 에러 정보를 담는 데이터 클래스
 */
data class ErrorInfo(
    val httpMethod: String,
    val requestUrl: String,
    val requestUri: String,
    val statusCode: Int,
    val errorMessage: String,
    val requestBody: String? = null,
    val userAgent: String? = null,
    val remoteAddr: String? = null,
    val exception: String? = null
)
