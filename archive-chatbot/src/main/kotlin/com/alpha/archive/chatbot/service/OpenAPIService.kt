package com.alpha.archive.chatbot.service

import com.alpha.archive.chatbot.config.OpenAPIConfig
import com.alpha.archive.chatbot.dto.Message
import com.alpha.archive.chatbot.dto.OpenAPIRequest
import com.alpha.archive.chatbot.dto.OpenAPIResponse
import com.alpha.archive.chatbot.dto.QuestionRequest
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ResponseStatusException

@Service
class OpenAPIService(
    private val openAPIConfig: OpenAPIConfig,
    private val webClientBuilder: WebClient.Builder
) {
    private val logger = LoggerFactory.getLogger(OpenAPIService::class.java)
    private val webClient = webClientBuilder
        .baseUrl("https://api.openai.com/v1/")
        .build()

    suspend fun askQuestion(requestDto: QuestionRequest): OpenAPIResponse? {
        if (requestDto.question.isBlank()) {
            // 비어있다면, OpenAI에 요청을 보내지 않고 즉시 400 에러를 클라이언트에게 반환합니다.
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "질문 내용은 비어 있을 수 없습니다.")
        }
        val messages = listOf(Message("user", requestDto.question))
        val openAPIRequest = OpenAPIRequest(
            model = OpenAPIConfig.MODEL,
            messages = messages,
            maxTokens = OpenAPIConfig.MAX_TOKENS,
            temperature = OpenAPIConfig.TEMPERATURE,
            topP = OpenAPIConfig.TOP_P
        )

        logger.info("requestBody: $openAPIRequest")

        return webClient.post()
            .uri("chat/completions")
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${openAPIConfig.apiKey}")
            .bodyValue(openAPIRequest)
            .retrieve()
            .bodyToMono(OpenAPIResponse::class.java)
            .doOnSuccess { logger.info("✅ OpenAI 응답 성공") }
            .doOnError { e -> logger.error("❌ OpenAI 호출 실패: ${e.message}") }
            .awaitSingle()
    }

    suspend fun askWithHistory(messages: List<Message>): OpenAPIResponse {
        logger.info("✉️ OpenAI에 대화 기록 전송 (메시지 ${messages.size}개)")
        val openAPIRequest = OpenAPIRequest(
            model = "gpt-4o",
            messages = messages,
            maxTokens = OpenAPIConfig.MAX_TOKENS,
            temperature = OpenAPIConfig.TEMPERATURE,
            topP = OpenAPIConfig.TOP_P,
        )

        return webClient.post()
            .uri("/chat/completions")
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${openAPIConfig.apiKey}")
            .bodyValue(openAPIRequest)
            .retrieve()
            .bodyToMono(OpenAPIResponse::class.java)
            .doOnSuccess { logger.info("✅ OpenAI 응답 성공") }
            .doOnError { e -> logger.error("❌ OpenAI 호출 실패: ${e.message}", e) }
            .awaitSingle()
    }
}