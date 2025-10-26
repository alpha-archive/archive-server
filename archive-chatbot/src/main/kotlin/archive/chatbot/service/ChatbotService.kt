package com.alpha.archive.chatbot.service

import com.alpha.archive.chatbot.SystemPrompt
import com.alpha.archive.chatbot.dto.EventDataDto
import com.alpha.archive.chatbot.dto.Message
import com.alpha.archive.domain.event.UserEvent
import com.alpha.archive.domain.event.embeddable.ActivityInfo
import com.alpha.archive.domain.event.repository.UserEventRepository
import com.alpha.archive.domain.user.UserRepository
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.concurrent.TimeUnit

@Service
class ChatbotService(
    private val openAPIService: OpenAPIService,
    private val userEventRepository: UserEventRepository,
    private val userRepository: UserRepository,
    private val redisTemplate: RedisTemplate<String, Any>
) {
    private val objectMapper = jacksonObjectMapper().findAndRegisterModules()

    private fun getHistoryKey(userId: String) = "chatbot:history:$userId"

    @Transactional
    suspend fun continueConversation(userId: String, userMessage: String): String {
        val historyKey = getHistoryKey(userId)

        val rawHistory: Any? = redisTemplate.opsForValue().get(historyKey)

        val history: MutableList<Message> = if (rawHistory == null) {
            // 'if'가 참일 때 이 값을 반환하여 history에 할당.
            mutableListOf(Message(role = "system", content = SystemPrompt.PROMPT))
        } else {
            // 'else'일 경우, 'try/catch' 표현식의 결과를 반환하여 history에 할당.
            try {
                objectMapper.convertValue(
                    rawHistory,
                    object : TypeReference<MutableList<Message>>() {}
                )
            } catch (e: Exception) {
                mutableListOf(Message(role = "system", content = SystemPrompt.PROMPT))
            }
        }

        history.add(Message(role = "user", content = userMessage))
        // OpenAI API 호출
        val aiResponse = openAPIService.askWithHistory(history)
        val aiMessageContent = aiResponse.choices?.firstOrNull()?.message?.content
            ?: return "죄송해요, 답변을 생성하는 데 문제가 생겼어요. 다시 시도해 주세요."

        // AI 응답이 JSON인지 파싱 시도
        try {
            val eventData = objectMapper.readValue<EventDataDto>(aiMessageContent)
            saveUserEvent(userId, eventData)
            // 성공 시 Redis에서 대화 기록 삭제
            redisTemplate.delete(historyKey)
            return "경험이 성공적으로 기록되었어요! 또 다른 이야기를 들려주세요."
        } catch (e: Exception) {
            history.add(Message(role = "assistant", content = aiMessageContent))
            // 실패 시 (대화 지속) 업데이트된 기록을 Redis에 다시 저장 (1시간 뒤 자동 만료되도록 설정)
            redisTemplate.opsForValue().set(historyKey, history, 1, TimeUnit.HOURS)
            return aiMessageContent
        }
    }

    private fun saveUserEvent(userId: String, data: EventDataDto) {
        val user = userRepository.findUserById(userId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "해당 ID의 사용자를 찾을 수 없습니다: $userId")

        val activityInfo = ActivityInfo(
            customTitle = data.customTitle,
            customCategory = data.customCategory,
            customLocation = data.customLocation,
            rating = data.rating,
            memo = data.memo
        )
        val userEvent = UserEvent(
            user = user,
            activityDate = data.activityDate,
            activityInfo = activityInfo
        )

        userEventRepository.save(userEvent)
    }
}