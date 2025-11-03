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
import java.time.LocalDate
import java.time.LocalDateTime
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

    suspend fun continueConversation(userId: String, userMessage: String): String {
        val historyKey = getHistoryKey(userId)

        val rawHistory: Any? = redisTemplate.opsForValue().get(historyKey)

        val currentSystemPrompt = SystemPrompt.getPrompt(LocalDate.now())

        val history: MutableList<Message> = if (rawHistory == null) {
            mutableListOf(Message(role = "system", content = currentSystemPrompt))
        } else {
            try {
                val loadedHistory: MutableList<Message> = objectMapper.convertValue(
                    rawHistory,
                    object : TypeReference<MutableList<Message>>() {}
                )

                if (loadedHistory.isNotEmpty() && loadedHistory[0].role == "system") {
                    loadedHistory[0] = Message(role = "system", content = currentSystemPrompt)
                } else {
                    loadedHistory.add(0, Message(role = "system", content = currentSystemPrompt))
                }
                loadedHistory

            } catch (e: Exception) {
                mutableListOf(Message(role = "system", content = currentSystemPrompt))
            }
        }

        history.add(Message(role = "user", content = userMessage))

        val aiResponse = openAPIService.askWithHistory(history)
        val aiMessageContent = aiResponse.choices?.firstOrNull()?.message?.content
            ?: return "죄송해요, 답변을 생성하는 데 문제가 생겼어요. 다시 시도해 주세요."

        try {
            val eventData = objectMapper.readValue<EventDataDto>(aiMessageContent)
            saveUserEvent(userId, eventData)
            redisTemplate.delete(historyKey)
            return "경험이 성공적으로 기록되었어요! 또 다른 이야기를 들려주세요."
        } catch (e: Exception) {
            history.add(Message(role = "assistant", content = aiMessageContent))
            redisTemplate.opsForValue().set(historyKey, history, 1, TimeUnit.HOURS)
            return aiMessageContent
        }
    }

    @Transactional
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
            activityDate = data.activityDate ?: LocalDateTime.now(),
            activityInfo = activityInfo
        )

        userEventRepository.save(userEvent)
    }
}