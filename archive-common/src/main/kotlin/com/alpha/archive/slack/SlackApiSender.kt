package com.alpha.archive.slack

import com.alpha.archive.slack.dto.ErrorInfo
import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.task.TaskExecutor
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Slack Bot Token API를 사용한 보안 에러 알림 전송자
 * Webhook보다 더 안전한 Bot Token만 사용
 */
@Component
class SlackApiSender(
    private val slackMethodsClient: MethodsClient,
    private val slackBotToken: String,
    private val slackChannel: String,
    @Qualifier("slackTaskExecutor") private val taskExecutor: TaskExecutor,
) {

    companion object {
        private val logger = LoggerFactory.getLogger(SlackApiSender::class.java)
    }

    fun sendErrorNotification(errorInfo: ErrorInfo) {
        taskExecutor.execute {
            sendViaBotApi(errorInfo)
        }
    }

    /**
     * Bot Token API를 사용하여 메시지 전송 (보안 강화)
     */
    private fun sendViaBotApi(errorInfo: ErrorInfo) {
        try {
            val message = createErrorMessage(errorInfo)
            
            val request = ChatPostMessageRequest.builder()
                .channel(slackChannel)
                .text(message)
                .build()
            
            val response = slackMethodsClient.chatPostMessage(request)
            
            if (response.isOk) {
                logger.info("💬 Slack Bot API 알림 전송 완료: ${errorInfo.httpMethod} ${errorInfo.requestUri}")
            } else {
                val errorMessage = when (response.error) {
                    "invalid_auth" -> "유효하지 않은 Slack Bot 토큰입니다. 토큰을 확인해주세요."
                    "channel_not_found" -> "Slack 채널을 찾을 수 없습니다: $slackChannel"
                    "not_in_channel" -> "Bot이 해당 채널에 초대되지 않았습니다: $slackChannel. /invite @bot-name 으로 초대해주세요."
                    "missing_scope" -> "Bot에 필요한 권한이 없습니다. chat:write 권한을 추가해주세요."
                    else -> response.error
                }
                logger.error("❌ Slack Bot API 실패: $errorMessage")
            }
        } catch (e: Exception) {
            logger.error("❌ Slack Bot API 에러", e)
        }
    }

    /**
     * Bot API용 간단한 텍스트 메시지 생성
     */
    private fun createErrorMessage(errorInfo: ErrorInfo): String {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val errorIcon = when {
            errorInfo.statusCode >= 500 -> "🚨"
            errorInfo.statusCode >= 400 -> "⚠️"
            else -> "❌"
        }

        return buildString {
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
