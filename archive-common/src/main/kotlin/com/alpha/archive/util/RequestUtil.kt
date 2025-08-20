package com.alpha.archive.util

import com.alpha.archive.slack.dto.ErrorInfo
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.util.ContentCachingRequestWrapper
import java.nio.charset.StandardCharsets

/**
 * HttpServletRequest 관련 유틸리티
 */
object RequestUtil {

    /**
     * HttpServletRequest에서 ErrorInfo 객체 생성
     */
    fun createErrorInfo(
        request: HttpServletRequest,
        statusCode: Int,
        errorMessage: String,
        exception: Exception? = null
    ): ErrorInfo {
        return ErrorInfo(
            httpMethod = request.method,
            requestUrl = getFullRequestUrl(request),
            requestUri = request.requestURI,
            statusCode = statusCode,
            errorMessage = errorMessage,
            requestBody = getRequestBody(request),
            userAgent = request.getHeader("User-Agent"),
            remoteAddr = getClientIpAddress(request),
            exception = exception?.let { getFullStackTrace(it) }
        )
    }

    /**
     * 예외의 전체 스택 트레이스를 문자열로 변환
     */
    private fun getFullStackTrace(exception: Exception): String {
        return buildString {
            append("${exception::class.simpleName}: ${exception.message}")
            append("\n")
            
            exception.stackTrace.take(20).forEach { element ->
                append("\tat $element")
                append("\n")
            }
            
            if (exception.stackTrace.size > 20) {
                append("\t... and ${exception.stackTrace.size - 20} more")
            }
        }
    }

    /**
     * 전체 요청 URL 생성 (쿼리 파라미터 포함)
     */
    private fun getFullRequestUrl(request: HttpServletRequest): String {
        val url = StringBuilder()
            .append(request.scheme)
            .append("://")
            .append(request.serverName)
            .append(if (request.serverPort != 80 && request.serverPort != 443) ":${request.serverPort}" else "")
            .append(request.requestURI)

        request.queryString?.let { queryString ->
            url.append("?").append(queryString)
        }

        return url.toString()
    }

    /**
     * 요청 본문 추출
     */
    private fun getRequestBody(request: HttpServletRequest): String? {
        return try {
            when (request) {
                is ContentCachingRequestWrapper -> {
                    val content = request.contentAsByteArray
                    if (content.isNotEmpty()) {
                        String(content, StandardCharsets.UTF_8)
                    } else null
                }
                else -> {
                    // Content-Type이 JSON인 경우에만 본문을 읽으려 시도
                    val contentType = request.contentType
                    if (contentType?.contains("application/json") == true) {
                        "Request body 읽기 실패 (ContentCachingRequestWrapper 미사용)"
                    } else null
                }
            }
        } catch (e: Exception) {
            "Request body 읽기 중 오류: ${e.message}"
        }
    }

    /**
     * 클라이언트 실제 IP 주소 추출 (프록시 고려)
     */
    private fun getClientIpAddress(request: HttpServletRequest): String {
        val headers = listOf(
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED"
        )

        for (header in headers) {
            val ip = request.getHeader(header)
            if (!ip.isNullOrBlank() && !"unknown".equals(ip, ignoreCase = true)) {
                // X-Forwarded-For의 경우 여러 IP가 있을 수 있으므로 첫 번째 것을 사용
                return ip.split(",")[0].trim()
            }
        }

        return request.remoteAddr ?: "unknown"
    }
}
