package com.alpha.archive.slack

import com.alpha.archive.slack.dto.ErrorInfo
import com.slack.api.Slack
import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.model.block.LayoutBlock
import com.slack.api.model.block.SectionBlock
import com.slack.api.model.block.composition.MarkdownTextObject
import com.slack.api.webhook.Payload
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.task.TaskExecutor
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Slack SDK를 사용한 에러 알림 전송자
 */
@Component
class SlackApiSender(
    private val slack: Slack,
    private val slackWebhookUrl: String,
    @Qualifier("slackTaskExecutor") private val taskExecutor: TaskExecutor,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(SlackApiSender::class.java)
        private const val DEFAULT_CHANNEL = "#백엔드_에러알림"
    }

    fun sendErrorNotification(errorInfo: ErrorInfo) {
        taskExecutor.execute {
            sendViaWebhook(errorInfo)
        }
    }

    private fun sendViaWebhook(errorInfo: ErrorInfo) {
        if (slackWebhookUrl.isBlank()) {
            logger.warn("Slack webhook URL이 설정되지 않아 알림을 전송하지 않습니다.")
            return
        }

        try {
            val payload = createOptimizedErrorPayload(errorInfo)
            val response = slack.send(slackWebhookUrl, payload)

            if (response.code == 200) {
                logger.info("Slack 웹훅 알림 전송 완료: ${errorInfo.httpMethod} ${errorInfo.requestUri}")
            } else {
                val errorMessage = when (response.body) {
                    "invalid_token" -> "유효하지 않은 Slack 웹훅 토큰입니다."
                    "channel_not_found" -> "Slack 채널을 찾을 수 없습니다."
                    else -> response.body
                }
                logger.error("Slack 웹훅 실패: ${response.code} - $errorMessage")
            }
        } catch (e: Exception) {
            logger.error("Slack 웹훅 에러", e)
        }
    }

    private fun createOptimizedErrorPayload(errorInfo: ErrorInfo): Payload {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val errorIcon = when {
            errorInfo.statusCode >= 500 -> "🚨"
            errorInfo.statusCode >= 400 -> "⚠️"
            else -> "❌"
        }

        val message = buildString {
            appendLine("$errorIcon **Archive API 에러 발생**")
            appendLine("**시간:** $timestamp")
            appendLine("**상태 코드:** ${errorInfo.statusCode}")
            appendLine("**HTTP 메서드:** ${errorInfo.httpMethod}")
            appendLine("**요청 URI:** ${errorInfo.requestUri}")
            appendLine("**에러 메시지:** ${errorInfo.errorMessage}")

            errorInfo.remoteAddr?.let {
                appendLine("**클라이언트 IP:** $it")
            }

            // 중요한 에러일 경우에만 상세 정보 포함
            if (errorInfo.statusCode >= 500) {
                errorInfo.requestBody?.let { body ->
                    if (body.isNotBlank() && body.length < 500) {
                        appendLine("**요청 Body:** ```${body}```")
                    }
                }

                errorInfo.exception?.let { ex ->
                    val truncatedException = truncateText(ex, 1000)
                    appendLine("**예외 정보:** ```$truncatedException```")
                }
            }
        }

        return Payload.builder()
            .text(message)
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
