package com.alpha.archive.slack

import com.alpha.archive.slack.dto.ErrorInfo
import com.slack.api.Slack
import com.slack.api.model.Attachment
import com.slack.api.model.Field
import com.slack.api.webhook.Payload
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * Slack SDK를 사용한 에러 알림 전송자
 */
@Component
class SlackApiSender(
    @Value("\${slack.webhook.url:}") private val slackWebhookUrl: String
) {
    
    companion object {
        private val logger = LoggerFactory.getLogger(SlackApiSender::class.java)
        private const val ERROR_COLOR = "danger"
        private const val WARNING_COLOR = "warning"
    }

    private val slack = Slack.getInstance()

    /**
     * 에러 정보를 Slack으로 전송
     */
    fun sendErrorNotification(errorInfo: ErrorInfo) {
        if (slackWebhookUrl.isBlank()) {
            logger.warn("Slack webhook URL이 설정되지 않아 알림을 전송하지 않습니다.")
            return
        }

        try {
            val payload = createErrorPayload(errorInfo)
            val response = slack.send(slackWebhookUrl, payload)
            
            if (response.code == 200) {
                logger.info("Slack 에러 알림 전송 완료: ${errorInfo.httpMethod} ${errorInfo.requestUri}")
            } else {
                logger.error("Slack 웹훅 요청 실패: ${response.code} - ${response.body}")
            }
        } catch (e: Exception) {
            logger.error("Slack 에러 알림 전송 실패", e)
        }
    }

    /**
     * 에러 정보를 기반으로 Slack Payload 생성
     */
    private fun createErrorPayload(errorInfo: ErrorInfo): Payload {
        val color = when {
            errorInfo.statusCode >= 500 -> ERROR_COLOR
            errorInfo.statusCode >= 400 -> WARNING_COLOR
            else -> ERROR_COLOR
        }

        val fields = buildList {
            add(Field.builder().title("HTTP Method").value(errorInfo.httpMethod).valueShortEnough(true).build())
            add(Field.builder().title("Status Code").value(errorInfo.statusCode.toString()).valueShortEnough(true).build())
            add(Field.builder().title("Request URL").value(errorInfo.requestUrl).valueShortEnough(false).build())
            add(Field.builder().title("Request URI").value(errorInfo.requestUri).valueShortEnough(false).build())
            add(Field.builder().title("Error Message").value(errorInfo.errorMessage).valueShortEnough(false).build())
            
            errorInfo.requestBody?.let { body ->
                if (body.isNotBlank()) {
                    add(Field.builder().title("Request Body").value(truncateText(body, 1000)).valueShortEnough(false).build())
                }
            }
            
            errorInfo.userAgent?.let { ua ->
                add(Field.builder().title("User Agent").value(truncateText(ua, 500)).valueShortEnough(false).build())
            }
            
            errorInfo.remoteAddr?.let { addr ->
                add(Field.builder().title("Remote Address").value(addr).valueShortEnough(true).build())
            }
            
            errorInfo.exception?.let { ex ->
                add(Field.builder().title("Exception & Stack Trace").value(truncateText(ex, 2000)).valueShortEnough(false).build())
            }
        }

        val attachment = Attachment.builder()
            .color(color)
            .title("🚨 서버 에러 발생 [${errorInfo.statusCode}]")
            .fields(fields)
            .footer("Archive API Error Monitor")
            .ts(Instant.now().epochSecond.toString())
            .build()

        return Payload.builder()
            .text("Archive API에서 에러가 발생했습니다.")
            .attachments(listOf(attachment))
            .build()
    }

    /**
     * 텍스트를 지정된 길이로 자르기
     */
    private fun truncateText(text: String, maxLength: Int): String {
        return if (text.length > maxLength) {
            "${text.take(maxLength)}... (총 ${text.length}자)"
        } else {
            text
        }
    }
}
